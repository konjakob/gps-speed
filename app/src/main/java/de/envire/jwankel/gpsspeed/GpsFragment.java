package de.envire.jwankel.gpsspeed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;

/**
 * Created by 10142017 on 28/09/2016.
 */
public class GpsFragment extends Fragment implements LocationListener, GpsStatus.Listener {

    LocationManager locationManager = null;
    private Location lastLoc;
    long minTime = 100;
    long minDistance = 0;
    String provider;
    int GPS_REQUEST_CODE = 1;

    private TaskCallbacks mCallbacks;

    interface TaskCallbacks {
        //void doSpeedCalculation(Location loc);
        void gpsSatInUse(int k);
        void gpsSatVis(int k);
        void setSpeed(float speed, float dT);
        void setGPSStateSearching();
        void setGPSStateFixed();
        void setGPSStateOff();
    }

    void setSpeed(float speed, float dT) {
        if (mCallbacks != null) {
            mCallbacks.setSpeed(speed, dT);
        }
    }
    /*
    void doSpeedCalculation(Location loc) {
        if (mCallbacks != null) {
            mCallbacks.doSpeedCalculation(loc);
        }
    }
*/
    void setGPSStateSearching() {
        if (mCallbacks != null) {
            mCallbacks.setGPSStateSearching();
        }
    }
    void setGPSStateFixed() {
        if (mCallbacks != null) {
            mCallbacks.setGPSStateFixed();
        }
    }
    void setGPSStateOff() {
        if (mCallbacks != null) {
            mCallbacks.setGPSStateOff();
        }
    }

    private void doGpsStuff() {
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(false); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Getting the name of the best provider
        provider = locationManager.getBestProvider(criteria, true);

        try {
            locationManager.addGpsStatusListener(this);
        } catch (SecurityException e) {
        } catch (Exception e){
        }

        try {
            // Getting Current Location
            /*Location location = locationManager.getLastKnownLocation(provider);
            if(location!=null){
                onLocationChanged(location);
            }*/
            locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
        } catch (SecurityException e) {
        } catch (Exception e){
        }

    }

    @Override
    public void onGpsStatusChanged(int event) {

        int iTempCountInView = 0;
        int iTempCountInUse = 0;
        try {
            //LocationManager locationManager = (LocationManager) v.getContext().getSystemService(Context.LOCATION_SERVICE);
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            if(gpsStatus != null) {
                Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                if (satellites != null) {
                    for (GpsSatellite gpsSatellite : satellites) {
                        iTempCountInView++;
                        if (gpsSatellite.usedInFix()) {
                            iTempCountInUse++;
                        }
                    }
                }
            }
        } catch ( Exception e) {

        }

        switch(event)
        {
            case GpsStatus.GPS_EVENT_STARTED:
                setGPSStateSearching();
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                if ( iTempCountInUse > 0) {
                    setGPSStateFixed();
                } else {
                    setGPSStateSearching();
                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                setGPSStateFixed();
                try {
                    // Getting Current Location
                    locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
                } catch (SecurityException e) {
                } catch (Exception e){
                }
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                setGPSStateOff();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null) return;

        if ( lastLoc != null) {
            float dT = location.getTime() - lastLoc.getTime(); // ms
            if (dT > 0) {
                float speed = (float)(location.getSpeed()*3.6);

                if (speed == 0.0) {
                    speed = (float)(3.6*1000 * location.distanceTo(lastLoc) / dT);
                }

                setSpeed(speed, dT);
/*
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
                */
            }
        }
        lastLoc = location;

        //doSpeedCalculation(location);



    }

    /**
     * Fragment initialization.  We way we want to be retained and
     * start our thread.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mCallbacks = (TaskCallbacks) context;
            checkForGps((Activity)context);
        }
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        mCallbacks = (TaskCallbacks) a;
        checkForGps(a);
    }

    private void checkForGps(Activity a) {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        } else {
            doGpsStuff();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == GPS_REQUEST_CODE && resultCode == 0){
            if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                doGpsStuff();
            }else{
                //Users did not switch on the GPS
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.GPS_disabled)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * This is called right before the fragment is detached from its
     * current activity instance.
     */
    @Override
    public void onDetach() {

        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}
