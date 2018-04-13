package com.elan_droid.elandroid.database.embedded;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by Peter Smith
 */

public class Size {

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
}
