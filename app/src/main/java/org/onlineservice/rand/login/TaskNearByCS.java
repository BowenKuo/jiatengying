package org.onlineservice.rand.login;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TaskNearByCS extends AsyncTask<String, Void, JsonCS> {
    LatLng start;
    LatLng end;

    public TaskNearByCS(LatLng s, LatLng e) {
        start = s; end = e;
    }

    @Override
    protected JsonCS doInBackground(String... strings) {
        LatLng mid = new LatLng((start.latitude+end.latitude)/2, (start.longitude+end.longitude)/2);
        double half = calDistance()*1000/2;
        String s = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?language=zh-TW"
                +"&location="+mid.latitude+","+mid.longitude
                +"&radius="+half
                +"&types=convenience_store|gas_station"
                +"&key=AIzaSyDMp2ee5C1i7XfnKuiVmNCrbCg667TBOzI";
        Log.d("NearStore", s);
        JsonCS jsonCS = new Gson().fromJson(new InputStreamReader(downloadStores(s)), JsonCS.class);
        String token = jsonCS.getNextPageToken();

        while(token!=null) {
            String ss = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                    +"&pagetoken="+token
                    +"&key=AIzaSyDMp2ee5C1i7XfnKuiVmNCrbCg667TBOzI";
            JsonCS j = new Gson().fromJson(new InputStreamReader(downloadStores(ss)), JsonCS.class);
            token = j.getNextPageToken();
            jsonCS.getResults().addAll(j.getResults());
        }

        return jsonCS;
    }

    @Override
    protected void onPostExecute(JsonCS jsonCS) { }

    private InputStream downloadStores(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream(); }
        catch (MalformedURLException e) { Log.d("downloadDirection", "MalformedURLException"); }
        catch (IOException e) { Log.d("downloadDirection", "IOException"); }
        return null;
    }

    private double calDistance() {
        double theta = start.longitude - end.longitude;
        double dist = Math.sin(deg2rad(start.latitude)) * Math.sin(deg2rad(end.latitude)) + Math.cos(deg2rad(start.latitude)) * Math.cos(deg2rad(end.latitude)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        return (dist);
    }
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians     ::*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::    This function converts radians to decimal degrees   ::*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
