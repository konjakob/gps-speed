package de.envire.jwankel.gpsspeed;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by 10142017 on 30/06/2016.
 */
public class BackgroundCircle extends View {
    Paint paint = new Paint();
    Paint p1 = new Paint();

    float x0;
    float y0;
    float r;

    public BackgroundCircle(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;


        r = (float)(width / 2 * 0.95);
        x0 = width / 2;
        y0 = height *2/3;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            y0 = height/2;
        } else {
            r = r*8/10;
        }

        p1.setColor(Color.BLACK);
        p1.setStrokeWidth(1);
        p1.setStyle(Paint.Style.FILL_AND_STROKE);
        p1.setShader(new RadialGradient(x0, y0,
                r, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.MIRROR));

    }
    @Override
    public void onDraw(Canvas canvas) {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            canvas.drawCircle( x0,  y0,  r + 2, paint);
            paint.setColor(Color.DKGRAY);
            canvas.drawCircle( x0,  y0,  r, paint);

            canvas.drawCircle( x0,  y0, r, p1);

        } else {
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);

            RectF rectf = new RectF ((x0-r-2), (y0-r-2),(x0+r+2), (y0+r+4));
            canvas.drawArc(rectf, 4, -188, true, paint);
            paint.setColor(Color.DKGRAY);
            RectF rectf2 = new RectF ((x0-r), (y0-r),(x0+r), (y0+r));
            canvas.drawArc(rectf2, 4, -188, true, paint);
            canvas.drawArc(rectf2, 4, -188, true, p1);

        }

    }

}
