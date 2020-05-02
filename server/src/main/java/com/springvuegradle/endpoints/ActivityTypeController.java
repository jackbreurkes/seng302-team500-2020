package com.springvuegradle.endpoints;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.repository.ActivityTypeRepository;

/**
 * REST endpoint for getting a list of activity types
 * 
 * @author Alex Hobson
 *
 */
@RestController
public class ActivityTypeController {
	
	/**
	 * Repository of activity types in the database
	 */
	@Autowired
	private ActivityTypeRepository activityRepo;
	
	/**
	 * REST Endpoint for GET /activities
	 * Returns a response of a list of all the activity types as strings
	 * 
	 * @return Response entity containing list of all activity types in database
	 */
	@GetMapping("/activity-types")
	@CrossOrigin
	public String[] getActivities() {
		List<ActivityType> allActivityTypes = activityRepo.findAll();
		
		String[] activityTypeStrings = allActivityTypes.stream().map(ActivityType::getActivityTypeName).toArray(String[]::new);
		
		return activityTypeStrings;
	}

}
