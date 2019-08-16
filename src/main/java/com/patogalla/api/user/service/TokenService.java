package com.patogalla.api.user.service;

import com.patogalla.api.email.service.EmailService;
import com.patogalla.api.google.service.GoogleUserService;
import com.patogalla.api.user.config.TokenConfig;
import com.patogalla.api.user.dto.*;
import com.patogalla.api.google.exception.GoogleException;
import com.patogalla.api.user.exception.IncorrectCredentialsException;
import com.patogalla.api.user.exception.UserNotFoundException;
import com.patogalla.api.user.model.Token;
import com.patogalla.api.user.model.TokenForgot;
import com.patogalla.api.user.model.User;
import com.patogalla.api.user.repository.TokenForgotRepository;
import com.patogalla.api.user.repository.TokenRepository;
import com.patogalla.api.utils.model.Identity;
import com.patogalla.api.utils.time.TimeService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import org.flywaydb.core.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    private final JwtService jwtService;
    private final TimeService timeService;
    private final Supplier<Identity> identitySupplier;
    private final TokenRepository tokenRepository;
    private final TokenForgotRepository tokenForgotRepository;
    private final UserService userService;
    private final TokenConfig config;
    private final LoadingCache<Identity, Optional<Identity>> currentUsers;
    private final SaltService saltService;
    private final EmailService emailService;
    private final GoogleUserService googleService;

    @Autowired
    public TokenService(final JwtService jwtService, final TokenConfig config,
                        final TimeService timeService, final TokenRepository tokenRepository, final TokenForgotRepository tokenForgotRepository,
                        final UserService userService, final SaltService saltService, final EmailService emailService,
                        final GoogleUserService googleService) {
        this.currentUsers = CacheBuilder.<Identity, Optional<Identity>>newBuilder()
                .expireAfterWrite(config.tokenCacheTtlSeconds().getSeconds(), TimeUnit.SECONDS)
                .maximumSize(config.tokenCacheMaxSize())
                .build(new CacheLoader<Identity, Optional<Identity>>() {
                    @Override
                    public Optional<Identity> load(final Identity tokenId) throws Exception {
                        return tokenRepository.findById(tokenId)
                                .filter(token -> timeService.isFuture(token.getExpiresOn()))
                                .map(Token::getUserId);
                    }
                });
        this.tokenRepository = Objects.requireNonNull(tokenRepository);
        this.tokenForgotRepository = Objects.requireNonNull(tokenForgotRepository);
        this.userService = Objects.requireNonNull(userService);
        this.jwtService = Objects.requireNonNull(jwtService);
        this.timeService = Objects.requireNonNull(timeService);
        this.identitySupplier = Identity::random;
        this.config = Objects.requireNonNull(config);
        this.saltService = saltService;
        this.emailService = emailService;
        this.googleService = googleService;
    }

    @Transactional(readOnly = true)
    public UserDTO verifyLoginToken(final String tokenEncrypted) throws UserNotFoundException, ExecutionException {
        LOGGER.debug("Verifying token : {}***", StringUtils.left(tokenEncrypted, 3));
        Optional<Identity> parse = jwtService.parse(tokenEncrypted);
        if (parse.isPresent()) {
            Optional<Identity> userId = this.currentUsers.get(parse.get());
            if (userId.isPresent()) {
                LOGGER.debug("Getting user : {}", userId.get());
                return this.userService.find(userId.get());
            }
        }
        LOGGER.debug("User not found.");
        throw new UserNotFoundException("Invalid or no token provided.");
    }

    @Transactional
    public TokenResponseDTO createUserToken(final TokenRequestDTO request) throws IncorrectCredentialsException {
        LOGGER.debug("Loging in user: {}", request);
        Optional<User> byUserName = this.userService.findByUsernameOrEmail(request.getUsername());
        return byUserName
                .filter(user -> this.saltService.check(user.getSalt(), user.getPassword(), request.getPassword()))
                .map(user -> {
                    LOGGER.debug("Getting token for user: {}", user);
                    return this.findOrCreateToken(user);
                })
                .orElseThrow(() -> new IncorrectCredentialsException("Incorrect credentials."));
    }

    @Transactional
    public TokenResponseDTO createUserGoogleToken(GoogleTokenRequestDTO tokenDTO) throws GoogleException, UserNotFoundException, IncorrectCredentialsException {
        LOGGER.debug("Verifying google token: {}", tokenDTO);
        Optional<GoogleIdToken> googleIdToken = this.googleService.verifyToken(tokenDTO.getToken());
        return googleIdToken.map(gtoken -> {
                //Finds User in the DB
                    String email = gtoken.getPayload().getEmail();
                    return this.userService.findByUsernameOrEmail(email);
                }).orElseThrow(() -> new UserNotFoundException("User not found in DB"))
                // Creates Token
                .map(user -> {
                    return this.findOrCreateToken(user);
                })
                .orElseThrow(() -> new IncorrectCredentialsException("Incorrect credentials."));
    }

    @Transactional(readOnly = true)
    public UserDTO verifyForgotToken(final String tokenString) throws UserNotFoundException {
        Optional<TokenForgot> token = this.tokenForgotRepository.findById(Identity.unsafeFromString(tokenString));
        if (token.isPresent()) {
            return this.userService.find(token.get().getUserId());
        }
        throw new UserNotFoundException("Invalid or no token provided.");
    }


    @Transactional
    public void createUserForgotToken(ForgotRequestDTO request) throws UserNotFoundException {
        LOGGER.debug("Creating user forgot token: {}", request);
        Optional<User> byUserName = this.userService.findByUsernameOrEmail(request.getEmail());
        TokenResponseDTO result = byUserName
                .map(user -> {
                    final TokenForgot token = tokenForgotRepository.findAllByUserId(user.getId())
                            // Check not expired token.
                            .filter((t) -> t.getExpiresOn().isAfter(this.timeService.now()))
                            // Get one.
                            .findFirst()
                            // If none valid, create one.
                            .orElseGet(() -> {
                                TokenForgot tokenForgot = new TokenForgot(identitySupplier.get(), user.getId(), timeService.now(), timeService.after(config.activationTokenTtlDays()));
                                LOGGER.debug("Saving new forgot token: {}", tokenForgot);
                                return tokenForgotRepository.save(tokenForgot);
                            });
                    return new TokenResponseDTO(token.getId().stringValue());
                })
                .orElseThrow(() -> new UserNotFoundException("Incorrect credentials."));
        String tokenUrl = request.getCallbackUrl() + result.getToken();
        LOGGER.debug("Sending forgot token email: {} -> {}", tokenUrl, request);
        this.emailService.sendNotification(request.getEmail(), "Recovery password", "templates/email/forgot.password.html", ImmutableMap.of("tokenUrl", tokenUrl));
    }

    @Transactional
    public UserDTO changePassword(ForgotChangePasswordRequestDTO requestDTO) throws UserNotFoundException {
        LOGGER.debug("Changing forgotten pass: {} ", requestDTO);
        UserDTO userDTO = this.verifyForgotToken(requestDTO.getToken());
        this.tokenForgotRepository.deleteById(Identity.unsafeFromString(requestDTO.getToken()));
        return this.userService.changePassword(userDTO.getId(), requestDTO.getPassword());
    }

    private TokenResponseDTO findOrCreateToken(User user) {
        final Token token = tokenRepository.findAllByUserId(user.getId())
                // Check not expired token.
                .filter((t) -> t.getExpiresOn().isAfter(this.timeService.now()))
                // Get one.
                .findFirst()
                .map((t) -> {
                    t.setCreatedOn(timeService.now());
                    t.setExpiresOn(timeService.after(config.tokenTtlDays()));
                    return tokenRepository.save(t);
                })
                // If none valid, create one.
                .orElseGet(() -> {
                    return tokenRepository.save(
                            new Token(identitySupplier.get(), user.getId(), timeService.now(), timeService.after(config.tokenTtlDays()))
                    );
                });
        return new TokenResponseDTO(jwtService.create(token));
    }

}
