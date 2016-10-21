package org.onlineservice.rand.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
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

import helper.SQLiteHandler;

/**
 * Created by leoGod on 2016/10/20.
 */

public class ErrorcodeActivity extends AppCompatActivity {
    private String err_id;
    private String err_info;
    TextView txterrid;
    TextView txterrinfo;
    private ListView listView;
    ErrorcommentAdapter commentAdapter;
    String mid;
    RequestQueue mQueue;
    SQLiteHandler sdb;
    String ERROR_CODE_COMMENT_URL = "https://whatsupbooboo.me/booboo/connect_db-shit/get_car_error_comment.php";
    ArrayList<Errorcomment> error_comment = new ArrayList<Errorcomment>();

    private FloatingActionButton comment_write_btn2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errorcode);
        sdb = new SQLiteHandler(this.getApplicationContext());
        mid = sdb.getMid();
        Bundle bundle = getIntent().getExtras();
        err_id      = bundle.getString("Errorcode");
        err_info    = bundle.getString("Errorcodeinfo");

        txterrid = (TextView) findViewById(R.id.ErrorCode);
        txterrinfo = (TextView) findViewById(R.id.ErrorInfo);
        txterrid.setText(err_id);
        txterrinfo.setText(err_info);
        mQueue = Volley.newRequestQueue(this);
        get_comment(err_id);
//        comment_write_btn2 = (FloatingActionButton) findViewById(R.id.fab);
//        comment_write_btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final View item = LayoutInflater.from(ErrorcodeActivity.this).inflate(R.layout.write_comment, null);
//                new AlertDialog.Builder(ErrorcodeActivity.this)
//                        .setView(item)
//                        .setPositiveButton("送出評論", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                EditText editText = (EditText) item.findViewById(R.id.editText);
//
//                                String comment = editText.getText().toString();
//
//                                HashMap<String, String> params = new HashMap<String, String>();
//
//
//                                params.put("errorcode", err_id);
//                                params.put("user", mid);
//                                params.put("comment", comment);
//
//
//                            }
//                        });
//
//
//            }
//
//
//        });
//
   }
    public void get_comment(final String err_id) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ERROR_CODE_COMMENT_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.d("sadsadasd", response);
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d("ffffff", "sadsdasde");
                        JSONArray errorcode = jObj.getJSONArray("data");
                        error_comment.clear();


                        for (int i = 0; i < errorcode.length(); i++) {
                            JSONObject object = errorcode.optJSONObject(i);
                            String objTimeValue = object.getString("time");
                            String objnameValue = object.getString("name");
                            String objcinfoValue = object.getString("content");

                            byte ptext[];
                            byte otext[];
                            try {
                                ptext = objcinfoValue.getBytes("ISO-8859-1");
                                otext = objnameValue.getBytes("ISO-8859-1");
                                String b = new String(ptext, "UTF-8");
                                String n = new String(otext, "UTF-8");
                                error_comment.add(new Errorcomment(n, b, objTimeValue));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            Log.d("errorcommentlist", error_comment.get(0).getmid()+" "+error_comment.get(0).getinfo()+" "+error_comment.get(0).getTime() );
                        }

                        listView = (ListView) findViewById(R.id.errorcomment);
                        commentAdapter = new ErrorcommentAdapter(ErrorcodeActivity.this, error_comment);
                        listView.setAdapter(commentAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Log.d("選擇的", error_comment.get(position).getmid());
//                                Toast.makeText(ErrorcodeActivity.this, "你選擇的是" + error_comment.get(position).getmid(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
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
                params.put("error_code", err_id);

                return params;
            }
        };

        mQueue.add(strReq);
    }
}

