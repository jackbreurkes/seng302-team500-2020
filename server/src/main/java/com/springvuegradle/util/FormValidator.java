package com.springvuegradle.util;

import java.util.regex.Pattern;

/**
 * Used to consistently validate form fields between endpoints
 * 
 * @author Alex Hobson
 *
 */
public class FormValidator {

	/**
	 * Maximum length of a firstname, middlename, lastname or nickname
	 */
	private static final int NAME_MAX_LENGTH = 30;

	/**
	 * Minimum length of a nickname
	 */
	private static final int NICKNAME_MIN_LENGTH = 6;

	/**
	 * Minimum length of a bio
	 */
	private static final int BIO_MIN_LENGTH = 8;

	/**
	 * Maximum length of a bio
	 */
	private static final int BIO_MAX_LENGTH = 60000;

	/**
	 * Maximum length of the user side (before the @) of an email address
	 */
	private static final int EMAIL_USER_MAX_LENGTH = 64;

	/**
	 * Maximum length of the domain side (after the @) of an email address
	 */
	private static final int EMAIL_DOMAIN_MAX_LENGTH = 128;

	/**
	 * Validates a first, middle or lastname
	 * 
	 * @param input
	 *            Name to validate
	 * @param isMandatory
	 *            True if the field is mandatory
	 * @return true if the input is a valid name
	 */
	public static boolean validateName(String input, boolean isMandatory) {
		// check it's mandatory and empty
		if (isMandatory && (input == null || input.length() == 0)) {
			return false;
		} else if (!isMandatory) {
			return true;
		}

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
	 * @param isMandatory
	 *            True if the field is mandatory
	 * @return true if the input is a valid nickname
	 */
	public static boolean validateNickname(String input, boolean isMandatory) {
		// check it's mandatory and empty
		if (isMandatory && (input == null || input.length() == 0)) {
			return false;
		} else if (!isMandatory) {
			return true;
		}

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
	 * @param isMandatory
	 *            True if the field is mandatory
	 * @return true if the input is a valid bio
	 */
	public static boolean validateBio(String input, boolean isMandatory) {
		// check it's mandatory and empty
		if (isMandatory && (input == null || input.length() == 0)) {
			return false;
		} else if (!isMandatory) {
			return true;
		}

		// check if appropriate length
		if (input.length() < BIO_MIN_LENGTH || input.length() > BIO_MAX_LENGTH) {
			return false;
		}

		// check for whitespace
		if (input.contains(" ")) {
			return false;
		}

		return true;
	}

	/**
	 * Validates an email address
	 * 
	 * @param input
	 *            Email to validate
	 * @param isMandatory
	 *            True if the field is mandatory
	 * @return true if the input is a valid email
	 */
	public static boolean validateEmail(String input, boolean isMandatory) {
		// check it's mandatory and empty
		if (isMandatory && (input == null || input.length() == 0)) {
			return false;
		} else if (!isMandatory) {
			return true;
		}

		// check if there is exactly 1 '@'
		if (input.indexOf("@") != -1 && input.indexOf("@") == input.lastIndexOf("@")) {
			String[] components = input.split(Pattern.quote("@"));

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
