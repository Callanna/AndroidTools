package com.ebanswers.yitian.btcontrol;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import org.simple.eventbus.EventBus;
import callannna.bluelibrary.dao.ClientMsgListener;
import callannna.bluelibrary.dao.ConnectListener;
import callannna.bluelibrary.dao.ServerMsgListener;
import callannna.bluelibrary.socket.ClientDeviceManager;
import callannna.bluelibrary.socket.ServerDeviceManager;

/**
 * Created by Callanna on 2017/3/17.
 */

public class CoreApplication extends Application {

    private static CoreApplication _application = null;
    public String connectMac = "";
    private Handler handler = new Handler();
    public ConnectListener stoveConnectListener = new ConnectListener() {
        @Override
        public void connect(BluetoothSocket socket) {

        }

        @Override
        public void disconnect() {

        }
    };

    private int address = 0;
    public ClientMsgListener msgConnectListener = new ClientMsgListener() {
        @Override
        public void connect(BluetoothDevice device) {
            connectMac = device.getAddress();
            Log.d("duanyl", "connect: "+connectMac);
            EventBus.getDefault().post("","connected");
            ClientDeviceManager.getInstence().sendCommand("Hello1".getBytes());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Connect Success", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void readMsg(byte[] msg) {
            Log.d("duanyl", "CreadMsg: " + new String(msg));
            ServerDeviceManager.getInstance().sendCommand(CoreApplication.getInstance().connectMac,"Hi !".getBytes());
        }

        @Override
        public void disConnect() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                }
            }) ;
        }
    };

    /**
     * instance
     */
    public static CoreApplication getInstance() {
        return _application;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        _application = this;
        ServerDeviceManager.getInstance().setClientConnectListener(new ServerDeviceManager.SocketConnectListener() {
            @Override
            public void clientConnected(BluetoothSocket socket) {
                Log.d("duanyl", "clientConnected: "+socket.getRemoteDevice().getName());
            }
        });
        ServerDeviceManager.getInstance().setServerMsgListener(new ServerMsgListener() {
            @Override
            public void connect(BluetoothDevice device) {
                EventBus.getDefault().post("","connected");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connected Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void readMsg(BluetoothDevice device, byte[] msg) {
                Log.d("duanyl", "SreadMsg: " +new String(msg) + "mac:"+device.getAddress());
                ServerDeviceManager.getInstance().sendCommand(device.getAddress(),"Hello2!".getBytes());
            }

            @Override
            public void disConnect(BluetoothDevice device) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                    }
                }) ;
            }
        });
    }
}
