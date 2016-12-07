package com.app.rye.filebrowser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.bluzwong.swipeback.SwipeBackActivityHelper;

public class SubActivity extends AppCompatActivity {

    SwipeBackActivityHelper helper = new SwipeBackActivityHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        helper.setEdgeMode(true)
                .setParallaxMode(true)
                .setParallaxRatio(3)
                .setNeedBackgroundShadow(true)
                .init(this);
    }

    @Override
    public void onBackPressed() {
        helper.finish();
    }
}
