package com.elan_droid.elandroid.database.prepopulate;

import com.elan_droid.elandroid.database.entity.Vehicle;
import com.elan_droid.elandroid.database.relation.Command;

/**
 * Created by Peter Smith
 */

public interface PrepopulateModel {

    Vehicle getVehicle();

    Command[] getCommands();

}
