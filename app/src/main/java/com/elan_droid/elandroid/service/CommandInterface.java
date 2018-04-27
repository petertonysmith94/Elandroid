package com.elan_droid.elandroid.service;

import com.elan_droid.elandroid.database.relation.Command;

/**
 * Created by Peter Smith on 4/26/2018.
 **/
public interface CommandInterface {

    void setCommand (Command command);

    boolean triggerCommand ();

}
