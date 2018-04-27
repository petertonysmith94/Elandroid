package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.view.View;
import android.widget.TextView;

import com.elan_droid.elandroid.R;
import com.elan_droid.elandroid.database.relation.ParameterFlag;
import com.elan_droid.elandroid.ui.page.ListPage;
import com.elan_droid.elandroid.ui.widget.Widget;

import java.math.BigInteger;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Peter Smith
 *
 * A ParameterFormatted represents a data item on the stream which can be converted into
 * a real value.
 */
@Entity (
    tableName = ParameterFormatted.TABLE_NAME,
    foreignKeys = {
        @ForeignKey (
            entity = Message.class,
            parentColumns = Message.COLUMN_ID,
            childColumns = Message.REFERENCE_COLUMN_ID,
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    }
)
public class ParameterFormatted extends ParameterStream {

    private static final Widget.Type[] DISPLAY_WIDGET = {
        Widget.Type.ANALOG_DIAL,
        Widget.Type.DIGITAL_DIAL
    };

    public final static String TABLE_NAME = "formatted_parameter";
    public final static String COLUMN_PREFIX = "formatted_";
    public final static String COLUMN_MULTIPLIER = COLUMN_PREFIX + "multiplier";
    public final static String COLUMN_OFFSET = COLUMN_PREFIX + "offset";
    public final static String COLUMN_FORMAT = COLUMN_PREFIX + "format";
    public final static String COLUMN_UNITS = COLUMN_PREFIX + "units";
    public final static String COLUMN_SIGNED = COLUMN_PREFIX + "signed";


    @ColumnInfo(name = COLUMN_MULTIPLIER, typeAffinity = ColumnInfo.REAL)
    private double multiplier;


    @ColumnInfo(name = COLUMN_OFFSET, typeAffinity = ColumnInfo.REAL)
    private double offset;


    @ColumnInfo(name = COLUMN_FORMAT, typeAffinity = ColumnInfo.TEXT)
    private String format;


    @ColumnInfo(name = COLUMN_UNITS, typeAffinity = ColumnInfo.TEXT)
    private String units;


    @ColumnInfo(name = COLUMN_SIGNED, typeAffinity = ColumnInfo.INTEGER)
    private int signed;

    /**
     *
     * @param id
     * @param messageId
     * @param identifier
     * @param name
     * @param type
     * @param position
     * @param length
     * @param multiplier
     * @param offset
     * @param format
     * @param units
     * @param signed
     */
    public ParameterFormatted(long id, long messageId, String identifier, String name, Type type, int position, int length,
                              double multiplier, double offset, String format, String units, int signed) {
        super(id, messageId, identifier, name, type, position, length);

        this.multiplier = multiplier;
        this.offset = offset;
        this.format = format;
        this.units = units;
        this.signed = signed;
    }

    /**
     * For creating a formatted parameter with a generated value
     * @param messageId
     * @param identifier
     * @param name
     * @param position
     * @param length
     * @param multiplier
     * @param offset
     * @param format
     * @param units
     * @param signed
     */
    @Ignore
    public ParameterFormatted(long messageId, String identifier, String name, int position, int length,
                              double multiplier, double offset, String format, String units, boolean signed) {
        this (0, messageId, identifier, name, Type.FORMATTED, position, length, multiplier, offset, format, units, signed ? 1 : 0);
    }

    @Override
    public Widget.Type[] getWidgetTypes() {
        return DISPLAY_WIDGET;
    }

    @Override
    // TODO: remaster this process to process any value (int, double,  hex)
    public void format(Packet packet, byte[] data) {
        final double value = format(data);
        packet.addFlag(new FlagFormatted(getId(), packet.getId(), data, value, format(value)));
    }

    /**
     * Formats bytes into double value => then applies multiplier and offset
     * @param data
     * @return
     */
    private double format (final byte[] data) {
        return ((new BigInteger(data).doubleValue()) * multiplier) + offset;
    }

    private String format (final Double data) {
        if (format.equals("%d") || format.equals("%x")) {
            return String.format(format, data.intValue());
        }
        else {
            return String.format(format, data);
        }
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getSigned() {
        return signed;
    }

    public void setSigned(int signed) {
        this.signed = signed;
    }

    public static class ListViewHolder extends ListPage.ParameterAdapter.BaseViewHolder {

        TextView paramName;
        TextView packetValue;
        TextView paramUnits;

        public ListViewHolder(View view) {
            super(view);

            paramName = (TextView) view.findViewById(R.id.list_item_formatted_parameter_title);
            packetValue = (TextView) view.findViewById(R.id.list_item_formatted_packet_value);
            paramUnits = (TextView) view.findViewById(R.id.list_item_formatted_parameter_units);
        }

        public void bind (ParameterFlag parameterFlag, View.OnClickListener listener) {
            final ParameterFormatted formatted = (ParameterFormatted) parameterFlag.getParameter();
            paramName.setText(formatted.getName());
            packetValue.setText(parameterFlag.getValue());
            paramUnits.setText(formatted.getUnits());
        }
    }

}
