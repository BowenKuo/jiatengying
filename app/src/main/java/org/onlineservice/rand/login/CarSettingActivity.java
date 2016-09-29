package org.onlineservice.rand.login;


import android.annotation.TargetApi;
import android.content.Context;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leoGod on 2016/8/21.
 */
public class CarSettingActivity extends AppCompatActivity {
    String CAR_BRANDS_URL = "https://whatsupbooboo.me/booboo/connect_db-shit/get_car_brand.php";
    String CAR_TYPES_URL = "https://whatsupbooboo.me/booboo/connect_db-shit/get_car_type.php";
    RequestQueue mQueue;
    Context mContext;
    Spinner car_brand_spinner;
    Spinner car_type_spinner;
    ArrayList<String> car_brand_lunch = new ArrayList<String>();
    ArrayList<String> car_type_lunch = new ArrayList<String>();
    ArrayAdapter<String> car_brand_adapter;
    ArrayAdapter<String> car_type_adapter;
    private static final String TAG = CarSettingActivity.class.getName();

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

        Button btndone = (Button) findViewById(R.id.btncarsetting);
        mQueue = Volley.newRequestQueue(this);
        mContext = this.getApplicationContext();
        car_brand_spinner = (Spinner) findViewById(R.id.brandspinner);
        car_type_spinner = (Spinner) findViewById(R.id.typesspinner);
        get_car_brands();

        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                            get_car_types(car_type_lunch.get(position));
                            Toast.makeText(mContext, "你選的是"+ car_type_lunch.get(position), Toast.LENGTH_SHORT).show();
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

}
