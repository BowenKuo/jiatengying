package org.onlineservice.rand.login;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Lillian Wu on 2016/7/20.
 */
public class Nevigation extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context thisActivity;
    private Nevigation nevigation = this;
    private GoogleMap mMap;
    private MapView mapView;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    private LatLng lastLatLng;
    private TextToSpeech tts;
    private int locationChangedFlag = LOCATION_FIRST_UPDATE;
    private ArrayList<JsonLocation.Result> results;
    private JsonLocation.Result selectedLocation;
    private JsonDirection jsonDirectionResults;
    private DirectionSpeaker directionSpeaker;
    private LocationRequest locationRequest;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        thisActivity = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_nevigation, container, false);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();// needed to get the map to display immediately

        MapsInitializer.initialize(thisActivity);

        // Update user location & set up all API will be used
        mGoogleApiClient = new GoogleApiClient
                .Builder(thisActivity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage((FragmentActivity) thisActivity, this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5);

        // Get the SearchView and set the searchable configuration
        final SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setBackgroundColor(Color.WHITE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String input) {
                mMap.clear();
//                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, nevigation);
                new TaskLocationSearch(input, lastLocation) {
                    @Override
                    protected void onResult() {
                        searchView.setSuggestionsAdapter(new CursorAdapter(thisActivity, super.mc, Cursor.FIELD_TYPE_STRING) {
                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                                return LayoutInflater.from(thisActivity).inflate(R.layout.suggestion_list, viewGroup, false);
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                TextView locName = (TextView) view.findViewById(R.id.locName);
                                String s = cursor.getString(cursor.getColumnIndexOrThrow("name")) + " - " + cursor.getString(cursor.getColumnIndexOrThrow("dist")) + " KM";
                                locName.setText(s);
                                TextView locDesc = (TextView) view.findViewById(R.id.locDesc);
                                locDesc.setText(cursor.getString(cursor.getColumnIndexOrThrow("desc")));
                            }
                        });
                        results = super.getResult();
//                        Toast.makeText(thisActivity, "TEST", Toast.LENGTH_LONG).show();
                    }
                }.execute();
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                selectedLocation = results.get(i);
                searchView.setQuery(selectedLocation.getName(), false);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(selectedLocation.getLocation().lat, selectedLocation.getLocation().lng), 17));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(selectedLocation.getLocation().lat, selectedLocation.getLocation().lng))
                        .title(selectedLocation.getName())
                        .snippet(selectedLocation.getVicinity())).showInfoWindow();

                InputMethodManager imm = (InputMethodManager)thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                return true;
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                setUpMap();
            }
        });

        // Initialize Google Map
//        FragmentActivity activity = (FragmentActivity) getActivity();
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

//        return inflater.inflate(R.layout.activity_nevigation, container, false);
        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {
        // Check permission
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_ACCESS_FINE_LOCATION);
        }
        mMap.setPadding(0, 130, 0, 0);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                startNavigation();
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
//                tts.speak("Fuck", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    // Dialog to REQUEST Permission
    private static final int PERMISSIONS_ACCESS_FINE_LOCATION = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    } catch (SecurityException e) {
                        Log.d("RequestPermissionResult", "SecurityException");
                    }
                } else {
                    Toast.makeText(thisActivity, "Permissions Deny !!", Toast.LENGTH_LONG).show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            // SecurityException
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, nevigation);

        } catch (SecurityException | NullPointerException e) {
            Log.d("onConnected", "Exception!");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private static final int LOCATION_UPDATING = 0;
    private static final int LOCATION_STOP = 1;
    private static final int LOCATION_FIRST_UPDATE = 2;

    @Override
    public void onLocationChanged(Location location) {
        switch (locationChangedFlag) {
            case LOCATION_UPDATING: {
                try {
                    directionSpeaker.check(location);
                    lastLocation = location;
                    lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                    Log.d("Location Changing", "Location Updating");
                } catch (SecurityException e) {
                    Log.d("onLocationChanged", "SecurityException");
                }
                break;
            }
            case LOCATION_FIRST_UPDATE: {
                try {
                    lastLocation = location;
                    lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    Log.d("Location Changed", "First Location Changing");
                } catch (SecurityException e) {
                    Log.d("onLocationChanged", "SecurityException");
                }
                locationChangedFlag = LOCATION_STOP;
                break;
            }
        }
    }

    private void startNavigation() {
        new TaskDirectionDownload(lastLatLng, selectedLocation.getPlaceId()) {
            @Override
            public void onDirectionDrown(JsonDirection jm, PolylineOptions plo) {
                plo.color(Color.rgb(106,219,217));
                plo.width(20);
                mMap.addPolyline(plo);
                jsonDirectionResults = jm;
                directionSpeaker = new DirectionSpeaker(jsonDirectionResults, thisActivity);
                locationChangedFlag = LOCATION_UPDATING;
                if (ActivityCompat.checkSelfPermission(thisActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, nevigation);
            }
        }.execute();
        new TaskNearByCS(lastLatLng, new LatLng(selectedLocation.getLocation().lat, selectedLocation.getLocation().lng)) {
            @Override
            protected void onPostExecute(JsonCS jsonCS) {
                int i;
                for (i = 0; i < jsonCS.getResults().size(); ++i) {
                    if(jsonCS.getResults().get(i).getTypes().equals("gas_station")) {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(jsonCS.getResults().get(i).getLocation().lat, jsonCS.getResults().get(i).getLocation().lng))
                                .title(jsonCS.getResults().get(i).getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                    else {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(jsonCS.getResults().get(i).getLocation().lat, jsonCS.getResults().get(i).getLocation().lng))
                                .title(jsonCS.getResults().get(i).getName()));
                    }
                }
            }
        }.execute();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng));
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(lastLatLng, 17, 67.5, )));
    }
}
