package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.ActivityOutcome;

/**
 * class used to return an ActivityOutcome entity as JSON data
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActivityOutcomeResponse {

    private final Long outcomeId;
    private final String description;
    private final String units;

    /**
     * default constructor
     * @param outcome the outcome to construct a response for
     */
    public ActivityOutcomeResponse(ActivityOutcome outcome) {
        outcomeId = outcome.getOutcomeId();
        description = outcome.getDescription();
        units = outcome.getUnits();
    }

    /**
     * @return the ID of this outcome
     */
    public Long getOutcomeId() {
        return outcomeId;
    }

    /**
     * @return the description associated with this outcome
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the units this outcome should be measured in
     */
    public String getUnits() {
        return units;
    }
}
