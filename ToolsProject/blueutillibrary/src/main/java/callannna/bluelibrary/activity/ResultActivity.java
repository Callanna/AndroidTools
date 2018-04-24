package callannna.bluelibrary.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import callannna.bluelibrary.R;
import callannna.bluelibrary.dao.BluetoothEnableListener;

/**
 * Description
 * Created by chenqiao on 2016/6/24.
 */
public class ResultActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 0x99;
    private static final int REQUEST_AVAILABLE = 0xAA;
    private LinearLayout wait;
    public static BluetoothEnableListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        wait = (LinearLayout) findViewById(R.id.wait_dialog);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        switch (type) {
            case 1:
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                break;
            case 2:
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                int time = intent.getIntExtra("availableTime", 120);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
                startActivityForResult(discoverableIntent, REQUEST_AVAILABLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                switch (resultCode) {
                    case RESULT_OK:
                        Toast.makeText(this, R.string.enable_ok, Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.enable();
                        }
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(this, R.string.enable_cancel, Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.disable();
                        }
                        break;
                }
                finish();
                break;
            case REQUEST_AVAILABLE:
                switch (resultCode) {
                    case RESULT_CANCELED:
                        Toast.makeText(this, R.string.set_failed, Toast.LENGTH_SHORT).show();
                        break;
                }
                finish();
                break;
        }
    }
}
