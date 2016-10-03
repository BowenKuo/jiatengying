package org.onlineservice.rand.login;

import android.content.Context;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Locale;

public class DirectionSpeaker {
    private ArrayList<JsonDirection.Step> steps;
    private JsonDirection.Step current;
    private Context context;
    private TextToSpeech tts;
    private int i=0;

    public DirectionSpeaker(JsonDirection jsonDirection, Context context ){
        steps = jsonDirection.getRoutes().get(0).getLegs().get(0).getSteps();
        current = steps.get(i++);
        this.context = context;
        if( tts == null ) {
            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int arg0) {
                    if (arg0 == TextToSpeech.SUCCESS) {
                        Locale l = Locale.TAIWAN;
                        if (tts.isLanguageAvailable(l) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                            tts.setLanguage(l);
                        }
                    }
                }
            });
        }
    }

    public void check(Location start){
        Location end = new Location("end");
        end.setLatitude(current.startLocation.lat);
        end.setLongitude(current.startLocation.lng);
        double dis = calDistance(start, end);
        Log.d("Start", String.valueOf(start.getLatitude())+", "+String.valueOf(start.getLongitude()));
        Log.d("end", String.valueOf(end.getLatitude())+", "+String.valueOf(end.getLongitude()));
        Log.d("Distance", String.valueOf(dis));
        if( dis<100 ) {
            tts.speak(Jsoup.parse(current.getHtmlInstructions()).text(), TextToSpeech.QUEUE_FLUSH, null);
            Log.d("Instruction", Jsoup.parse(current.getHtmlInstructions()).text());
            if( i<steps.size() )
                current = steps.get(i++);
        }
    }

    private double calDistance(Location start, Location end) {
        double theta = start.getLongitude() - end.getLongitude();
        double dist = Math.sin(deg2rad(start.getLatitude())) * Math.sin(deg2rad(end.getLatitude())) + Math.cos(deg2rad(start.getLatitude())) * Math.cos(deg2rad(end.getLatitude())) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        dist = dist *1000;
        return dist;
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