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
    private final static String URL_DOMAIN       = "https://whatsupbooboo.me/booboo/connect_db-shit/";
    private final static String URL_GET_GARAGE   = URL_DOMAIN + "nearby_garage.php";
    private final static String URL_GET_COMMENT  = URL_DOMAIN + "retrieve_comment.php";
    private final static String URL_GET_COUPON   = URL_DOMAIN + "coupon.php";
    private final static String URL_SEND_COMMENT = URL_DOMAIN + "send_comment.php";

    private final static String LOG_TAG = "NetworkHelper";
    private final static String ERRMSG_CONNECTION_FAILED = "Connection Error";

    private final static String TAG_GET_GARAGE   = "GET_GARAGE";
    private final static String TAG_GET_COMMENT  = "GET_COMMENT";
    private final static String TAG_GET_COUPON   = "GET_COUPON";
    private final static String TAG_SEND_COMMENT = "SEND_COMMENT";


    private final static int BUFFER_SIZE = 4096;
    private final static int CONNECT_TIMEOUT = 10 * 1000;

    private String httpGETContent;

    private String GETNearbyGarage;
    private String GETComment;
    private String GETCoupon;
    private String GETSendComment;

    public NetworkHelper() {}

    /**********************
     * Public Interface
     *********************/

    public void sendCoordinateData(Map<String, String> data) {
        asyncHttpGETRequest(URL_GET_GARAGE, data, TAG_GET_GARAGE);
    }

    public void getCommentData(Map<String, String> data) {
        asyncHttpGETRequest(URL_GET_COMMENT, data, TAG_GET_COMMENT);
    }

    public void getCouponData(Map<String, String> data) {
        asyncHttpGETRequest(URL_GET_COUPON, data, TAG_GET_COUPON);
    }

    public void sendCommentData(Map<String, String> data) {
        asyncHttpGETRequest(URL_SEND_COMMENT, data, TAG_SEND_COMMENT);
    }

    public String getLastGETResponse() {
        return httpGETContent;
    }
    public String getNearbyGarage() {return GETNearbyGarage;}
    public String getComment()      {return GETComment;     }
    public String getCoupon()       {return GETCoupon;      }
    public String getSendMessage()  {return GETSendComment; }

    public void asyncHttpGETRequest(String base_url, Map<String, String> params, String type) {
        new AsyncTaskHttpGET(base_url, params, type).execute();
    }

    private class AsyncTaskHttpGET extends android.os.AsyncTask<Void, Void, String> {
        private String url;
        private Map<String, String> params;
        private String type;

        public AsyncTaskHttpGET(String url, Map<String, String> params, String type) {
            this.url    = url;
            this.params = params;
            this.type   = type;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return httpGETRequest(url, this.params);
            } catch (IOException e) {
                Log.e(LOG_TAG, "AsyncTaskHttpGET: IOE " + e.getMessage());
                return ERRMSG_CONNECTION_FAILED;
            }
        }

        @Override
        protected void onPostExecute(String content) {
            super.onPostExecute(content);

            switch (this.type) {
                case TAG_GET_GARAGE:
                    GETNearbyGarage = content;
                    break;
                case TAG_GET_COMMENT:
                    GETComment = content;
                    break;
                case TAG_GET_COUPON:
                    GETCoupon = content;
                    break;
                case TAG_SEND_COMMENT:
                    GETSendComment = content;
                    break;
            }
            httpGETContent = content;
        }
    }

    /**********************
     * Core
     *********************/

    /**
     * Send HTTP GET request
     * @param url
     * @param params
     */
    public String httpGETRequest(String url, Map<String, String> params) throws IOException{
        BufferedReader reader = null;
        StringBuilder builder;
        String contentAsString = "";
        char[] buffer = new char[BUFFER_SIZE];

        try {
            String request_url = getGETUrl(url, params);
            URL urlObj = new URL(request_url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Start the query
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.connect();

            int response_code = connection.getResponseCode();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            builder = new StringBuilder();
            // int readSize = reader.read(buffer, 0, BUFFER_SIZE);
            // contentAsString = new String(buffer, 0, readSize);
            String line;
            while((line = reader.readLine()) != null){
                builder.append(line);
            }
            contentAsString = builder.toString();

            Log.d(LOG_TAG, "httpGETRequest: URL = " + request_url);
            Log.d(LOG_TAG, "httpGETRequest: response code = " + response_code);
            // Log.d(LOG_TAG, "httpGETRequest: reader read size = " + readSize);

            // httpGETContent = contentAsString;
            Log.d(LOG_TAG, "httpGETRequest: response = " + contentAsString);
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
