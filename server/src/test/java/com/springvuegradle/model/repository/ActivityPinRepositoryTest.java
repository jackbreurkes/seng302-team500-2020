package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ActivityPinRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityTypeRepository activityTypeRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ActivityPinRepository activityPinRepository;

    private ActivityType testActivityType;


    @BeforeAll
    public void setupTests() {

        testActivityType = new ActivityType("ActivityType4444");
        activityTypeRepository.save(testActivityType);
    }

    /**
     * helper method to generate an activity and activityPin with a given name and lat/lon.
     * @param name the name to give the activity
     * @param lat float of the latitude of the pin
     * @param lon float of the longitude of the pin
     * @return the generated activityPin
     */
    private ActivityPin generateActivityAndPin(String name, float lat, float lon) {
        Profile creator = new Profile(new User(), "firstName", "lastName", LocalDate.now(), Gender.NON_BINARY);
        profileRepository.save(creator);
        Activity activity = new Activity(name, false, "Location", creator, Set.of(testActivityType));
        ActivityPin activityPin = new ActivityPin(activity, lat, lon, 0f,0f,0f,0f);
        activity.setActivityPin(activityPin);
        activityRepository.save(activity);
        activityPinRepository.save(activityPin);
        return activityPin;
    }

    @Test
    void topLeftCorner_expectNone(){
        generateActivityAndPin("Top Left", 0, 1);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertTrue(activityPins.isEmpty());
    }

    @Test
    void bottomLeftCorner_exepctNone(){
        generateActivityAndPin("Bottom Left", 0, 0);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertTrue(activityPins.isEmpty());
    }

    @Test
    void bottomRightCorner_expectNone(){
        generateActivityAndPin("Bottom Right", 1, 0);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertTrue(activityPins.isEmpty());
    }

    @Test
    void topRightCorner_expectOne(){
        generateActivityAndPin("Top Right", 1, 1);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertEquals(1, activityPins.size());
    }

    @Test
    void middle_expectOne(){
        generateActivityAndPin("Middle", 0.5f, 0.5f);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertEquals(1, activityPins.size());
    }

    @Test
    void middle_expectTwo(){
        generateActivityAndPin("Middle", 0.5f, 0.5f);
        generateActivityAndPin("Middle2", 0.66f, 0.1f);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertEquals(2, activityPins.size());
    }

    @Test
    void outside_expectNone(){
        generateActivityAndPin("Outside", 5f, 2f);
        generateActivityAndPin("Outside2", -0.66f, -0.1f);
        generateActivityAndPin("Outside3", -6f, 2f);
        generateActivityAndPin("Outside4", 1, -0.1f);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertTrue(activityPins.isEmpty());
    }

    @Test
    void rightEdge_expectOne(){
        generateActivityAndPin("Right Edge", 1, 0.5f);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertEquals(1, activityPins.size());
    }

    @Test
    void topEdge_expectOne(){
        generateActivityAndPin("Top Edge", 0.5f, 1f);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertEquals(1, activityPins.size());
    }

    @Test
    void bottomEdge_expectNone(){
        generateActivityAndPin("Bottom Edge", 0.5f, 0f);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertTrue(activityPins.isEmpty());
    }

    @Test
    void leftEdge_ExpectNone(){
        generateActivityAndPin("Left Edge", 0, 0.5f);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(1, 1, 0, 0);
        assertTrue(activityPins.isEmpty());
    }

    @Test
    void negativeBoundingBox_expectOne() {
        generateActivityAndPin("Pin1", -1, -1);
        generateActivityAndPin("Pin2", -0.6f, -1.5f);
        generateActivityAndPin("Pin3", -1, -2.2f);
        generateActivityAndPin("OutsidePin1", 0, 0);
        generateActivityAndPin("OutsidePin2", 0.3f, 0.5f);
        generateActivityAndPin("OutsidePin3", -2.4f, -1);
        generateActivityAndPin("OutsidePin4", -1, -0.55f);
        List<ActivityPin> activityPins = activityPinRepository.findPinsInBounds(-0.5f, -0.6f, -2, -2.5f);
        assertEquals(3, activityPins.size());
    }
}
