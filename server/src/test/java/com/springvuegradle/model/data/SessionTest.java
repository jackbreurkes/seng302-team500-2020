package com.springvuegradle.model.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {

    private Session session;

    @BeforeEach
    void setup(){
        session = new Session(new User(), "tokenTest", null);
    }

    @Test
    void setToken(){
        session.setToken("newTokenTest");
        assertEquals(session.getToken(), "newTokenTest");
    }
}
