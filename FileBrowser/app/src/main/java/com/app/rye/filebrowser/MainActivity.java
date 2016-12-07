package com.app.rye.filebrowser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.bluzwong.swipeback.SwipeBackActivityHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                SwipeBackActivityHelper.activityBuilder(MainActivity.this)
                        .intent(intent)
                        .needParallax(true)
                        .needBackgroundShadow(true)
                        .startActivity();
            }
        });


    }
}
