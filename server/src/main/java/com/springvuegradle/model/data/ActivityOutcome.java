package com.springvuegradle.model.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class ActivityOutcome {

    @GeneratedValue
    @Id
    private long outcomeId;

    @ManyToOne
    @JoinColumn(name = "activity", nullable = false)
    private Activity activity;

    @NotNull
    private String description;

    @NotNull
    private String units;

    /**
     * default no-arg constructor required by hibernate
     */
    protected ActivityOutcome() {
    }

    /**
     * constructor that does not link to an Activity.
     * Useful when adding an outcome to an activity using Activity.addOutcome(_)
     * @param description the description of this outcome
     * @param units the units in which participants' results will be measured
     */
    public ActivityOutcome(@NotNull String description, @NotNull String units) {
        this.description = description;
        this.units = units;
    }

    /**
     * @return the unique id of this outcome
     */
    public long getOutcomeId() {
        return outcomeId;
    }

    /**
     * @return the activity associated with this outcome
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * @param activity the activity this outcome should be associated with
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * @return the description of the outcome
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to give to this outcome
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the units in which participants should measure their results for this outcome
     */
    public String getUnits() {
        return units;
    }

    /**
     * @param units the units in which participants should measure their results for this outcome
     */
    public void setUnits(String units) {
        this.units = units;
    }
}
