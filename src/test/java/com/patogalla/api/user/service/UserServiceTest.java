package com.patogalla.api.user.service;

import com.patogalla.api.email.service.EmailService;
import com.patogalla.api.google.service.GoogleUserService;
import com.patogalla.api.user.config.TokenConfig;
import com.patogalla.api.user.dto.UserDTO;
import com.patogalla.api.user.dto.UserRegistrationDTO;
import com.patogalla.api.user.exception.UserException;
import com.patogalla.api.user.exception.UserNotFoundException;
import com.patogalla.api.user.model.User;
import com.patogalla.api.user.repository.UserRepository;
import com.patogalla.api.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock(stubOnly = true)
    private TimeService timeService;
    @Mock(stubOnly = true)
    private SaltService saltService;
    @Mock
    private TokenConfig tokenConfig;
    @Mock
    private EmailService emailService;
    @Mock
    private GoogleUserService googleService;

    private UserService userService;

    @Before
    public void init() {
        this.userService = new UserService(this.userRepository, this.timeService, this.saltService, this.tokenConfig, this.emailService, this.googleService);
    }

    @Test
    public void testRegistration_success() throws UserException {

        // INIT - DATA
        String email = "email";
        UserRegistrationDTO userDTO = getUserRegistrationDTO(email);

        // INIT - MOCK
        Mockito.when(this.userRepository.save(Mockito.any())).thenReturn(getUser(email));

        // TEST
        UserDTO result = this.userService.startRegistration(userDTO);

        // ASSERTS
        Assert.assertEquals(email, result.getEmail());

        // Verifies
        Mockito.verify(this.userRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(this.userRepository, Mockito.times(1)).findByUsernameOrEmail(Mockito.anyString(), Mockito.same(email));
        Mockito.verify(this.emailService, Mockito.times(1)).sendNotification(Mockito.same(email), Mockito.anyString(), Mockito.anyString(), Mockito.any());
    }

    @Test(expected = UserNotFoundException.class)
    public void testSave_withoutId() throws UserException, UserNotFoundException {
        // INIT -DATA
        UserDTO userDTO = getUserDTO("email");

        // TEST
        this.userService.save(userDTO);
    }

    private UserDTO getUserDTO(String email) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        return userDTO;
    }

    private User getUser(String email) {
        User savedUsed = new User();
        savedUsed.setEmail(email);
        return savedUsed;
    }

    private UserRegistrationDTO getUserRegistrationDTO(String email) {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setEmail(email);
        userDTO.setUsername("user");
        userDTO.setPassword("pass");
        userDTO.setFirstName("name");
        userDTO.setLastName("lastname");
        return userDTO;
    }
}
