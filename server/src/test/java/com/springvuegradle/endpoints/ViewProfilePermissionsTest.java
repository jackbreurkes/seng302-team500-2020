package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.ProfileObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

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
        userProfileController = new UserProfileController();
        pom = new ProfileObjectMapper();
        MockitoAnnotations.initMocks(userProfileController);
        this.tempUser = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
    }

    @Test
    void testUpdateOwnProfile(){
        MockHttpServletRequest request = new MockHttpServletRequest();
    }

    @Test
    void testUpdateOtherProfile(){
        //auth as other
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 2L);
        assertThrows(UserNotAuthenticatedException.class, () -> {
            userProfileController.updateProfile(new ProfileObjectMapper(),1L, request);
        });
    }

    @Test
    void testUpdateProfileNoAuth(){

    }

    @Test
    void viewProfilesNoAuth(){

    }

    @Test
    void viewProfilesAuth(){

    }
}
