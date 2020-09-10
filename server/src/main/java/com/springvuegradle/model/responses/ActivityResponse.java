package com.springvuegradle.model.responses;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;

/**
 * class used to return an Activity entity as JSON data
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActivityResponse {

    private Long activityId;
    private String activityName;
    private boolean continuous;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String startTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String endTime;
    private String description;
    private String location;
    private Long creatorId;
    private List<ActivityOutcomeResponse> outcomes;
    
    private Long numFollowers;
    private Long numParticipants;

	@JsonProperty("activity_type")
    private List<String> activityTypes;

    /**
     * default constructor
     * @param activity the activity whose data should be used to populate the JSON response data
     */
    public ActivityResponse(Activity activity) {
    	this(activity, 1L, 1L);
    }
    
    /**
     * Constructs an ActivityResponse suitable for sending to the client
     * @param activity Activity to respond with
     * @param numFollowers Number of followers the activity has
     * @param numParticipants Number of participants the activity has
     */
    public ActivityResponse(Activity activity, Long numFollowers, Long numParticipants) {
        this.activityId = activity.getId();
        this.activityName = activity.getActivityName();
        this.continuous = !activity.isDuration();
        this.description = activity.getDescription();
        this.location = activity.getLocation();
        if (activity.isDuration()) {
            this.startTime = activity.getStartTime();
            this.endTime = activity.getEndTime();
        }
        this.creatorId = activity.getCreator().getUser().getUserId();
        this.activityTypes = activity.getActivityTypes()
                .stream()
                .map(ActivityType::getActivityTypeName)
                .collect(Collectors.toList());
        
        this.numFollowers = numFollowers;
        this.numParticipants = numParticipants;
        this.outcomes = activity.getOutcomes()
                .stream()
                .map(ActivityOutcomeResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * @return the value to be returned as activity_id in the JSON response
     */
    public Long getActivityId() {
        return activityId;
    }

    /**
     * @return the value to be returned as activity_name in the JSON response
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * @return the value to be returned as continuous in the JSON response
     */
    public boolean isContinuous() {
        return continuous;
    }

    /**
     * @return the value to be returned as start_time in the JSON response
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @return the value to be returned as end_time in the JSON response
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @return the value to be returned as description in the JSON response
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the value to be returned as location in the JSON response
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the value to be returned as creator_id in the JSON response
     */
    public Long getCreatorId() {
        return creatorId;
    }

    /**
     * @return the value to be returned as activity_type in the JSON response
     */
    public List<String> getActivityTypes() {
        return activityTypes;
    }
    
    /**
     * @return the number of users following this activity
     */
    public Long getNumFollowers() {
		return numFollowers;
	}

    /**
     * @return the number of users marked as participating in this activity
     */
	public Long getNumParticipants() {
		return numParticipants;
	}

    public List<ActivityOutcomeResponse> getOutcomes() {
        return outcomes;
    }
}
