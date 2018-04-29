package com.elan_droid.elandroid.service.new_strategy;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Peter Smith on 4/27/2018.
 *
 *
 **/
public interface ResponseStrategy extends RequestStrategy {

    int executeResponse (final int resultCode, final InputStream in) throws IOException;

    int postResponse (final int resultCode);

}
