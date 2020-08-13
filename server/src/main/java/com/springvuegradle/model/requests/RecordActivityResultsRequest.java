package com.springvuegradle.model.requests;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * jackson JSON binding class for requests to POST
 * /activities/{activityId}/results
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RecordActivityResultsRequest {

	@NotNull(message = "missing outcomes field")
    @Size(min = 1, message = "must include at least one outcome")
	@Valid
	private List<RecordOneActivityResultsRequest> outcomes = new ArrayList<RecordOneActivityResultsRequest>();

	/**
	 * JPA default constructor
	 */
	public RecordActivityResultsRequest() {
	}

	public List<RecordOneActivityResultsRequest> getOutcomes() {
		return outcomes;
	}

	public void setOutcomes(List<RecordOneActivityResultsRequest> outcomes) {
		this.outcomes = outcomes;
	}

}
