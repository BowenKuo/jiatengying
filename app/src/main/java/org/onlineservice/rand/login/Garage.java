package org.onlineservice.rand.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lillian Wu on 2016/7/20.
 */
public class Garage extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private final static String LOG_TAG = "Garage";
    private ListView listView;
    private TextView serviceInfo;
    private SwipeRefreshLayout swipeLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_garage, container, false);

        serviceInfo = (TextView) view.findViewById(R.id.textView );
        listView    = (ListView) view.findViewById(R.id.listView );

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(this);

        // 改下拉更新的圈圈的顏色
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.btn_login_bg));

        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
                new AsyncTaskHttpGET().execute();
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        new AsyncTaskHttpGET().execute();
    }

    private class AsyncTaskHttpGET extends AsyncTask<Void, Void, String> {

        private final static String LOG_TAG = "AsyncTaskHttpGET";
        private HashMap<String, String> params = new HashMap<String, String>();
        private NetworkHelper netHelper;
        private LocationHelper locationHelper = new LocationHelper(getActivity());
        private ArrayList<HashMap<String, String>> responseList = new ArrayList<HashMap<String, String>>();;

        private String serviceProvider;
        private double latitude;
        private double longitude;

        public AsyncTaskHttpGET() { }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            this.serviceProvider = this.locationHelper.getServiceProvider();
            this.latitude        = this.locationHelper.getLatitude();
            this.longitude       = this.locationHelper.getLongitude();

            this.params.put("latitude" , "" + this.latitude );
            this.params.put("longitude", "" + this.longitude);

            serviceInfo.setText("Locate via " + this.serviceProvider + ": (" + this.latitude + ", " + this.longitude + ")");
            Log.d(LOG_TAG, "provider: " + this.serviceProvider + ", " + "coordinate: (" + this.latitude + ", " + this.longitude + ")");
        }

        @Override
        protected String doInBackground(Void... voids) {
            netHelper = new NetworkHelper();
            String response = "";

            if (!this.params.get("latitude").equals("0.0") && !this.params.get("longitude").equals("0.0")) {
                // Parse JSON
                try {
                    netHelper.sendSensorData(this.params);
                    response = netHelper.getGETResponse();

                    if (response != "") {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("ok")) {

                            JSONArray garageData = jsonObject.getJSONArray("data");

                            for (int index = 0; index < garageData.length(); index++) {
                                JSONObject sub = garageData.getJSONObject(index);

                                String name = sub.getString("NAME");
                                String address = sub.getString("ADDRESS");
                                String phone = sub.getString("PHONE");
                                String latitude = sub.getString("LAT");
                                String longitude = sub.getString("LNG");
                                String distance = sub.getString("DIST_IN_KM");

                                HashMap<String, String> garage = new HashMap<>();
                                garage.put("NAME", name);
                                garage.put("ADDRESS", address);
                                garage.put("PHONE", phone);
                                garage.put("LAT", latitude);
                                garage.put("LNG", longitude);
                                garage.put("DIST", distance);

                                responseList.add(garage);
                            }
                        } else {
                            Log.e(LOG_TAG, "Data Format Error");
                            return null;
                        }
                    }
                    else {
                        Log.e(LOG_TAG, "AsyncTaskHttpGET: No response");
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "AsyncTaskHttpGET: JSONE: " + e.getMessage());
                    Log.e(LOG_TAG, "AsyncTaskHttpGET: Response: " + response);
                }
            }
            else {
                Log.e(LOG_TAG, "AsyncTaskHttpGET: No coordinate");
            }
            return response;
        }

        @Override
        protected void onPostExecute(String content) {
            super.onPostExecute(content);
            update(responseList);
        }
    }

    private void update(ArrayList<HashMap<String, String>> list_map) {

        ListAdapter mAdapter = new GarageListAdapter(getActivity(), list_map);
        listView.setAdapter(mAdapter);

        if (list_map.size() == 0) {
            Toast.makeText(getActivity(), "無法取得資料，請稍候再試", Toast.LENGTH_LONG).show();
        }

        swipeLayout.setRefreshing(false);
    }


}
