package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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
        )
    }
)
public class PageItem {

    public final static String TABLE_NAME = "page_item";
    public final static String REFERENCE_COLUMN_ID = "page_item_id";
    public final static String COLUMN_ID = "pageItemId";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_ORDER = "order";


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long id;

    @ColumnInfo (name = Page.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long pageId;

    @NonNull
    @ColumnInfo(name = COLUMN_NAME, typeAffinity = ColumnInfo.TEXT)
    private String name;

    public PageItem (long id, long pageId, @NonNull String name) {
        this.id = id;
        this.pageId = pageId;
        this.name = name;
    }




}
