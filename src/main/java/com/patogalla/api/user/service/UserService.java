package com.patogalla.api.user.service;

import com.patogalla.api.email.service.EmailService;
import com.patogalla.api.google.exception.GoogleException;
import com.patogalla.api.google.service.GoogleUserService;
import com.patogalla.api.user.config.TokenConfig;
import com.patogalla.api.user.dto.GoogleTokenRequestDTO;
import com.patogalla.api.user.dto.UserActivationDTO;
import com.patogalla.api.user.dto.UserDTO;
import com.patogalla.api.user.dto.UserRegistrationDTO;
import com.patogalla.api.user.exception.UserAlreadyExistsException;
import com.patogalla.api.user.exception.UserException;
import com.patogalla.api.user.exception.UserNotFoundException;
import com.patogalla.api.user.model.User;
import com.patogalla.api.user.repository.UserRepository;
import com.patogalla.api.utils.PatogallaUtils;
import com.patogalla.api.utils.model.Identity;
import com.patogalla.api.utils.time.TimeService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.common.collect.ImmutableMap;
import org.flywaydb.core.internal.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final int DEFAULT_PASSWORD_LENGTH = 20;

    private final UserRepository userRepository;
    private final Supplier<Identity> userIdentitySupplier;
    private final Supplier<String> saltSupplier;
    private final TimeService timeService;
    private final SaltService saltService;
    private final ModelMapper modelMapper;
    private final TokenConfig tokenConfig;
    private final EmailService emailService;
    private final GoogleUserService googleUserService;

    public UserService(final UserRepository userRepository,
                       final TimeService timeService,
                       final SaltService saltService,
                       final TokenConfig tokenConfig,
                       final EmailService emailService,
                       final GoogleUserService googleUserService) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.userIdentitySupplier = Identity::random;
        this.saltSupplier = SaltService::random;
        this.saltService = saltService;
        this.timeService = Objects.requireNonNull(timeService);
        this.tokenConfig = tokenConfig;
        this.emailService = emailService;
        this.googleUserService = googleUserService;
        this.modelMapper = new ModelMapper();
        this.modelMapper.createTypeMap(User.class, UserDTO.class)
                .addMappings(mapper -> mapper.skip(UserDTO::setPassword));
        this.modelMapper.createTypeMap(UserDTO.class, User.class)
                .addMappings(mapper -> mapper.skip(User::setId))
                .addMappings(mapper -> mapper.skip(User::setActive))
                .addMappings(mapper -> mapper.skip(User::setActivationToken))
                .addMappings(mapper -> mapper.skip(User::setCreatedOn));
        this.modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE);
    }

    /**
     * FIND
     */
    @Transactional(readOnly = true)
    public UserDTO find(final Identity userId) throws UserNotFoundException {
        LOGGER.debug("Find user : {}", userId);
        return userRepository.findById(userId).map(u -> this.modelMapper.map(u, UserDTO.class)).orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * FIND by USERNAME
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String username) {
        LOGGER.debug("Find user : {}", username);
        return userRepository.findByUsernameOrEmail(username, username);
    }

    /**
     * FIND ALL
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        LOGGER.debug("Find All users.");
        Iterable<User> all = this.userRepository.findAll();
        return StreamSupport.stream(all.spliterator(), true)
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * SAVE
     */
    @Transactional
    public UserDTO save(UserDTO userDTO) throws UserException, UserNotFoundException {
        if (userDTO.getId() == null) {
            throw new UserNotFoundException(userDTO.getId());
        }
        LOGGER.debug("Updating user : {}.", userDTO);
        Optional<User> user = updateUser(userDTO);
        return saveObject(user).orElseThrow(() -> new UserException("Couldn't save user"));
    }


    /**
     * REGISTER
     */
    @Transactional
    public UserDTO startRegistration(UserRegistrationDTO userDTO) throws UserException {
        LOGGER.debug("Starting registration: {}.", userDTO);
        if(this.findByUsernameOrEmail(userDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        Optional<User> optionalUser = newUser(userDTO);
        Optional<UserDTO> result = this.saveObject(optionalUser);
        result.ifPresent(u -> {
            LOGGER.debug("Sending registration email: {} -- {}.", u.getEmail(), optionalUser.get().getActivationToken());
            this.sendRegistrationEmail(u, optionalUser.get().getActivationToken());
        });
        return result.orElseThrow(() -> new UserException("Couldn't start registration"));
    }

    @Transactional
    public UserDTO startRegistrationGoogle(GoogleTokenRequestDTO tokenRequestDTO) throws GoogleException, UserException {
        LOGGER.debug("Registration by google: {}", tokenRequestDTO);
        Optional<GoogleIdToken> googleIdToken = this.googleUserService.verifyToken(tokenRequestDTO.getToken());
        UserRegistrationDTO registrationDTO = googleIdToken.map(gtoken -> {
            //Finds User in the DB
            GoogleIdToken.Payload payload = gtoken.getPayload();
            String email = payload.getEmail();
            UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
            userRegistrationDTO.setUsername(email);
            userRegistrationDTO.setEmail(email);
            userRegistrationDTO.setFirstName((String) payload.get("given_name"));
            userRegistrationDTO.setLastName((String) payload.get("family_name"));
            userRegistrationDTO.setPictureUrl((String) payload.get("picture"));
            userRegistrationDTO.setPassword(PatogallaUtils.generateRandomText(DEFAULT_PASSWORD_LENGTH));
            return userRegistrationDTO;
        }).orElseThrow(() -> new GoogleException("Couldn't auth google user."));
        return this.startRegistration(registrationDTO);
    }

    /**
     * CHECK REGISTRATION
     */
    @Transactional
    public UserDTO checkToken(UserActivationDTO userDTO) throws UserNotFoundException {
        LOGGER.debug("Checking token activation: {}", userDTO);
        User user = checkTokenValidation(userDTO);
        return this.modelMapper.map(user, UserDTO.class);
    }

    /**
     * COMPLETE REGISTRATION
     */
    @Transactional
    public UserDTO completeRegistration(UserActivationDTO userDTO) throws UserNotFoundException {
        LOGGER.debug("Completing activation: {}", userDTO);
        User user = this.checkTokenValidation(userDTO);
        user.setActive(Boolean.TRUE);
        user.setActivationExpiresOn(null);
        user.setActivationToken(null);
        return this.saveObject(Optional.of(user))
                .orElseThrow(() -> new UserNotFoundException("Invalid user registration."));
    }

    /**
     * CHANGE PASSWORD
     */
    @Transactional
    public UserDTO changePassword(Identity userId, String password) throws UserNotFoundException {
        LOGGER.debug("Changin password: {} -- {}***", userId, StringUtils.left(password, 2));
        return this.userRepository.findById(userId).map(u -> {
            setPassword(u, password);
            User result = this.userRepository.save(u);
            return this.modelMapper.map(result, UserDTO.class);
        }).orElseThrow(() -> new UserNotFoundException("Invalid user to change password."));
    }

    private User checkTokenValidation(UserActivationDTO userDTO) throws UserNotFoundException {
        Optional<User> user = this.userRepository.findByEmailAndActivationToken(userDTO.getEmail(), UUID.fromString(userDTO.getActivationToken()));
        user.filter(u -> u.getActivationExpiresOn().isAfter(this.timeService.now()));
        return user.orElseThrow(() -> new UserNotFoundException(userDTO.getEmail()));
    }


    private void sendRegistrationEmail(UserDTO result, UUID activationToken) {
        Map<String, Object> params = ImmutableMap.of("confirmationCode", activationToken.toString());
        this.emailService.sendNotification(result.getEmail(), "Registration info", "templates/email/confirmation.code.html", params);
    }

    private Optional<UserDTO> saveObject(Optional<User> user) {
        return user.map(u -> userRepository.save(u))
                .map(u -> this.modelMapper.map(u, UserDTO.class));
    }

    private Optional<User> newUser(UserRegistrationDTO userDTO) {User user = new User();
        user.setId(this.userIdentitySupplier.get());
        user.setSalt(this.saltSupplier.get());
        user.setCreatedOn(this.timeService.now());
        user.setActive(Boolean.FALSE);
        user.setActivationToken(UUID.randomUUID());
        user.setActivationExpiresOn(getActivationExpirationDate());
        this.setProperties(user, userDTO);
        return Optional.of(user);
    }

    private Optional<User> updateUser(UserDTO userDTO) throws UserNotFoundException {
        Optional<User> user;
        user = this.userRepository.findById(userDTO.getId());
        user.ifPresent(u -> {
            setProperties(u, userDTO);
        });
        user.orElseThrow(()->new UserNotFoundException(userDTO.getId()));
        return user;
    }

    private void setProperties(User u, UserDTO userDTO) {
        Optional.ofNullable(userDTO.getFirstName()).ifPresent(v -> u.setFirstName(v));
        Optional.ofNullable(userDTO.getLastName()).ifPresent(v -> u.setLastName(v));
        Optional.ofNullable(userDTO.getPhone()).ifPresent(v -> u.setPhone(v));
        Optional.ofNullable(userDTO.getPhoneCountry()).ifPresent(v -> u.setPhoneCountry(v));
        Optional.ofNullable(userDTO.getUsername()).ifPresent(v -> u.setUsername(v));
        Optional.ofNullable(userDTO.getRole()).ifPresent(v -> u.setRole(v));
        Optional.ofNullable(userDTO.getEmail()).ifPresent(v -> u.setEmail(v));
        setPassword(u, userDTO.getPassword());
    }

    private void setProperties(User u, UserRegistrationDTO userDTO) {
        Optional.ofNullable(userDTO.getFirstName()).ifPresent(v -> u.setFirstName(v));
        Optional.ofNullable(userDTO.getLastName()).ifPresent(v -> u.setLastName(v));
        Optional.ofNullable(userDTO.getPhone()).ifPresent(v -> u.setPhone(v));
        Optional.ofNullable(userDTO.getPhoneCountry()).ifPresent(v -> u.setPhoneCountry(v));
        Optional.ofNullable(userDTO.getUsername()).ifPresent(v -> u.setUsername(v));
        Optional.ofNullable(userDTO.getEmail()).ifPresent(v -> u.setEmail(v));
        String passwordString = userDTO.getPassword();
        setPassword(u, passwordString);
    }

    private void setPassword(User u, String passwordString) {
        Optional.ofNullable(passwordString).ifPresent(v -> {
            String password = this.saltService.apply(u.getSalt(), v);
            u.setPassword(password);
        });
    }

    private LocalDateTime getActivationExpirationDate() {
        return this.timeService.after(this.tokenConfig.activationTokenTtlDays());
    }

}
