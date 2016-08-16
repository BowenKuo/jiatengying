package org.onlineservice.rand.login;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Lillian Wu on 2016/8/16.
 */
public class LocationHelper extends Service implements LocationListener {

    private final static String LOG_TAG = "LocationHelper";
    private LocationManager locationManager;
    private Location location;
    private Context context;
    private double latitude;
    private double longitude;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    private String serviceProvider;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    public LocationHelper(Context context) {
        this.context = context;
        this.init();
    }

    /**********************
     * Public Interface
     *********************/
    public String getServiceProvider() { return this.serviceProvider; }

    public double getLatitude() { return this.latitude; }

    public double getLongitude() { return this.longitude; }

    /**
     * Core
     */
    private void init() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        this.isGPSEnabled     = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER    );
        this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d(LOG_TAG, "Status: GPS=" + this.isGPSEnabled + ", NETWORK=" + this.isNetworkEnabled);

        if (this.isGPSEnabled || this.isNetworkEnabled) {
            this.serviceProvider = locationManager.getBestProvider(new Criteria(), true);
            Log.d(LOG_TAG, "Service Provider: " + this.serviceProvider);

            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(LOG_TAG, "Permisson Denied");

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION)) { }
                else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 12);
                    Log.d(LOG_TAG, "Request Location Permission");
                }
                return;
            }

            locationManager.requestLocationUpdates(this.serviceProvider, MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_BW_UPDATES, this);
            location = locationManager.getLastKnownLocation(serviceProvider);
            this.locate(location);
        }
        else {
            Toast.makeText(this.context, "請開啟 GPS 或網路定位服務", Toast.LENGTH_LONG).show();
            // startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    private void locate(Location location) {
        if (location != null) {
            this.latitude  = location.getLatitude();
            this.longitude = location.getLongitude();
            Log.d(LOG_TAG, "LAT = " + this.latitude + ", LNG = " + this.longitude);
        }
        else {
            Toast.makeText(this.context, "無法取得定位座標", Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.locate(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this.context, "定位已關閉，請開啟 GPS 或網路定位服務", Toast.LENGTH_LONG).show();
    }
}