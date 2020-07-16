package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.*;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdatePasswordRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EditPasswordControllerTest {

    /**
     * Creates the edit password controller and inserts the mocks we define in the place of the repositories
     */
    @InjectMocks
    EditPasswordController editPasswordController;

    /**
     * Mock of the userRepository
     */
    @Mock
    UserRepository userRepository;

    public User tempUser;

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void beforeEach(){
        //Create a new user and set a password
        this.tempUser = new User(1l);
        try{
            tempUser.setPassword("goodOldPassword");
        }catch (Exception e){
            return;
        }
    }

    @Test
    public void testNoAuthId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThrows(UserNotAuthenticatedException.class, () -> {
            editPasswordController.editPassword(
                    1L,
                    new UpdatePasswordRequest("oldpassword", "newpassword", "newpassword"),
                    request
            );
        });
    }

    @Test
    public void testEditOtherUsersPasswordNotAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 2L);

        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L)));
        assertThrows(UserNotAuthenticatedException.class, () -> {
            editPasswordController.editPassword(
                    1L,
                    new UpdatePasswordRequest("oldpassword", "newpassword", "newpassword"),
                    request
            );
        });
    }

    @Test
    public void testOldPasswordIncorrect() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        //mock the return of userRepositroy.findById
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        //is authenticated
        request.setAttribute("authenticatedid", 1L);
        assertThrows(ForbiddenOperationException.class, () -> {
            editPasswordController.editPassword(1L, new UpdatePasswordRequest("badOldPassword", "newPassValid", "newPassValid"),
                    request);
        });
    }

    @Test
    public void blueSkyScenario() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        //is authenticated
        request.setAttribute("authenticatedid", 1L);
        try {
            ResponseEntity<Object> response = editPasswordController.editPassword(1l, new UpdatePasswordRequest("goodOldPassword", "newValidPass", "newValidPass"), request);
            assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        }catch (Exception e){
            return;
        }

        //assertEquals();
    }

    @Test
    public void adminEditNonExistingUserTest(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        User tempAdminUser = new User(1L);
        tempAdminUser.setPermissionLevel(127);
        request.setAttribute("authenticatedid", 1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempAdminUser));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            editPasswordController.editPassword(2L, new UpdatePasswordRequest("goodOldPassword", "newPassValid", "newPassValid"),
                    request);
        });
    }

    @Test
    public void adminBadRequestTest(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        User tempAdminUser = new User(2L);
        tempAdminUser.setPermissionLevel(127);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(tempAdminUser));
        //is authenticated
        request.setAttribute("authenticatedid", 2L);

        assertThrows(InvalidRequestFieldException.class,() -> {
            editPasswordController.editPassword(1L, new UpdatePasswordRequest(null, null, null), request);
        });
    }

    @Test
    public void adminChangePassword() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();

        User tempAdminUser = new User(2L);
        tempAdminUser.setPermissionLevel(127);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(tempAdminUser));
        //is authenticated
        request.setAttribute("authenticatedid", 2L);

        ResponseEntity<Object> response = editPasswordController.editPassword(1L, new UpdatePasswordRequest(null, "newValidPass", "newValidPass"), request);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void testMissingOldPassword() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        assertThrows(InvalidRequestFieldException.class, () -> {
            editPasswordController.editPassword(
                    1L,
                    new UpdatePasswordRequest(null, "newpassword", "newpassword"),
                    request
            );
        });
    }

    @Test
    public void testMissingNewPassword() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        assertThrows(InvalidRequestFieldException.class, () -> {
            editPasswordController.editPassword(
                    1L,
                    new UpdatePasswordRequest("oldpassword", null, "newpassword"),
                    request
            );
        });
    }

    @Test
    public void testMissingRepeatPassword() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        assertThrows(InvalidRequestFieldException.class, () -> {
            editPasswordController.editPassword(
                    1L,
                    new UpdatePasswordRequest("oldpassword", "newpassword", null),
                    request
            );
        });
    }

    @Test
    public void testPasswordNotRepeatedCorrectly() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        assertThrows(InvalidRequestFieldException.class, () -> {
            editPasswordController.editPassword(
                    1L,
                    new UpdatePasswordRequest("oldpassword", "newpassword", "differentpassword"),
                    request
            );
        });
    }

    @Test
    public void testNewPasswordNotValid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        assertThrows(InvalidRequestFieldException.class, () -> {
            editPasswordController.editPassword(
                    1L,
                    new UpdatePasswordRequest("oldpassword", "bad", "bad"),
                    request
            );
        });
    }
}
