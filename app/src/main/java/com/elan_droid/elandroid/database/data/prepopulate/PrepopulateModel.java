package com.elan_droid.elandroid.database.data.prepopulate;

import com.elan_droid.elandroid.database.data.entity.Vehicle;
import com.elan_droid.elandroid.database.data.relation.Command;

/**
 * Created by Peter Smith
 */

public interface PrepopulateModel {

    Vehicle getVehicle();

    Command[] getCommands();

}
