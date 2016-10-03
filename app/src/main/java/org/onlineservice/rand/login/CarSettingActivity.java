package org.onlineservice.rand.login;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by leoGod on 2016/8/21.
 */
public class CarSettingActivity extends AppCompatActivity {
    String CAR_BRANDS_URL = "https://whatsupbooboo.me/booboo/connect_db-shit/get_car_brand.php";
    String CAR_TYPES_URL = "https://whatsupbooboo.me/booboo/connect_db-shit/get_car_type.php";
    String ADD_MYCAR_URL = "https://whatsupbooboo.me/booboo/connect_db-shit/setMcar.php";
    String BASIC_CAR_IMAGE_URL = "https://whatsupbooboo.me/booboo/img/car_image/";
    RequestQueue mQueue;
    Context mContext;
    ImageView car_image;
    Spinner car_brand_spinner;
    Spinner car_type_spinner;
    ArrayList<String> car_brand_lunch = new ArrayList<String>();
    ArrayList<String> car_type_lunch = new ArrayList<String>();
    private String car_brand_selected = "";
    private String car_type_selected = "";
    private String car_image_url = "";
    private SessionManager session;
    ArrayAdapter<String> car_brand_adapter;
    ArrayAdapter<String> car_type_adapter;
    private ProgressDialog pDialog;
    private static final String TAG = CarSettingActivity.class.getName();
    private SQLiteHandler db;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carsetting);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.bg_login));
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_login)));
//        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Car Setting </font>"));
//        actionBar.setDisplayHomeAsUpEnabled(true);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Session manager
        session = new SessionManager(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Button btndone = (Button) findViewById(R.id.btncarsetting);
        mQueue = Volley.newRequestQueue(this);
        mContext = this.getApplicationContext();
        car_image = (ImageView) findViewById(R.id.car_image);
        car_brand_spinner = (Spinner) findViewById(R.id.brandspinner);
        car_type_spinner = (Spinner) findViewById(R.id.typesspinner);

        pDialog.setMessage("查詢廠牌中 ...");
        showDialog();
        get_car_brands();
        hideDialog();

        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!car_brand_selected.isEmpty() && !car_type_selected.isEmpty()) {
                    addInMycar(car_brand_selected, car_type_selected, car_image_url, db.getUserDetail().get("mid"));
                } else {
                    Toast.makeText(getApplicationContext(), "請選擇品牌與車種後再新增", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void get_car_brands(){
        // before user choose car brand, we need to disable the spinner of car type
        car_type_spinner.setEnabled(false);

        // first, request car brands
        StringRequest strReq = new StringRequest(Request.Method.POST,
                CAR_BRANDS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONArray Brand = jObj.getJSONArray("data");
                        car_brand_lunch.clear();

                        for (int i = 0; i < Brand.length(); i++) {
                            JSONObject object = Brand.optJSONObject(i);
                            String objValue = object.getString("cBrand");
                            byte ptext[];
                            try {
                                ptext = objValue.getBytes("ISO-8859-1");
                                String b = new String(ptext, "UTF-8");
                                car_brand_lunch.add(b);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    car_brand_adapter = new ArrayAdapter<>(getApplication(), R.layout.car_brand_spinner_item, car_brand_lunch);
                    car_brand_spinner.setAdapter(car_brand_adapter);
                    car_brand_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                            get_car_types(car_brand_lunch.get(position));
                            Toast.makeText(mContext, "你選的是"+ car_brand_lunch.get(position), Toast.LENGTH_SHORT).show();
                            pDialog.setMessage("查詢車型中 ...");
                            showDialog();
                            car_brand_selected = car_brand_lunch.get(position);
                            hideDialog();
                        }

                        public void onNothingSelected(AdapterView arg0) {

                        }
                    });
                    Log.d("0-0-0-", String.valueOf(car_brand_lunch));
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("錯誤", error.getMessage(), error);
            }
        });

        mQueue.add(strReq);
    }

    public void get_car_types(final String car_brand){
        // after user choose car brand, we enable the spinner of car type
        car_type_spinner.setEnabled(true);

        // and first, we request car types by car brand
        StringRequest strReq = new StringRequest(Request.Method.POST,
                CAR_TYPES_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONArray Brand = jObj.getJSONArray("data");
                        car_type_lunch.clear();

                        for (int i = 0; i < Brand.length(); i++) {
                            JSONObject object = Brand.optJSONObject(i);
                            String objValue = object.getString("cType");
                            byte ptext[];
                            try {
                                ptext = objValue.getBytes("ISO-8859-1");
                                String b = new String(ptext, "UTF-8");
                                car_type_lunch.add(b);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    car_type_adapter = new ArrayAdapter<>(getApplication(), R.layout.car_type_spinner_item, car_type_lunch);
                    car_type_spinner.setAdapter(car_type_adapter);
                    car_type_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                            pDialog.setMessage("獲取該車款圖片中 ...");
                            showDialog();
                            car_type_selected = car_type_lunch.get(position);
                            car_image_url = car_brand_selected + "-" + car_type_selected + ".png";
                            //建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑
                            new AsyncTask<String, Void, Bitmap>()
                            {
                                @Override
                                protected Bitmap doInBackground(String... params)
                                {
                                    String url = params[0];
                                    return getBitmapFromURL(url);
                                }

                                @Override
                                protected void onPostExecute(Bitmap result)
                                {
                                    car_image.setImageBitmap (result);
                                    super.onPostExecute(result);
                                }
                            }.execute(BASIC_CAR_IMAGE_URL+car_image_url);
                            hideDialog();
                            Log.w("Image url", BASIC_CAR_IMAGE_URL+car_image_url);
                        }

                        public void onNothingSelected(AdapterView arg0) {

                        }
                    });
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + car_brand + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("錯誤", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to car types url
                Map<String, String> params = new HashMap<String, String>();
                params.put("brand", car_brand);

                return params;
            }
        };

        mQueue.add(strReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addInMycar(final String car_brand, final String car_type, final String car_image_url, final String mid) {
        String tag_string_req = "add car in mcar";
        pDialog.setMessage("新增車子資訊中");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ADD_MYCAR_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // add in mcar (SQLite)
                        session.setCar(true);
                        db.addMcar(car_brand, car_type, car_image_url);
                        Toast.makeText(getApplicationContext(), "汽車基本設定成功", Toast.LENGTH_LONG).show();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Add in mcar Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("car_brand", car_brand);
                params.put("car_type", car_type);
                params.put("mId", mid);

                return params;
            }

        };

        // Adding request to request queue
        AppControl.getInstance().addToRequestQuene(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    //讀取網路圖片，型態為Bitmap
    private static Bitmap getBitmapFromURL(String imageUrl)
    {
        try
        {
            URL url = new URL(imageUrl);
            Log.w("Before", "get image");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            Log.w("Doing", "get image");
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
