package com.elan_droid.elandroid.database.model;

import com.elan_droid.elandroid.database.entity.Vehicle;

/**
 * Created by Peter Smith
 */

public abstract class LotusElanS2 {

    public static final long VEHICLE_ID = 1;
    public static final String VEHICLE_MAKE = "Lotus";
    public static final String VEHICLE_MODEL = "Elan S2";

    public static Vehicle getVehicle() {
        return new Vehicle (VEHICLE_ID, VEHICLE_MAKE, VEHICLE_MODEL);
    }




}
