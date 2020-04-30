package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidPasswordException;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdatePasswordRequest;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@Import(EditPasswordController.class)
class EditPasswordControllerTest {

    private EditPasswordController editPasswordController;

    @MockBean
    private UserRepository userRepository;


    @BeforeEach
    void beforeEach() {
        editPasswordController = new EditPasswordController();
    }

    @Test
    public void testMissingOldPassword() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
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
        assertThrows(InvalidRequestFieldException.class, () -> {
            editPasswordController.editPassword(
                    1L,
                    new UpdatePasswordRequest("oldpassword", "bad", "bad"),
                    request
            );
        });
    }

    /**
     * Have split the tests into two files because the mocking of userRepository was not working
     * in this file. The rest of the tests for EditPasswordController are in EditPasswordControllerMockTest.java**/

}