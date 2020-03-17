package com.springvuegradle.model.data;

import com.springvuegradle.auth.ChecksumUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User testUser;

    @BeforeEach
    void setupUser(){
        testUser = new User();
    }

    @Test
    void setPassword() throws NoSuchAlgorithmException {
        testUser.setPassword("testPassword");
        assertEquals(ChecksumUtils.hashPassword(testUser.getUserId(), "testPassword"), testUser.getPassword());
    }
}
