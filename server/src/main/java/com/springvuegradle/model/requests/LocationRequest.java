package com.springvuegradle.model.requests;

/**
 * wraps a location object so that it can be passed as a field in the request
 */
public class LocationRequest {

    String city;
    String state;
    String country;

    /**
     * no args constructor
     */
    public LocationRequest() {}

    /**
     * @return
     */
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
