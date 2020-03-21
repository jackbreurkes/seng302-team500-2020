package com.springvuegradle.model.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserRequestTest {

    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setup(){
        createUserRequest = new CreateUserRequest("last", "first", "middle", "nickname", "primary@email.com", "password", "BIO", "1999-11-06", "male");
    }

    @Test
    void TestGetPrimaryEmail(){
        assertEquals("primary@email.com", createUserRequest.getPrimaryEmail());
    }
}
