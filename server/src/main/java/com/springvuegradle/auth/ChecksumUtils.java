package com.springvuegradle.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.springvuegradle.model.data.User;

/**
 * 
 * @author Alex Hobson
 *
 */
public class ChecksumUtils {

	/**
	 * Method to validate a user's password by hashing the plain-text password and comparing it
	 * with what is in the database
	 * @param user User account to check the password against
	 * @param givenPassword Password the user sent to check
	 * @return true if the password is valid, false otherwise
	 * @throws NoSuchAlgorithmException If SHA-256 doesn't exist for some reason (in which case update java)
	 */
	public static boolean checkPassword(User user, String givenPassword) throws NoSuchAlgorithmException {
		String hashed = hashPassword(user.getUserId(), givenPassword);
		return hashed.equals(user.getPassword());
	}
	
	/**
	 * Hash a password for a given user id, useful when registering and logging in
	 * @param userId User ID the password is being hashed for
	 * @param plaintextPassword The password in plain text
	 * @return hex string of hashed password
	 * @throws NoSuchAlgorithmException If SHA-256 doesn't exist for some reason (in which case update java)
	 */
	public static String hashPassword(long userId, String plaintextPassword) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String toChecksum = String.valueOf(userId) + plaintextPassword; //userid as salt for added security
		byte[] hash = digest.digest(toChecksum.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(hash);
	}
	
	/**
	 * 
	 * @param userId User ID to generate session token for
	 * @return hex string of session token
	 * @throws NoSuchAlgorithmException 
	 */
	public static String generateToken(long userId) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		String toChecksum = String.valueOf(userId) + "-" + String.valueOf(System.currentTimeMillis());
		byte[] hash = digest.digest(toChecksum.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(hash);
	}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	//copied from https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
	/**
	 * Turns a byte array into a hex string
	 * @param bytes Bytes to be turned into hex
	 * @return Hex string of given bytes
	 */
	private static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
