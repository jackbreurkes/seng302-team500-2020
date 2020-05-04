package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.CreateActivityRequest;
import com.springvuegradle.model.responses.ActivityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for all endpoints relating to activities
 * @author Michael Freeman
 * @author Jack van Heugten Breurkes
 * @author James Auman
 */
@RestController
public class ActivitiesController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityTypeRepository activityTypeRepository;

    @PostMapping("/profiles/{profileId}/activities")
    @CrossOrigin
    public ActivityResponse createActivity(@PathVariable("profileId") long profileId,
            @RequestBody CreateActivityRequest createActivityRequest,
                                   HttpServletRequest httpRequest) throws InvalidRequestFieldException, RecordNotFoundException, UserNotAuthenticatedException {
        // authentication
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
        if (authId == null) {
            throw new UserNotAuthenticatedException("you are not logged in");
        } else if (!authId.equals((long)-1) && !authId.equals(profileId)) {
            throw new AccessDeniedException("Insufficient permission");
        }

        if (createActivityRequest.getActivityName() == null) {
            throw new InvalidRequestFieldException("missing activity_name field");
        }
        if (createActivityRequest.getActivityName().length() < 4 || createActivityRequest.getActivityName().length() > 30) {
            throw new InvalidRequestFieldException("activity_name must be between 4 and 30 characters inclusive");
        }
        if (createActivityRequest.getActivityTypes() == null) {
            throw new InvalidRequestFieldException("missing activity_type field");
        }
        if (createActivityRequest.getActivityTypes().size() == 0) {
            throw new InvalidRequestFieldException("must have at least one activity_type");
        }
        if (createActivityRequest.isContinuous() == null) {
            throw new InvalidRequestFieldException("missing continuous field");
        }
        if (createActivityRequest.getDescription() != null && createActivityRequest.getDescription().length() < 8) {
            throw new InvalidRequestFieldException("activity description must be at least 8 characters");
        }

        List<ActivityType> activityTypeList = new ArrayList<>();
        for (String activityType : createActivityRequest.getActivityTypes()) {
            Optional<ActivityType> optionalActivityType = activityTypeRepository.getActivityTypeByActivityTypeName(activityType);
            if (optionalActivityType.isEmpty()) {
                throw new RecordNotFoundException("no activity type called " + activityType + " found");
            }
            activityTypeList.add(optionalActivityType.get());
        }

        LocalDate startDate = null;
        LocalDate endDate = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        if (!createActivityRequest.isContinuous()) {
            if (createActivityRequest.getStartTime() == null || createActivityRequest.getEndTime() == null) {
                throw new InvalidRequestFieldException("duration activities must have start_time and end_time values");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

            LocalDateTime startDateTime, endDateTime;
            try {
                startDateTime = LocalDateTime.parse(createActivityRequest.getStartTime(), formatter);
                endDateTime = LocalDateTime.parse(createActivityRequest.getEndTime(), formatter);
            } catch (DateTimeParseException e) {
                throw new InvalidRequestFieldException("invalid time string " + e.getParsedString());
            }
            startDate = startDateTime.toLocalDate();
            endDate = endDateTime.toLocalDate();
            startTime = startDateTime.toLocalTime();
            endTime = endDateTime.toLocalTime();
        }

        Optional<Profile> optionalCreator = profileRepository.findById(profileId);
        if (optionalCreator.isEmpty()) {
            throw new RecordNotFoundException("profile with id " + profileId + " not found");
        }

        Activity activity = new Activity(
                createActivityRequest.getActivityName(),
                !createActivityRequest.isContinuous(),
                createActivityRequest.getLocation(),
                optionalCreator.get(),
                activityTypeList);
        activity.setDescription(createActivityRequest.getDescription());
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setStartTime(startTime);
        activity.setEndTime(endTime);
        activity.setLocation(createActivityRequest.getLocation());

        return new ActivityResponse(activityRepository.save(activity));

    }

}