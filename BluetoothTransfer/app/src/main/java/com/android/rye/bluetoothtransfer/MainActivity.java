package com.android.rye.bluetoothtransfer;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

    List<RFile>             m_arrayList;
    RFileAdapter            m_arrayAdapter;
    ListView                m_listView;

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

        m_arrayList = FileHelper.GetFiles(Environment.getExternalStorageDirectory().getAbsolutePath());
        m_arrayAdapter = new RFileAdapter(MainActivity.this, 1, m_arrayList);

        m_listView.setAdapter(m_arrayAdapter);

        m_listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String _targetPath = m_arrayAdapter.getItem(position).getPath();

                m_arrayList.clear();
                m_arrayList = FileHelper.GetFiles(_targetPath);

                m_arrayAdapter.addAll(m_arrayList);
                m_arrayAdapter.notifyDataSetChanged();
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
}
