package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    EmailRepository emailRepository;

    private User testUser;

    @BeforeAll
    void beforeAll() {
        testUser = new User();
        userRepository.save(testUser);
    }

    @Test
    public void deleteUser_CascadesSessionsEmails() {
        Session firstSession = new Session(testUser, "testtoken", OffsetDateTime.MAX);
        Session secondSession = new Session(testUser, "othertoken", OffsetDateTime.MAX);
        testUser.setSessions(Arrays.asList(firstSession, secondSession));
        Email testPrimaryEmail = new Email(testUser, "test@primary.com", true);
        Email testSecondaryEmail = new Email(testUser, "test@secondary.com", false);
        testUser.setEmails(Arrays.asList(testPrimaryEmail, testSecondaryEmail));

        userRepository.delete(testUser);
        assertFalse(userRepository.existsById(testUser.getUserId()));
        assertFalse(sessionRepository.existsById(firstSession.getToken()));
        assertFalse(sessionRepository.existsById(secondSession.getToken()));
        assertFalse(emailRepository.existsById(firstSession.getToken()));
        assertFalse(emailRepository.existsById(secondSession.getToken()));
    }

}