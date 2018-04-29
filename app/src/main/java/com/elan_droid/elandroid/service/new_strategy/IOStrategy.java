package com.elan_droid.elandroid.service.new_strategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Peter Smith on 4/27/2018.
 **/
public interface IOStrategy {

    // SUCCESS CODES > 0


    int RESULT_HANDLED = 5;
    int RESULT_VALIDATED = 4;
    int RESULT_TRIGGERED = 3;
    int RESULT_TRIGGER = 2;
    int RESULT_IDLE = 1;

    // ERROR CODES < 0
    int ERROR_READ = -1;
    int ERROR_TIMEOUT = -2;
    int ERROR_VALIDATION = -3;
    int ERROR_DATABASE = -4;

    int idleTimeout();

    int execute (int requestCode, OutputStream out, InputStream in) throws IOException;

}
