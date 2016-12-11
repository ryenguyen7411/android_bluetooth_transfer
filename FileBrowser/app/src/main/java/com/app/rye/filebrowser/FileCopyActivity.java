package com.app.rye.filebrowser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.github.bluzwong.swipeback.SwipeBackActivityHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FileCopyActivity extends AppCompatActivity {

    SwipeBackActivityHelper helper = new SwipeBackActivityHelper();

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
        ArrayList<File> selectedFiles = (ArrayList<File>) new Gson().fromJson(data, listType);

        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.finish();
            }
        });

        Button btn_action = (Button) findViewById(R.id.btn_action);
        btn_action.setText(getIntent().getStringExtra("btn_action"));
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        helper.finish();
    }
}
