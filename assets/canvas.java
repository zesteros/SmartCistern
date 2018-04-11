package itl.angelo.smartcistern.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.util.volumecomputer.VolumeCalculator;
import itl.angelo.smartcistern.util.connection.GlobalData;
import processing.core.PApplet;

/**
 * Created by Angelo on 20/03/2017.
 */

public class MonitorFragment extends PApplet {
    private static final String DECIMAL_FORMAT = "0.00";
    /*
        * The height and width of screen device
        * */
    private int screenHeight, screenWidth;

    private float y = 0;
    /*
    * Establish the margin of top for cylinder in px
    * */
    private int marginTop = 125;
    /*
    * Establish the margin of bottom for cylinder in px
    * */
    private int marginBottom = -36;
    /*
    * Establish the width of cylinder in px
    * */
    private int ellipseWidth = 125;
    /*
    * Establish the height of cylinder in px
    * */
    private int ellipseHeight = 22;

    private VolumeCalculator calculator;

    private int limit;

    private int volumeIndicatorRadius = 7;
    private int volumeIndicatorLineLength = 80;
    private int textSize = 8;
    private DecimalFormat format;

    private int[] screenTypes = {
            320,
            480,
            768,
            1080,
            1440
    };

    /**
     * Initialize the canvas
     */
    @Override
    public void setup() {
        background(255);
        strokeWeight(5);
        calculator = new VolumeCalculator(getActivity());
        limit = -(marginTop + (marginBottom * -1));
        format = new DecimalFormat(DECIMAL_FORMAT);
    }

    public void increaseCylinderSize(double times) {
        marginTop *= times;
        marginBottom *= times;
        ellipseWidth *= times;
        ellipseHeight *= times;
        volumeIndicatorRadius *= times;
        volumeIndicatorLineLength += times;
        textSize *= times;
    }

    /**
     * Infinite loop to execute in 60 fps
     */
    @Override
    public void draw() {
        background(255);
        stroke(5);
        fill(150, 255, 255, 180);
        /*INFERIOR ELLIPSE (HALF SCREEN, -50 HEIGHT/2 SCREEN, 100 WIDTH, 50 HEIGHT)*/
        ellipse(
                width / 2,
                height / 2 - marginBottom,
                ellipseWidth,
                ellipseHeight
        );
        calculator.getPreferenceData();
        y = calculator.getValueForGraphic(
                limit,0,
                GlobalData.getInstance().getDistance()
        );
        fillCistern();
        stroke(5);
        fill(255, 0);
        /*SUPERIOR ELLIPSE (HALF SCREEN, -200 HEIGHT/2 SCREEN, 100 WIDTH, 50 HEIGHT)*/
        ellipse(
                width / 2,
                height / 2 - marginTop,
                ellipseWidth,
                ellipseHeight
        );
        fill(255);
        stroke(5);
        /*LINES X: THE LEFT/right RADIUS OF ELLIPSE, Y: HEIGHT OF CYLINDER*/
        line(
                width / 2 - (ellipseWidth / 2),
                height / 2 - marginTop,
                width / 2 - (ellipseWidth / 2),
                height / 2 - marginBottom
        );
        line(
                width / 2 + (ellipseWidth / 2),
                height / 2 - marginTop,
                width / 2 + (ellipseWidth / 2),
                height / 2 - marginBottom);
        if (y >= limit)
            if (y < -1) showLevelIndicator(y);
            else showLevelIndicator(0);
        else showLevelIndicator(limit);

        delay(200);
    }

    /**
     * Fill the cistern with water
     */
    public void fillCistern() {
        fill(150, 255, 255, 80);
        //if y is more than top of cylinder (-1300)
        if (y >= limit)
            for (int i = -1; i >= (int) y; i--)
                fillWithWater(i);
            //fill it up
        else for (int i = -1; i >= limit; i--)
            fillWithWater(i);
    }

    public void fillWithWater(int i) {
        //if(is the last water layer
        fill(150, 255, 255, 80);
        if (i - 1 <= y) {
            //set black stroke
            stroke(0);
            fill(0, 200, 200, 255);
        } else noStroke();
        ellipse(width / 2, height / 2 - marginBottom + i, ellipseWidth, ellipseHeight);
    }

    public void showLevelIndicator(float y) {
        line(width / 2 + (ellipseWidth / 2),
                (height / 2 - marginBottom + y),
                width / 2 + (ellipseWidth / 2) + volumeIndicatorLineLength,
                height / 2 - marginBottom + y);
        fill(0);
        showText((float) (width / 2 + (ellipseWidth / 2) + (volumeIndicatorLineLength * 1.2)),
                height / 2 - marginBottom + y);
        fill(83, 109, 255);
        ellipse(width / 2 + (ellipseWidth / 2),
                height / 2 - marginBottom + y,
                volumeIndicatorRadius,
                volumeIndicatorRadius);
    }

    public void showText(float x, float y) {
        float volume = calculator.getVolumeTotal() - calculator.getVolume();
        volume = volume < 0 ? 0 : volume;
        fill(0);
        text(
                format.format(GlobalData.getInstance().getDistance()) +
                        " " + getActivity().getString(R.string.cm) + "\n" +
                        format.format(volume) +
                        " " + getActivity().getString(R.string.ml),
                x,
                y
        );
        textSize(textSize);
        noFill();
    }

    /**
     * Get the screen dimension according device for draw purposes
     */
    @Override
    public void settings() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        size(width,height);
        setScreenHeight(height);
        setScreenWidth(width);
        Log.d("DIMENS", "height:" + height + ", width " + width);
        increaseCylinderSize(getScaleFactor());
    }

    public double getScaleFactor() {
        for (int i = 0; i < screenTypes.length; i++)
            if (getScreenWidth() <= screenTypes[i])
                return i + 1.4;
        return 0;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}