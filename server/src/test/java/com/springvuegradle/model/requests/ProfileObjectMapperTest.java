package com.springvuegradle.model.requests;

import com.springvuegradle.endpoints.UserProfileController;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.CountryRepository;
import com.springvuegradle.model.repository.LocationRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@EnableAutoConfiguration
@ContextConfiguration(classes = {ProfileObjectMapper.class})
@WebMvcTest
class ProfileObjectMapperTest {

    ProfileObjectMapper profileObjectMapper;
    Field parseErrors;

    @MockBean
    private ProfileRepository profileRepository;
    @MockBean
    private CountryRepository countryRepository;
    @MockBean
    private LocationRepository locationRepository;

    @BeforeEach
    void createProfileObjectMapper() throws NoSuchFieldException {
        // allows us to access the private field parseErrors of ProfileObjectMapper objects for testing
        parseErrors = ProfileObjectMapper.class.getDeclaredField("parseErrors");
        parseErrors.setAccessible(true);
        profileObjectMapper = new ProfileObjectMapper();
    }

    @Test
    void testSetPrimaryEmail() throws InvalidRequestFieldException, IllegalAccessException {
        profileObjectMapper.setPrimaryEmail("a@a");

        // gets the parseErrors field value for a specific ProfileObjectMapper object
        List<String> parseErrorMessages = (List<String>)parseErrors.get(profileObjectMapper);
        assertEquals(0, parseErrorMessages.size());
    }

    @Test
    void testInvalidPrimaryEmail() throws InvalidRequestFieldException, IllegalAccessException {
        profileObjectMapper.setPrimaryEmail("a");

        // gets the parseErrors field value for a specific ProfileObjectMapper object
        List<String> parseErrorMessages = (List<String>)parseErrors.get(profileObjectMapper);
        assertEquals(1, parseErrorMessages.size());
    }

    @Test
    void testNoErrorOnEmptyNonMandatories() throws InvalidRequestFieldException, IllegalAccessException {
        profileObjectMapper.setBio("");
        profileObjectMapper.setNickname("");
        profileObjectMapper.setMiddlename("");

        // gets the parseErrors field value for a specific ProfileObjectMapper object
        List<String> parseErrorMessages = (List<String>)parseErrors.get(profileObjectMapper);
        
        assertEquals(0, parseErrorMessages.size());
        assertNull(profileObjectMapper.getBio());
        assertNull(profileObjectMapper.getNickname());
        assertNull(profileObjectMapper.getMiddlename());
    }

    @Test
    void testDeleteNonMandatoryAttributes() throws RecordNotFoundException, InvalidRequestFieldException {
        User user = new User();
        Profile profile = new Profile(user, "Bob", "Andrews", LocalDate.of(1990, 01, 01), Gender.NON_BINARY);
        profile.setMiddleName("MiddleNameExists");
        profile.setNickName("NickNameExists");
        profile.setBio("BioExists");

        profileObjectMapper.setMiddlename("");
        profileObjectMapper.setNickname("");
        profileObjectMapper.setBio("");
        profileObjectMapper.updateExistingProfile(profile, profileRepository, countryRepository, locationRepository);
        assertNull(profile.getMiddleName());
        assertNull(profile.getNickName());
        assertNull(profile.getBio());
    }
}