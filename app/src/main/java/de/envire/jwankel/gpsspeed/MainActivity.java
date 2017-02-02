package de.envire.jwankel.gpsspeed;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GpsFragment.TaskCallbacks {

    DrawView drawView;
    TextView tv;

    private GpsFragment mTaskFragment;
    public float fMax = 100;
    ImageView ivFixed;
    ImageView ivOff;
    ImageView ivNotFixed;
    DecimalFormat df = new DecimalFormat("##00.0");

    private int UNIT_SPEED = 0;

    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    public void gpsSatInUse(int k) {

    }
    public void gpsSatVis(int k) {

    }

    public void setSpeed(float speed, float dT) {

        if ( UNIT_SPEED == 1 ) {
            // in miles per hour
            speed *= 0.621371;
        }

        float s1 = speed/fMax;

        if (s1 > 1) { s1 = 1; }

        float fRadStart = 0;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            fRadStart = 2*45;
        } else {

        }
        drawView.animate().rotation((180+fRadStart)*s1).setDuration((int)dT).start();
        //drawView.changeVal(speed/100);
        //drawView.invalidate();

        //int i = Integer.valueOf(Math.round(speed));

        tv.setText(df.format(speed));
        tv.bringToFront();

    }

        /*
        if ( lastLoc != null) {
            float dT = location.getTime() - lastLoc.getTime(); // ms
            if (dT > 0) {
                float speed = (float)(location.getSpeed()*3.6);

                if (speed == 0.0) {
                    speed = (float)(3.6*1000 * location.distanceTo(lastLoc) / dT);
                }

                float s1 = speed/fMax;

                if (s1 > 1) { s1 = 1; }

                float fRadStart = 0;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    fRadStart = 2*45;
                } else {

                }
                drawView.animate().rotation((180+fRadStart)*s1).setDuration((int)dT).start();
                //drawView.changeVal(speed/100);
                //drawView.invalidate();

                //int i = Integer.valueOf(Math.round(speed));

                tv.setText(df.format(speed));
                tv.bringToFront();
            }
        }
        lastLoc = location;
        */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = (DrawView) findViewById(R.id.myDrawView1);
        tv = (TextView) findViewById(R.id.textView);
        try {
            tv.bringToFront();
        } catch (NullPointerException e) {
            // nothing we can do
        }
        ivFixed = (ImageView) findViewById(R.id.imageViewFixed);
        ivNotFixed = (ImageView) findViewById(R.id.imageViewNotFixed);
        ivOff = (ImageView) findViewById(R.id.imageViewOff);

        Typeface myTypeface = Typeface.createFromAsset(this.getAssets(),
                "fonts/digital-7.mono.ttf");
        tv.setTypeface(myTypeface);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        tv.setText("00.0");
        tv.measure(0, 0);       //must call measure!

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tv.setX(width/2-tv.getMeasuredWidth()/2);
            tv.setY(height*2/3);

            ivFixed.setX(width / 3);
            ivFixed.setY(height/2);
            ivNotFixed.setX(width / 3);
            ivNotFixed.setY(height/2);
            ivOff.setX(width / 3);
            ivOff.setY(height/2);
        } else {
            tv.setX(width/2-tv.getMeasuredWidth()/2);
            tv.setY(height/3);

            ivFixed.setX(width / 2 - tv.getMeasuredWidth() / 2 -ivFixed.getWidth()-60);
            ivFixed.setY(height  / 3 + ivFixed.getHeight()/2);
            ivNotFixed.setX(width / 2 - tv.getMeasuredWidth() / 2 -ivNotFixed.getWidth()-60);
            ivNotFixed.setY(height / 3+ ivFixed.getHeight()/2);
            ivOff.setX(width / 2 - tv.getMeasuredWidth() / 2 -ivOff.getWidth()-60);
            ivOff.setY(height / 3+ ivFixed.getHeight()/2);
        }

        ivFixed.setVisibility(View.INVISIBLE);
        ivNotFixed.setVisibility(View.INVISIBLE);
/*
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        } else {
            doGpsStuff();
        }
*/
        FragmentManager fm = getFragmentManager();
        mTaskFragment = (GpsFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new GpsFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }

        Log.d("GPSSPEED", "onCreate: " + Locale.getDefault().getISO3Country());
        if ( Locale.getDefault().getISO3Country().equalsIgnoreCase("usa") || Locale.getDefault().getISO3Country().equalsIgnoreCase("mmr") ) {
            UNIT_SPEED = 1;
        }

    }



    public void setGPSStateOff() {
        ivOff.setVisibility(View.VISIBLE);
        ivNotFixed.setVisibility(View.INVISIBLE);
        ivFixed.setVisibility(View.INVISIBLE);
    }
    public void setGPSStateSearching() {
        ivOff.setVisibility(View.INVISIBLE);
        ivNotFixed.setVisibility(View.VISIBLE);
        ivFixed.setVisibility(View.INVISIBLE);
    }
    public void setGPSStateFixed() {
        ivOff.setVisibility(View.INVISIBLE);
        ivNotFixed.setVisibility(View.INVISIBLE);
        ivFixed.setVisibility(View.VISIBLE);
    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onItemClick(MenuItem item)  {
        switch (item.getItemId()) {
            case R.id.item1:
                final EditText txtTripName = new EditText(this);
                txtTripName.setText(String.valueOf(fMax));
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_max_speed_title)
                        .setMessage(R.string.dialog_max_speed_msg)
                        .setView(txtTripName)
                        .setPositiveButton(R.string.dialog_max_speed_set, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String sMaxVal = txtTripName.getText().toString();
                                if ( !sMaxVal.equals("")) {
                                    fMax = Float.parseFloat(sMaxVal);
                                    BackgroundView bv = (BackgroundView) findViewById(R.id.myDrawViewBackground);
                                    try {
                                        bv.updateMaximum(fMax);
                                    } catch (NullPointerException e) {
                                        // nothing we can do here
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_max_speed_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();

                break;
            case R.id.item2:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_speed_title)
                        .setMessage(R.string.dialog_speed_msg)
                        .setPositiveButton(R.string.unit_kmh, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                UNIT_SPEED = 0;
                            }
                        })
                        .setNegativeButton(R.string.uunit_mph, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                UNIT_SPEED = 1;
                            }
                        })
                        .show();

                break;
            default:

        }
    }



}
