package callannna.bluelibrary.socket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Description
 * Created by chenqiao on 2016/6/27.
 */
public class ServerAcceptThread extends Thread {

    private BluetoothServerSocket serverSocket;
    private static final int TIMEOUT = -1;

    public ServerAcceptThread(UUID uuid, BluetoothAdapter mBluetoothAdapter) {
        try {
            serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(mBluetoothAdapter.getName(), uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            BluetoothSocket socket;
            try {
                socket = serverSocket.accept(TIMEOUT);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            if (socket != null) {
                ServerDeviceManager.getInstance().add(socket);
            }
        }
    }

    public void cancel() {
        try {
            interrupt();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(long timeout) {
        if (timeout > 0) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    ServerAcceptThread.this.cancel();
                }
            };
            timer.schedule(task, timeout);
        }
        start();
    }
}
