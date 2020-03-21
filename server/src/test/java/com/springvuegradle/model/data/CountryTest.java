package com.springvuegradle.model.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CountryTest {

    private Country country;

    @BeforeEach
    void setup(){
        country = new Country(100, "TestCountry");
    }

    @Test
    void getNameTest(){
        assertEquals("TestCountry", country.getName());
    }
}
