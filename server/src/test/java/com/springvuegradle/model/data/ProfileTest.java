package com.springvuegradle.model.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Date;

public class ProfileTest {

    private Profile testProfile;

    @BeforeEach
    void setup(){
        User testUser = new User();
        testProfile = new Profile(testUser, "firstname", "lastname", null,Gender.NON_BINARY);
    }

    @Test
    void firstNameNull(){
        assertThrows(IllegalArgumentException.class, () -> testProfile.setFirstName(null));
    }

    @Test
    void firstNameEmpty(){
        assertThrows(IllegalArgumentException.class, () -> testProfile.setFirstName(""));
    }

    @Test
    void firstNameValid(){
        testProfile.setFirstName("David");
        assertEquals(testProfile.getFirstName(), "David");
    }

    @Test
    void lastNameNull(){
        assertThrows(IllegalArgumentException.class, () -> testProfile.setLastName(null));
    }

    @Test
    void lastNameEmpty(){
        assertThrows(IllegalArgumentException.class, () -> testProfile.setLastName(""));
    }

    @Test
    void lastNameValid(){
        testProfile.setLastName("David2");
        assertEquals(testProfile.getLastName(), "David2");
    }

    @Test
    void setGenderNull(){
        assertThrows(IllegalArgumentException.class, () -> testProfile.setGender(null));
    }

    @Test
    void setGenderCorrect(){
        testProfile.setGender(Gender.FEMALE);
        assertEquals(testProfile.getGender(), Gender.FEMALE);
    }
}
