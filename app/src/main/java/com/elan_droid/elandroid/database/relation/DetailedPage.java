package com.elan_droid.elandroid.database.relation;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;
import android.os.Parcel;
import android.os.Parcelable;

import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.PageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Smith
 */

public class DetailedPage implements Parcelable {

    public static final String EXTRA = "com.elan_droid.elandroid.database.relation.EXTRA_DETAILED_PAGE";

    @Embedded
    public Page page;

    @Relation (
        parentColumn = Page.COLUMN_ID,
        entityColumn = Page.REFERENCE_COLUMN_ID
    )
    public List<PageItem> items;

    public DetailedPage (Page page) {
        this (page, new ArrayList<PageItem>());
    }

    public DetailedPage (Page page, List<PageItem> items) {
        this.page = page;
        this.items = items;
    }

    public List<PageItem> getItems() {
        return items;
    }

    public void setItems(List<PageItem> items) {
        this.items = items;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * Constructing the page from a parcel
     * @param in
     */
    @Ignore
    private DetailedPage(Parcel in) {
        page = (Page) in.readParcelable(Page.class.getClassLoader());
        items = new ArrayList<>();
        in.readTypedList(items, PageItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(page, flags);
        out.writeTypedList(items);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<DetailedPage> CREATOR
            = new Parcelable.Creator<DetailedPage>() {
        public DetailedPage createFromParcel(Parcel in) {
            return new DetailedPage(in);
        }

        public DetailedPage[] newArray(int size) {
            return new DetailedPage[size];
        }
    };

}
