package de.envire.jwankel.gpsspeed;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by 10142017 on 25/06/2016.
 */
public class BackgroundView extends View {
    Paint paint = new Paint();

    float x0;
    float y0;
    float r;
    float dMax;

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        paint.setColor(Color.WHITE);
        paint.setTextSize(20);

        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/digital-7.ttf");
        paint.setTypeface(myTypeface);

        r = (float)( width / 2*.95);
        dMax = 100;
        x0 = width / 2;
        y0 = height *2/3;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            y0 = height/2;
        } else {
            r = r*8/10;
        }
    }

    public void updateMaximum(float fMax) {
        this.dMax = fMax;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {

        double x;
        double y;
        double xs;
        double ys;

        int iSegments = 5;
        int iSegmentsDetail = iSegments*4;

        double ratio;
        String sTmp;

        double dRadStart = 0;
        double dRadCircle;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            dRadStart = Math.PI/4;

        } else {

        }

        dRadCircle = Math.PI + 2*dRadStart;

        for ( int i = 0; i <= iSegments; i++) {

            ratio = (double)i/(double)iSegments;

            x = -r * 0.8 * Math.cos(ratio * dRadCircle - dRadStart) + x0;
            y = -r * 0.8 * Math.sin(ratio * dRadCircle - dRadStart) + y0;

            sTmp = String.valueOf((int)(ratio * dMax));
            Rect bounds = new Rect();
            paint.getTextBounds(sTmp, 0, 1, bounds);

            canvas.drawText(sTmp, (int) (x - paint.measureText(sTmp) / 2), (int) (y + bounds.height() / 2), paint);

        }

        for ( int i = 0; i <= iSegmentsDetail; i++) {
            ratio = (double)i/(double)iSegmentsDetail;

            x = r * Math.cos(ratio * dRadCircle - dRadStart) + x0;
            y = -r * Math.sin(ratio * dRadCircle - dRadStart) + y0;

            xs = r* 0.9 * Math.cos(ratio * dRadCircle - dRadStart) + x0;
            ys = -r* 0.9 * Math.sin(ratio * dRadCircle - dRadStart) + y0;

            canvas.drawLine((int)x,(int)y,(int)xs,(int)ys, paint);
        }


    }

    public void changeVal(float r) {
        this.dMax = r;
    }
}