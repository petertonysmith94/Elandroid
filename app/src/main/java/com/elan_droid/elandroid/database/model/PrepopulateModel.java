package com.elan_droid.elandroid.database.model;

import com.elan_droid.elandroid.database.dao.MessageDao;
import com.elan_droid.elandroid.database.dao.ParameterDao;
import com.elan_droid.elandroid.database.entity.Message;
import com.elan_droid.elandroid.database.entity.Vehicle;
import com.elan_droid.elandroid.database.relation.Command;

import java.util.List;

/**
 * Created by Peter Smith
 */

public interface PrepopulateModel {

    Vehicle getVehicle();

    Command[] getCommands();

}
