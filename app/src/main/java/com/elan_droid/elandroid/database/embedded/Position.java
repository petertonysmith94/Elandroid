package com.elan_droid.elandroid.database.embedded;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.elan_droid.elandroid.database.entity.PageItem;

/**
 * Created by Peter Smith
 */
public class Position implements Parcelable {

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

    /**
     * Constructing a position from a parcel
     * @param in
     */
    @Ignore
    private Position(Parcel in) {
        x = in.readInt();
        y = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(x);
        out.writeInt(y);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Position> CREATOR
            = new Parcelable.Creator<Position>() {
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
