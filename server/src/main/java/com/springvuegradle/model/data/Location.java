package com.springvuegradle.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * JPA POJO to represent a location
 */
@Entity
@Table(name="location", uniqueConstraints={
        @UniqueConstraint(columnNames = {"city", "country"})
})
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Location implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    private long locationId;

    @NotNull
    private String city;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String state;

    @NotNull
    private String country;

    /**
     * Constructor required by Spring
     */
    protected Location() {}

    /**
     * constructor with required args
     * @param city the city
     * @param country the country the city is in
     */
    public Location(String city, String country) {
        this.city = city;
        this.country = country;
        this.state = null;
    }

    /**
     * constructor with optional state parameter
     * @param city the city
     * @param state optional state
     * @param country the country the city is in
     */
    public Location(String city, String state, String country) {
        this.city = city;
        this.state = state;
        this.country = country;
    }

    /**
     * @return unique ID for the location, required for retrieving from repository
     */
    public long getLocationId() {
        return locationId;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the optional state value, or null if not defined
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the optional state value
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the country the city is in
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country the city is in
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
