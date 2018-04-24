package callannna.bluelibrary.dao;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Callanna on 2017/3/23.
 */

public interface ClientMsgListener {
    void connect(BluetoothDevice device);
    void readMsg(byte[] msg);
    void disConnect();
}
