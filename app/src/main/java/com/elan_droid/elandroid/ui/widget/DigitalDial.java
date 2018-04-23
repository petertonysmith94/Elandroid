package com.elan_droid.elandroid.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Peter Smith
 */

public class DigitalDial extends Widget {

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
        public View createView(Context context) {
            return new DigitalDial(context, null);
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
