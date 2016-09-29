package org.onlineservice.rand.login;

import android.database.MatrixCursor;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

// Taiwan Center 23.627281, 120.940634
// radius 200km
public class TaskLocationSearch extends AsyncTask<Void, Void, Void> {
    private String name;
    private Location start;
    protected MatrixCursor mc;
    private ArrayList<JsonLocation.Result> results;
    private JsonLocation jsonLocation;

    public TaskLocationSearch(String name, Location location) {
        this.name = name;
        this.start = location;
        this.mc = new MatrixCursor(new String[] { "_id", "name", "desc", "dist"});
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String s = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?language=zh-TW"
                +"&location="+start.getLatitude()+","+start.getLongitude()
                +"&keyword="+name
                +"&rankby=distance"
                +"&key=AIzaSyDMp2ee5C1i7XfnKuiVmNCrbCg667TBOzI";
        Log.d("LocationSearchAPI : ", s);
        jsonLocation = new Gson().fromJson(new InputStreamReader(downloadLocations(s)), JsonLocation.class);
        results = jsonLocation.getResults();
        return null;
    }

    protected void onResult() {};
    public ArrayList<JsonLocation.Result> getResult() { return results; };

    @Override
    protected void onPostExecute(Void v) {
        int i;
        for (i=0; i< jsonLocation.getResults().size(); ++i) {
            if (i<10) {
                Location end = new Location(jsonLocation.getResults().get(i).getName());
                end.setLatitude(jsonLocation.getResults().get(i).getLocation().lat);
                end.setLongitude(jsonLocation.getResults().get(i).getLocation().lng);
                mc.addRow(new Object[]{i, jsonLocation.getResults().get(i).getName(), jsonLocation.getResults().get(i).getVicinity(), calDistance(end)});
            }
            else break;
        }
        onResult();
    }

    private InputStream downloadLocations(String url) {
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

    private String calDistance(Location end) {
        double theta = start.getLongitude() - end.getLongitude();
        double dist = Math.sin(deg2rad(start.getLatitude())) * Math.sin(deg2rad(end.getLatitude())) + Math.cos(deg2rad(start.getLatitude())) * Math.cos(deg2rad(end.getLatitude())) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(dist);
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
