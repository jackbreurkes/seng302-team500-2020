package com.springvuegradle.model.requests;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileObjectMapperTest {

    ProfileObjectMapper profileObjectMapper;
    Field parseErrors;

    @BeforeEach
    void createProfileObjectMapper() throws NoSuchFieldException {
        parseErrors = ProfileObjectMapper.class.getDeclaredField("parseErrors");
        parseErrors.setAccessible(true);
        profileObjectMapper = new ProfileObjectMapper();
    }

    @Test
    void testSetPrimaryEmail() throws InvalidRequestFieldException, IllegalAccessException {
        profileObjectMapper.setPrimaryEmail("a@a");
        List<String> parseErrorMessages = (List<String>)parseErrors.get(profileObjectMapper);
        assertEquals(0, parseErrorMessages.size());
    }

    @Test
    void testInvalidPrimaryEmail() throws InvalidRequestFieldException, IllegalAccessException {
        profileObjectMapper.setPrimaryEmail("a");
        List<String> parseErrorMessages = (List<String>)parseErrors.get(profileObjectMapper);
        assertEquals(1, parseErrorMessages.size());
    }
}