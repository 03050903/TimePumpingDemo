package com.jusfoun.timepumpingdemo;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private TimePumpingView time;
    private CustomeScrollView scroll;


    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String string = Utils.getString(getResources().openRawResource(R.raw.temp));
        TimeModel model = new Gson().fromJson(string, TimeModel.class);

        time = (TimePumpingView) findViewById(R.id.time);
        scroll = (CustomeScrollView) findViewById(R.id.scroll);

        time.addData(model);

        for (TimeItemModel data : model.getData()) {
            index+=data.getValue().size()+1;
        }
        scroll.refresh(index);


        time.setListener(new TimePumpingView.OnTouchChangeListener() {
            @Override
            public void touchUp(float currentY, float moveY) {
                scroll.pointUp(-currentY, -moveY, false);
            }

            @Override
            public void touchMove(float moveY) {
                scroll.pointMove(-moveY, false);
            }

            @Override
            public void addData(int count) {
                scroll.refresh(count);
            }

        });

        scroll.setListener(new CustomeScrollView.OnScrollTouchListener() {
            @Override
            public void touchUp(float currentY, float moveY) {
                time.pointUp(-currentY, -moveY, false);
            }

            @Override
            public void touchMove(float moveY) {
                time.pointMove(-moveY, false);
            }

            @Override
            public void onClick(int clickCount) {
                //TODO:点击操作
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
