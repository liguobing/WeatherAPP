package cn.lixyz.weatherapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.lixyz.weatherapp.R;
import cn.lixyz.weatherapp.adapter.AdapterForHotCityGridView;

/**
 * Created by LGB on 2016/4/5.
 */
public class SelectCityActivity extends Activity {

    private Spinner provSpinner, regionSpinner, citySpinner;
    private String[] allProv, allRegion, allCity;
    private String selectProv, selectRegion, selectCity;
    private SQLiteDatabase database;
    private Button select_city_commit;
    private GridView hot_city;
    private List<String> hotCityList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        initView();
        initHotCityList();

        String DB_PATH = "/data/data/cn.lixyz.weatherapp/databases/";
        String DB_NAME = "city.db";
        // 检查 SQLite 数据库文件是否存在
        if ((new File(DB_PATH + DB_NAME)).exists() == false) {
            // 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
            File f = new File(DB_PATH);
            // 如 database 目录不存在，新建该目录
            if (!f.exists()) {
                f.mkdir();
            }
            try {
                // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
                InputStream is = getBaseContext().getAssets().open(DB_NAME);
                // 输出流
                OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                // 文件写入
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                // 关闭文件流
                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        database = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);

        //查找数据库中所有省份，并将数据设置给provSpinner
        Cursor cursor = database.query(true, "city", new String[]{"prov"}, null, null, null, null, "_id asc", null);
        allProv = cursorToArray(cursor, "prov");
        ArrayAdapter<String> provAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allProv);
        provSpinner.setAdapter(provAdapter);

        provSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectProv = allProv[position];
                Cursor cursor = database.query(true, "city", new String[]{"region"}, "prov=?", new String[]{selectProv}, null, null, "_id asc", null);
                allRegion = cursorToArray(cursor, "region");
                ArrayAdapter<String> regionAdapter = new ArrayAdapter<String>(SelectCityActivity.this, android.R.layout.simple_spinner_item, allRegion);
                regionSpinner.setAdapter(regionAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectRegion = allRegion[position];
                Cursor cursor = database.query(true, "city", new String[]{"city"}, "region=?", new String[]{selectRegion}, null, null, "_id asc", null);
                allCity = cursorToArray(cursor, "city");
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(SelectCityActivity.this, android.R.layout.simple_spinner_item, allCity);
                citySpinner.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectCity = allCity[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        select_city_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = database.query(true, "city", new String[]{"id"}, "region=? and city=?", new String[]{selectRegion, selectCity}, null, null, "_id asc", null);
                cursor.moveToFirst();
                String id = cursor.getString(cursor.getColumnIndex("id"));
                Intent intent = new Intent();
                intent.putExtra("city", selectCity);
                intent.putExtra("id", id);
                setResult(RESULT_OK, intent);
                getSharedPreferences("config", MODE_PRIVATE).edit().putString("cityID", id).commit();
                finish();
            }
        });

        AdapterForHotCityGridView adapter = new AdapterForHotCityGridView(this, hotCityList);
        hot_city.setAdapter(adapter);

        hot_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    private void initHotCityList() {
        hotCityList.add("北京");
        hotCityList.add("上海");
        hotCityList.add("广州");
        hotCityList.add("深圳");
        hotCityList.add("南京");
        hotCityList.add("天津");
    }


    /**
     * 初始化组件
     */
    private void initView() {
        provSpinner = (Spinner) findViewById(R.id.prov);
        regionSpinner = (Spinner) findViewById(R.id.region);
        citySpinner = (Spinner) findViewById(R.id.city);
        hot_city = (GridView) findViewById(R.id.hot_city);
        select_city_commit = (Button) findViewById(R.id.select_city_commit);
    }

    /**
     * 该方法用于将SQLite查询出来的cursor结果集转化为数组
     */

    public String[] cursorToArray(Cursor cursor, String str) {
        ArrayList<String> list = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String prov = cursor.getString(cursor.getColumnIndex(str));
            list.add(prov);
        }
        return (String[]) list.toArray(new String[cursor.getCount()]);
    }
}
