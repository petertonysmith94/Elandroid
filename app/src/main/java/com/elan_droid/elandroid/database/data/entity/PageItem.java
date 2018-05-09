package com.elan_droid.elandroid.database.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.data.converter.WidgetTypeConverter;
import com.elan_droid.elandroid.database.data.embedded.Position;
import com.elan_droid.elandroid.database.data.embedded.Size;
import com.elan_droid.elandroid.ui.dashboard.widget.Widget;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Peter Smith
 */
@Entity(
    tableName = PageItem.TABLE_NAME,
    foreignKeys = {
        @ForeignKey (
            entity = Page.class,
            parentColumns = Page.COLUMN_ID,
            childColumns = Page.REFERENCE_COLUMN_ID,
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        @ForeignKey (
            entity = ParameterFormatted.class,
            parentColumns = Parameter.COLUMN_ID,
            childColumns = Parameter.REFERENCE_COLUMN_ID,
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    }
)
public class PageItem implements Parcelable {

    public final static String EXTRA = "com.elan_droid.elandroid.database.entity.EXTRA_PAGE_ITEM";

    public final static String TABLE_NAME = "page_item";
    public final static String REFERENCE_COLUMN_ID = "page_item_id";
    public final static String COLUMN_ID = "pageItemId";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_WIDGET_TYPE = "widget_type";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long id;

    @ColumnInfo (name = Page.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long pageId;

    @ColumnInfo (name = Parameter.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long parameterId;

    @NonNull
    @ColumnInfo(name = COLUMN_NAME, typeAffinity = ColumnInfo.TEXT)
    private String name;

    @ColumnInfo (name = COLUMN_WIDGET_TYPE)
    @TypeConverters(value = WidgetTypeConverter.class)
    private Widget.Type type;

    @NonNull
    @Embedded
    private Position position;

    @NonNull
    @Embedded
    private Size size;



    @Ignore
    public PageItem (long pageId, long parameterId, @NonNull String name,
                     @NonNull Widget.Type type, Position position, Size size) {
        this (0, pageId, parameterId, name, type, position, size);
    }

    public PageItem (long id, long pageId, long parameterId, @NonNull String name,
                     @NonNull Widget.Type type, Position position, Size size) {
        this.id = id;
        this.pageId = pageId;
        this.parameterId = parameterId;
        this.name = name;
        this.type = type;
        this.position = position;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public long getParameterId() {
        return parameterId;
    }

    public void setParameterId(long parameterId) {
        this.parameterId = parameterId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Widget.Type getType() {
        return type;
    }

    public void setType(Widget.Type type) {
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(@NonNull Position position) {
        this.position = position;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    /**
     * Constructing a PageItem from a parcel
     * @param in
     */
    @Ignore
    private PageItem(Parcel in) {
        id = in.readLong();
        pageId = in.readLong();
        parameterId = in.readLong();
        name = in.readString();
        position = in.readParcelable(Position.class.getClassLoader());
        size = in.readParcelable(Size.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(pageId);
        out.writeLong(parameterId);
        out.writeString(name);
        out.writeParcelable(position, flags);
        out.writeParcelable(size, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<PageItem> CREATOR
            = new Parcelable.Creator<PageItem>() {
        public PageItem createFromParcel(Parcel in) {
            return new PageItem(in);
        }

        public PageItem[] newArray(int size) {
            return new PageItem[size];
        }
    };


    @Override
    public String toString() {
        return "PageItem{" +
                "id=" + id +
                ", pageId=" + pageId +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", size=" + size +
                '}';
    }
}
