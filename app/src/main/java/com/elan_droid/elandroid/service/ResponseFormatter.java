package com.elan_droid.elandroid.service;

import com.elan_droid.elandroid.database.entity.Flag;
import com.elan_droid.elandroid.database.entity.Packet;

/**
 * Created by Peter Smith on 4/26/2018.
 **/
public interface ResponseFormatter {

    Packet format (Packet packet);

}
