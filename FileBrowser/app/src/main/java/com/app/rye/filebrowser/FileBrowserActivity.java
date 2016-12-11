package com.app.rye.filebrowser;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.rye.filebrowser.data.FileAdapter;
import com.app.rye.filebrowser.helper.BluetoothHelper;
import com.app.rye.filebrowser.helper.FileHelper;
import com.github.bluzwong.swipeback.SwipeBackActivityHelper;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

public class FileBrowserActivity extends AppCompatActivity {

    SwipeBackActivityHelper helper = new SwipeBackActivityHelper();

    ActionMode                  m_actionMode;
    MainActivity.ToolbarMode    m_mode;

    ListView                m_listView;

    ArrayList<File>         m_arrayList;
    FileAdapter             m_arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_browser);

        helper.setEdgeMode(true)
                .setParallaxMode(true)
                .setParallaxRatio(3)
                .setNeedBackgroundShadow(true)
                .init(this);

        m_mode = MainActivity.ToolbarMode.MODE_NONE;

        m_listView = (ListView) findViewById(R.id.lv_main);
        m_arrayList = FileHelper.GetFiles(getIntent().getStringExtra("currentPath"));
        m_arrayAdapter = new FileAdapter(FileBrowserActivity.this, 1, m_arrayList);

        m_listView.setAdapter(m_arrayAdapter);

        m_listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if(m_arrayAdapter.isSelectedMode()) {
                    m_arrayAdapter.toggleCurrentFile(view, position, m_arrayAdapter.getItem(position));

                    if(m_arrayAdapter.isSelectedMode()) {
                        m_mode = MainActivity.ToolbarMode.MODE_SELECTED;
                        m_actionMode = FileBrowserActivity.this.startActionMode(new FileBrowserActivity.ActionBarCallback());
                    } else {
                        m_mode = MainActivity.ToolbarMode.MODE_NONE;
                        m_actionMode.finish();
                    }
                } else {
                    String currentItemPath = m_arrayAdapter.getItem(position).getPath();
                    File currentItem = new File(currentItemPath);

                    if(currentItem.isDirectory()) {
                        Intent intent = new Intent(FileBrowserActivity.this, FileBrowserActivity.class);
                        intent.putExtra("currentPath", currentItemPath);

                        SwipeBackActivityHelper.activityBuilder(FileBrowserActivity.this)
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

                if(m_arrayAdapter.isSelectedMode()) {
                    m_mode = MainActivity.ToolbarMode.MODE_SELECTED;
                    m_actionMode = FileBrowserActivity.this.startActionMode(new FileBrowserActivity.ActionBarCallback());
                } else {
                    m_mode = MainActivity.ToolbarMode.MODE_NONE;
                    m_actionMode.finish();
                }

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        helper.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == BluetoothHelper.DISCOVER_DURATION && requestCode == BluetoothHelper.REQUEST_CODE) {
            Intent intent = new Intent();
            intent.setAction((Intent.ACTION_SEND_MULTIPLE));
            intent.setType("*/*");

            ArrayList<Uri> uris = new ArrayList<Uri>();
            ArrayList<File> files = m_arrayAdapter.getSelectedFile();

            for(int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                uris.add(Uri.fromFile(file));
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            this.startActivity(intent);
        } else {
            Toast.makeText(this, "File Not Found", Toast.LENGTH_LONG).show();
        }
    }

    class ActionBarCallback implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()) {
                case R.id.action_share:
                    if(!m_arrayAdapter.isShareable()) {
                        Toast.makeText(FileBrowserActivity.this, "Cannot shared folder.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent sharedIntent = BluetoothHelper.createSharedIntent();

                        if(sharedIntent != null) {
                            startActivityForResult(BluetoothHelper.createSharedIntent(), BluetoothHelper.REQUEST_CODE);
                        } else {
                            Toast.makeText(FileBrowserActivity.this, "Your device is not support bluetooth.", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case R.id.action_copy:
                    Intent intent = new Intent(FileBrowserActivity.this, FileCopyActivity.class);
                    intent.putExtra("selected_file", new Gson().toJson(m_arrayAdapter.getSelectedFile()));
                    intent.putExtra("btn_action", "Copy here");

                    SwipeBackActivityHelper.activityBuilder(FileBrowserActivity.this)
                            .intent(intent)
                            .needParallax(true)
                            .needBackgroundShadow(true)
                            .startActivity();
                default:
                    break;
            }

            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            switch (m_mode) {
                case MODE_SELECTED:
                    mode.getMenuInflater().inflate(R.menu.cab_toolbar, menu);
                    break;
                case MODE_COPY:
                    mode.getMenuInflater().inflate(R.menu.cab_toolbar_copy, menu);
                    break;
                default:
                    break;
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub

            return false;
        }
    }
}
