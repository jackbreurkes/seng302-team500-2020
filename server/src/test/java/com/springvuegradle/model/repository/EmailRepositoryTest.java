package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailRepository emailRepository;

    Email email1;
    Email email2;
    Email email3;
    Email email4;
    Email email5;
    Email email6;
    Email email7;

    @BeforeAll
    public void createTestProfiles() {
        //create users
        User user1 = new User(1);
        User user2 = new User(2);
        User user3 = new User(3);
        User user4 = new User(4);
        User user5 = new User(5);
//        User user5 = new User(5);
        //save users
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);

        //create emails and assign them to users
        email1 = new Email(user1, "primary@test.com", true);
        email2 = new Email(user2, "primary@1234.com", true);
        email3 = new Email(user3, "secondary@test.com", false);
        email4 = new Email(user4, "josh@josh.com", true);
        email5 = new Email(user1, "olivia@olivia.com", false);
        email6 = new Email(user1, "olivia@double.com", false);
        email7 = new Email(user5,"test@test.com", true);

        //save users
        emailRepository.save(email1);
        emailRepository.save(email2);
        emailRepository.save(email3);
        emailRepository.save(email4);
        emailRepository.save(email5);
        emailRepository.save(email6);
        emailRepository.save(email7);

    }

    @ParameterizedTest
    @CsvSource({
            "primary@test.com,1",
            "primary@1234.com,2",
            "secondary@test.com,3",
            "josh@josh.com,4",
            "olivia@olivia.com,1",
            "olivia@double.com,1", // duplicate terms
            "test@test.com,5"
    })
    public void retrieveSingleUserWithValidFullEmail(String email, String userId) {
        int userIdNum = Integer.parseInt(userId);
        List<Email> result = emailRepository.findByEmailStartingWith(email);
        assertEquals(userIdNum, result.get(0).getUser().getUserId());
    }

    @ParameterizedTest
    @CsvSource({
            "josh,4",
            "olivia@,1",
            "olivia,1", // duplicate terms
            "test,5",
            "test@,5"
    })
    public void retrieveSingleUserWithValidPartialEmail(String email, String userId) {
        int userIdNum = Integer.parseInt(userId);
        List<Email> result = emailRepository.findByEmailStartingWith(email);
        assertEquals(userIdNum, result.get(0).getUser().getUserId());
    }
    @ParameterizedTest
    @CsvSource({
            "primary,2",
            "primary@,2",
            "primary@1234.com,2",
            "primary@test.com,1",


    })
    public void retrieveMultipleUsersWithSimilarValidPartialEmail(String email, String userId) {
        int userIdNum = Integer.parseInt(userId);
        List<Email> result = emailRepository.findByEmailStartingWith(email);
        assertEquals(userIdNum, result.get(0).getUser().getUserId());
    }

    @Test
    public void retrieveSingleUserWithInvalidFullEmail() {
        String email = "thisdoesntexist@repository.com";
        List<Email> result = emailRepository.findByEmailStartingWith(email);
        assertEquals(0, result.size());
    }
    @Test
    public void retrieveSingleUserWithInvalidPartialEmail() {
        String email = "thisdoesntexist";
        List<Email> result = emailRepository.findByEmailStartingWith(email);
        assertEquals(0, result.size());
    }
    @Test
    public void retrieveSingleUserWithInvalidEmptyEmail() {
        String email = " ";
        List<Email> result = emailRepository.findByEmailStartingWith(email);
        assertEquals(0, result.size());
    }
    @Test
    public void retrieveMultipleUsersWithIdenticalValidPartialEmail() {
        String email = "primary";
        List<Email> result = emailRepository.findByEmailStartingWith(email);
        assertEquals(2, result.size());
    }
}
