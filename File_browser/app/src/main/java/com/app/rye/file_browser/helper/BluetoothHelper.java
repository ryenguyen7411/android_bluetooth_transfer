package com.app.rye.file_browser.helper;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * Created by ryeng on 12/10/2016.
 */

public class BluetoothHelper {
    public static final int DISCOVER_DURATION = 300;
    public static final int REQUEST_CODE = 1;

    public static Intent createSharedIntent() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null)
            return null;

        Intent discoveryIntent = new Intent((BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);

        return discoveryIntent;
    }
}
