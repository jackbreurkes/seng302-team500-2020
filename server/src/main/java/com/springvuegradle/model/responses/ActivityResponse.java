package com.springvuegradle.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    @JsonProperty("activity_type")
    private List<String> activityTypes;

    /**
     * default constructor
     * @param activity the activity whose data should be used to populate the JSON response data
     */
    public ActivityResponse(Activity activity) {
        this.activityId = activity.getId();
        this.activityName = activity.getActivityName();
        this.continuous = !activity.isDuration();
        this.description = activity.getDescription();
        this.location = activity.getLocation();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        if (activity.isDuration()) {
            this.startTime = activity.getStartDate().toString() + "T" + activity.getStartTime().format(timeFormatter) + "+1300";
            this.endTime = activity.getEndDate().toString() + "T" + activity.getEndTime().format(timeFormatter) + "+1300";
        }
        this.creatorId = activity.getCreator().getUser().getUserId();
        this.activityTypes = activity.getActivityTypes()
                .stream()
                .map(ActivityType::getActivityTypeName)
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
}
