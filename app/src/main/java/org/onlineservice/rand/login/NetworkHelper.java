package org.onlineservice.rand.login;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Lillian Wu on 2016/8/16.
 */
public class NetworkHelper {
    private final static String URL_DOMAIN     = "https://whatsupbooboo.me";
    private final static String URL_GET_GARAGE = URL_DOMAIN + "/booboo/connect_db-shit/nearby_garage.php";

    private final static String LOG_TAG  = "NetworkHelper";
    private final static int BUFFER_SIZE = 4096;
    private final static int CONNECT_TIMEOUT = 10 * 1000;

    private String httpGETContent;

    public NetworkHelper() {}

    /**********************
     * Public Interface
     *********************/

    public void sendSensorData(Map<String, String> params) {
        this.httpGETRequest(URL_GET_GARAGE, params);
    }

    public String getGETResponse(){
        return httpGETContent;
    }

    /**********************
     * Core
     *********************/

    /**
     * Send HTTP GET request
     * @param url
     * @param params
     */
    public String httpGETRequest(String url, Map<String, String> params) {
        BufferedReader reader = null;
        String contentAsString = "";
        char[] buffer   = new char[BUFFER_SIZE];

        try {
            String request_url = getGETUrl(url, params);
            URL urlObj         = new URL(request_url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Starts the query
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.connect();
            int response_code = connection.getResponseCode();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            // int readSize   = reader.read(buffer, 0, BUFFER_SIZE);
            // contentAsString = new String(buffer, 0, readSize);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) { sb.append(line); }

            int readSize = sb.toString().length();
            contentAsString = sb.toString();

            Log.d(LOG_TAG, "httpGETRequest: URL = " + request_url);
            Log.d(LOG_TAG, "httpGETRequest: response code = " + response_code);
            Log.d(LOG_TAG, "httpGETRequest: read size = " + readSize);

            connection.disconnect();
        }
        catch (MalformedURLException e) { Log.e(LOG_TAG, "MalformedURLException:" + e.getMessage() ); }
        catch (IOException e) {
            Log.e(LOG_TAG, "httpGETRequest: IOException: " + e.getMessage());
        }
        finally {
            httpGETContent = contentAsString;
        }

        return contentAsString;
    }

    /********************************
     * Utils
     *******************************/

    /**
     * Get a GET-styled URL with params based on base_url
     * @param base_url
     * @param params
     * @return
     */
    public String getGETUrl(String base_url, Map<String, String> params) {
        String full_request_url = base_url;
        if (params != null)
            full_request_url += ("?" + getParamsFromMap(params));
        return full_request_url;
    }

    /**
     * Combine params in Map to a param String
     * @param params
     * @return
     */
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
            Log.e(LOG_TAG, "UnsupporedEncodingException");
            return request_params;
        }
    }

}
