package com.springvuegradle.model.requests;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * jackson JSON binding class for individual activity results
 * to /activities/{activityId}/results 
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RecordOneActivityResultsRequest {

	@NotNull(message = "missing outcome_id field")
	private long outcomeId;
	
	@NotNull(message = "missing result field")
    @Size(min = 1, max = 30, message = "result must be between 1 and 30 characters inclusive")
    private String result;
	
	@NotNull(message = "missing completed_date field")
	private OffsetDateTime completedDate;
	
	/**
	 * JPA default constructor
	 */
	public RecordOneActivityResultsRequest() {
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

	public OffsetDateTime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(OffsetDateTime completedDate) {
		this.completedDate = completedDate;
	}
}
