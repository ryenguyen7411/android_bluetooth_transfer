package com.android.rye.bluetoothtransfer;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class Send_File extends AppCompatActivity {

    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void SendBlutooth(View v) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, " Blutooth is not supppored on this device", Toast.LENGTH_LONG).show();
        } else {
            enableBlutooth();
        }
    }

    public void enableBlutooth() {
        Log.e(TAG, "enableBlutooth: ");
        Intent discoveryIntent = new Intent((BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: ");
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
            Intent intent = new Intent();
            intent.setAction((Intent.ACTION_SEND));

            //send audio file
            intent.setType("audio/*");


            File f = new File(Environment.getExternalStorageDirectory(), "dfdfd");
            Log.e(TAG, "onActivityResult: " + f.getAbsolutePath());
            if (f.exists()) {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> appsList = packageManager.queryIntentActivities(intent, 0);
                if ((appsList.size() > 0)) {
                    String packageName = null;
                    String ClassName = null;
                    boolean found = false;
                    for (ResolveInfo info : appsList) {
                        packageName = info.activityInfo.packageName;
                        if ((packageName.equals("com.android.bluetooth"))) {
                            ClassName = info.activityInfo.name;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Toast.makeText(this, " Blutooth  have been not found", Toast.LENGTH_LONG).show();
                    } else {
                        intent.setClassName(packageName, ClassName);
                        startActivity(intent);
                    }

                }

            } else {
                Toast.makeText(this, " Blutooth is canceld", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "File Not Found", Toast.LENGTH_LONG).show();
        }


    }
}
