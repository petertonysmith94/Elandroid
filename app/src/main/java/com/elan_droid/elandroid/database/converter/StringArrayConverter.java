package com.elan_droid.elandroid.database.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Peter Smith
 */

public class StringArrayConverter {

    @TypeConverter
    public static String[] fromJson (String json) {
        Type type = new TypeToken<String[]>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    @TypeConverter
    public static String toJson (String[] data) {
        return new Gson().toJson(data);
    }

}
