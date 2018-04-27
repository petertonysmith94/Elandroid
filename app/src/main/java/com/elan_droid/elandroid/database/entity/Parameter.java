package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.elan_droid.elandroid.database.converter.ParameterType;
import com.elan_droid.elandroid.ui.widget.DisplayType;
import com.elan_droid.elandroid.ui.widget.Widget;

/**
 * Created by Peter Smith
 *
 * A parameter is an object that represents some form of data that can be displayed to the user.
 *
 * TODO: change the relationship between parameter and message to MANY to MANY
 *  -> Add vehicle ID instead, 0 if not vehicle specific otherwise link vehicle
 *  -> This will allow for sensory data from the phones actuators
 */
public abstract class Parameter {

    /**
     * Defaults and units
     */
    public static final double MULTIPLIER_DEFAULT = 1.0;
    public static final double OFFSET_DEFAULT = 0.0;

    public static final String UNIT_NONE = "";
    public static final String UNIT_DEGREE_C = "°C";
    public static final String UNIT_PERCENT = "%";
    public static final String UNIT_RPM = "RPM";
    public static final String UNIT_MSEC = "msec";
    public static final String UNIT_MPH = "mph";
    public static final String UNIT_DEGREES = "Degrees";
    public static final String UNIT_VOLTS = "V";
    public static final String UNIT_SECONDS = "s";
    public static final String UNIT_BAR = "bar";
    public static final String UNIT_STEPS = "steps";
    public static final String UNIT_RATIO = ":";
    public static final String UNIT_KPA = "kPa";
    public static final String UNIT_GRAMS_PER_SEC = "s";
    public static final String UNIT_VALUE = "";
    public static final String UNIT_KPH = "KPH";

    /**
     * Database constants
     */
    public final static String COLUMN_ID = "parameterId";
    public final static String REFERENCE_COLUMN_ID = "parameter_id";
    public final static String COLUMN_IDENTIFIER = "identifier";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_TYPE = "type";

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo (name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long id;

    @ColumnInfo (name = Message.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long messageId;

    @ColumnInfo (name = COLUMN_IDENTIFIER, typeAffinity = ColumnInfo.TEXT)
    private String identifier;

    @ColumnInfo (name = COLUMN_NAME, typeAffinity = ColumnInfo.TEXT)
    private String name;

    @ColumnInfo (name = COLUMN_TYPE, typeAffinity = ColumnInfo.INTEGER)
    @TypeConverters (value = ParameterType.class)
    private Type type;

    /**
     * Constructor for creating the parameter from the database
     * @param id
     * @param messageId
     * @param identifier
     * @param name
     * @param type
     */
    public Parameter (long id, long messageId, String identifier, String name, Type type) {
        this.id = id;
        this.messageId = messageId;
        this.identifier = identifier;
        this.name = name;
        this.type = type;
    }

    public abstract void format (Packet packet, Object data);

    public abstract Widget.Type[] getWidgetTypes();

    @Override
    public String toString() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     *
     */
    public enum Type {

        BITWISE_8 (Type.BITWISE_VALUE),
        FORMATTED (Type.FORMATTED_VALUE);

        public static final int BITWISE_VALUE = 1;
        public static final int FORMATTED_VALUE = 2;

        private int value;

        Type (int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type parse(int value) {
            switch (value) {
                case BITWISE_VALUE:
                    return Type.BITWISE_8;

                case FORMATTED_VALUE:
                    return Type.FORMATTED;

                default:
                    return null;
            }
        }
    }

}
