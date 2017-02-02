package de.envire.jwankel.gpsspeed;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by 10142017 on 25/06/2016.
 */
public class DrawView extends View {
    Paint paint = new Paint();

    float x;
    float y;
    float x0;
    float y0;
    float r;

    public DrawView(Context context, AttributeSet attrs) {
        super(context,attrs);

        x = 0;
        y = 0;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        r = (float)(width/2*.85);

        x0 = width/2;
        y0 = height*2/3;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            y0 = height/2;
        } else {
            r = r*8/10;
        }

        setPivotX(x0);
        setPivotY(y0);

        calcPos(0);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(3f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    private void calcPos(float k) {


        float dRadStart = 0;
        float dRadCircle;

        if ( k > 1 ) {
            k = 1;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            dRadStart = (float)Math.PI/4;
        } else {

        }

        dRadCircle = (float)Math.PI + 2*dRadStart;

        x = -r*(float)Math.cos(k* dRadCircle - dRadStart) + x0;
        y = -r*(float)Math.sin(k* dRadCircle - dRadStart) + y0;
    }

    @Override
    public void onDraw(Canvas canvas) {

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3.5f);
        canvas.drawLine((int)x0, (int)y0, (int)x, (int)y, paint);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3f);
        canvas.drawLine((int)x0, (int)y0, (int)x, (int)y, paint);
    }

    public void changeVal(float k) {
        calcPos(k);
    }

}
