package com.usst.androidtermprogram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.hjm.bottomtabbar.BottomTabBar;
import com.usst.androidtermprogram.fragment.Crosswalk;
import com.usst.androidtermprogram.fragment.Download;
import com.usst.androidtermprogram.fragment.Volley;

public class MainActivity extends AppCompatActivity {

    private BottomTabBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        mBottomBar = findViewById(R.id.bottom_tab_bar);
        mBottomBar.init(getSupportFragmentManager(), 500, 900)
                .addTabItem("crosswalk", R.mipmap.home_selected, R.mipmap.home, Crosswalk.class)
                .addTabItem("volley", R.mipmap.list, Volley.class)
                .addTabItem("Download", R.mipmap.list, Download.class)
                .setOnTabChangeListener(new BottomTabBar.OnTabChangeListener() {
                    @Override
                    public void onTabChange(int position, String name, View view) {
                        if (position == 1) {
                            mBottomBar.setSpot(1, false);
                        }
                    }
                });
    }
}
