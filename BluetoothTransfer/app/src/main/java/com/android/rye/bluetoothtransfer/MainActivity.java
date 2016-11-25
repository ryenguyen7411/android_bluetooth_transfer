package com.android.rye.bluetoothtransfer;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.android.rye.bluetoothtransfer.data.RFile;
import com.android.rye.bluetoothtransfer.data.RFileAdapter;
import com.android.rye.bluetoothtransfer.helper.FileHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<RFile>        m_arrayList;
    RFileAdapter            m_arrayAdapter;
    ListView                m_listView;

    Menu                    m_menu;

     String                 path;

    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        /* Init variable - S */
        m_listView = (ListView) findViewById(R.id.lv_main);

        m_arrayList = FileHelper.GetFiles(Environment.getExternalStorageDirectory().getPath());
        m_arrayAdapter = new RFileAdapter(MainActivity.this, 1, m_arrayList);

        m_listView.setAdapter(m_arrayAdapter);
        /* Init variable - E */

        m_listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if(m_arrayAdapter.isCheckable() != true) { /* File/folder browsing */
                    String currentItemPath = m_arrayAdapter.getItem(position).getPath();
                    File currentItem = new File(currentItemPath);

                    if(currentItem.isDirectory()) {
                        m_arrayList.clear();
                        m_arrayList = FileHelper.GetFiles(currentItemPath);

                        m_arrayAdapter.clear();
                        m_arrayAdapter.addAll(m_arrayList);
                        m_arrayAdapter.notifyDataSetChanged();
                    } else if(currentItem.isFile()) {
                        startActivity(FileHelper.OpenFile(currentItemPath));
                    }
                } else { /* Selected mode */
                    if(!m_arrayAdapter.isChecked(view)) {
                        m_arrayAdapter.toggleCurrentRFile(view, true, m_arrayAdapter.getItem(position));
                    } else {
                        m_arrayAdapter.toggleCurrentRFile(view, false, m_arrayAdapter.getItem(position));

                        if(m_arrayAdapter.getCheckedList().size() == 0) {
                            m_arrayAdapter.setCheckBoxCheckable(false);
                            m_arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }

//                String _targetPath = m_arrayAdapter.getItem(position).getPath();
//                if(!TextUtils.isEmpty(_targetPath)){
//                    File file = new File(_targetPath);
//                    if(file.isDirectory()){
//                        m_arrayList = FileHelper.GetFiles(_targetPath);
//                        if(m_arrayList != null && m_arrayList.size() != 0){
//                            m_arrayList.clear();
//                            m_arrayList = FileHelper.GetFiles(_targetPath);
//
//                            m_arrayAdapter.clear();
//                            m_arrayAdapter.addAll(m_arrayList);
//                            m_arrayAdapter.notifyDataSetChanged();
//                        }
//                    } else {
//
//                        Log.e(TAG, "onItemClick: ");
//                        Toast.makeText(MainActivity.this, "Send File", Toast.LENGTH_LONG).show();
//                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                        path= _targetPath;
//                        if (bluetoothAdapter == null) {
//
//                        } else {
//                            enableBlutooth();
//                        }
//
//                    }
//                }
            }
        });

        m_listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(m_arrayAdapter.isCheckable() != true) { /* Start selected mode */
                    m_arrayAdapter.setCheckBoxCheckable(true);
                    m_arrayAdapter.toggleCurrentRFile(view, true, m_arrayAdapter.getItem(position));
                } else { /* Selected mode */
                    if(!m_arrayAdapter.isChecked(view)) {
                        m_arrayAdapter.toggleCurrentRFile(view, true, m_arrayAdapter.getItem(position));
                    } else {
                        m_arrayAdapter.toggleCurrentRFile(view, false, m_arrayAdapter.getItem(position));

                        if(m_arrayAdapter.getCheckedList().size() == 0) {
                            m_arrayAdapter.setCheckBoxCheckable(false);
                            m_arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }

                return true;
            }
        });

//        Button btn = (Button) findViewById(R.id.button_temp);
//        btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BTransfers";
//                try {
//                    splitFile(path);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        Button btn2 = (Button) findViewById(R.id.button);
//        btn2.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BTransfers";
//                try {
//                    joinFiles(path);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        m_listView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                if (bluetoothAdapter == null) {
//                    Toast.makeText(this, " Blutooth is not supppored on this device", Toast.LENGTH_LONG).show();
//                } else {
//                    enableBlutooth();
//                }
//
//                return false;
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.m_menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_shared:
                if(!m_arrayAdapter.isShareable()) {
                    Toast.makeText(this, "Cannot shared folder.", Toast.LENGTH_LONG).show();
                } else {
                    if(shareFiles() == false) {
                        Toast.makeText(this, "Your device is not support bluetooth.", Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void splitFile(String filePath) throws IOException {
        File f = new File(filePath, "apk_file.apk");
        if(!f.isFile()){
            return;
        }

        int partCounter = 1;

        int sizeOfFiles = 1024 * 1024;// 1MB
        byte[] buffer = new byte[sizeOfFiles];

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
            String name = f.getName();

            int tmp = 0;
            while ((tmp = bis.read(buffer)) > 0) {
                //write each chunk of data into separate file with different number in name
                File newFile = new File(f.getParent(), name + "." + String.format("%03d", partCounter++));
                newFile.createNewFile();
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, tmp);//tmp is chunk size
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void joinFiles(String filePath) throws IOException {
        int partCounter = 1;

        int sizeOfFiles = 1024 * 1024;// 1MB

        while(true) {
            File f = new File(filePath, "apk_file.apk" + "." + String.format("%03d", partCounter++));
            if(!f.isFile()){
                return;
            }

            int fileSize = (int)f.length();
            byte[] buffer = new byte[fileSize];

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
                bis.read(buffer);

                try (FileOutputStream out = new FileOutputStream(new File(f.getParent() + "/apk_file_new.apk"), true)) {
                    out.write(buffer);
                    out.close();
                }
            }
        }
    }

    public boolean shareFiles() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null)
            return false;

        enableBlutooth();

        return true;
    }


    public void enableBlutooth() {
        Intent discoveryIntent = new Intent((BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
            Intent intent = new Intent();
            intent.setAction((Intent.ACTION_SEND_MULTIPLE));
            intent.setType("*/*");

            ArrayList<Uri> uris = new ArrayList<Uri>();
            ArrayList<RFile> files = m_arrayAdapter.getCheckedList();

            for(int i = 0; i < files.size(); i++) {
                File file = new File(files.get(i).getPath());
                uris.add(Uri.fromFile(file));
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            this.startActivity(intent);
        } else {
            Toast.makeText(this, "File Not Found", Toast.LENGTH_LONG).show();
        }
    }
}
