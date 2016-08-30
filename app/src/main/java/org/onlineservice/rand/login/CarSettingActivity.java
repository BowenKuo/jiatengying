package org.onlineservice.rand.login;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leoGod on 2016/8/21.
 */
public class CarSettingActivity extends AppCompatActivity {
    String GETJASONURL="https://whatsupbooboo.me/booboo/connect_db-shit/get_car_brand.php";
    private RequestQueue mQueue;
    ArrayList<String> ccbrand = new ArrayList<String>();
    private static final String TAG = CarSettingActivity.class.getName();
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carsetting);


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.bg_login));
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_login)));
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Car Setting </font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);
        Spinner bspinner;
        Spinner tspinner;
        ArrayAdapter<String> lunchList1;
        ArrayAdapter<String> lunchList2;
        Context mContext;
        String[] lunch2 = {" ","雙門跑車", "五門小鋼砲", "三輪車", "房車", "水餃"};
        Button btndone = (Button) findViewById(R.id.btncarsetting);
        mQueue = Volley.newRequestQueue(this);
        get();
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                }
        });



        mContext = this.getApplicationContext();
        bspinner = (Spinner) findViewById(R.id.brandspinner);
        lunchList1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lunch1);
        bspinner.setAdapter(lunchList1);
        tspinner = (Spinner) findViewById(R.id.typespinner);
        lunchList2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lunch2);
        tspinner.setAdapter(lunchList2);
        bspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {

            }

            public void onNothingSelected(AdapterView arg0) {

            }
        });
    }
    public void get(){
        String[] str_arr = {null};
        StringRequest strReq = new StringRequest(Request.Method.POST,
                GETJASONURL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            if (!error) {
                                JSONArray Brand = jObj.getJSONArray("data");
                                for (int i = 0; i < Brand.length(); i++) {
                                    JSONObject object = Brand.optJSONObject(i);
                                     String objValue = object.getString("cBrand");
                                    byte ptext[] = new byte[0];
                                    try {
                                        ptext = objValue.getBytes("ISO-8859-1");
                                        String b = new String(ptext, "UTF-8");
                                        ccbrand.add(b);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("array---------",ccbrand.get(i));

                                }
                            }
                            Object[] objectList = ccbrand.toArray();
                            //str_arr[0] = Arrays.copyOf(objectList,objectList.length,String[].class);


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

