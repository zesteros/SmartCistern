package itl.angelo.smartcistern.fragments;

import android.app.Fragment;
import android.graphics.*;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.util.connection.GlobalData;
import itl.angelo.smartcistern.util.volumecomputer.VolumeCalculator;

/**
 * Created by Angelo on 20/03/2017.
 */

public class MonitorFragment extends Fragment {

    /*
        * The height and width of screen device
        * */
    private int screenHeight, screenWidth;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float mMarginTop = 720;
    private float mMarginBottom = 500;
    private int strokeWidth = 5;
    private View mView;
    private DrawThread mDrawThread;
    private VolumeCalculator mCalculator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_monitor, container, false);

        mImageView = (ImageView) mView.findViewById(R.id.graphic);
        getSizes();
        mBitmap = Bitmap.createBitmap(getScreenWidth(), getScreenHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCalculator = new VolumeCalculator(getActivity());
        //mCanvas.drawCircle(250, 250, 100, paint);
        mDrawThread = new DrawThread();
        mDrawThread.start();

        return mView;
    }

    public void draw() {
        //mCanvas.drawColor(Color.WHITE);
        float limit = mCalculator.getValueForGraphic(
                mMarginTop,
                mBitmap.getHeight() - mMarginBottom,
                GlobalData.getInstance().getDistance()
        );
        drawOval(
                mBitmap.getHeight() / 2,
                mBitmap.getWidth() / 2 + mMarginTop,
                250,
                mBitmap.getHeight() - mMarginBottom,
                strokeWidth);
        fillWithWater(limit);
        drawOval(
                mBitmap.getHeight() / 2,//left
                mBitmap.getWidth() / 2,//top
                250,//right
                250,//bottom
                strokeWidth);
        try {
            getActivity().runOnUiThread(() -> mImageView.setImageBitmap(mBitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        drawLines(0, 0, strokeWidth);
        redraw();
        mCanvas.drawColor(Color.WHITE);
    }

    private void fillWithWater(float limit) {
        float top = mMarginTop;
        float bottom = mMarginBottom;
        float increase = 1;
        //720 is the top
        //1892 is the bottom
        //-491 final top
        //700 final bottom
        while (mBitmap.getHeight() - bottom > limit) {
            drawWater(

                    mBitmap.getHeight() / 2,//left
                    mBitmap.getWidth() / 2 + top,//top-700
                    250,//right
                    mBitmap.getHeight() - bottom//bottom-(-500)
            );
            bottom += increase;
            top -= increase;
        }
        Paint paint = new Paint();
        paint.setColor(Color.rgb(50, 240, 240));
        paint.setStyle(Paint.Style.FILL);
        RectF rect = new RectF(
                mBitmap.getHeight() / 2,
                mBitmap.getWidth() / 2 + top,
                250,
                mBitmap.getHeight() - bottom);
        mCanvas.drawOval(rect, paint);
        drawOval(
                mBitmap.getHeight() / 2,//left
                mBitmap.getWidth() / 2 + top,//top-700
                250,//right
                mBitmap.getHeight() - bottom,//bottom-(-500)
                strokeWidth);
    }

    private void redraw() {
        try {
            getActivity().runOnUiThread(() -> mView.invalidate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawWater(float x, float y, float width, float height) {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(150, 255, 255));
        paint.setStyle(Paint.Style.FILL);
        RectF rect = new RectF(x, y, width, height);
        mCanvas.drawOval(rect, paint);
    }

    private void drawLines(float x, float y, float strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        mCanvas.drawLine(
                250,//left
                mBitmap.getWidth() / 2 - 250,//top
                250,
                mBitmap.getWidth() / 2 + 950,
                paint
        );
        mCanvas.drawLine(
                mBitmap.getHeight() / 2,//left
                mBitmap.getWidth() / 2 - 250,//top
                mBitmap.getHeight() / 2,
                mBitmap.getWidth() / 2 + 950,
                paint
        );
    }

    public void drawOval(float x, float y, float width, float height, int strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        RectF rect = new RectF(x, y, width, height);
        mCanvas.drawOval(rect, paint);
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
            while (isKeepRunning()) {
                draw();
            }
        }
    }
}
