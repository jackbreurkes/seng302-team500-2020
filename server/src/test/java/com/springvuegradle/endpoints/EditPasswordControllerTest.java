package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidPasswordException;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdatePasswordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EditPasswordControllerTest {

    private EditPasswordController editPasswordController;

    @BeforeEach
    void beforeEach() {
        editPasswordController = new EditPasswordController();
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
    public void testDifferentUsersAuthId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 2L);
        assertThrows(UserNotAuthenticatedException.class, () -> {
            editPasswordController.editPassword(
                    1L,
                    new UpdatePasswordRequest("oldpassword", "newpassword", "newpassword"),
                    request
            );
        });
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

    // TODO once we know how to mock userRepository, implement tests for:
    //  TODO incorrect old password field
    //  TODO correct info (blue sky scenario)
    //  TODO changing password as admin
    //  TODO profile_id for nonexistent user (record not found)
}