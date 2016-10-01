package org.onlineservice.rand.login;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import helper.BluetoothSocketSerializable;


public class CarInfo extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    //Variables
    private ImageView clearHistory, carPicture;
    private TextView carStatus;
    private Button toMonitor, toRecord;
    private ListView listView;
    private BluetoothSocket socket;
    private BluetoothSocketSerializable socketSerializable;
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

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
    private View.OnClickListener setToMonotorListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO   To Monitor Activity
                Intent intent = new Intent(getContext(),MonitorActivity.class);
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
//        return inflater.inflate(R.layout.activity_carinfo, container, false);
        return view;
    }

    @Override
    public void onRefresh() {
        loadUI();
    }
}
