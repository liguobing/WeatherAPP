package cn.lixyz.weatherapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.lixyz.weatherapp.MainActivity;
import cn.lixyz.weatherapp.R;
import cn.lixyz.weatherapp.adapter.AdapterForViewPager;
import cn.lixyz.weatherapp.util.SetBarColorUtil;

/**
 * Created by LGB on 2016/4/12.
 */
public class WelcomeActivity extends Activity {

    private ViewPager vp;
    private AdapterForViewPager adapter;
    private List<View> views;
    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetBarColorUtil.setBarColor(getWindow());
        setContentView(R.layout.activity_welcome);
        initViews();
    }

    public void initViews() {

        LayoutInflater inflate = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflate.inflate(R.layout.welcome_one, null, false));
        views.add(inflate.inflate(R.layout.welcome_two, null, false));
        views.add(inflate.inflate(R.layout.welcome_three, null, false));

        adapter = new AdapterForViewPager(views, this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(adapter);

        bt = (Button) views.get(2).findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                getSharedPreferences("config", MODE_PRIVATE).edit().putBoolean("firstIn", false).commit();
                finish();
            }
        });
    }

}
