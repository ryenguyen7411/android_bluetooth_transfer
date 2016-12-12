package com.app.rye.file_browser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.rye.file_browser.data.FileAdapter;
import com.app.rye.file_browser.helper.BluetoothHelper;
import com.app.rye.file_browser.helper.FileHelper;
import com.github.bluzwong.swipeback.SwipeBackActivityHelper;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

public class FileBrowserActivity extends AppCompatActivity {

    SwipeBackActivityHelper helper = new SwipeBackActivityHelper();

    ActionMode              m_actionMode;

    ListView                m_listView;

    ArrayList<File>         m_arrayList;
    FileAdapter             m_arrayAdapter;

    String                  m_currentPath;
    Action                  m_savedAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        helper.setEdgeMode(true)
                .setParallaxMode(true)
                .setParallaxRatio(3)
                .setNeedBackgroundShadow(true)
                .init(this);

        m_currentPath = getIntent().getStringExtra("currentPath");
        m_actionMode = null;

        m_listView = (ListView) findViewById(R.id.lv_main);
        m_arrayList = FileHelper.GetFiles(m_currentPath);
        m_arrayAdapter = new FileAdapter(FileBrowserActivity.this, 1, m_arrayList);

        m_listView.setAdapter(m_arrayAdapter);

        m_listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if(m_arrayAdapter.isSelectedMode()) {
                    m_arrayAdapter.toggleCurrentFile(view, position, m_arrayAdapter.getItem(position));

                    if(m_arrayAdapter.isSelectedMode()) {
                        if(m_actionMode == null) {
                            m_actionMode = FileBrowserActivity.this.startActionMode(new FileBrowserActivity.ActionBarCallback());
                        }
                    } else {
                        m_actionMode.finish();
                        m_actionMode = null;
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
                    if(m_actionMode == null) {
                        m_actionMode = FileBrowserActivity.this.startActionMode(new FileBrowserActivity.ActionBarCallback());
                    }
                } else {
                    m_actionMode.finish();
                    m_actionMode = null;
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
    public void onResume() {
        super.onResume();

        m_arrayAdapter.unselectAll();
        if(m_actionMode != null)
            m_actionMode.finish();

        m_arrayList.clear();
        m_arrayList = FileHelper.GetFiles(m_currentPath);

        m_arrayAdapter.clear();
        m_arrayAdapter.addAll(m_arrayList);
        m_arrayAdapter.notifyDataSetChanged();
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
                case R.id.action_copy: {
                    m_savedAction = Action.ACTION_COPY;

                    Intent intent = new Intent(FileBrowserActivity.this, FileCopyActivity.class);
                    intent.putExtra("selected_file", new Gson().toJson(m_arrayAdapter.getSelectedFile()));
                    intent.putExtra("btn_action", 1);

                    SwipeBackActivityHelper.activityBuilder(FileBrowserActivity.this)
                            .intent(intent)
                            .needParallax(true)
                            .needBackgroundShadow(true)
                            .startActivity();
                    break;
                }
                case R.id.action_cut: {
                    m_savedAction = Action.ACTION_CUT;

                    Intent intent = new Intent(FileBrowserActivity.this, FileCopyActivity.class);
                    intent.putExtra("selected_file", new Gson().toJson(m_arrayAdapter.getSelectedFile()));
                    intent.putExtra("btn_action", 2);

                    SwipeBackActivityHelper.activityBuilder(FileBrowserActivity.this)
                            .intent(intent)
                            .needParallax(true)
                            .needBackgroundShadow(true)
                            .startActivity();
                    break;
                }
                case R.id.action_delete: {
                    m_savedAction = Action.ACTION_DELETE;

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FileBrowserActivity.this)
                            .setTitle("Delete file")
                            .setMessage("Your selected file will be deleted.")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FileHelper.DeleteFiles(m_arrayAdapter.getSelectedFile());
                                    m_arrayAdapter.deleteAllSelected();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(FileBrowserActivity.this, "Your files are safe.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    alertDialog.show();

                    break;
                }
                default:
                    break;
            }

            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.cab_toolbar, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
            m_arrayAdapter.unselectAll();
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub

            return false;
        }
    }
}
