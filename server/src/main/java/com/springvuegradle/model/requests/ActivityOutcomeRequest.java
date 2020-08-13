package com.springvuegradle.model.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.ActivityOutcome;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * class used to bind ActivityOutcome JSON data to Java class attributes.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActivityOutcomeRequest {

    @NotNull(message = "outcome missing description field")
    @Size(min = 3, max = 30, message = "outcome descriptions must be between 3 and 30 characters")
    private String description;

    @NotNull(message = "outcome missing units field")
    @Size(min = 1, max = 10, message = "outcome units must be between 1 and 10 characters")
    private String units;

    /**
     * default constructor
     */
    public ActivityOutcomeRequest() {}

    /**
     * @return the activity outcome's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to give to this outcome
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the units this outcome should be measured in
     */
    public String getUnits() {
        return units;
    }

    /**
     * @param units the units this outcome should be measured in
     */
    public void setUnits(String units) {
        this.units = units;
    }
}
