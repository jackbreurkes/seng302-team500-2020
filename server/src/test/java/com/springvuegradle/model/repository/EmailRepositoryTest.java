//package com.springvuegradle.model.repository;
//
//import com.springvuegradle.model.data.*;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class EmailRepositoryTest {
//
//
//    @Autowired
//    EmailRepository emailRepository;
//
//    Email email1;
//    Email email2;
//    Email email3;
//    Email email4;
//    Email email5;
//    Email email6;
//
//    @BeforeAll
//    public void createTestProfiles() {
//        email1 = new Email(new User(1), "primary@test.com", true);
//        email2 = new Email(new User(2), "primary@1234.com", true);
//        email3 = new Email(new User(3), "secondary@test.com", false);
//        email4 = new Email(new User(4), "josh@josh.com", true);
//        email5 = new Email(new User(5), "olivia@olivia.com", false);
//        email6 = new Email(new User(6), "olivia@double.com", false);
//
//
//        emailRepository.save(email1);
//        emailRepository.save(email2);
//        emailRepository.save(email3);
//        emailRepository.save(email4);
//        emailRepository.save(email5);
//        emailRepository.save(email6);
//
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "primary@test.com,1",
//            "primary@1234.com,2",
//            "secondary@test.com,3",
//            "josh@josh.com,4",
//            "olivia@olivia.com,1",
//            "olivia@double.com,1" // duplicate terms
//    })
//
//    public void retrieveSingleUserWithValidEmail(String input) {
//        String[] splitString = input.split(",");
//        String email = splitString[0];
//        int userId = Integer.parseInt(splitString[1]);
//        List<Email> result = emailRepository.findByEmailStartingWith(email);
//        assertEquals(userId, result.get(0).getUser().getUserId());
//    }
//}
