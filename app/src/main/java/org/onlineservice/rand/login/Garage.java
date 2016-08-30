package org.onlineservice.rand.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class Garage extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final static String LOG_TAG = "Garage";
    private ListView listView;
    private TextView serviceInfo;
    private SwipeRefreshLayout swipeLayout;

    private NetworkHelper  networkHelper;
    private LocationHelper locationHelper;

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
                loadUI();
            }
        });

        return view;
    }

    private void loadUI() {
        HashMap<String, String> params = new HashMap<String, String>();

        networkHelper  = new NetworkHelper();
        locationHelper = new LocationHelper(getActivity());

        String provider  = locationHelper.getServiceProvider();
        double latitude  = locationHelper.getLatitude();
        double longitude = locationHelper.getLongitude();

        if (latitude != 0.0 && longitude != 0.0) {
            serviceInfo.setText("Locate via " + provider + ": (" + latitude + ", " + longitude + ")");
            Log.d(LOG_TAG, "provider: " + provider + ", " + "coordinate: (" + latitude + ", " + longitude + ")");

            params.put("latitude" , "" + latitude );
            params.put("longitude", "" + longitude);

            networkHelper.sendCoordinateData(params);

            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String response = networkHelper.getNearbyGarage();

                                if (response != null) {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if (jsonObject.getString("status").equals("ok") && jsonObject.getJSONArray("data").length() > 0) {
                                        JSONArray data_map = jsonObject.getJSONArray("data");

                                        final ArrayList<HashMap<String, String>> responseList = new ArrayList<HashMap<String, String>>();

                                        for (int i = 0; i < data_map.length(); i++) {
                                            JSONObject data = data_map.getJSONObject(i);

                                            String id       = data.getString("ID");
                                            String name     = data.getString("NAME");
                                            String address  = data.getString("ADDRESS");
                                            String phone    = data.getString("PHONE");
                                            String lat      = data.getString("LAT");
                                            String lng      = data.getString("LNG");
                                            String distance = data.getString("DIST_IN_KM");

                                            HashMap<String, String> node = new HashMap<>();
                                            node.put("ID"     , id      );
                                            node.put("NAME"   , name    );
                                            node.put("ADDRESS", address );
                                            node.put("PHONE"  , phone   );
                                            node.put("LAT"    , lat     );
                                            node.put("LNG"    , lng     );
                                            node.put("DIST"   , distance);

                                            responseList.add(node);
                                        }

                                        ListAdapter mAdapter = new GarageListAdapter(getActivity(), responseList);
                                        listView.setAdapter(mAdapter);
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                Intent intent = new Intent(getActivity(), GarageInfoActivity.class);
                                                Bundle bundle = new Bundle();

                                                bundle.putString("ID"     , responseList.get(i).get("ID"     ));
                                                bundle.putString("NAME"   , responseList.get(i).get("NAME"   ));
                                                bundle.putString("ADDRESS", responseList.get(i).get("ADDRESS"));
                                                bundle.putString("PHONE"  , responseList.get(i).get("PHONE"  ));

                                                intent.putExtras(bundle);

                                                startActivity(intent);
                                            }
                                        });

                                        swipeLayout.setRefreshing(false);
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "抱歉，資料發生錯誤，請稍候再試", Toast.LENGTH_LONG).show();
                                        Log.e(LOG_TAG, "loadUI: Format error");
                                    }
                                }
                                else {
                                    Toast.makeText(getActivity(), "抱歉，伺服器沒有回應，請稍候再試", Toast.LENGTH_LONG).show();
                                    Log.e(LOG_TAG, "loadUI: No response");
                                }
                            } catch (JSONException e) {
                                Log.e(LOG_TAG, "JSONE" + e.getMessage());
                            }
                        }
                    }, 1000);
            swipeLayout.setRefreshing(false);
        }
        else {
            Log.e(LOG_TAG, "loadUI: No coordinate");
        }
    }

    @Override
    public void onRefresh() {
        loadUI();
    }

}
