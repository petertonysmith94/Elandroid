package com.elan_droid.elandroid.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import com.elan_droid.elandroid.R;

/**
 * Created by Peter Smith
 */

public class AnalogDialWidget extends BaseWidget {

    private static final int ID = 1;
    private static final String NAME = "Radial dial";
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


    /**
     * Dial parameters
     */
    private static final float DEFAULT_VALUE = 1f;
    private static final float DEFAULT_MIN_VALUE = 0f;
    private static final float DEFAULT_MAX_VALUE = 100f;
    private static final int DEFAULT_START_ANGLE_DEGREE = 45;
    private static final int DEFAULT_FINISH_ANGLE_DEGREE = 315;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    private Paint backgroundPaint;
    private double startAngle;
    private double finishAngle;
    private float degreesPerUnit;
    private float value, minValue, maxValue;
    private float pivotX;
    private float pivotY;
    private int radius;
    private double baseTheta;

    private float maxX = Float.MIN_VALUE;
    private float maxY = Float.MIN_VALUE;


    /**
     * Needle parameters
     */
    private static final float DEFAULT_NEEDLE_OUTER_RAD_MULTIP = 0.68f;
    private static final float DEFAULT_NEEDLE_INNER_RAD_MULTIP = 0.01f;
    private static final int DEFAULT_NEEDLE_COLOUR = Color.CYAN;
    private static final float DEFAULT_NEEDLE_STROKE_WIDTH = 4.0f;
    private static final Paint.Cap DEFAULT_NEEDLE_STROKE_CAP = Paint.Cap.ROUND;
    private static final boolean DEFAULT_NEEDLE_ANTI_ALIAS = false;
    private Paint needlePaint;
    private int needleColor;
    private float outerNeedleRadMultiplier;
    private float innerNeedleRadMultiplier;
    private double needleTheta;
    private double sinNeedleTheta;
    private double cosNeedleTheta;

    /**
     * Primary tick parameters
     */
    private static final int DEFAULT_PRI_TICK_COUNT = 4;
    private static final float DEFAULT_PRI_TICK_OUTER_RAD_MULTIP = 0.82f;
    private static final float DEFAULT_PRI_TICK_INNER_RAD_MULTIP = 0.75f;
    private static final int DEFAULT_PRI_TICK_COLOUR = Color.YELLOW;
    private static final float DEFAULT_PRI_TICK_STROKE_WIDTH = 8.0f;
    private static final Paint.Cap DEFAULT_PRI_TICK_STROKE_CAP = Paint.Cap.ROUND;
    private static final boolean DEFAULT_PRI_TICK_ANTI_ALIAS = false;
    private Paint priTickPaint;
    private float outerPriTickRadMultiplier;
    private float innerPriTickRadMultiplier;
    private int primaryTickCount;

    /**
     * Primary text parmaters
     */
    private static final float DEFAULT_PRI_TEXT_RAD_MULTIP = 0.9f;
    private static final int DEFAULT_PRI_TEXT_COLOR = Color.BLACK;
    private Paint priTextPaint;
    private float priTextRadMultiplier;


    /**
     * Secondary tick parameters
     */
    private static final int DEFAULT_SEC_TICK_COUNT = 2;
    private static final float DEFAULT_SEC_TICK_OUTER_RAD_MULTIP = 0.8f;
    private static final float DEFAULT_SEC_TICK_INNER_RAD_MULTIP = 0.71f;
    private static final int DEFAULT_SEC_TICK_COLOUR = Color.RED;
    private static final float DEFAULT_SEC_TICK_STROKE_WIDTH = 4.0f;
    private static final Paint.Cap DEFAULT_SEC_TICK_STROKE_CAP = Paint.Cap.ROUND;
    private static final boolean DEFAULT_SEC_TICK_ANTI_ALIAS = false;
    private Paint secTickPaint;
    private float outerSecTickRadMultiplier;
    private float innerSecTickRadMultiplier;
    private int secondaryTickCount;


    private Bitmap bitmapBackground;


    public AnalogDialWidget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        backgroundPaint = new Paint();
        priTickPaint = new Paint();
        priTextPaint = new Paint();
        secTickPaint = new Paint();
        needlePaint = new Paint();

        TypedArray attributes = context.getTheme().obtainStyledAttributes
                (attributeSet, R.styleable.AnalogDialWidget, 0, 0);
        loadAttributes(attributes);

        bitmapBackground = createBG(400, 400);
        BitmapDrawable background = new BitmapDrawable(getResources(), bitmapBackground);
        setBackground(background);
    }

    public void loadAttributes(TypedArray a) {
        try {
            //Dial
            startAngle = Math.toRadians(a.getInteger(R.styleable.AnalogDialWidget_startAngle, DEFAULT_START_ANGLE_DEGREE));
            finishAngle = Math.toRadians(a.getInteger(R.styleable.AnalogDialWidget_finishAngle, DEFAULT_FINISH_ANGLE_DEGREE));
            value = a.getFloat(R.styleable.AnalogDialWidget_value, DEFAULT_VALUE);
            minValue = a.getFloat(R.styleable.AnalogDialWidget_minValue, DEFAULT_MIN_VALUE);
            maxValue = a.getFloat(R.styleable.AnalogDialWidget_maxValue, DEFAULT_MAX_VALUE);



            //Primary tick
            primaryTickCount = a.getInteger(R.styleable.AnalogDialWidget_primaryTickCount, DEFAULT_PRI_TICK_COUNT);
            outerPriTickRadMultiplier = a.getFloat(R.styleable.AnalogDialWidget_primaryTickOuterRadMultiplier,
                    DEFAULT_PRI_TICK_OUTER_RAD_MULTIP);
            innerPriTickRadMultiplier = a.getFloat(R.styleable.AnalogDialWidget_primaryTickInnerRadMultiplier,
                    DEFAULT_PRI_TICK_INNER_RAD_MULTIP);
            priTickPaint.setColor(a.getColor(R.styleable.AnalogDialWidget_primaryTickColour, DEFAULT_PRI_TICK_COLOUR));
            priTickPaint.setStrokeWidth(a.getFloat(R.styleable.AnalogDialWidget_primaryTickStrokeWidth, DEFAULT_PRI_TICK_STROKE_WIDTH));
            priTickPaint.setStrokeCap(DEFAULT_PRI_TICK_STROKE_CAP); //TODO: figure out a way to pass this in
            priTickPaint.setAntiAlias(a.getBoolean(R.styleable.AnalogDialWidget_primaryTickAntiAlias, DEFAULT_PRI_TICK_ANTI_ALIAS));

            //Primary text
            priTextRadMultiplier = a.getFloat(R.styleable.AnalogDialWidget_primaryTextRadMultiplier, DEFAULT_PRI_TEXT_RAD_MULTIP);
            priTextPaint.setColor(a.getColor(R.styleable.AnalogDialWidget_primaryTextColour, DEFAULT_PRI_TEXT_COLOR));
            priTextPaint.setTextSize(32.0f);

            //Secondary tick
            secondaryTickCount = a.getInteger(R.styleable.AnalogDialWidget_secondaryTickCount, DEFAULT_SEC_TICK_COUNT);
            outerSecTickRadMultiplier = a.getFloat(R.styleable.AnalogDialWidget_secondaryTickInnerRadMultiplier,
                    DEFAULT_SEC_TICK_OUTER_RAD_MULTIP);
            innerSecTickRadMultiplier = a.getFloat(R.styleable.AnalogDialWidget_secondaryTickOuterRadMultiplier,
                    DEFAULT_SEC_TICK_INNER_RAD_MULTIP);
            secTickPaint.setColor(a.getColor(R.styleable.AnalogDialWidget_secondaryTickColour, DEFAULT_SEC_TICK_COLOUR));
            secTickPaint.setStrokeWidth(a.getFloat(R.styleable.AnalogDialWidget_secondaryTickStrokeWidth, DEFAULT_SEC_TICK_STROKE_WIDTH));
            secTickPaint.setStrokeCap(DEFAULT_SEC_TICK_STROKE_CAP); //TODO: figure out a way to pass this in
            secTickPaint.setAntiAlias(a.getBoolean(R.styleable.AnalogDialWidget_secondaryTickAntiAlias, DEFAULT_SEC_TICK_ANTI_ALIAS));

            //Needle
            outerNeedleRadMultiplier = a.getFloat(R.styleable.AnalogDialWidget_needleOuterRadMultiplier,
                    DEFAULT_NEEDLE_OUTER_RAD_MULTIP);
            innerNeedleRadMultiplier = a.getFloat(R.styleable.AnalogDialWidget_needleInnerRadMultiplier,
                    DEFAULT_NEEDLE_INNER_RAD_MULTIP);
            needleColor = a.getColor(R.styleable.AnalogDialWidget_needleColour, DEFAULT_NEEDLE_COLOUR);
            needlePaint.setColor(needleColor);
            needlePaint.setStrokeWidth(a.getFloat(R.styleable.AnalogDialWidget_needleStrokeWidth, DEFAULT_NEEDLE_STROKE_WIDTH));
            needlePaint.setStrokeCap(DEFAULT_NEEDLE_STROKE_CAP); //TODO: figure out a way to pass this in
            needlePaint.setAntiAlias(a.getBoolean(R.styleable.AnalogDialWidget_needleAntiAlias, DEFAULT_NEEDLE_ANTI_ALIAS));
        }
        finally {
            a.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public Bitmap createBG (int height, int width) {
        Bitmap backgroundBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (backgroundBitmap);

        float totalAngle = (float) (finishAngle - startAngle);
        degreesPerUnit = totalAngle / (maxValue - minValue);
        baseTheta = (Math.PI / 2) + startAngle;
        pivotX = width / 2;
        pivotY = height / 2;
        radius = height / 2;

        canvas.drawCircle(pivotX, pivotY, radius, backgroundPaint);

        final int totalTicks = primaryTickCount * secondaryTickCount + primaryTickCount;
        final double tickAngle = totalAngle / totalTicks;

        double cosTheta, sinTheta;
        int outerX, outerY, innerX, innerY;

        for (int p = 0; p <= totalTicks; p++) {



        }


        double theta = baseTheta;

        return backgroundBitmap;
    }

    private void drawText (Canvas canvas, Paint paint, Rect bounds, String text, Path path) {
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), bounds);

    }

    private void drawPrimaryTick (Canvas canvas, Paint paint, Path path) {

    }

    private Path perpendicularPath (Path path) {
        return path;
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public String toString() {
        return NAME;
    }



}
