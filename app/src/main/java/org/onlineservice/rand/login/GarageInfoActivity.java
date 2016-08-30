package org.onlineservice.rand.login;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by davidkuo on 8/26/16.
 */
public class GarageInfoActivity extends AppCompatActivity {

    private final static String LOG_TAG = "InfoActivity";

    private NetworkHelper networkHelper;

    private TextView  info_name;
    private Button    info_locate_btn;
    private Button    info_call_btn;
    private TextView  comment_no_data_prompt;
    private TextView  coupon_no_data_prompt;
    private RatingBar ratingBar;

    private FloatingActionButton comment_write_btn;

    private RecyclerView mRecyclerView;
    private RecyclerView cRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter cAdapter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayoutManager cLayoutManager;

    private String auto_id;
    private String auto_name;
    private String auto_address;
    private String auto_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage_info);

        Bundle bundle = getIntent().getExtras();

        auto_id      = bundle.getString("ID"     );
        auto_name    = bundle.getString("NAME"   );
        auto_address = bundle.getString("ADDRESS");
        auto_phone   = bundle.getString("PHONE"  );

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        info_name       = (TextView) findViewById(R.id.info_name      );
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        info_locate_btn = (Button) findViewById(R.id.info_location_btn);
        info_call_btn   = (Button) findViewById(R.id.info_call_btn    );

        mRecyclerView = (RecyclerView) findViewById(R.id.coupon_recycler_view);
        cRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);

        comment_no_data_prompt = (TextView) findViewById(R.id.comment_no_data_prompt);
        coupon_no_data_prompt  = (TextView) findViewById(R.id.coupon_no_data_prompt );

        comment_write_btn = (FloatingActionButton) findViewById(R.id.fab);
        comment_write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View item = LayoutInflater.from(GarageInfoActivity.this).inflate(R.layout.write_comment, null);

                new AlertDialog.Builder(GarageInfoActivity.this)
                        .setView(item)
                        .setPositiveButton("送出評論", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText editText   = (EditText)  item.findViewById(R.id.editText         );
                                RatingBar ratingBar = (RatingBar) item.findViewById(R.id.comment_ratingBar);

                                String comment = editText.getText().toString();
                                int    rating  = (int) ratingBar.getRating();

                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("auto"   , auto_id    );
                                params.put("user"   , "" + 1     );
                                params.put("rate"   , "" + rating);
                                params.put("comment", comment    );

                                networkHelper.sendCommentData(params);

                                new Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String response = networkHelper.getSendMessage();
                                                    String status = new JSONObject(response).getString("status");

                                                    if (status.equals("ok")) {
                                                        // Toast.makeText(getApplicationContext(), "送出成功", Toast.LENGTH_LONG).show();
                                                        loadUI();
                                                    } else Toast.makeText(getApplicationContext(), "評論沒有成功送出，請重新送出", Toast.LENGTH_LONG).show();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        , 500);
                            }
                        })
                        .setNegativeButton("取消評論", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });


        info_name.setText(auto_name);
        info_locate_btn.setText(auto_address);

        if (auto_phone.equals("")) {
            info_call_btn.setText("無提供聯絡電話");
            info_call_btn.setAlpha(.5f);
            info_call_btn.setClickable(false);
        }
        else {
            info_call_btn.setText(auto_phone);

            info_call_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + auto_phone));
                    Log.d(LOG_TAG, "Dial: Phone number: " + auto_phone);

                    if (ActivityCompat.checkSelfPermission(GarageInfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Log.e(LOG_TAG, "Permisson Denied");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(GarageInfoActivity.this, Manifest.permission.CALL_PHONE)) {
                        } else {
                            ActivityCompat.requestPermissions(GarageInfoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 11);
                            Log.d(LOG_TAG, "Phone-Call: Request Permission");
                        }
                        return;
                    }
                    GarageInfoActivity.this.startActivity(intent);
                }
            });
        }

        loadUI();
    }

    private void loadUI() {

        if (mRecyclerView.getVisibility() == View.GONE || cRecyclerView.getVisibility() == View.GONE) {
            mRecyclerView.setVisibility(View.VISIBLE);
            cRecyclerView.setVisibility(View.VISIBLE);
            coupon_no_data_prompt.setVisibility(View.INVISIBLE);
            comment_no_data_prompt.setVisibility(View.INVISIBLE);
        }

        networkHelper = new NetworkHelper();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", auto_id);

        networkHelper.getCouponData(params);
        networkHelper.getCommentData(params);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String coupon_response  = networkHelper.getCoupon();
                            String comment_response = networkHelper.getComment();

                            if (coupon_response != null && comment_response != null) {

                                JSONObject coupon_json  = new JSONObject(coupon_response );
                                JSONObject comment_json = new JSONObject(comment_response);

                                if (coupon_json.getString("status").equals("ok") && comment_json.getString("status").equals("ok")) {
                                    JSONArray coupon_data_map  = coupon_json.getJSONArray("data");
                                    JSONArray comment_data_map = comment_json.getJSONArray("data");

                                    if (coupon_data_map.length() == 0) {

                                        if (coupon_no_data_prompt.getVisibility() == View.INVISIBLE) {
                                            coupon_no_data_prompt.setVisibility(View.VISIBLE);
                                        }
                                        mRecyclerView.setVisibility(View.GONE);
                                    }
                                    else {
                                        ArrayList<HashMap<String, String>> responseList = new ArrayList<HashMap<String, String>>();

                                        for (int i = 0; i < coupon_data_map.length(); i++) {
                                            JSONObject data = coupon_data_map.getJSONObject(i);

                                            String id      = data.getString("ID"    );
                                            String coupon  = data.getString("COUPON");
                                            String start   = data.getString("START" );
                                            String end     = data.getString("END"   );

                                            HashMap<String, String> node = new HashMap<>();
                                            node.put("ID"    , id);
                                            node.put("COUPON", coupon);
                                            node.put("START" , start);
                                            node.put("END"   , end);

                                            responseList.add(node);
                                        }

                                        mAdapter       = new CouponListAdapter(responseList);
                                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                        mRecyclerView.setHasFixedSize(true);
                                        mRecyclerView.setLayoutManager(mLayoutManager);
                                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                        mRecyclerView.setAdapter(mAdapter);
                                    }


                                    if (comment_data_map.length() == 0) {

                                        if (comment_no_data_prompt.getVisibility() == View.INVISIBLE) {
                                            comment_no_data_prompt.setVisibility(View.VISIBLE);
                                        }
                                        cRecyclerView.setVisibility(View.GONE);
                                    }
                                    else {
                                        ArrayList<HashMap<String, String>> responseList = new ArrayList<HashMap<String, String>>();

                                        float rating_score = 0;
                                        for (int i = 0; i < comment_data_map.length(); i++) {
                                            JSONObject data = comment_data_map.getJSONObject(i);

                                            String id      = data.getString("ID"       );
                                            String user    = data.getString("USER"     );
                                            String time    = data.getString("TIME"     );
                                            String star    = data.getString("STAR_RATE");
                                            String comment = data.getString("COMMENT"  );

                                            HashMap<String, String> node = new HashMap<>();
                                            node.put("ID"     , id     );
                                            node.put("USER"   , user   );
                                            node.put("TIME"   , time   );
                                            node.put("STAR"   , star   );
                                            node.put("COMMENT", comment);

                                            rating_score += Float.parseFloat(star);

                                            responseList.add(node);
                                        }

                                        cAdapter       = new CommentListAdapter(responseList);
                                        cLayoutManager = new LinearLayoutManager(getApplicationContext());
                                        cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                        cRecyclerView.setHasFixedSize(true);
                                        cRecyclerView.setLayoutManager(cLayoutManager);
                                        cRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                        cRecyclerView.setAdapter(cAdapter);

                                        ratingBar.setRating(comment_data_map.length() > 0 ? rating_score / comment_data_map.length() : 0);
                                    }
                                }
                                else {
                                    Toast.makeText(GarageInfoActivity.this, "抱歉，資料發生錯誤，請稍候再試", Toast.LENGTH_LONG).show();
                                    Log.e(LOG_TAG, "loadUI: Format error");
                                }
                            }
                            else {
                                Toast.makeText(GarageInfoActivity.this, "抱歉，伺服器沒有回應，請稍候再試", Toast.LENGTH_LONG).show();
                                Log.e(LOG_TAG, "loadUI: No response");
                            }
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "JSONE" + e.getMessage());
                        }
                    }
                }, 1000
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}