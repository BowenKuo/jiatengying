package org.onlineservice.rand.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;


public class RegisterActivity extends FragmentActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private TextView btnBirthday;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPhone;
    private String birthday;
    private EditText inputPassword;
    private EditText inputConfirm;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Encrypt encrypt = new Encrypt();
    private Dateselect ds = new Dateselect();
    private int type=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPhone=(EditText) findViewById(R.id.phone);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirm = (EditText) findViewById(R.id.confirm);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        btnBirthday = (TextView) findViewById(R.id.birthday);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnBirthday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                doDatePicker(view);


            }
        });

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirm = inputConfirm.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();
                birthday=btnBirthday.getText().toString();
                Log.d("生日--------------------",birthday);
                if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !birthday.isEmpty() && !password.isEmpty() && !confirm.isEmpty()) {
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        if(password.equals(confirm)){
                            String encipherPassword = encrypt.getEncryptedPassword(password);
                            registerUser(name, email,phone,birthday, encipherPassword,type);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Password is not the same as confirm!", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please check your email format!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email,final String phone,final String birthday,
                              final String password, final int type ) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String type = user.getString("type");
                        String email = user.getString("email");
                        String phone = user.getString("phone");
                        String photo = user.getString("photo");
                        String birthday = user.getString("birthday");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
//                        db.addUser(name, email, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("phone",phone);
                params.put("birthday",birthday);
                params.put("type",Integer.toString(type));

                return params;
            }

        };

        // Adding request to request queue
        (AppControl.getInstance()).addToRequestQuene(strReq, tag_string_req);
    }
    public void doDatePicker(View view) {
        DialogFragment myDatePickerFragment = new Dateselect();
        myDatePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
