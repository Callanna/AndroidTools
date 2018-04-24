package callannna.bluelibrary.socket;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import callannna.bluelibrary.Hex;
import callannna.bluelibrary.dao.ServerMsgListener;

/**
 * Description  服务器端设备连接监听
 * Created by chenqiao on 2016/6/27.
 */
public class ServerDeviceManager {


    private static ServerDeviceManager instance;

    private ArrayList<BluetoothSocket> connectedSockets;
    private ExecutorService singleThread;
    private HashMap<String, ReadThread> readmap = new HashMap<>();
    private HashMap<String, OutputStream> sendmap = new HashMap<>();
    private ServerMsgListener serverMsgListener;
    private SocketConnectListener listener;

    private ServerDeviceManager(){
        connectedSockets = new ArrayList<>();
        singleThread = Executors.newSingleThreadExecutor();
    }

    public static ServerDeviceManager getInstance() {
        if (instance == null) {
            synchronized (ServerDeviceManager.class) {
                if (instance == null) {
                    instance = new ServerDeviceManager();
                }
            }
        }
        return instance;
    }

    public int size() {
        return connectedSockets.size();
    }

    public void add(BluetoothSocket socket) {
        connectedSockets.add(socket);
        if (serverMsgListener != null){
            serverMsgListener.connect(socket.getRemoteDevice());
        }
        if (listener != null) {
            listener.clientConnected(socket);
            ReadThread readThread = new ReadThread(socket);
            readmap.put(socket.getRemoteDevice().getAddress(), readThread);
            try {
                sendmap.put(socket.getRemoteDevice().getAddress(),socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            readThread.start();
        }
    }
    public void sendCommand(final String address,final byte[] cmd) {
        if (singleThread != null && sendmap.get(address) != null) {
            singleThread.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Clue Client send:" + Hex.bytesToHexString(cmd));
                        sendmap.get(address) .write(cmd);
                        sendmap.get(address) .flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    public void remove(BluetoothSocket socket) {
        connectedSockets.remove(socket);
        if (serverMsgListener != null){
            serverMsgListener.disConnect(socket.getRemoteDevice());
        }
        readmap.remove(socket.getRemoteDevice().getAddress());
        sendmap.remove(socket.getRemoteDevice().getAddress());
    }

    public void clearAll() {
        for (BluetoothSocket socket : connectedSockets) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connectedSockets.clear();
        readmap.clear();
    }

    public void setClientConnectListener(SocketConnectListener listener) {
        this.listener = listener;
    }

    public void setServerMsgListener(ServerMsgListener serverMsgListener) {
        this.serverMsgListener = serverMsgListener;
    }

    public void removeListener() {
        listener = null;
    }

    public interface SocketConnectListener {
        void clientConnected(BluetoothSocket socket);
    }

    private class ReadThread extends Thread {
        private BluetoothDevice device;
        private InputStream input;
        private String server;

        public ReadThread(BluetoothSocket socket) {

            try {
                this.device = socket.getRemoteDevice();
                this.input = socket.getInputStream();
                this.server = socket.getRemoteDevice().getName() + socket.getRemoteDevice().getAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            byte[] data = new byte[100];
            int len;
            try {
                while (!isInterrupted() && (len = input.read(data)) >= 0) {
                    byte[] temp = Arrays.copyOfRange(data, 0, len);
                    System.out.println("Server " + server + "  receive:" + Hex.bytesToHexString(temp));
                    if (serverMsgListener != null){
                        serverMsgListener.readMsg(device,temp);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
