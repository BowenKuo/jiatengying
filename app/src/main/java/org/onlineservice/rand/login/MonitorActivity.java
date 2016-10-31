package org.onlineservice.rand.login;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DtcNumberCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import helper.SQLiteHandler;

/**
 * Created by Rand on 2016/9/13. Outrageous~~
 */
public class MonitorActivity extends AppCompatActivity {
    //Value
    private final String CAR_ERROR_CODE_RT_URL = "https://whatsupbooboo.me/booboo/connect_db-shit/add_car_error_real_time.php";
    //Variables
    private Button button;
    private Button confirm;
    private ListView list;
    private ListAdapter adapter;
    private static final boolean NOTCHECK = false, CHECK = true;
    private ScalableFrameLayout radiator, battery, rpm, brake, dLight, frontLight, fFogLight;
    private ScalableFrameLayout bLight, malFuncTime, fuelLevel;
    private Map<CharSequence, ScalableFrameLayout> frameLayoutMap;
    private String deviceAddress;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private Bundle bundle;
    private static final String brakeTroubleCode = "P0571";
    private static final String fogTroubleCode = "B2472";
    private static final String turnLampTroubleCode = "B1499";
    private static final String headLampTroubleCode = "B2249";

    private boolean isBrakeGood = true;
    private boolean isFogLampGood = true;
    private boolean isTurnLampGood = true;
    private boolean isHeadLampGood = true;

    RequestQueue mQueue;
    private SQLiteHandler sdb;

    private ArrayList<CharSequence> troubleCodeList = new ArrayList<>();


    //Private Method
    private void initialize() {
        Intent intent = getIntent();
        if(intent == null){
            Log.e("Shoot","Ya die");
        }else {
            Log.e("Yas","I lived");
            bundle = intent.getExtras();
        }
        if (bundle != null && bundle.get("bluetoothSocket") != null){
            deviceAddress = ((String) bundle.get("bluetoothSocket"));
        }
        list = (ListView) findViewById(R.id.listView);
        button = (Button) findViewById(R.id.chooseButton);
        confirm = (Button) findViewById(R.id.confirm);

        button.setOnClickListener(setButtonListener());
        confirm.setOnClickListener(setConfirmListener());

        //Setting ListView
        frameLayoutMap = new HashMap<>();

        radiator = (ScalableFrameLayout) findViewById(R.id.radiator);
        battery = (ScalableFrameLayout) findViewById(R.id.battery);
        rpm = (ScalableFrameLayout) findViewById(R.id.rpm);
        brake = (ScalableFrameLayout) findViewById(R.id.brake);
        dLight = (ScalableFrameLayout) findViewById(R.id.dLight);
        frontLight = (ScalableFrameLayout) findViewById(R.id.frontLight);
        fFogLight = (ScalableFrameLayout) findViewById(R.id.frontFogLight);
        bLight = (ScalableFrameLayout) findViewById(R.id.bLight);
        malFuncTime = (ScalableFrameLayout) findViewById(R.id.malFunctionTime);
        fuelLevel = (ScalableFrameLayout) findViewById(R.id.fuelLow);

        frameLayoutMap.put("水箱", radiator);
        frameLayoutMap.put("電瓶", battery);
        frameLayoutMap.put("引擎轉數", rpm);
        frameLayoutMap.put("煞車", brake);
        frameLayoutMap.put("方向燈", dLight);
        frameLayoutMap.put("大燈", frontLight);
        frameLayoutMap.put("前霧燈", fFogLight);
        frameLayoutMap.put("後霧燈", bLight);
        frameLayoutMap.put("故障時行車距離", malFuncTime);
        frameLayoutMap.put("油耗", fuelLevel);

        initFrameLayout(frameLayoutMap.get("水箱"), R.mipmap.water_tank_for_vehicles_g, "test");
        initFrameLayout(frameLayoutMap.get("電瓶"), R.mipmap.car_battery_g, "test");
        initFrameLayout(frameLayoutMap.get("引擎轉數"), R.mipmap.normalengine, "test");
        initFrameLayout(frameLayoutMap.get("煞車"), R.mipmap.brake_disk_g, "test");
        initFrameLayout(frameLayoutMap.get("方向燈"), R.mipmap.turn_signals_g, "test");
        initFrameLayout(frameLayoutMap.get("大燈"), R.mipmap.frontlightbad, "test");
        initFrameLayout(frameLayoutMap.get("前霧燈"), R.mipmap.fog_light_g, "test");
        initFrameLayout(frameLayoutMap.get("後霧燈"), R.mipmap.blightgood, "test");
        initFrameLayout(frameLayoutMap.get("故障時行車距離"), R.mipmap.toolongtime, "test");
        initFrameLayout(frameLayoutMap.get("油耗"), R.mipmap.fuel_g, "test");

        mQueue = Volley.newRequestQueue(this);
        sdb = new SQLiteHandler(getApplicationContext());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,
                frameLayoutMap.keySet().toArray());
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setOnItemClickListener(getItemViewListener());
    }

    private void obd2Handler() {
        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);


        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        BluetoothSocket socket = null;
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "BluetoothSocket exception", Toast.LENGTH_LONG)
                    .show();
        }

        try {
            assert socket != null;
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();
        }

        try {
            handleOBD2(socket);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "handleOBD2 exception", Toast.LENGTH_LONG)
                    .show();
            Log.v("Exception", "e");
        }

/*        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }*/

        // show list
        //showAlertDialog(btAdapter, deviceStrs, devices);
    }

    // initialize FrameLayout
    private void initFrameLayout(@NonNull final ScalableFrameLayout layout,
                                 @DrawableRes final int res,
                                 @NonNull final CharSequence text) {
        layout.setText(text);
        layout.setImageResource(res);
        layout.hide();
        layout.init(100, 100);
    }

    //Button Listener
    private View.OnClickListener setButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
                Log.e("map", frameLayoutMap.values().toString());
                for (ScalableFrameLayout layout : frameLayoutMap.values()) {
                    layout.hide();
                }
                button.setVisibility(View.GONE);
            }
        };
    }

    //Confirm Listener
    private View.OnClickListener setConfirmListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ScalableFrameLayout layout : frameLayoutMap.values()) {
                    Log.e(ScalableFrameLayout.OPTCHECHED + "  ", String.valueOf(layout.checkedStatus));
                    if (layout.checkedStatus == ScalableFrameLayout.OPTCHECHED)
                        layout.show();
                    else
                        layout.hide();
                }
                list.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
            }
        };
    }

    //onItemClick Listener
    private AdapterView.OnItemClickListener getItemViewListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toggleCheckStatus(adapterView.getItemAtPosition(i).toString());
            }
        };
    }

    //Set Toggle
    private void toggleCheckStatus(String name) {
        ScalableFrameLayout layout = frameLayoutMap.get(name);
        if (layout.checkedStatus == ScalableFrameLayout.OPTCHECHED) {
            layout.checkedStatus = ScalableFrameLayout.OPTNOTCHECKED;
        } else {
            layout.checkedStatus = ScalableFrameLayout.OPTCHECHED;
        }
    }

 /*   private void showAlertDialog(@NonNull final BluetoothAdapter btAdapter,
                                 @NonNull ArrayList deviceStrs,
                                 @NonNull final ArrayList devices) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                int position = ((AlertDialog) dialogInterface).getListView()
                        .getCheckedItemPosition();
                deviceAddress = (String) devices.get(position);
                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                BluetoothSocket socket = null;
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "BluetoothSocket exception", Toast.LENGTH_LONG)
                            .show();
                }

                try {
                    assert socket != null;
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG)
                            .show();
                }

                try {
                    handleOBD2(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "handleOBD2 exception", Toast.LENGTH_LONG)
                            .show();
                    Log.v("Exception", "e");
                }

            }
        });

        alertDialog.setTitle("選擇OBD2裝置");
        alertDialog.show();
    }*/

    private void handleOBD2(final BluetoothSocket socket) throws IOException, InterruptedException {
        executeCommand(new EchoOffCommand(),socket);
        executeCommand(new LineFeedOffCommand(),socket);
        executeCommand(new SelectProtocolCommand(ObdProtocols.ISO_15765_4_CAN),socket);

        //Initialize commands
        final RPMCommand engineRPMCmd = new RPMCommand();
        final SpeedCommand speedCommand = new SpeedCommand();
        final FuelLevelCommand fuelLevel = new FuelLevelCommand();
        final ModuleVoltageCommand voltageCommand = new ModuleVoltageCommand();
        final DtcNumberCommand dtcNumber = new DtcNumberCommand();
        final FuelPressureCommand fuelPressure = new FuelPressureCommand();
        final EngineCoolantTemperatureCommand engineCoolantTemperature =
                new EngineCoolantTemperatureCommand();

        //Thread Running
        //RPM
        Thread rpmThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        result = executeCommand(engineRPMCmd, socket);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        Log.wtf(this.getClass().getName(), "RPM", new IOException());
                    }

                    final String finalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            frameLayoutMap.get("引擎轉數").setText(finalResult);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //Speed
        Thread speedThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        result = executeCommand(speedCommand, socket);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        Log.wtf(this.getClass().getName(), "Speed Command");
                    }

                    final String finalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            frameLayoutMap.get("故障時行車距離").setText(finalResult);
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        //Fuel Level
        Thread fuelThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        result = executeCommand(fuelLevel, socket);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        Log.wtf(this.getClass().getName(), "Fuel");
                    }

                    final String finalResult = result;
                    final double value = Double.parseDouble(result.replace("%",""));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            frameLayoutMap.get("油耗").setText(finalResult);
                            if(value<=15){
                                frameLayoutMap.get("油耗").setImageResource(R.mipmap.fuel_o);
                            }else if (value<=30){
                                frameLayoutMap.get("油耗").setImageResource(R.mipmap.fuel_r);
                            }else {
                                frameLayoutMap.get("油耗").setImageResource(R.mipmap.fuel_g);
                            }
                        }
                    });

                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //電壓
        Thread voltageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        result = executeCommand(voltageCommand, socket);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        Log.wtf(this.getClass().getName(), "Voltage");
                    }

                    final String finalResult = result;
                    final double value = Double.parseDouble(finalResult.replace("V",""));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            frameLayoutMap.get("電瓶").setText(finalResult);
                            if (value <= 11.15){
                                frameLayoutMap.get("電瓶").setImageResource(R.mipmap.car_battery_r);
                            }else if (value <=11.5){
                                frameLayoutMap.get("電瓶").setImageResource(R.mipmap.car_battery_o);
                            }else{
                                frameLayoutMap.get("電瓶").setImageResource(R.mipmap.car_battery_g);
                            }
                        }
                    });

                    try {
                        //Thread.sleep(60000);
                        Thread.sleep(5000);//for test
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //故障碼數
        Thread dtcNumberThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        result = executeCommand(dtcNumber, socket);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        Log.wtf(this.getClass().getName(), "DtcNumber");
                    }

                    final String finalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Get TroubleCode
        final Thread troubleCodeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        result = executeCommand(new TroubleCodesCommand(), socket);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        Log.wtf(this.getClass().getName(), "Trouble");
                    }

                    if (!result.isEmpty()){
                        //TODO Tokenize the trouble code
                        StringTokenizer tokenizer = new StringTokenizer(result);
                        Log.wtf("DIE!!",result);
                        while(tokenizer.hasMoreTokens()){
                            String tmp = tokenizer.nextToken();
                            troubleCodeList.add(tmp);
                            if (tmp.equalsIgnoreCase(fogTroubleCode)){
                                isFogLampGood = false;
                            }else if(tmp.equalsIgnoreCase(brakeTroubleCode)){
                                isBrakeGood = false;
                                Log.wtf("DIE",brakeTroubleCode);
                            }else if(tmp.equalsIgnoreCase(headLampTroubleCode)){
                                isHeadLampGood = false;
                            }else if(tmp.equalsIgnoreCase(turnLampTroubleCode)){
                                isTurnLampGood = false;
                            }else {
                                doNothing();
                            }
                        }
                    }
                    // Handle writeIntoDB exception
                    try {
                        writeIntoDB(troubleCodeList, DateFormat.getDateInstance().format(new Date()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final String finalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalResult.isEmpty()){
                                //At good condition~
                                frameLayoutMap.get("煞車").setImageResource(R.mipmap.brake_disk_g);
                                frameLayoutMap.get("方向燈").setImageResource(R.mipmap.turn_signals_g);
                                frameLayoutMap.get("大燈").setImageResource(R.mipmap.fflightgood);
                                frameLayoutMap.get("前霧燈").setImageResource(R.mipmap.fog_light_g);
                                frameLayoutMap.get("後霧燈").setImageResource(R.mipmap.fflightgood);
                            }else{
                                //TODO Change picture
                                if (!isFogLampGood) {
                                    frameLayoutMap.get("前霧燈").setImageResource(R.mipmap.fog_light_r);
                                    frameLayoutMap.get("後霧燈").setImageResource(R.mipmap.fog_light_r);
                                }
                                if (!isBrakeGood){
                                    frameLayoutMap.get("煞車").setImageResource(R.mipmap.brake_disk_r);
                                    Log.wtf("DIE","WELL");
                                }
                                if (!isHeadLampGood){
                                    frameLayoutMap.get("大燈").setImageResource(R.mipmap.high_beam_r);
                                }
                                if (!isTurnLampGood){
                                    frameLayoutMap.get("方向燈").setImageResource(R.mipmap.dlightbad);
                                }
                            }
                        }
                    });

                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //油壓
        Thread fuelPressureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        result = executeCommand(fuelPressure, socket);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        Log.wtf(this.getClass().getName(), "Fuel Pressure");
                    }

                    final String finalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            frameLayoutMap.get("方向燈").setText(finalResult);
                        }
                    });

                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //引擎冷卻液
        Thread engineCoolantThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        result = executeCommand(engineCoolantTemperature, socket);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    final String finalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        speedThread.start();
        rpmThread.start();
        fuelThread.start();
        voltageThread.start();
        dtcNumberThread.start();
        troubleCodeThread.start();
        fuelPressureThread.start();
        engineCoolantThread.start();
    }

    private synchronized String executeCommand(@NonNull final ObdCommand command,
                                               @NonNull final BluetoothSocket socket)
            throws IOException, InterruptedException {
        command.run(socket.getInputStream(), socket.getOutputStream());
        return command.getFormattedResult();
    }

    private void doNothing(){
        //TODO nothing
    }

    private synchronized void writeIntoDB(@NonNull final ArrayList<CharSequence> result,
                                          @NonNull final String currentDateString)
            throws Exception{
        //TODO  Write data to database
        String mId = sdb.getMid();
        for (CharSequence token : result){
            // insert error code to database
            add_error_code_to_database((String) token, mId, currentDateString);
            Log.e(currentDateString,token.toString());
        }
    }

    private void add_error_code_to_database(final String error_code,
                                            final String mId,
                                            final String datetime) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                CAR_ERROR_CODE_RT_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "錯誤碼"+ error_code + "發生於" + datetime, Toast.LENGTH_LONG);
                    } else {
                        Toast.makeText(getApplicationContext(), "錯誤碼"+ error_code + "發生於~~" + datetime, Toast.LENGTH_LONG);
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
                params.put("mId", mId);
                params.put("errorCode", error_code);
                params.put("datetime", datetime);
                return params;
            }
        };

        mQueue.add(strReq);
    }

    //Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        initialize();
        obd2Handler();
    }
}
