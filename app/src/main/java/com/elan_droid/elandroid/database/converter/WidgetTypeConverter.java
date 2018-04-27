package com.elan_droid.elandroid.database.converter;

import android.arch.persistence.room.TypeConverter;

import com.elan_droid.elandroid.database.entity.Parameter;
import com.elan_droid.elandroid.ui.widget.Widget;

/**
 * Created by Peter Smith on 4/21/2018.
 *
 * Converts the Enum Widget.Type into a integer value and vice-versa
 */
public class WidgetTypeConverter {

    @TypeConverter
    public static Widget.Type toType (int id) {
        return Widget.Type.parseId(id);
    }

    @TypeConverter
    public static int fromType (Widget.Type type) {
        return type.getId();
    }

}
