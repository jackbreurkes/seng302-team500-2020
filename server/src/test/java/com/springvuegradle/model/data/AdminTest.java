package com.springvuegradle.model.data;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class AdminTest {

    @Test
    void testConstructorProper(){
        User u = new User();
        long uuid = u.getUserId();
        Admin testAdmin = new Admin(u);
        assert(uuid == testAdmin.getUser().getUserId());
    }
}
