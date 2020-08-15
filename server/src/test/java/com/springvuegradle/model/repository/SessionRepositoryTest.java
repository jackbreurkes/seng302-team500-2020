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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.TestTransaction;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
        TestTransaction.flagForCommit(); // need this, otherwise the next line does a rollback
        TestTransaction.end(); // ensures the test session is deleted before trying findById
        TestTransaction.start();
        User savedUser = userRepository.findById(testUser.getUserId()).get();

        assertNotNull(savedUser);
        assertEquals(1, savedUser.getSessions().size());
        assertFalse(savedUser.getSessions().contains(firstTestSession));
        Session savedSession = savedUser.getSessions().get(0);
        assertEquals(secondTestSession.getToken(), savedSession.getToken());
    }

}