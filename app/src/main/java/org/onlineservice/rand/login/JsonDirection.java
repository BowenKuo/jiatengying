package org.onlineservice.rand.login;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class JsonDirection {
    @SerializedName("goecoded_waypoints")
    ArrayList<GeocodedWayPoints> gws;
    @SerializedName("routes")
    ArrayList<Route> routes;
    @SerializedName("status")
    String status;

    public ArrayList<Route> getRoutes () { return routes; }
    public String getStatus() { return status; }

    public class GeocodedWayPoints {
        String geocoderStatus;
        String placeId;
        ArrayList<Type> types;
    }

    public class Route {
        Bound bound;
        @SerializedName("copyrights")
        String copyRights;

        @SerializedName("legs")
        ArrayList<Leg> legs;

        @SerializedName("overview_polyline")
        OverViewPolyLine ovpl;
        String summary;
        ArrayList<Warning> warnings;
        ArrayList<WayPointOrder> wayPointOrders;

        public String getCopyRights() { return copyRights; }
        public Bound getBounds() { return bound; }
        public ArrayList<Leg> getLegs() { return legs; }
        public OverViewPolyLine getOverViewPolyLine() { return ovpl; }
    }

    public class Bound {
        Northeast northeast;
        Southwest southwest;
    }

    public class Northeast {
        double lat;
        double lng;
    }

    public class Southwest {
        double lat;
        double lng;
    }

    public class Warning {
        String w;
    }

    public class WayPointOrder {
        String wpo;
    }

    public class OverViewPolyLine {
        public String points;

        public List<LatLng> getDecodedPolyLinePoints() { return PolyUtil.decode(points);}
    }

    public class Leg {
        String startAddress;
        String endAddress;
        StartLocation startLocation;
        EndLocation endLocation;

        Distance distance;
        Duration duration;

        @SerializedName("steps")
        ArrayList<Step> steps;
        ArrayList<TrafficSpeedEntry> tse;
        ArrayList<ViaWayPoint> vwp;

        public ArrayList<Step> getSteps() { return steps; }
    }

    public class TrafficSpeedEntry {
        String tse;
    }

    public class ViaWayPoint {
        String vwp;
    }

    public class Distance {
        String text;
        double mile;
    }

    public class Duration {
        String minute;
        double second;
    }

    public class StartLocation {
        double lat;
        double lng;
    }

    public class EndLocation {
        double lat;
        double lng;
    }

    public class Step {
        Distance distance;
        Duration duration;
        @SerializedName("start_location")
        StartLocation startLocation;
        @SerializedName("end_location")
        EndLocation endLocation;
        PolyLine polyLine;

        @SerializedName("html_instructions")
        String htmlIns;

        String maneuver;
        String travelMode;

        public String getHtmlInstructions() { return htmlIns; }
    }

    public class PolyLine {
        String point;
    }

    public class Type {
        String t;
        public String getType() { return t; }
    }
}