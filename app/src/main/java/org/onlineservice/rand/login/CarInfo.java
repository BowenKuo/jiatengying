package org.onlineservice.rand.login;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.pires.obd.commands.protocol.EchoOffCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import helper.SQLiteHandler;

/**
  * Created by Rand on 2016/9/13.  double  e = 2.718281828
  */

public class CarInfo extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    //Variables
    private ImageView clearHistory, carPicture;
    private TextView carStatus;
    private Button toMonitor, toRecord;
    private ListView listView;
    RequestQueue mQueue;
    private SQLiteHandler db;
    private String ERROR_CODE_URL="https://whatsupbooboo.me/booboo/connect_db-shit/get_car_error.php";
    private String mid;
    private Map<String,String> user = new HashMap<>();
    private MyAdapter listAdapter;
    private BluetoothSocket socket;
    //private BluetoothSocketSerializable socketSerializable;
    private String address = null;
    private String deviceAddress;
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<errorcodelist> error_code = new ArrayList<errorcodelist>();

    //Private methods
    private void initialize(@NonNull final View view){
        carPicture = (ImageView) view.findViewById(R.id.carPicture);
        clearHistory = (ImageView) view.findViewById(R.id.clearHistory);
        carStatus = (TextView) view.findViewById(R.id.carStatus);
        toMonitor = (Button) view.findViewById(R.id.toMonitor);
        toRecord = (Button) view.findViewById(R.id.toRecord);
        listView = (ListView) view.findViewById(R.id.troubleCodesHistory);

        carPicture.setOnClickListener(setCarPictureListener());
        clearHistory.setOnClickListener(setClearHistoryListener());
        carStatus.setOnClickListener(setCarStatusListener());

        toMonitor.setOnClickListener(setToMonitorListener());
        toRecord.setOnClickListener(setToRecordListener());
        mQueue = Volley.newRequestQueue(getActivity());
        loadUI();
        //Check Bluetooth Status
        if (adapter == null){
            Toast.makeText(getContext(),R.string.bluetoothNotAvailable,Toast.LENGTH_LONG).show();
        }else{
            if (!adapter.isEnabled()){
                Toast.makeText(getContext(), R.string.bluetoothNotEnabled,
                        Toast.LENGTH_LONG).show();
                Intent intentBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intentBt);
            }
        }

    }

    //ToMonitorOnClickListener
    private View.OnClickListener setToMonitorListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO   To Monitor Activity
                if (socket == null){
                    Toast.makeText(getActivity().getApplicationContext(),"藍芽未連接!!"
                            ,Toast.LENGTH_LONG).show();
                }else{
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getContext(),MonitorActivity.class);
//                    socketSerializable = new BluetoothSocketSerializable(socket);
                    intent.putExtra("bluetoothSocket",address.toString());
                    Log.e("test",(String) intent.getExtras().get("bluetoothSocket"));
                    getActivity().startActivity(intent);
                }
            }
        };
    }

    //ToRecordOnClickListener
    private View.OnClickListener setToRecordListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO  To Record Activity
                Intent intent = new Intent(view.getContext(), Record.class);
                getActivity().startActivity(intent);
            }
        };
    }

    // CarPictureOnclickListener
    private View.OnClickListener setCarPictureListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO  Change Car Avatar
            }
        };
    }

    //ClearHistoryOnclickListener
    private View.OnClickListener setClearHistoryListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTroubleCode();
            }
        };
    }

    //CarStatusListener
    private View.OnClickListener setCarStatusListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList deviceStrs = new ArrayList();
                final ArrayList devices = new ArrayList();
                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        deviceStrs.add(device.getName() + "\n" + device.getAddress());
                        devices.add(device.getAddress());
                    }
                }

                showAlertDialog(btAdapter,deviceStrs,devices);
            }
        };
    }

    //Show Alert Dialog
    private void showAlertDialog(@NonNull final BluetoothAdapter btAdapter,
                                 @NonNull ArrayList deviceStrs,
                                 @NonNull final ArrayList devices) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getActivity());

        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter,0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                int position = ((AlertDialog) dialogInterface).getListView()
                        .getCheckedItemPosition();
                deviceAddress = (String) devices.get(position);
                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                socket = null;
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    Toast.makeText(getActivity().getApplicationContext(),"well done.",Toast.LENGTH_LONG)
                            .show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "藍芽連接失敗!!", Toast.LENGTH_LONG)
                            .show();
                }

                try {
                    assert socket != null;
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), e.toString() + deviceAddress, Toast.LENGTH_LONG)
                            .show();
                }
                address = deviceAddress;
                try {
                    new EchoOffCommand().run(socket.getInputStream(),socket.getOutputStream());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), e.toString() + deviceAddress, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        alertDialog.setTitle("選擇您OBD2的裝置，如果沒有看到您的裝置，請至手機藍芽設定那邊，" +
                "配對您的OBD2裝置");
        alertDialog.show();
    }

    //

    //Refresh UI
    private void loadUI() {
        //TODO  Get Obd2 trouble codes  ;  Load data from Database
        db = new SQLiteHandler(getActivity().getApplicationContext());
        user = db.getUserDetail();
        mid =user.get("mid").toString();
        Log.d("dafdsaf",mid);
        get_error_code(mid);
        Log.d("jdfidjfi","goodgoodeat");

    }



                //Clear Trouble codes
    private void clearTroubleCode(){
        //TODO Clear all trouble code history ;  Clear data from Database
    }

    //Public methods

    //Override methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_carinfo, container, false);
        initialize(view);
//        return inflater.inflate(R.layout.activity_carinfo, container, false);
        return view;
    }

    private void get_error_code(final String mid){
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ERROR_CODE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.d("hello", response);
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d("ffffff","sadsdasde");
                        JSONArray errorcode = jObj.getJSONArray("data");
                        error_code.clear();


                        for (int i = 0; i < errorcode.length(); i++) {
                            JSONObject object = errorcode.optJSONObject(i);
                            String objTimeValue = object.getString("ceTime");
                            String objcodeValue = object.getString("errorcode");
                            String objinfoValue = object.getString("einfo");
                            byte ptext[];
                            try {
                                ptext = objinfoValue.getBytes("ISO-8859-1");
                                String b = new String(ptext, "UTF-8");
                                error_code.add(new errorcodelist(objcodeValue,b,objTimeValue));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            Log.d("errorlist", error_code.toString());
                        }

                    }

                    listView = (ListView) listView.findViewById(R.id.troubleCodesHistory);
                    listAdapter = new MyAdapter(getActivity(), error_code);
                    listView.setAdapter(listAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getActivity().getApplicationContext(), "你選擇的是" + error_code.get(position), Toast.LENGTH_SHORT).show();
                        }
                    });

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
                params.put("mId", mid);

                return params;
            }
        };
//Creating a Request Queue
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(strReq);
    }

    @Override
    public void onRefresh() {
        loadUI();
    }
}
