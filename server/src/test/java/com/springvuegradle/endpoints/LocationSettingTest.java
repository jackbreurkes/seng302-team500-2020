package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.ProfileObjectMapper;
import com.springvuegradle.model.responses.ProfileResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LocationSettingTest {

    @InjectMocks
    private UserProfileController userProfileController;

    @InjectMocks
    private ProfileObjectMapper pom;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ActivityTypeRepository activityTypeRepository;

    @Mock
    private LocationRepository locationRepository;

    private User user;

    private MockHttpServletRequest request;

    @BeforeAll
    void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void beforeEach(){
        this.user = new User(1L);
        Profile profile = new Profile(this.user, "David", "Clarke", LocalDate.ofEpochDay(1L), Gender.MALE);
        profile.setCountries(new ArrayList<Country>());
        profile.setActivityTypes(new ArrayList<ActivityType>());
        Mockito.when(emailRepository.existsById(Mockito.any())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(this.user);
        Mockito.when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        Mockito.when(profileRepository.save(Mockito.any())).thenReturn(profile);
        Mockito.when(countryRepository.save(Mockito.any())).thenReturn(null);
        Mockito.when(activityTypeRepository.save(Mockito.any())).thenReturn(null);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(this.user));

        this.request = new MockHttpServletRequest();
        this.request.setAttribute("authenticatedid", 1L);
    }

    @Test
    void createProfileWithLocation() throws InvalidRequestFieldException, NoSuchAlgorithmException, RecordNotFoundException {
    	Location location = mockLocation("City", "Country");
        
        Mockito.when(locationRepository.save(Mockito.any())).thenReturn(true);
        Mockito.when(locationRepository.findLocationByCityAndCountry(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.of(location));
        this.pom = createProfileObjectMappers(location);
        ResponseEntity response = (ResponseEntity) userProfileController.createprofile(pom);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    void noDuplicateCountries() throws InvalidRequestFieldException, NoSuchAlgorithmException, RecordNotFoundException {
    	Location location = mockLocation("City1", "Country1");
        
        Mockito.when(locationRepository.findLocationByCityAndCountry(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.of(location));
        this.pom = createProfileObjectMappers(location);
        userProfileController.createprofile(this.pom);
        Mockito.verify(locationRepository, Mockito.times(0)).save(Mockito.any());
        //assertEquals(Mockito.mockingDetails(locationRepository).getInvocations(), 1);
    }

    @Test
    void editProfileWithLocation() throws ParseException, UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException, UserNotAuthorizedException {
    	Location location = mockLocation("City3", "Country5");

        Mockito.when(locationRepository.findLocationByCityAndCountry("City3", "Country5")).thenReturn(Optional.of(location));
        this.pom = createProfileObjectMappers(location);
        ProfileResponse response = userProfileController.updateProfile(pom, 1L, request);
        assertEquals(response.getLocation(), location);
    }

    @Test
    void testPutRemoveLocation() throws UserNotAuthenticatedException, InvalidRequestFieldException, ParseException, RecordNotFoundException, UserNotAuthorizedException {
        //Mockito.when(locationRepository.findLocationByCityAndCountry("City3", "Country5")).thenReturn(Optional.of(location));
        this.pom = createProfileObjectMappers(null);
        ProfileResponse response = userProfileController.updateProfile(pom, 1L, request);
        assertNull(response.getLocation());
    }


    private ProfileObjectMapper createProfileObjectMappers(Location location){
        ProfileObjectMapper returnProfile = new ProfileObjectMapper();
        returnProfile.setFirstname("David");
        returnProfile.setLastname("Test");
        returnProfile.setDateOfBirth("1999-11-16");
        returnProfile.setGender("male");
        returnProfile.setPassword("password123");
        returnProfile.setPrimaryEmail("eamil@email.com");

        if(location != null){
            returnProfile.setLocation(location);
        }

        return returnProfile;
    }

    private Location mockLocation(String city, String country) {
    	Location location = Mockito.mock(Location.class);
        Mockito.when(location.getCity()).thenReturn(city);
        Mockito.when(location.getCountry()).thenReturn(country);
        Mockito.when(location.getState()).thenReturn("");
        Mockito.when(location.lookupAndValidate()).thenReturn(location);
        return location;
    }
}

