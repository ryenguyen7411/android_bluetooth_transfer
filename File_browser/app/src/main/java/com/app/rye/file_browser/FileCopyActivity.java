package com.app.rye.file_browser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.rye.file_browser.data.FileAdapter;
import com.app.rye.file_browser.helper.FileHelper;
import com.github.bluzwong.swipeback.SwipeBackActivityHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FileCopyActivity extends AppCompatActivity {

    SwipeBackActivityHelper helper = new SwipeBackActivityHelper();

    ListView            m_listView;

    ArrayList<File>     m_arrayList;
    FileAdapter         m_arrayAdapter;

    ArrayList<File>     m_selectedFiles;
    String              m_currentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_copy);

        helper.setEdgeMode(true)
                .setParallaxMode(true)
                .setParallaxRatio(3)
                .setNeedBackgroundShadow(true)
                .init(this);

        String data = getIntent().getStringExtra("selected_file");
        Type listType = new TypeToken<ArrayList<File>>(){}.getType();
        m_selectedFiles = (ArrayList<File>) new Gson().fromJson(data, listType);

        m_currentFolder = Environment.getExternalStorageDirectory().getPath();

        m_listView = (ListView) findViewById(R.id.lv_main);
        m_arrayList = FileHelper.GetFiles(m_currentFolder);
        m_arrayAdapter = new FileAdapter(FileCopyActivity.this, 1, m_arrayList);

        m_listView.setAdapter(m_arrayAdapter);
        m_listView.setScrollingCacheEnabled(false);

        m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentItemPath = m_arrayAdapter.getItem(position).getPath();
                File currentItem = new File(currentItemPath);

                if (currentItem.isDirectory()) {
                    m_arrayList.clear();
                    m_arrayList = FileHelper.GetFiles(currentItemPath);

                    m_currentFolder = currentItemPath;

                    m_arrayAdapter.clear();
                    m_arrayAdapter.addAll(m_arrayList);
                    m_arrayAdapter.notifyDataSetChanged();
                } else if (currentItem.isFile()) {
                    startActivity(FileHelper.OpenFile(currentItemPath));
                }
            }
        });

        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.finish();
            }
        });

        final int action = getIntent().getIntExtra("btn_action", 0);
        Button btn_action = (Button) findViewById(R.id.btn_action);
        btn_action.setText(action == 1 ? "Paste here" : "Move here");
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (action) {
                    case 1:
                        FileHelper.CopyFiles(m_selectedFiles, m_currentFolder, FileCopyActivity.this);
                        break;
                    case 2:
                        FileHelper.MoveFiles(m_selectedFiles, m_currentFolder);
                        break;
                    default:
                        break;
                }

                Toast.makeText(FileCopyActivity.this, "Success.", Toast.LENGTH_LONG).show();
                helper.finish();
            }
        });

        Button btn_createFolder = (Button) findViewById(R.id.btn_create_folder);
        btn_createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(FileCopyActivity.this);
                View dialog = li.inflate(R.layout.dlg_create_folder, null);
                final EditText et_create_folder = (EditText) dialog.findViewById(R.id.et_create_folder);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FileCopyActivity.this)
                        .setView(dialog)
                        .setCancelable(false)
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FileHelper.CreateFolder(m_currentFolder + "/" + et_create_folder.getText().toString());

                                m_arrayList.clear();
                                m_arrayList = FileHelper.GetFiles(m_currentFolder);

                                m_arrayAdapter.clear();
                                m_arrayAdapter.addAll(m_arrayList);
                                m_arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                 alertDialog.create().show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        helper.finish();
    }
}
