package com.elan_droid.elandroid.database.relation;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.PageItem;

import java.util.List;

/**
 * Created by Peter Smith
 */

public class PageContent {

    @Embedded
    public Page page;

    @Relation (
        parentColumn = Page.COLUMN_ID,
        entityColumn = Page.REFERENCE_COLUMN_ID
    )
    public List<PageItem> items;

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

}
