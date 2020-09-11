package com.springvuegradle.model.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * JPA POJO representing an Activity Pin.
 */
@Entity
@Table(name = "activity_pin")
public class ActivityPin {

    /**
     * Id for this pin
     */
    @Id
    @GeneratedValue
    private long pin_id;

    /**
     * Latitudinal position of this activity pin location
     */
    @NotNull
    private float latitude;

    /**
     * Longitudinal position of this activity pin location
     */
    @NotNull
    private float longitude;

    /**
     * Latitudinal position of the southwest corner of the bounding box for the pin location
     */
    @NotNull
    private float southwestBoundingLatitude;

    /**
     * Latitudinal position of the northeast corner of the bounding box for the pin location
     */
    @NotNull
    private float northeastBoundingLatitude;

    /**
     * Longitudinal position of the southwest corner of the bounding box for the pin location
     */
    @NotNull
    private float southwestBoundingLongitude;

    /**
     * Longitudinal position of the northeast corner of the bounding box for the pin location
     */
    @NotNull
    private float northeastBoundingLongitude;

    /**
     * Activity which this pin is for
     */
    @OneToOne
    @JoinColumn(name = "activity", nullable = false)
    private Activity activity;

    /**
     * no arg constructor required by JPA
     */
    public ActivityPin() {
        this.activity = new Activity();
        this.latitude = 0;
        this.longitude = 0;
        this.southwestBoundingLatitude = 0;
        this.southwestBoundingLongitude = 0;
        this.northeastBoundingLatitude = 0;
        this.northeastBoundingLongitude = 0;
    }

    /**
     * Create new activity pin with the required information (activity, latitude and longitude)
     * @param activity activity which this pin is for
     * @param latitude latitudinal position of the location of the pin
     * @param longitude longitudinal position of the location of the pin
     * @param southwestBoundingLatitude latitudinal position of the southwest corner of the bounding box of the pin
     * @param northeastBoundingLatitude latitudinal position of the northeast corner of the bounding box of the pin
     * @param southwestBoundingLongitude longitudinal position of the southwest corner of the bounding box of the pin
     * @param northeastBoundingLongitude longitudinal position of the northeast corner of the bounding box of the pin
     */
    public ActivityPin(Activity activity, float latitude, float longitude,
                       float southwestBoundingLatitude, float northeastBoundingLatitude,
                       float southwestBoundingLongitude, float northeastBoundingLongitude)
    {
        this.activity = activity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.southwestBoundingLatitude = southwestBoundingLatitude;
        this.southwestBoundingLongitude = southwestBoundingLongitude;
        this.northeastBoundingLatitude = northeastBoundingLatitude;
        this.northeastBoundingLongitude = northeastBoundingLongitude;
    }

    public long getPin_id() {
        return pin_id;
    }

    public void setPin_id(long pin_id) {
        this.pin_id = pin_id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}