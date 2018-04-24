package callannna.bluelibrary.dao;

import android.bluetooth.BluetoothSocket;

/**
 * Description
 * Created by chenqiao on 2016/8/19.
 */
public interface ConnectListener {

    void connect(BluetoothSocket socket);

    void disconnect();
}
