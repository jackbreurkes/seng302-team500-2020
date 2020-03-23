package com.springvuegradle.util;

import com.springvuegradle.model.data.Gender;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Used to consistently validate form fields between endpoints.
 * Tests are based on the rules laid out in the Form Field Rules page on the wiki.
 * 
 * @author Alex Hobson
 * @author Jack van Heugten Breurkes
 */
public class FormValidator {

	/**
	 * Maximum length of a firstname, middlename, lastname or nickname
	 */
	public static final int NAME_MAX_LENGTH = 30;

	/**
	 * Minimum length of a password
	 */
	public static final int PASSWORD_MIN_LENGTH = 8;

	/**
	 * Minimum length of a nickname
	 */
	public static final int NICKNAME_MIN_LENGTH = 6;

	/**
	 * Minimum length of a bio
	 */
	public static final int BIO_MIN_LENGTH = 8;

	/**
	 * Maximum length of a bio
	 */
	public static final int BIO_MAX_LENGTH = 60000;

	/**
	 * Maximum length of the user side (before the @) of an email address
	 */
	public static final int EMAIL_USER_MAX_LENGTH = 64;

	/**
	 * Maximum length of the domain side (after the @) of an email address
	 */
	public static final int EMAIL_DOMAIN_MAX_LENGTH = 128;

	/**
	 * Minimum LocalDate for date of birth
	 */
	public static final LocalDate MIN_DATE_OF_BIRTH = LocalDate.of(1900, 01, 01);

	/**
	 * Validates a first, middle or lastname
	 * 
	 * @param input
	 *            Name to validate
	 * @return true if the input is a valid name
	 */
	public static boolean validateName(@NotNull String input) {
		if (input.length() == 0) return false;
		if (input.isBlank()) return false; // only whitespace

		// check for numbers
		for (char c : input.toCharArray()) {
			if (Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Validates a nickname
	 * 
	 * @param input
	 *            Nickname to validate
	 * @return true if the input is a valid nickname
	 */
	public static boolean validateNickname(@NotNull String input) {
		// check if appropriate length
		if (input.length() < NICKNAME_MIN_LENGTH || input.length() > NAME_MAX_LENGTH) {
			return false;
		}

		// check for whitespace
		for (char c : input.toCharArray()) {
			if (Character.isWhitespace(c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Validates a bio
	 * 
	 * @param input
	 *            Bio to validate
	 * @return true if the input is a valid bio
	 */
	public static boolean validateBio(@NotNull String input) {
		// check if appropriate length
		if (input.length() < BIO_MIN_LENGTH || input.length() > BIO_MAX_LENGTH) {
			return false;
		}

		return true;
	}

	/**
	 * Validates a date of birth
	 *
	 * @param input
	 *            Date of birth string to validate
	 * @return true if the input is a valid date of birth
	 */
	public static boolean validateDateOfBirth(@NotNull String input) {
		if (input == null) throw new NullPointerException("date of birth cannot be null");

		LocalDate date = null;
		try {
			date = LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeParseException e) {
			return false;
		}

		if (date.isBefore(MIN_DATE_OF_BIRTH) || date.isAfter(LocalDate.now())) {
			return false;
		}

		return true;
	}

	/**
	 * Validates a gender
	 *
	 * @param input
	 *           gender string to validate
	 * @return true if the input is a valid gender
	 */
	public static boolean validateGender(@NotNull String input) {
		if (input == null) throw new NullPointerException();

		Gender gender = Gender.matchGender(input); // returns null if invalid
		return gender != null;
	}

	/**
	 * Validates a password
	 *
	 * @param input
	 *           password string to validate
	 * @return true if the input is a valid password
	 */
	public static boolean validatePassword(@NotNull String input) {
		// check if appropriate length
		if (input.length() < PASSWORD_MIN_LENGTH) {
			return false;
		}

		return true;
	}

	/**
	 * Validates a list of passport countries
	 *
	 * @param input
	 *           list of strings to validate
	 * @return true if all passport countries in the list are valid
	 */
    public static boolean validatePassportCountries(String[] input) {
		if (input == null) throw new NullPointerException("passport countries cannot be null");
    	for (String country : input) {
			if (country.equals("")) {
				return false;
			}
		}
		return true;
    }


	/**
	 * Validates an email address
	 *
	 * @param input
	 *            Email to validate
	 * @return true if the input is a valid email
	 */
	public static boolean validateEmail(String input) {
		if (input.length() == 0) return false;

		// check if there is exactly 1 '@'
		if (!input.contains("@") || input.indexOf("@") != input.lastIndexOf("@")) {
			return false;
		}

		String[] components = input.split(Pattern.quote("@"));
		if (components.length != 2) return false;

		if (components[0].length() == 0 || components[0].length() > EMAIL_USER_MAX_LENGTH) {
			return false;
		}

		if (components[1].length() == 0 || components[1].length() > EMAIL_DOMAIN_MAX_LENGTH) {
			return false;
		}

		return true;
	}

	/**
	 * Returns a valid date of birth as a LocalDate or null if not possible
	 *
	 * @param input
	 *            date of birth string to convert
	 * @return the LocalDate if a valid date exists or null if not possible
	 */
	public static LocalDate getValidDateOfBirth(String input) {
		try {
			return LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

}
