package callannna.bluelibrary.dao;

import android.bluetooth.BluetoothDevice;

/**
 * Description
 * Created by chenqiao on 2016/6/27.
 */
public interface DeviceFoundListener {

    void findADevice(BluetoothDevice device);
}