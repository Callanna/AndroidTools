package callannna.bluelibrary.dao;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.UUID;

/**
 * Description
 * Created by chenqiao on 2016/6/28.
 */
public interface ConnectImpl {
    void startAsServer(UUID uuid, long timeout);

    void stopAsServer();

    ClientAction connectAsClient(BluetoothDevice device, UUID uuid);

    ClientAction connectAsClient(BluetoothDevice device, UUID uuid, ConnectListener listener);

    boolean bondDevice(BluetoothDevice device);

    void connectAsA2dp(Context context, BluetoothDevice device);

    void connectAsHeadset(Context context, BluetoothDevice device);

    void connectAsPan(Context context, BluetoothDevice device);
}
