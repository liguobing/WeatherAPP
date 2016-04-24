package cn.lixyz.weatherapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

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
    private Intent intent = new Intent();

    //以下内容为百度定位添加
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        initView();
        initHotCityList();

        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener); // 注册监听函数
        initLocation();

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
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                intent.putExtra("cityName", selectCity);
                intent.putExtra("cityID", id);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        AdapterForHotCityGridView adapter = new AdapterForHotCityGridView(this, hotCityList);
        hot_city.setAdapter(adapter);
        hot_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startLocation();
                        break;
                    case 1:
                        intent.putExtra("cityName", "北京");
                        intent.putExtra("cityID", "CN101010100");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case 2:
                        intent.putExtra("cityName", "上海");
                        intent.putExtra("cityID", "CN101020100");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case 3:
                        intent.putExtra("cityName", "广州");
                        intent.putExtra("cityID", "CN101280101");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case 4:
                        intent.putExtra("cityName", "深圳");
                        intent.putExtra("cityID", "CN101280601");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case 5:
                        intent.putExtra("cityName", "天津");
                        intent.putExtra("cityID", "CN101030100");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                }
            }
        });
    }

    private void initHotCityList() {
        hotCityList.add("定位..");
        hotCityList.add("北京");
        hotCityList.add("上海");
        hotCityList.add("广州");
        hotCityList.add("深圳");
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

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(SelectCityActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SelectCityActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            } else {
                mLocationClient.start();
            }
        } else {
            mLocationClient.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationClient.start();
                } else {
                    Toast.makeText(SelectCityActivity.this, "定位失败，请您手动选择城市", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 配置定位SDK参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    /**
     * 内部类，用来监听定位
     */
    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            if (location.getCity() != null) {
                Message msg = new Message();
                msg.obj = location.getCity();
                handler.sendMessage(msg);
            }
        }
    }


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mLocationClient.stop();

            String cityName = ((String) msg.obj).replace("市", "");
            Cursor cursor = database.query(true, "city", new String[]{"id"}, "city=?", new String[]{selectCity}, null, null, "_id asc", null);
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex("id"));
            intent.putExtra("cityName", cityName);
            intent.putExtra("cityID", id);

            AlertDialog.Builder dialog = new AlertDialog.Builder(SelectCityActivity.this);
            dialog.setCancelable(true);
            dialog.setMessage("定位到您的城市为：" + (String) msg.obj);
            dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            dialog.setNegativeButton("NO,手动选择", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            dialog.show();
        }
    };
}
