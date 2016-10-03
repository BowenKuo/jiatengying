package helper;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;

/**
 *  Created by Rand on 2016/9/25. Testing is boring...
 */

public class Obd2Service extends Service{
    private BluetoothSocket socket;
    private Handler handler = new Handler();
    private Runnable dtcRunnable = new Runnable() {
        @Override
        public void run() {
            //TODO Handle Obd2
            try {
                executeCommand(new TroubleCodesCommand(),socket);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            handler.postDelayed(this,60000);
        }
    };

    private String radiator, battery, rpm, dtc;

    //Constructor
    public Obd2Service(@NonNull BluetoothSocket socket){
        super();
        this.socket = socket;
        radiator = null;
        battery = null;
        rpm = null;
        dtc = null;
        try {
            executeCommand(new EchoOffCommand(),socket);
            executeCommand(new LineFeedOffCommand(),socket);
            executeCommand(new SelectProtocolCommand(ObdProtocols.ISO_15765_4_CAN),socket);
        } catch (IOException | InterruptedException e) {
            Log.e("Stab","Ya die...");
            e.printStackTrace();
        }
    }

    //Override methods
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId){
        super.onStart(intent,startId);

    }

    //Private method
    private synchronized String executeCommand(@NonNull final ObdCommand command,
                                      @NonNull final BluetoothSocket socket)
            throws IOException, InterruptedException {
        command.run(socket.getInputStream(),socket.getOutputStream());
        return command.getFormattedResult();
    }

    //Public Methods
    @Nullable
    public String getRadiator(){
        return radiator;
    }

    @Nullable
    public String getRPM(){
        return rpm;
    }

    @Nullable
    public String getBattery(){
        return battery;
    }

    @Nullable
    public String getDtc(){
        return dtc;
    }
}
