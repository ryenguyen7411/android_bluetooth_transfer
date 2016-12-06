package com.android.rye.bluetoothtransfer;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rye.bluetoothtransfer.data.RFile;
import com.android.rye.bluetoothtransfer.data.RFileAdapter;
import com.android.rye.bluetoothtransfer.helper.FileHelper;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMenuItemClickListener {

    ArrayList<RFile>        m_arrayList;
    RFileAdapter            m_arrayAdapter;
    ListView                m_listView;

    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    private static final String TAG = "MainActivity";

    private android.support.v4.app.FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();

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
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        this.m_menu = menu;
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
////        switch(id) {
////            case R.id.action_settings:
////                return true;
////            case R.id.action_shared:
////                if(!m_arrayAdapter.isShareable()) {
////                    Toast.makeText(this, "Cannot shared folder.", Toast.LENGTH_LONG).show();
////                } else {
////                    if(shareFiles() == false) {
////                        Toast.makeText(this, "Your device is not support bluetooth.", Toast.LENGTH_LONG).show();
////                    }
////                }
////                return true;
////            default:
////                break;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

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



    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.mipmap.icn_close);

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.mipmap.icn_1);

        MenuObject like = new MenuObject("Like profile");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.icn_2);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.mipmap.icn_3));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.mipmap.icn_4);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.mipmap.icn_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
        return menuObjects;
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationIcon(R.mipmap.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolBarTextView.setText("Samantha");
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onMenuItemLongClick(View clickedView, int position) {
//        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
//    }
}
