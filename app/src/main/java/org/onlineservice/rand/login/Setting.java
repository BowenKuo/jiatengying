package org.onlineservice.rand.login;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Lillian Wu on 2016/7/20.
 */
public class Setting extends Fragment {
    private ImageView profilePict;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private SQLiteDatabase db = null;
    private SQLiteDatabase checkdb = null;
    private Cursor c = null;
    private Cursor cgg = null;
    private byte[] img = null;
    private SessionManager session;
    protected SQLiteHandler sdb;
    private String UPLOAD_URL ="https://whatsupbooboo.me/booboo/connect_db-shit/store_photo.php";
    private String KEY_IMAGE = "photo";
    private String KEY_EMAIL="email";
    private TextView userName;
    private TextView userEmail;
    private TextView userPhone;
    private HashMap<String, String> newMap = new HashMap<String, String>();
    private HashMap<String, String> usermap = new HashMap<String, String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // connect fragment
        View view = inflater.inflate(R.layout.activity_setting, container, false);

        // connect elements
        Button btnLogout = (Button) view.findViewById(R.id.btnLogout);
        Button btnsetcar = (Button) view.findViewById(R.id.btnSetcarinfo);
        userName = (TextView) view.findViewById(R.id.userName);
        userEmail = (TextView) view.findViewById(R.id.userEmail);
        userPhone = (TextView) view.findViewById(R.id.userPhone);
        profilePict = (ImageView) view.findViewById(R.id.profilePict);

        // set profile picture to default first
        profilePict.setImageResource(R.mipmap.nopic);


//        mdb=new MyDatabase(getActivity().getApplicationContext());
//        db=mdb.getWritableDatabase();
        // SQLite database handler
        sdb = new SQLiteHandler(getActivity().getApplicationContext());
        usermap = sdb.getUserDetail();
        // Session manager
        session = new SessionManager(getActivity().getApplicationContext());
        String uname =usermap.get("name");
        String uemail = usermap.get("email");
        String uphone = usermap.get("phone");
        byte[] uphoto = sdb.getPhoto();

        userName.setText(uname);
        userEmail.setText(uemail);
        userPhone.setText(uphone);

        if (uphoto != null) {
            bitmap = BitmapFactory.decodeByteArray(uphoto, 0, uphoto.length);
            profilePict.setImageBitmap(bitmap);
        }
        else {
            Log.w("FUCK PHOTO", "YOU");
        }


        // Set changing profile picture event
        profilePict.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Set car event
        btnsetcar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CarSettingActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;

    }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        sdb.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public byte[] getbyteImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null)
        {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                profilePict.setImageBitmap(bitmap);
                img = getbyteImage(bitmap);
                Log.d("img----", String.valueOf(img));
                //insert into SQLite
                ContentValues cv = new ContentValues();
                cv.put("photo", img);
                // Get user id
                String id = usermap.get("_id");
                sdb.updateUserPhoto(cv, id);
//                uploadImage();
                Toast.makeText(getActivity(), "大頭照更新成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Log.d("Update photo Response: ", response.toString());


                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                //insert into SQLite
                                ContentValues cv = new ContentValues();
                                cv.put("photo", img);
                                // Get user id
                                String id = usermap.get("_id");
                                sdb.updateUserPhoto(cv, id);
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getActivity().getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast

                        Toast.makeText(getActivity(), volleyError.getMessage()+"-----"+volleyError.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String photo = Base64.encodeToString(img, Base64.DEFAULT);
                String email = usermap.get("email");

                Log.d("photo----------", photo);
                Log.d("image----------", img.toString());

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("photo", photo);
                params.put("email", email);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
