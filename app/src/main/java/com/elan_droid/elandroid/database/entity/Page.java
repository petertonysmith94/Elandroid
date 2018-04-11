package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Peter Smith
 */
@Entity (
tableName = Page.TABLE_NAME,
foreignKeys =
    @ForeignKey(
        entity = User.class,
        parentColumns = User.COLUMN_ID,
        childColumns = User.REFERENCE_COLUMN_ID,
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
)
public class Page implements Parcelable
{

    public final static String TABLE_NAME = "page";
    public final static String REFERENCE_COLUMN_ID = "page_id";
    public final static String COLUMN_ID = "pageId";
    public final static String COLUMN_TITLE = "title";
    public final static String COLUMN_ORDER = "order";


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long id;

    @ColumnInfo (name = User.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long userId;

    @ColumnInfo(name = COLUMN_TITLE, typeAffinity = ColumnInfo.TEXT)
    private String title;

    @ColumnInfo(name = COLUMN_ORDER, typeAffinity = ColumnInfo.INTEGER)
    private int order;

    /**
     * Creates a page which is displayed on the dashboard
     * @param id       the page id
     * @param userId    the associated user id
     * @param title     the title of the page
     * @param order
     */
    public Page (long id, long userId, @NonNull String title, int order) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.order = order;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     *
     * @param in
     */
    @Ignore
    private Page(Parcel in) {
        title = in.readString();
        order = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeInt(order);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Page> CREATOR
            = new Parcelable.Creator<Page>() {
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        public Page[] newArray(int size) {
            return new Page[size];
        }
    };

}
