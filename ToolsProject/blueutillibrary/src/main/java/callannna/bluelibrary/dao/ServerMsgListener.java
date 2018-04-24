package callannna.bluelibrary.dao;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Callanna on 2017/3/23.
 */

public interface ServerMsgListener {
    void connect(BluetoothDevice device);
    void readMsg(BluetoothDevice device,byte[] msg);
    void disConnect(BluetoothDevice device);
}
