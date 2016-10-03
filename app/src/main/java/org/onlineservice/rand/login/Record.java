package org.onlineservice.rand.login;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Record extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final static String LOG_TAG = "MaintenanceActivity";

    private SwipeRefreshLayout   swipeLayout;
    private RecyclerView         recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager  layoutManager;

    private int car_id = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                new Task().execute();
            }
        });
    }

    private class Task extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

        ArrayList<HashMap<String, String>> response_list;

        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
            NetHelper netHelper = new NetHelper();

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", "" + car_id);

                String response = netHelper.getHttpGETContent(parameters);

                if (response != null) {
                    JSONObject JSONObject = new JSONObject(response);

                    if (JSONObject.getString("status").equals("ok")) {
                        JSONArray data_map = JSONObject.getJSONArray("results");

                        response_list = new ArrayList<HashMap<String, String>>();

                        for (int i = 0; i < data_map.length(); i++) {

                            HashMap<String, String> node = new HashMap<>();

                            node.put( "ID"     , data_map.getJSONObject(i).getString("ID"     ) );
                            node.put( "TIME"   , data_map.getJSONObject(i).getString("TIME"   ) );
                            node.put( "CONTENT", data_map.getJSONObject(i).getString("HISTORY") );
                            node.put( "PLACE"  , data_map.getJSONObject(i).getString("PLACE"  ) );

                            response_list.add(node);
                        }
                    }
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "IOE: " + e.getMessage());
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONE: " + e.getMessage());
            }
            return response_list;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> content) {
            swipeLayout.setRefreshing(false);

            if (content != null) {
                adapter = new MaintenanceListAdapter(content);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private class NetHelper {

        private final static String LOG_TAG = "NetHelper";

        private static final String URL_DOMAIN = "https://whatsupbooboo.me/booboo/connect_db-shit/maintain_history.php";

        private final static int BUFFER_SIZE     = 4096;
        private final static int CONNECT_TIMEOUT = 10 * 1000;

        public NetHelper() {}

        public String getHttpGETContent(HashMap<String, String> params) throws IOException {
            return httpGETRequest(URL_DOMAIN, params);
        }

        public String httpGETRequest(String url, Map<String, String> params) throws IOException {

            BufferedReader reader = null;
            String contentAsString = "";
            char[] buffer = new char[BUFFER_SIZE];

            try {
                String request_url = getGETUrl(url, params);
                URL urlObj = new URL(request_url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.connect();

                int response_code = connection.getResponseCode();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                int readSize = reader.read(buffer, 0, BUFFER_SIZE);
                contentAsString = new String(buffer, 0, readSize);

                Log.d(LOG_TAG, "httpGETRequest: URL = " + request_url);
                Log.d(LOG_TAG, "httpGETRequest: response code = " + response_code);
                Log.d(LOG_TAG, "httpGETRequest: reader read size = " + readSize);

                // Log.d(LOG_TAG, "httpGETRequest: response = " + contentAsString);
                connection.disconnect();
            }
            catch (MalformedURLException e) { Log.e(LOG_TAG, "httpGetRequest: MalformedURLException " + e.getMessage() ); }
            catch (IOException e) {
                Log.e(LOG_TAG, "httpGetRequest: IOException " + e.getMessage() );
                throw new IOException(e);
            }

            try { if (reader != null) reader.close(); }
            catch (IOException e) {}

            return contentAsString;
        }

        public String getGETUrl(String base_url, Map<String, String> params) {
            String full_request_url = base_url;
            if (params != null)
                full_request_url += ("?" + getParamsFromMap(params));
            return full_request_url;
        }

        public String getParamsFromMap(Map<String, String> params) {
            String request_params = "";
            boolean isFirst = true;
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (isFirst)    isFirst = false;
                    else            request_params += "&";

                    request_params +=
                            (URLEncoder.encode(entry.getKey(), "utf-8") + "=" +
                                    URLEncoder.encode(entry.getValue(), "utf-8"));
                }

                return request_params;
            }
            catch (UnsupportedEncodingException e) {
                Log.e(LOG_TAG, "UEE");
                return request_params;
            }
        }
    }

    @Override
    public void onRefresh() {
        new Task().execute();
    }
}
