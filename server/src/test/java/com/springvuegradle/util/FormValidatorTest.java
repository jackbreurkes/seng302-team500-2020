package com.springvuegradle.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
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
        String maxLengthName = "A".repeat(FormValidator.NAME_MAX_LENGTH);
        assertTrue(FormValidator.validateName(maxLengthName));
    }

    @Test
    void testNameOverMaxLength() {
        String maxLengthName = "A".repeat(FormValidator.NAME_MAX_LENGTH + 1);
        assertTrue(FormValidator.validateName(maxLengthName));
    }
}