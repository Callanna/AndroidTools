package callannna.bluelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.pbap.BluetoothPbapClient;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.android.vcard.VCardEntry;

import java.util.ArrayList;

import callannna.bluelibrary.dao.ClientAction;
import callannna.bluelibrary.dao.ConnectListener;

/**
 * Description
 * Created by chenqiao on 2016/6/28.
 */
public class ConnectDelegate {

    public static final int TYPE_PBAP = 1;

    private ClientAction<ArrayList<VCardEntry>> client;

    public ConnectDelegate() {

    }

    public ClientAction connect(int type, BluetoothDevice device, final ConnectListener connectListener) {
        switch (type) {
            case TYPE_PBAP:
                HandlerThread thread = new HandlerThread("BluetoothPbapClient");
                thread.start();
                Handler handler = new Handler(thread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case BluetoothPbapClient.EVENT_SESSION_CONNECTED:
                                if (connectListener != null) {
                                    connectListener.connect(null);
                                }
                                break;
                            case BluetoothPbapClient.EVENT_SESSION_DISCONNECTED:
                                if (connectListener != null) {
                                    connectListener.disconnect();
                                }
                                break;
                            case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_DONE: {
                                if (msg.obj instanceof ArrayList) {
                                    ArrayList<VCardEntry> entrys = (ArrayList<VCardEntry>) msg.obj;
                                    //TODO 将结果发送出去
                                    if (client != null) {
                                        client.notifyResult(entrys);
                                    }
                                }
                            }
                            break;
                        }
                    }
                };
                client = new BluetoothPbapClient(device, handler);
                client.connect();
                return client;
            default:
                return null;
        }
    }
}
