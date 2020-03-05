package com.springvuegradle.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Class for creating JWT utils
 * @author Michael Freeman
 */

public class JwtUtil {

    //Placeholder
    private static String SECRET_KEY = "seceryt";

    /**
     * Generates the Jwt Token with the userdetails passed in
     * @param userDetails of the user attempting to login
     * @return new JWT token in string form
     */

    public static String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        //the username being passed is the primary email
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * JWT token creation, live for 10 hours but subject to change with design decisions
     * @param claims Extra claims and permissions for the user
     * @param primaryEmail
     * @return String object of the JWT authentication token
     */

    private static String createToken(Map<String, Object> claims, String primaryEmail){
        return Jwts.builder().setClaims(claims).setSubject(primaryEmail).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public static Boolean validateToken(String token, UserDetails userDetails){
        final String userEmail = extractUserEmail(token);
        return (userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Takes the JWT token and extracts the identifiable information from it, returns the requested info
     * @param token String form of the JWT authentication token
     * @param claimsResolver Specific claim the be returned
     * @param <T> Function Arguments
     * @return The requested claim/claims
     */
    public static <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Proccesses the JSON token format to be read by extractClaims
     * @param token String form of the JWT token
     * @return All claims from the token
     */

    private static Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * Checking if a token is expired
     * @param token Token in string format
     * @return true/false if token is expired
     */

    private static Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    /**
     * Returns the expiration date of the token
     * @param token String form of JWT token
     * @return The time and date the token expires
     */

    public static Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    /**
     * Returns the user email
     * @param token Token string format
     * @return user email string
     */

    public static String extractUserEmail(String token){
        return extractClaims(token, Claims::getSubject);
    }

}
