package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidPasswordException;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdatePasswordRequest;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EditPasswordControllerMockTest {

    @InjectMocks
    EditPasswordController editPasswordController;

    @Mock
    UserRepository userRepository;

    @BeforeAll
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOldPasswordIncorrect() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        User tempUser = new User();
        try{
            tempUser.setPassword("goodOldPassword");
        }catch (Exception e){
            return;
        }

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(tempUser));
        //is authenticated
        request.setAttribute("authenticatedid", 1L);
        assertThrows(InvalidPasswordException.class, () -> {
            editPasswordController.editPassword(1L, new UpdatePasswordRequest("badOldPassword", "newPassValid", "newPassValid"),
                    request);
        });
    }
}
