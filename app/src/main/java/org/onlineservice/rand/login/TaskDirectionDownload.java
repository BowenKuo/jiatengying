package org.onlineservice.rand.login;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TaskDirectionDownload extends AsyncTask<String, Void, JsonDirection> {
    LatLng last;
    String placeId;
    String url;

    public TaskDirectionDownload(LatLng l, String p){
        last = l;
        placeId = p;
        // API + origin(Attitude) + destination(PlaceID) + KEY
        url = "https://maps.googleapis.com/maps/api/directions/json?language=zh-TW"
                + "&origin="+last.latitude+","+last.longitude
                + "&destination=place_id:"+placeId
                + "&key=AIzaSyDMp2ee5C1i7XfnKuiVmNCrbCg667TBOzI";
    }

    @Override
    protected JsonDirection doInBackground(String... urls) {
        return new Gson().fromJson(new InputStreamReader(downloadDirection()), JsonDirection.class); }

    @Override
    protected void onPostExecute(JsonDirection jsonDirection) {
        PolylineOptions plo = new PolylineOptions();
        plo.addAll(jsonDirection.getRoutes().get(0).getOverViewPolyLine().getDecodedPolyLinePoints());
        Log.d("Start Address", jsonDirection.getRoutes().get(0).getCopyRights());
        onDirectionDrown(jsonDirection, plo);
    }

    public void onDirectionDrown(JsonDirection jm, PolylineOptions plo) { }

    private InputStream downloadDirection() {
        Log.d("downloadDirection", url);
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
}