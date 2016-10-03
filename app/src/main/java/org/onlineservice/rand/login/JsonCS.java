package org.onlineservice.rand.login;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

// Convenience Store
public class JsonCS {
    @SerializedName("html_attributions")
    ArrayList<String> htmlAttributions;

    @SerializedName("next_page_token")
    String nextPageToken;
    public String getNextPageToken() { return nextPageToken; }

    @SerializedName("results")
    ArrayList<Result> results;

    @SerializedName("status")
    String status;

    public ArrayList<Result> getResults() {
        return results;
    }

    public class Result {
        @SerializedName("geometry")
        Geometry geometry;
        @SerializedName("icon")
        String icon;
        @SerializedName("id")
        String id;
        @SerializedName("name")
        String name;
        @SerializedName("opening_hours")
        OpeningHours openingHours;
        @SerializedName("place_id")
        String place_id;
        @SerializedName("reference")
        String reference;
        @SerializedName("scope")
        String scope;
        @SerializedName("types")
        ArrayList<String> types;
        @SerializedName("vicinity")
        String vicinity;

        public Location getLocation() { return geometry.getLocation(); }
        public String getName() { return name; }
        public String getTypes() { return types.get(0); }
    }

    public class Geometry {
        @SerializedName("location")
        Location location;
        @SerializedName("viewport")
        Viewport viewport;

        public Location getLocation() { return location; }
    }

    public class Location {
        @SerializedName("lat")
        double lat;
        @SerializedName("lng")
        double lng;
    }

    public class Viewport {
        @SerializedName("northeast")
        Northeast northeast;
        @SerializedName("southeast")
        Southeast southeast;
    }

    public class Northeast {
        @SerializedName("lat")
        double lat;
        @SerializedName("lng")
        double lng;
    }

    public class Southeast {
        @SerializedName("lat")
        double lat;
        @SerializedName("lng")
        double lng;
    }

    public class OpeningHours {
        @SerializedName("open_now")
        boolean openNow;
        @SerializedName("weekday_text")
        ArrayList<String> weekdayTexts;
    }
}