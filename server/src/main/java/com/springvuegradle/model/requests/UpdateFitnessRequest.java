package com.springvuegradle.model.requests;

/**
 * Update fitness request class
 * May, in future, be integrated into the update profile class but
 * for now it is simpler to do it like this and more atomic
 */
public class UpdateFitnessRequest {

    private long uuid;
    private int fitnessLevel;

    public UpdateFitnessRequest(long profile_id, int fitness_level){
        this.uuid = profile_id;
        this.fitnessLevel = fitness_level;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public int getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(int fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }
}
