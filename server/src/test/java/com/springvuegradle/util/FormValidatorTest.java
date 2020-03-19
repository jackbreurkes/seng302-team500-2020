package com.springvuegradle.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * tests for FormValidator utility class.
 * tests are derived from requirements given in our
 * @author Jack van Heugten Breurkes
 */
class FormValidatorTest {

    @Test
    void testValidName() {
        String validName = "Jack";
        assertTrue(FormValidator.validateName(validName));
    }

    @Test
    void testNameOfMaxLength() {
        String maxLengthName = "A".repeat(30);
        assertTrue(FormValidator.validateName(maxLengthName));
    }

    @Test
    void testNameOverMaxLength() {
        String maxLengthName = "A".repeat(31);
        assertTrue(FormValidator.validateName(maxLengthName));
    }

    @Test
    void testEmptyName() {
        assertFalse(FormValidator.validateName(""));
    }

    @Test
    void testNullName() {
        assertThrows(NullPointerException.class, () -> {
            FormValidator.validateName(null);
        });
    }

    @Test
    void testAllSpacesName() {
        String name = "     ";
        assertFalse(FormValidator.validateName(name));
    }

    @Test
    void testAllNewlinesName() {
        String name = "\n\n\n";
        assertFalse(FormValidator.validateName(name));
    }

    @Test
    void testOneCharacterName() {
        assertTrue(FormValidator.validateName("A"));
    }

    @Test
    void testNameWithNumbers() {
        assertFalse(FormValidator.validateName("Jack2"));
    }

    @Test
    void testValidNickname() {
        assertTrue(FormValidator.validateNickname("NickanemTest"));
    }

    @Test
    void testMaxLengthNickname() {
        String maxLengthNickname = "A".repeat(30);
        assertTrue(FormValidator.validateNickname(maxLengthNickname));
    }

    @Test
    void testOneOverMaxLengthNickname() {
        String maxLengthNickname = "A".repeat(31);
        assertFalse(FormValidator.validateNickname(maxLengthNickname));
    }

    @Test
    void testMinLengthNickame() {
        String nickname = "LenSix";
        assertTrue(FormValidator.validateNickname(nickname));
    }

    @Test
    void testUnderMinLengthNickame() {
        String nickname = "Leng5";
        assertFalse(FormValidator.validateNickname(nickname));
    }

    @Test
    void testEmptyNickname() {
        String nickname = "";
        assertFalse(FormValidator.validateNickname(nickname));
    }

    @ParameterizedTest
    @CsvSource({
            "This is bad", "this\nis\nworse", "worst\tof\tall"
    })
    void testNicknameWithWhitespace(String nickname) {
        assertFalse(FormValidator.validateNickname(nickname));
    }

    @Test
    void testNullNickname() {
        String nickname = null;
        assertThrows(NullPointerException.class, () -> {
            FormValidator.validateNickname(nickname);
        });
    }

    @Test
    void testValidEmail() {
        String email = "test@gmail.com";
        assertTrue(FormValidator.validateEmail(email));
    }

    @ParameterizedTest
    @CsvSource({
            "jeff@cavaliere@athleanx.com", "@dam.conover@test.com", "justin.tredeu@govt.c@"
    })
    void testEmailTooManyAts(String email) {
        assertFalse(FormValidator.validateEmail(email));
    }

    @Test
    void testEmailNothingBeforeAt() {
        String email = "@gmail.com";
        assertFalse(FormValidator.validateEmail(email));
    }

    @Test
    void testEmailNothingAfterAt() {
        String email = "test@";
        assertFalse(FormValidator.validateEmail(email));
    }

    @Test
    void testEmailLongestBeforeAt() {
        String email = "A".repeat(64);
        email += "@gmail.com";
        assertTrue(FormValidator.validateEmail(email));
    }

    @Test
    void testEmailTooMuchBeforeAt() {
        String email = "A".repeat(65);
        email += "@gmail.com";
        assertFalse(FormValidator.validateEmail(email));
    }

    @Test
    void testEmailLongestAfterAt() {
        String email = "testemail@";
        email += "A".repeat(128);
        assertTrue(FormValidator.validateEmail(email));
    }

    @Test
    void testEmailTooMuchAfterAt() {
        String email = "testemail@";
        email += "A".repeat(129);
        assertFalse(FormValidator.validateEmail(email));
    }

    @Test
    void testEmailMaxSize() {
        String email = "A".repeat(64);
        email += "@";
        email += "A".repeat(128);
        assertTrue(FormValidator.validateEmail(email));
    }


    @Test
    void testNullEmail() {
        String email = null;
        assertThrows(NullPointerException.class, () -> {
            FormValidator.validateEmail(email);
        });
    }

    @Test
    void testValidBio() {
        String bio = "This is a really cool bio. It's valid and super cool.";
        assertTrue(FormValidator.validateBio(bio));
    }

    @Test
    void testEmptyBio() {
        String bio = "";
        assertFalse(FormValidator.validateBio(bio));
    }

    @Test
    void testNullBio() {
        String bio = null;
        assertThrows(NullPointerException.class, () -> {
            FormValidator.validateBio(bio);
        });
    }

    @Test
    void testMinLengthBio() {
        String bio = "8charmin";
        assertTrue(FormValidator.validateBio(bio));
    }

    @Test
    void testTooSmallBio() {
        String bio = "toosmal";
        assertFalse(FormValidator.validateBio(bio));
    }

    @ParameterizedTest
    @CsvSource({
            "2000-04-15", "1990-06-18", "1970-12-12", "1969-07-17"
    })
    void testValidDateOfBirth(String dob) {
        // DOB must be in yyyy-MM-dd format
        assertTrue(FormValidator.validateDateOfBirth(dob));
    }

    @ParameterizedTest
    @CsvSource({
            "99-05-17", "1990/06/18", "yesterday", "yyyy-MM-dd", "wrongformat"
    })
    void testInvalidDateOfBirthFormat(String dob) {
        assertFalse(FormValidator.validateDateOfBirth(dob));
    }


    @Test
    void testEmptyDateOfBirth() {
        String dob = "";
        assertFalse(FormValidator.validateDateOfBirth(dob));
    }


    @ParameterizedTest
    @CsvSource({
            "1899-12-31", "99-05-17", "1000-05-19"
    })
    void testDateBefore1900(String dob) {
        assertFalse(FormValidator.validateDateOfBirth(dob));
    }

    @Test
    void testDateInFuture() {
        String dob = "9999-01-23"; // must be yyyy-MM-dd
        assertFalse(FormValidator.validateDateOfBirth(dob));
    }

    @Test
    void testNullDateOfBirth() {
        String dob = null;
        assertThrows(NullPointerException.class, () -> {
            FormValidator.validateDateOfBirth(dob);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "2000-02-31", "1990-04-55", "2000-70-01"
    })
    void testNonexistentDate(String dob) {
        assertFalse(FormValidator.validateDateOfBirth(dob));
    }

    @ParameterizedTest
    @CsvSource({
            "male", "female", "non-binary"
    })
    void testValidGenders(String gender) {
        assertTrue(FormValidator.validateGender(gender));
    }

    @ParameterizedTest
    @CsvSource({
            "Male", "FEMALE", "nON-bInAry"
    })
    void testDifferentCasingsGender(String gender) {
        assertTrue(FormValidator.validateGender(gender));
    }

    @ParameterizedTest
    @CsvSource({
            "femail", "nonbinary", "wolf", "helicopter", "test engineer"
    })
    void testInvalidGenders(String gender) {
        assertFalse(FormValidator.validateGender(gender));
    }

    @Test
    void testEmptyGenderString() {
        String gender = "";
        assertFalse(FormValidator.validateGender(gender));
    }

    @Test
    void testNullGender() {
        String gender = null;
        assertThrows(NullPointerException.class, () -> {
            FormValidator.validateGender(gender);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "password", "length 8", "dictionary attack", "s#cuR3-@-P455//w8rd"
    })
    void testValidPassword(String password) {
        assertTrue(FormValidator.validatePassword(password));
    }

    @Test
    void testTooShortPassword() {
        String password = "7 chars";
        assertFalse(FormValidator.validatePassword(password));
    }

    @Test
    void testEmptyPassword() {
        String password = "";
        assertFalse(FormValidator.validatePassword(password));
    }

    @Test
    void testNullPassword() {
        String password = null;
        assertThrows(NullPointerException.class, () -> {
            FormValidator.validatePassword(password);
        });
    }

}