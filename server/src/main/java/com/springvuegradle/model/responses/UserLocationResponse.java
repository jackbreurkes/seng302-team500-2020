package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Response class for {lat: ..., lon: ...} location formats, easily nestable in other classes
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserLocationResponse {

	float lat;
	float lon;
	
	/**
	 * Construct a UserLocationResponse with a latitude and longitude
	 * @param latitude latitude of response
	 * @param longitude longitude of response
	 */
	public UserLocationResponse(float latitude, float longitude) {
		this.lat = latitude;
		this.lon = longitude;
	}

	/**
	 * Get the latitude of the response
	 * @return latitude
	 */
	public float getLat() {
		return lat;
	}

	/**
	 * Get the longitude of the response
	 * @return longitude
	 */
	public float getLon() {
		return lon;
	}
}
