package com.app.rye.filebrowser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.app.rye.filebrowser.data.FileAdapter;
import com.app.rye.filebrowser.helper.BluetoothHelper;
import com.app.rye.filebrowser.helper.FileHelper;
import com.github.bluzwong.swipeback.SwipeBackActivityHelper;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    enum ToolbarMode {
        MODE_NONE,
        MODE_SELECTED,
        MODE_COPY
    }

    ActionMode          m_actionMode;
    ToolbarMode         m_mode;

    ListView            m_listView;

    ArrayList<File>     m_arrayList;
    FileAdapter         m_arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_mode = ToolbarMode.MODE_NONE;

        m_listView = (ListView) findViewById(R.id.lv_main);
        m_arrayList = FileHelper.GetFiles(Environment.getExternalStorageDirectory().getPath());
        m_arrayAdapter = new FileAdapter(MainActivity.this, 1, m_arrayList);

        m_listView.setAdapter(m_arrayAdapter);

        m_listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (m_arrayAdapter.isSelectedMode()) {
                    m_arrayAdapter.toggleCurrentFile(view, position, m_arrayAdapter.getItem(position));

                    if(m_arrayAdapter.isSelectedMode()) {
                        m_mode = ToolbarMode.MODE_SELECTED;
                        m_actionMode = MainActivity.this.startActionMode(new ActionBarCallback());
                    } else {
                        m_mode = ToolbarMode.MODE_NONE;
                        m_actionMode.finish();
                    }
                } else {
                    String currentItemPath = m_arrayAdapter.getItem(position).getPath();
                    File currentItem = new File(currentItemPath);

                    if (currentItem.isDirectory()) {
                        Intent intent = new Intent(MainActivity.this, FileBrowserActivity.class);
                        intent.putExtra("currentPath", currentItemPath);

                        SwipeBackActivityHelper.activityBuilder(MainActivity.this)
                                .intent(intent)
                                .needParallax(true)
                                .needBackgroundShadow(true)
                                .startActivity();
                    } else if (currentItem.isFile()) {
                        startActivity(FileHelper.OpenFile(currentItemPath));
                    }
                }
            }
        });

        m_listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (m_arrayAdapter.isSelectedMode()) {
                    m_arrayAdapter.toggleCurrentFile(view, position, m_arrayAdapter.getItem(position));
                } else {
                    m_arrayAdapter.toggleSelectedMode();
                    m_arrayAdapter.toggleCurrentFile(view, position, m_arrayAdapter.getItem(position));
                }

                if(m_arrayAdapter.isSelectedMode()) {
                    m_mode = ToolbarMode.MODE_SELECTED;
                    m_actionMode = MainActivity.this.startActionMode(new ActionBarCallback());
                } else {
                    m_mode = ToolbarMode.MODE_NONE;
                    m_actionMode.finish();
                }

                return true;
            }
        });
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
                        Toast.makeText(MainActivity.this, "Cannot shared folder.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent sharedIntent = BluetoothHelper.createSharedIntent();

                        if(sharedIntent != null) {
                            startActivityForResult(BluetoothHelper.createSharedIntent(), BluetoothHelper.REQUEST_CODE);
                        } else {
                            Toast.makeText(MainActivity.this, "Your device is not support bluetooth.", Toast.LENGTH_LONG).show();
                        }
                    }
                case R.id.action_copy: {
                    Intent intent = new Intent(MainActivity.this, FileCopyActivity.class);
                    intent.putExtra("selected_file", new Gson().toJson(m_arrayAdapter.getSelectedFile()));
                    intent.putExtra("btn_action", 1);

                    SwipeBackActivityHelper.activityBuilder(MainActivity.this)
                            .intent(intent)
                            .needParallax(true)
                            .needBackgroundShadow(true)
                            .startActivity();
                    break;
                }
                case R.id.action_cut: {
                    Intent intent = new Intent(MainActivity.this, FileCopyActivity.class);
                    intent.putExtra("selected_file", new Gson().toJson(m_arrayAdapter.getSelectedFile()));
                    intent.putExtra("btn_action", 2);

                    SwipeBackActivityHelper.activityBuilder(MainActivity.this)
                            .intent(intent)
                            .needParallax(true)
                            .needBackgroundShadow(true)
                            .startActivity();
                    break;
                }
                case R.id.action_delete: {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete file")
                            .setMessage("Your selected file will be deleted.")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FileHelper.DeleteFiles(m_arrayAdapter.getSelectedFile());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MainActivity.this, "Your files are safe.", Toast.LENGTH_SHORT).show();
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
