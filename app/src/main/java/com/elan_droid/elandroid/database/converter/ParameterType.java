package com.elan_droid.elandroid.database.converter;

import android.arch.persistence.room.TypeConverter;

import com.elan_droid.elandroid.database.entity.Parameter;
import com.elan_droid.elandroid.database.entity.Parameter.Type;


/**
 * Created by Peter Smith
 */

public class ParameterType {

    @TypeConverter
    public static Type toType (int value) {
        return Type.parse(value);
    }

    @TypeConverter
    public static int fromType (Type type) {
        return type.getValue();
    }

}
