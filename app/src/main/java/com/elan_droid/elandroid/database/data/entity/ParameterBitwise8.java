package com.elan_droid.elandroid.database.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;
import android.view.View;

import com.elan_droid.elandroid.database.data.converter.StringArrayConverter;
import com.elan_droid.elandroid.database.data.relation.ParameterFlag;
import com.elan_droid.elandroid.ui.dashboard.page.ListPage;
import com.elan_droid.elandroid.ui.dashboard.widget.Widget;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Peter Smith
 */
@Entity(
    tableName = ParameterBitwise8.TABLE_NAME,
    foreignKeys = {
        @ForeignKey(
            entity = Message.class,
            parentColumns = Message.COLUMN_ID,
            childColumns = Message.REFERENCE_COLUMN_ID,
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    }
)
public class ParameterBitwise8 extends ParameterStream {

    private static final Widget.Type[] DISPLAY_WIDGET = {

    };

    private static final int LENGTH = 1;
    private static final int SIZE = 8;

    public static final String BIT_1 = "Bit 0";
    public static final String BIT_2 = "Bit 1";
    public static final String BIT_3 = "Bit 2";
    public static final String BIT_4 = "Bit 3";
    public static final String BIT_5 = "Bit 4";
    public static final String BIT_6 = "Bit 5";
    public static final String BIT_7 = "Bit 6";
    public static final String BIT_8 = "Bit 7";

    public final static String TABLE_NAME = "bitwise8_parameter";
    public final static String COLUMN_PREFIX = "bitwise8_";
    public final static String COLUMN_DESCRIPTION = COLUMN_PREFIX + "description";

    private static final Type TYPE = Type.BITWISE_8;

    @TypeConverters ({StringArrayConverter.class})
    @ColumnInfo ( name = COLUMN_DESCRIPTION, typeAffinity = ColumnInfo.TEXT )
    private String[]  descriptions;

    /**
     * Constructor for the creation from the database
     * @param id
     * @param messageId
     * @param identifier
     * @param name
     * @param type
     * @param position
     * @param length
     */
    public ParameterBitwise8 (long id, long messageId, String identifier, String name, Type type, int position, int length,
                             String... descriptions) {
        super(id, messageId, identifier, name, type, position, length);

        this.descriptions = descriptions;
    }

    @Ignore
    public ParameterBitwise8 (long messageId, String identifier, String name, int position, String... descriptions) {
        this (0, messageId, identifier, name, TYPE, position, LENGTH);

        this.descriptions = descriptions;
    }

    @Override
    public Widget.Type[] getWidgetTypes() {
        return DISPLAY_WIDGET;
    }

    @Override
    public void format(Packet packet, byte[] data) {
        //TODO: implement formatting method
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String[] descriptions) {
        this.descriptions = descriptions;
    }


    //TODO: implement list view display for the parameter
    public static class ListViewHolder extends ListPage.ParameterAdapter.BaseViewHolder {

        public ListViewHolder(View itemView) {
            super(itemView);
        }

        public void bind (ParameterFlag parameterFlag, View.OnClickListener listener) {
            final ParameterBitwise8 bitwise = (ParameterBitwise8) parameterFlag.getParameter();

        }
    }


}
