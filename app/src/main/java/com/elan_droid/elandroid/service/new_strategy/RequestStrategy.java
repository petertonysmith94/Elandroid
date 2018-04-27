package com.elan_droid.elandroid.service.new_strategy;


import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Peter Smith on 4/27/2018.
 **/
public interface RequestStrategy extends IOStrategy {

    int executeRequest(int requestCode, OutputStream out) throws IOException;

}
