package callannna.bluelibrary.socket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import callannna.bluelibrary.dao.ClientAction;
import callannna.bluelibrary.dao.ConnectListener;

/**
 * Description
 * Created by chenqiao on 2016/6/27.
 */
public class ConnectThread extends Thread implements ClientAction<byte[]> {

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectListener connectListener;

    public ConnectThread(BluetoothDevice device, UUID uuid, BluetoothAdapter bluetoothAdapter, ConnectListener connectListener) {
        this.device = device;
        this.bluetoothAdapter = bluetoothAdapter;
        this.connectListener = connectListener;
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        connect();
    }

    public void cancel() {
        try {
            socket.close();
            if (connectListener != null) {
                connectListener.disconnect();
            }
            ClientDeviceManager.getInstence().disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        //暂停蓝牙的扫描，因为会降低连接速度
        bluetoothAdapter.cancelDiscovery();
        if (socket != null) {
            try {
                socket.connect();
                if (connectListener != null) {
                    connectListener.connect(socket);
                }
                ClientDeviceManager.getInstence().connect(socket);
            } catch (IOException e) {
                e.printStackTrace();
                cancel();
            }
        }
    }

    @Override
    public void doAction(Object param1, Object param2, Object param3) {

    }

    @Override
    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }

    @Override
    public void notifyResult(byte[] result) {

    }

    @Override
    public void setResultListener(ResultListener listener) {

    }
}
