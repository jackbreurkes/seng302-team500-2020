package com.springvuegradle.util;

import com.springvuegradle.model.data.Gender;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Used to consistently validate form fields between endpoints
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
	 * date format to use when validating and parsing date strings
	 */
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Validates a first, middle or lastname
	 * 
	 * @param input
	 *            Name to validate
	 * @return true if the input is a valid name
	 */
	public static boolean validateName(@NotNull String input) {
		if (input.length() < 1) return false;

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
		if (input == null) return false;

		// check if appropriate length
		if (input.length() < NICKNAME_MIN_LENGTH || input.length() > NAME_MAX_LENGTH) {
			return false;
		}

		// check for whitespace
		if (input.contains(" ")) {
			return false;
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
		dateFormat.setLenient(false);
		if (input == null) throw new NullPointerException("date of birth cannot be null");
		try {
			Date date = dateFormat.parse(input);
			if (date.before(dateFormat.parse("1900-01-01"))) {
				return false;
			}
			if (date.after(new Date())) { // a new Date object defaults to the current date
				return false;
			}
			return true;
		} catch (ParseException e) {
			return false;
		}
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
		Gender gender = Gender.matchGender(input);
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
	 * Returns a valid date of birth or null if not possible
	 *
	 * @param input
	 *            Date of birth string to convert
	 * @return the Date if a valid date exists or null if not possible
	 */
	public static Date getValidDateOfBirth(String input) {

		try {
			return dateFormat.parse(input);
		} catch (ParseException e) {
			return null;
		}
	}



	/**
	 * Validates an email address
	 *
	 * @param input
	 *            Email to validate
	 * @return true if the input is a valid email
	 */
	public static boolean validateEmail(String input) {
		// check if there is exactly 1 '@'
		if (input.length() == 0) return false;
		if (input.indexOf("@") != -1 && input.indexOf("@") == input.lastIndexOf("@")) {
			String[] components = input.split(Pattern.quote("@"));

			if (components.length != 2) return false;
			if (components[0].length() == 0 || components[0].length() > EMAIL_USER_MAX_LENGTH) {
				return false;
			}

			if (components[1].length() == 0 || components[1].length() > EMAIL_DOMAIN_MAX_LENGTH) {
				return false;
			}

			return true;
		} else {
			return false;
		}
	}
}
