package com.app.rye.filebrowser;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.app.rye.filebrowser.data.FileAdapter;
import com.app.rye.filebrowser.helper.FileHelper;
import com.github.bluzwong.swipeback.SwipeBackActivityHelper;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView            m_listView;

    ArrayList<File>     m_arrayList;
    FileAdapter         m_arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_listView = (ListView) findViewById(R.id.lv_main);
        m_arrayList = FileHelper.GetFiles(Environment.getExternalStorageDirectory().getPath());
        m_arrayAdapter = new FileAdapter(MainActivity.this, 1, m_arrayList);

        m_listView.setAdapter(m_arrayAdapter);

        m_listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if(m_arrayAdapter.isSelectedMode()) {
                    m_arrayAdapter.toggleCurrentFile(view, position, m_arrayAdapter.getItem(position));
                } else {
                    String currentItemPath = m_arrayAdapter.getItem(position).getPath();
                    File currentItem = new File(currentItemPath);

                    if(currentItem.isDirectory()) {
                        Intent intent = new Intent(MainActivity.this, FileBrowserActivity.class);
                        intent.putExtra("currentPath", currentItemPath);

                        SwipeBackActivityHelper.activityBuilder(MainActivity.this)
                                                .intent(intent)
                                                .needParallax(true)
                                                .needBackgroundShadow(true)
                                                .startActivity();
                    } else if(currentItem.isFile()) {
                        startActivity(FileHelper.OpenFile(currentItemPath));
                    }
                }
            }
        });

        m_listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                if(m_arrayAdapter.isSelectedMode()) {
                    m_arrayAdapter.toggleCurrentFile(view, position, m_arrayAdapter.getItem(position));
                } else {
                    m_arrayAdapter.toggleSelectedMode();
                    m_arrayAdapter.toggleCurrentFile(view, position, m_arrayAdapter.getItem(position));
                }

                return true;
            }
        });
    }
}
