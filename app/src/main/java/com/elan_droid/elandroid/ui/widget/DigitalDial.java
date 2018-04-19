package com.elan_droid.elandroid.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Peter Smith
 */

public class DigitalDial extends BaseWidget {

    private static final int ID = 2;
    private static final String NAME = "Digital dial";
    private static final DisplaySize[] SIZES = {
        DisplaySize.SQUARE_SMALL,
        DisplaySize.SQUARE_MEDIUM,
        DisplaySize.SQUARE_LARGE
    };

    public static final DisplayType DISPLAY = new DisplayType() {
        @Override
        public int getId() {
            return ID;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public DisplaySize[] getSizes() {
            return SIZES;
        }

        @Override
        public String toString() {
            return NAME;
        }
    };

    public DigitalDial(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

    }

    @Override
    public String toString() {
        return NAME;
    }



}
