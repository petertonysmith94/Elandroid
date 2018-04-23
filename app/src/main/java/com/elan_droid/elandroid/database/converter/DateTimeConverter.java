package com.elan_droid.elandroid.database.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by BorisJohnson on 4/19/2018.
 */

public class DateTimeConverter {

    @TypeConverter
    public static Date fromTimestamp (Long value) {
        return value == null ? null : new Date (value);
    }

    @TypeConverter
    public static Long toTimestamp (Date date) {
        return date == null ? null : date.getTime();
    }

}
