package com.elan_droid.elandroid.ui.dashboard.widget;

/**
 * Created by Peter Smith
 */

public interface DisplaySize {


    int getHeight();

    int getWidth();

    DisplaySize SQUARE_SMALL = new DisplaySize() {

        private static final String NAME = "Small";

        private static final int HEIGHT = 100;
        private static final int WIDTH = 100;

        @Override
        public int getHeight() {
            return HEIGHT;
        }

        @Override
        public int getWidth() {
            return WIDTH;
        }

        @Override
        public String toString() {
            return NAME;
        }
    };

    DisplaySize SQUARE_MEDIUM = new DisplaySize() {

        private static final String NAME = "Medium";

        private static final int HEIGHT = 200;
        private static final int WIDTH = 200;

        @Override
        public int getHeight() {
            return HEIGHT;
        }

        @Override
        public int getWidth() {
            return WIDTH;
        }

        @Override
        public String toString() {
            return NAME;
        }
    };

    DisplaySize SQUARE_LARGE = new DisplaySize() {

        private static final String NAME = "Large";

        private static final int HEIGHT = 300;
        private static final int WIDTH = 300;

        @Override
        public int getHeight() {
            return HEIGHT;
        }

        @Override
        public int getWidth() {
            return WIDTH;
        }

        @Override
        public String toString() {
            return NAME;
        }
    };

}
