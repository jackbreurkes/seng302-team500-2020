package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.ActivityParticipantResult;

/**
 * wrapper class for JSON responses for activity participant results
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ParticipantResultResponse {

    private long outcomeId;

    private String result;

    private String completedDate;

    /**
     * constructor takes in an ActivityParticipantResult and sets properties accordingly.
     * @param result the ActivityParticipantResult this class should contain the information of
     */
    public ParticipantResultResponse(ActivityParticipantResult result) {
        this.outcomeId = result.getOutcome().getOutcomeId();
        this.result = result.getValue();
        this.completedDate = result.getCompletedDate();
    }

    public long getOutcomeId() {
        return outcomeId;
    }

    public void setOutcomeId(long outcomeId) {
        this.outcomeId = outcomeId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }
}
