package com.elan_droid.elandroid.database.relation;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;

import com.elan_droid.elandroid.database.entity.User;
import com.elan_droid.elandroid.database.entity.Vehicle;

/**
 * Created by Peter Smith
 */

public class Profile {

    private static final String MAKE_MODEL_FORMAT = "%s, %s";

    @Embedded
    private User user;

    @Embedded
    private Vehicle vehicle;

    @Ignore
    private boolean active = false;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public long getProfileId() {
        return user.getId();
    }

    public long getDefaultMessageId() {
        return vehicle.getDefaultMessageId();
    }

    /**
     * The name of the User
     * @return      the name of the user vehicle
     */
    public String getName() {
        return user.getName();
    }

    /**
     * The name of the User
     * @param name  the new name of the user vehicle
     */
    public void setName(String name) {
        getUser().setName(name);
    }

    /**
     *
     * @return
     */
    public String getMake() {
        return vehicle.getMake();
    }

    /**
     * The model of the Vehicle
     * @return  the model of vehicle
     */
    public String getModel() {
        return vehicle.getModel();
    }

    public String getMakeModel() {
        return String.format(MAKE_MODEL_FORMAT, getMake(), getModel());
    }

    /**
     * The registration of the User
     * @return      the registration of the user vehicle
     */
    public String getRegistration() {
        return user.getRegistration();
    }

    /**
     * The registration of the User
     * @param registration  the new registration of the user vehicle
     */
    public void setRegistration(String registration) {
        getUser().setRegistration(registration);
    }

    /**
     * Is this profile the active one?
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Profile)) {
            return false;
        }

        Profile profile = (Profile) o;

        return getProfileId() == profile.getProfileId();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (getProfileId() ^ (getProfileId() >>> 32));
        return result;
    }
}
