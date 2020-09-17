package com.springvuegradle.model.data;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Objects;

/**
 * class to represent geographical points using latitude and longitude, easily nestable in other classes
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GeoPosition {

	float lat;
	float lon;
	
	/**
	 * empty constructor for mapping JSON objects into objects within tests. 
	 */
	protected GeoPosition() {
		lat = 0;
		lon = 0;
	}
	
	/**
	 * Construct a GeoPosition with a latitude and longitude
	 * @param latitude latitude of the geographical point
	 * @param longitude longitude of geographical point
	 */
	public GeoPosition(float latitude, float longitude) {
		this.lat = latitude;
		this.lon = longitude;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeoPosition that = (GeoPosition) o;
		return Float.compare(that.lat, lat) == 0 &&
				Float.compare(that.lon, lon) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(lat, lon);
	}
}
