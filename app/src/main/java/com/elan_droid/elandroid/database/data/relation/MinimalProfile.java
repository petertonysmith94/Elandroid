package com.elan_droid.elandroid.database.data.relation;

/**
 * Created by Peter Smith
 */

public class MinimalProfile {

    public long id;

    public String name;

    public String makeModel;

    public String registration;


    /**
     * The name of the User
     * @return      the name of the user vehicle
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the User
     * @param name  the new name of the user vehicle
     */
    public void setName(String name) {
        this.name = name;
    }


    public String getMakeModel() {
        return makeModel;
    }

    public void setMakeModel() {
        this.makeModel = makeModel;
    }

    /**
     * The registration of the User
     * @return      the registration of the user vehicle
     */
    public String getRegistration() {
        return registration;
    }

    /**
     * The registration of the User
     * @param registration  the new registration of the user vehicle
     */
    public void setRegistration(String registration) {
        this.registration = registration;
    }


}
