package com.springvuegradle.model.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * JPA POJO representing an Activity Pin.
 */
@Entity
@Table(name = "activity_pin")
public class ActivityPin {

    @Id
    @GeneratedValue
    private long pin_id;

    @NotNull
    private long latitude;

    @NotNull
    private long longitude;

    /**
     * Activity which this pin is for
     */
    @OneToOne
    @JoinColumn(name = "activity", nullable = false)
    private Activity activity;

    /**
     * no arg constructor required by JPA
     */
    public ActivityPin() {}

    /**
     * Create new activity pin with the required information (activity, latitude and longitude)
     * @param activity activity which this pin is for
     * @param latitude latitudinal position of the location of the pin
     * @param longitude longitudinal position of the location of the pin
     */
    public ActivityPin(Activity activity, long latitude, long longitude)
    {
        this.activity = activity;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getPin_id() {
        return pin_id;
    }

    public void setPin_id(long pin_id) {
        this.pin_id = pin_id;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}