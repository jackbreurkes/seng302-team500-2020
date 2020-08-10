package com.springvuegradle.model;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * this application class does not have @EnableWebMvc.
 * This is required for properly testing repositories using @DataJpaTest.
 */
@SpringBootApplication
public class TestApplication {
}
