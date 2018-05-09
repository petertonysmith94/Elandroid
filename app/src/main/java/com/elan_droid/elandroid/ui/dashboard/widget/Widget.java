package com.elan_droid.elandroid.ui.dashboard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Peter Smith
 */
public abstract class Widget extends View {

    TextView mWidgetName;

    protected Widget(Context context, AttributeSet attributeSet) {
        super (context, attributeSet);
    }



    public enum Type {

        ANALOG_DIAL (Type.ANALOG_DIAL_ID, AnalogDialWidget.DISPLAY),
        DIGITAL_DIAL (Type.DIGITAL_DIAL_ID, DigitalDial.DISPLAY);

        private static final int ANALOG_DIAL_ID = 1;
        private static final int DIGITAL_DIAL_ID = 2;

        private DisplayType type;

        Type (int id, DisplayType type) {
            this.type = type;
        }

        public int getId() {
            return this.type.getId();
        }

        public DisplayType getDisplay() {
            return type;
        }

        public static Type parseId (int id) {
            switch (id) {
                case ANALOG_DIAL_ID:
                    return Type.ANALOG_DIAL;

                case DIGITAL_DIAL_ID:
                    return Type.DIGITAL_DIAL;

                default:
                    return null;
            }
        }

    }


}
