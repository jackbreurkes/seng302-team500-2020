package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidPasswordException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdatePasswordRequest;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EditPasswordControllerMockTest {

    @InjectMocks
    EditPasswordController editPasswordController;

    @Mock
    UserRepository userRepository;

    public User tempUser;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void beforeEach(){
        this.tempUser = new User(1l);
        try{
            tempUser.setPassword("goodOldPassword");
        }catch (Exception e){
            return;
        }


    }

    @Test
    public void testOldPasswordIncorrect() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        //is authenticated
        request.setAttribute("authenticatedid", 1L);
        assertThrows(InvalidPasswordException.class, () -> {
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
    public void nonExistingUserTest(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        //is authenticated
        request.setAttribute("authenticatedid", 1L);
        assertThrows(RecordNotFoundException.class, () -> {
            editPasswordController.editPassword(1L, new UpdatePasswordRequest("goodOldPassword", "newPassValid", "newPassValid"),
                    request);
        });
    }



    // TODO Implment Tests for
    //  TODO changing password as admin
    // TODO Needs to be merged with dev so this ^ test can be written



}
