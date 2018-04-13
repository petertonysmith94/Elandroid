package com.elan_droid.elandroid.database.embedded;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by Peter Smith
 */
public class Position {

    public final static String COLUMN_COORD_X = "coord_x";
    public final static String COLUMN_COORD_Y = "coord_y";

    @ColumnInfo(name = COLUMN_COORD_X, typeAffinity = ColumnInfo.INTEGER)
    private int x;

    @ColumnInfo(name = COLUMN_COORD_Y, typeAffinity = ColumnInfo.INTEGER)
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


}
