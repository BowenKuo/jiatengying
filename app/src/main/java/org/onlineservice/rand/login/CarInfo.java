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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import helper.BluetoothSocketSerializable;

/**
  * Created by Rand on 2016/9/13.  double  e = 2.718281828
  */

public class CarInfo extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    //Variables
    private ImageView clearHistory, carPicture;
    private TextView carStatus, toMonitor, toRecord;
    private ListView listView;
    private BluetoothSocket socket;
    //private BluetoothSocketSerializable socketSerializable;
    private String address = null;
    private String deviceAddress;
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    //Private methods
    private void initialize(@NonNull final View view){
        carPicture = (ImageView) view.findViewById(R.id.carPicture);
        clearHistory = (ImageView) view.findViewById(R.id.clearHistory);
        carStatus = (TextView) view.findViewById(R.id.carStatus);
        toMonitor = (TextView) view.findViewById(R.id.toMonitor);
        toRecord = (TextView) view.findViewById(R.id.toRecord);
        listView = (ListView) view.findViewById(R.id.troubleCodesHistory);


        carPicture.setOnClickListener(setCarPictureListener());
        clearHistory.setOnClickListener(setClearHistoryListener());
        carStatus.setOnClickListener(setCarStatusListener());
        toMonitor.setOnClickListener(setToMonitorListener());
        toRecord.setOnClickListener(setToRecordListener());

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
                    Intent intent = new Intent(getContext(),MonitorActivity.class);
//                    socketSerializable = new BluetoothSocketSerializable(socket);
                    intent.putExtra("bluetoothSocket",address);
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
                    Toast.makeText(getActivity().getApplicationContext(),
                            "藍芽連接失敗!!", Toast.LENGTH_LONG)
                            .show();
                }

                try {
                    assert socket != null;
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_LONG)
                            .show();
                }
                address = deviceAddress;
            }
        });

        alertDialog.setTitle("選擇您OBD2的裝置，如果沒有看到您的裝置，請至手機藍芽設定那邊，" +
                "配對您的OBD2裝置");
        alertDialog.show();
    }

    //

    //Refresh UI
    private void loadUI(){
        //TODO  Get Obd2 trouble codes
    }

    //Clear Trouble codes
    private void clearTroubleCode(){
        //TODO Clear all trouble code history
    }

    //Public methods

    //Override methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_carinfo, container, false);
        initialize(view);
        return inflater.inflate(R.layout.activity_carinfo, container, false);
    }

    @Override
    public void onRefresh() {
        loadUI();
    }
}
