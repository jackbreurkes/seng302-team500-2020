package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @BeforeEach
    void beforeAll() {
        testUser = new User();
        userRepository.save(testUser);
    }

    @Test
    public void saveUser_CascadesSessionsEmails() {
        Session firstSession = new Session(testUser, "testtoken", OffsetDateTime.MAX);
        Session secondSession = new Session(testUser, "othertoken", OffsetDateTime.MAX);
        List<Session> userSessions = Arrays.asList(firstSession, secondSession);
        testUser.setSessions(userSessions);
        Email testPrimaryEmail = new Email(testUser, "test@primary.com", true);
        Email testSecondaryEmail = new Email(testUser, "test@secondary.com", false);
        List<Email> userEmails = Arrays.asList(testPrimaryEmail, testSecondaryEmail);
        testUser.setEmails(userEmails);
        testUser = userRepository.save(testUser);

        assertTrue(userRepository.existsById(testUser.getUserId()));
        assertTrue(sessionRepository.existsById(firstSession.getToken()));
        assertTrue(sessionRepository.existsById(secondSession.getToken()));
        assertTrue(emailRepository.existsById(testPrimaryEmail.getEmail()));
        assertTrue(emailRepository.existsById(testSecondaryEmail.getEmail()));
        System.out.println(testUser.getSessions());
        List<String> savedSessionTokens = userSessions.stream().map(Session::getToken).collect(Collectors.toList());
        assertTrue(testUser.getSessions().stream().map(Session::getToken).collect(Collectors.toList()).containsAll(savedSessionTokens));
        List<String> savedEmails = userEmails.stream().map(Email::getEmail).collect(Collectors.toList());
        assertTrue(testUser.getEmails().stream().map(Email::getEmail).collect(Collectors.toList()).containsAll(savedEmails));
    }

    @Test
    public void deleteUser_CascadesSessionsEmails() {
        Session firstSession = new Session(testUser, "testtoken", OffsetDateTime.MAX);
        Session secondSession = new Session(testUser, "othertoken", OffsetDateTime.MAX);
        testUser.setSessions(Arrays.asList(firstSession, secondSession));
        Email testPrimaryEmail = new Email(testUser, "test@primary.com", true);
        Email testSecondaryEmail = new Email(testUser, "test@secondary.com", false);
        testUser.setEmails(Arrays.asList(testPrimaryEmail, testSecondaryEmail));
        userRepository.save(testUser);

        userRepository.delete(testUser);
        assertFalse(userRepository.existsById(testUser.getUserId()));
        assertFalse(sessionRepository.existsById(firstSession.getToken()));
        assertFalse(sessionRepository.existsById(secondSession.getToken()));
        assertFalse(emailRepository.existsById(testPrimaryEmail.getEmail()));
        assertFalse(emailRepository.existsById(testSecondaryEmail.getEmail()));
    }

}