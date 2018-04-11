package itl.angelo.smartcistern.fragments;

import android.app.Fragment;
import android.graphics.*;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.text.DecimalFormat;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.util.connection.GlobalData;
import itl.angelo.smartcistern.util.volumecomputer.VolumeCalculator;

/**
 * Created by Angelo on 20/03/2017.
 */

public class MonitorFragment extends Fragment {
    public static final String DECIMAL_FORMAT = "0.00";
    private int screenHeight, screenWidth;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private float y1a;
    private float y2a;
    private int strokeWidth = 5;
    private int circleIndicatorRadius = 20;
    private int textSize = 25;
    private int textLineSeparation = 30;
    private View mView;
    private VolumeCalculator mCalculator;
    private DecimalFormat mFormat;
    private DrawThread mDrawThread;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_monitor, container, false);
        mImageView = (ImageView) mView.findViewById(R.id.graphic);
        getSizes();
        mBitmap = Bitmap.createBitmap(getScreenWidth(), getScreenHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCalculator = new VolumeCalculator(getActivity());
        mFormat = new DecimalFormat(DECIMAL_FORMAT);
        determineSize();
        draw();
        mImageView.setImageBitmap(mBitmap);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDrawThread = new DrawThread();
        mDrawThread.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDrawThread != null) mDrawThread.setKeepRunning(false);
    }

    /**
     * Main method for draw the cylinder and the water according sensor values
     */
    public void draw() {
        mCanvas.drawColor(Color.WHITE);
        float limit = mCalculator.getValueForGraphic(y1, y1a, GlobalData.getInstance().getDistance());
        drawLine(x1, y1 * 2, x1, y2a - y1, strokeWidth);
        drawOval(x1, y1a, x2, y2a, strokeWidth);
        drawLine(x2, y1 * 2, x2, y2a - y1, strokeWidth);
        fillWithWater(limit);
        drawOval(x1, y1, x2, y2, strokeWidth);
        drawText(mFormat.format(mCalculator.getPercentage()) + " %",
                (x1 + x2) / 2,
                y1 - 30
        );
        try {
            getActivity().runOnUiThread(() -> mImageView.setImageBitmap(mBitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param limit the limit to draw water filling
     */
    private void fillWithWater(float limit) {
        float y1Start = y1a;
        float y2Start = y2a;
        Paint paint = new Paint();
        paint.setColor(Color.rgb(150, 255, 255));
        paint.setStyle(Paint.Style.FILL);
        while (y1Start > limit) {
            drawOval(x1, y1Start, x2, y2Start, paint, strokeWidth);
            y1Start -= 1;
            y2Start -= 1;
        }
        paint.setColor(Color.rgb(50, 240, 240));
        drawOval(x1, y1Start, x2, y2Start, paint, strokeWidth);
        drawOval(x1, y1Start, x2, y2Start, strokeWidth);
        paint.setColor(Color.BLACK);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCanvas.drawCircle(x2, y1Start + y1, circleIndicatorRadius, paint);
        drawLine(x2, y1Start + y1, x2 + 30, y1Start + y1, strokeWidth);
        drawText(mFormat.format(GlobalData.getInstance().getDistance()) + " cm",
                x2 + textLineSeparation, y1Start + y1);
        drawText(mFormat.format(mCalculator.getVolume()) + " ml", x2 + textLineSeparation,
                y1Start + y1 + 50);
    }

    /**
     * @param y the y position of text
     */
    public void drawText(String text, float x, float y) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        mCanvas.drawText(text, x, y, paint);
    }


    public void determineSize() {
        x1 = getScreenWidth() / 5;
        y1 = getScreenHeight() / 8;
        x2 = x1 * 1.5f + getScreenWidth() / 2;
        y2 = y1 * 3;
        y1a = getScreenHeight() - y2;
        y2a = getScreenHeight() - y1;
    }

    public void drawOval(float left, float top, float right, float bottom, int strokeWidth) {
        //left,top,right,bottom
        //left from 0 to 500
        //top from 0 to 500
        //right from 500 to 0
        //bottom from 500 to 0
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        RectF rect = new RectF(left, top, right, bottom);
        mCanvas.drawOval(rect, paint);
    }

    public void drawOval(float left, float top, float right, float bottom, Paint paint, int strokeWidth) {
        //left,top,right,bottom
        //left from 0 to 500
        //top from 0 to 500
        //right from 500 to 0
        //bottom from 500 to 0
        paint.setStrokeWidth(strokeWidth);
        RectF rect = new RectF(left, top, right, bottom);
        mCanvas.drawOval(rect, paint);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        mCanvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void getSizes() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        setScreenHeight(height);
        setScreenWidth(width);
        Log.d("DIMENS", "height:" + height + ", width " + width);
        //increaseCylinderSize(getScaleFactor());
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

    private class DrawThread extends Thread {

        private boolean keepRunning = true;

        public boolean isKeepRunning() {
            return keepRunning;
        }

        public void setKeepRunning(boolean keepRunning) {
            this.keepRunning = keepRunning;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            int counter = 0;
            while (isKeepRunning()) {
                draw();
                counter++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}