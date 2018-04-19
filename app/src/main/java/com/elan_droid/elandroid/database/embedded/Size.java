package com.elan_droid.elandroid.database.embedded;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.elan_droid.elandroid.database.entity.PageItem;
import com.elan_droid.elandroid.ui.widget.DisplaySize;

/**
 * Created by Peter Smith
 */

public class Size implements Parcelable {

    public final static String COLUMN_WIDTH = "width";
    public final static String COLUMN_HEIGHT = "height";

    @ColumnInfo(name = COLUMN_HEIGHT, typeAffinity = ColumnInfo.INTEGER)
    private int height;

    @ColumnInfo(name = COLUMN_WIDTH, typeAffinity = ColumnInfo.INTEGER)
    private int width;

    public Size(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public Size(DisplaySize size) {
        this (size.getHeight(), size.getWidth());
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    /**
     * Constructing a PageItem from a parcel
     * @param in
     */
    @Ignore
    private Size(Parcel in) {
        height = in.readInt();
        width = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(height);
        out.writeInt(width);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Size> CREATOR
            = new Parcelable.Creator<Size>() {
        public Size createFromParcel(Parcel in) {
            return new Size(in);
        }

        public Size[] newArray(int size) {
            return new Size[size];
        }
    };

}
