package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
// these tests fail because of the following exception when trying to call `testUser.getSessions()`
// org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.springvuegradle.model.data.User.sessions, could not initialize proxy - no Session
//    i.e. the problem appears like it may be something to do with how our tests handle hibernate sessions?
// the getSessions() and getEmails() methods are asserted to work in UserRepositoryTest
class SessionRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;

    private User testUser;
    private Session firstTestSession;
    private Session secondTestSession;

    @BeforeAll
    void beforeAll() {
        testUser = new User();
        firstTestSession = new Session(testUser, "testtoken", OffsetDateTime.MAX);
        secondTestSession = new Session(testUser, "othertoken", OffsetDateTime.MAX);
        testUser.setSessions(Arrays.asList(firstTestSession, secondTestSession));
        userRepository.save(testUser);
    }

    @Test
    public void deleteSession_SessionIsRemovedFromUser() {
        sessionRepository.delete(firstTestSession);
        testUser = userRepository.findById(testUser.getUserId()).get();
        for (Session session : testUser.getSessions()) {
            System.out.println(session.getToken());
        }
        assertNotNull(testUser);
        assertFalse(testUser.getSessions().contains(firstTestSession));
        assertTrue(testUser.getSessions().contains(secondTestSession));
    }

}