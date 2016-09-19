package helper;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Rand on 2016/9/18. y = ax + b
 */
public class BluetoothSocketSerializable implements Serializable{
    //Variables
    private BluetoothSocket socket;

    //Constructor
    public BluetoothSocketSerializable(@NonNull final BluetoothSocket socket){
        this.socket = socket;
    }

    //Public Method
    public BluetoothSocket getSocket(){
        return socket;
    }

    public void setSocket(@NonNull final BluetoothSocket socket){
        this.socket = socket;
    }
}
