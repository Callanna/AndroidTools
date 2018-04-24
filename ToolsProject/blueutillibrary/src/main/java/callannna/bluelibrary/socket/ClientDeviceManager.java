package callannna.bluelibrary.socket;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import callannna.bluelibrary.Hex;
import callannna.bluelibrary.dao.ConnectListener;
import callannna.bluelibrary.dao.ClientMsgListener;

/**
 * Created by Callanna on 2017/3/23.
 */

public class ClientDeviceManager implements ConnectListener {

    private static ClientDeviceManager clientDeviceManager;
    private InputStream input;
    private OutputStream output;
    private ExecutorService singleThread;
    private Thread readThread;
    private ClientMsgListener msgListener;
    private ClientDeviceManager(){
        singleThread = Executors.newSingleThreadExecutor();
    }

    public static ClientDeviceManager getInstence(){
        synchronized (ClientDeviceManager.class) {
            if (clientDeviceManager == null) {
                clientDeviceManager = new ClientDeviceManager();
            }
        }
        return clientDeviceManager;
    }

    public void setMsgListener(ClientMsgListener msgListener) {
        this.msgListener = msgListener;
    }

    @Override
    public void connect(BluetoothSocket socket) {
        if(this.msgListener != null){
            this.msgListener.connect(socket.getRemoteDevice());
        }
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initRead();
    }

    private void initRead() {
        if (input != null) {
            readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] data = new byte[100];
                    int len;
                    try {
                        while (!readThread.isInterrupted() && (len = input.read(data)) >= 0) {
                            byte[] temp = Arrays.copyOfRange(data, 0, len);
                            System.out.println("Blue Client receive:" + Hex.bytesToHexString(temp));
                            msgListener.readMsg(temp);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            readThread.start();
        }

    }

    @Override
    public void disconnect() {
     if(this.msgListener != null){
         this.msgListener.disConnect();
     }
    }

    public void sendCommand(final byte[] cmd) {
        if (singleThread != null && output != null) {
            singleThread.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Clue Client send:" + Hex.bytesToHexString(cmd));
                        output.write(cmd);
                        output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
