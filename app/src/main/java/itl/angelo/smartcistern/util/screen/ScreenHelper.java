package itl.angelo.smartcistern.util.screen;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;

/**
 * Created by Angelo on 01/04/2017.
 */

public class ScreenHelper {

    private Context mContext;
    private Display display;
    private Point size;
    private int width;
    private int height;

    private int[] screenTypes = {
            320,
            480,
            768,
            1080,
            1440
    };

    public ScreenHelper(Context context) {
        this.mContext = context;

    }

    public int getScaleFactor() {
        for (int i = 0; i < screenTypes.length; i++)
            if(getWidth() <= screenTypes[i])
                return i+1;
        return 0;
    }

    public void getScreenValues() {
        display = ((AppCompatActivity) mContext).getWindowManager().getDefaultDisplay();
        setSize(new Point());
        display.getSize(getSize());
        setWidth(getSize().x);
        setHeight(getSize().y);
        Log.d("sizes",getSize().x+", "+getSize().y);
    }

    public Point getSize() {
        return size;
    }

    public void setSize(Point size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
