package com.elan_droid.elandroid.database.data.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Peter Smith on 4/19/2018.
 *
 * Converts the Date into a Long value and vice-versa
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
