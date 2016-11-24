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

        m_listView = (ListView) findViewById(R.id.lv_main);

        m_arrayList = FileHelper.GetFiles(Environment.getExternalStorageDirectory().getPath());
        m_arrayAdapter = new RFileAdapter(MainActivity.this, 1, m_arrayList);

        m_listView.setAdapter(m_arrayAdapter);

        m_listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                if(m_arrayAdapter.isCheckable() != true) {
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
                } else {
                    if(!m_arrayAdapter.isChecked(view)) {
                        m_arrayAdapter.toggleCurrentRFile(view, true, m_arrayAdapter.getItem(position));
                    } else {
                        m_arrayAdapter.toggleCurrentRFile(view, false, m_arrayAdapter.getItem(position));
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

                if(m_arrayAdapter.isCheckable() != true) {
                    m_arrayAdapter.setCheckBoxCheckable(true);

//                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.rfileCheckBox);
//                    checkBox.setVisibility(View.VISIBLE);

                    m_arrayAdapter.toggleCurrentRFile(view, true, m_arrayAdapter.getItem(position));
                } else {
                    if(!m_arrayAdapter.isChecked(view)) {
                        m_arrayAdapter.toggleCurrentRFile(view, true, m_arrayAdapter.getItem(position));
                    } else {
                        m_arrayAdapter.toggleCurrentRFile(view, false, m_arrayAdapter.getItem(position));
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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


    public void enableBlutooth() {
        Intent discoveryIntent = new Intent((BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
            Intent intent = new Intent();
            intent.setAction((Intent.ACTION_SEND));

            //send audio file
//            intent.setType("audio/*");
            intent.setType("*/*");


            File f = new File( path);
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
