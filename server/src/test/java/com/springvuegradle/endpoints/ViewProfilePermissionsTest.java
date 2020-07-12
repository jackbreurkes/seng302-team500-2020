package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.IncorrectAuthenticationException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.ProfileObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;

import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ViewProfilePermissionsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private UserProfileController userProfileController;

    private ProfileObjectMapper pom;
    private User tempUser;

    @BeforeAll
    void setup() {
        this.userProfileController = new UserProfileController();
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void beforeEach(){
        this.pom = new ProfileObjectMapper();
        this.tempUser = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(this.tempUser));
    }

    @Test
    void testUpdateOtherProfileAsNonAdmin(){
        //auth as other
        MockHttpServletRequest request = new MockHttpServletRequest();
        User tempUser = new User(2L);
        tempUser.setPermissionLevel(0);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(tempUser));
        request.setAttribute("authenticatedid", 2L);
        assertThrows(IncorrectAuthenticationException.class, () -> {
            userProfileController.updateProfile(new ProfileObjectMapper(),1L, request);
        });
    }

    @Test
    void testUpdateProfileNotAuthenticated(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", null);
        assertThrows(UserNotAuthenticatedException.class, () -> {
           userProfileController.updateProfile(new ProfileObjectMapper(), 2L, request);
        });
    }

    @Test
    void viewProfilesNoAuth(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("athenticatedid", null);
        assertThrows(UserNotAuthenticatedException.class, () ->{
            userProfileController.viewProfile(1L, request);
        });
    }

}
