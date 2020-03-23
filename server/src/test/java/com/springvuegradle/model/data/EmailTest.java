package com.springvuegradle.model.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailTest {

    private Email testEmail;
    private User testUser;

    @BeforeEach
    void setup(){
        testUser = new User();
        testEmail = new Email(testUser, "test@email.com", false);
    }

    @Test
    void testgetPrimaryFalse(){
        assertEquals(null, testEmail.getPrimaryEmail(testUser));
    }

    @Test
    void testGetPrimaryTrue(){
        testEmail.setIsPrimary(true);
        assertEquals(testEmail, testEmail.getPrimaryEmail(testUser));
    }
}

