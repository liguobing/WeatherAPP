package cn.lixyz.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.lixyz.weatherapp.activity.SelectCityActivity;
import cn.lixyz.weatherapp.activity.WelcomeActivity;
import cn.lixyz.weatherapp.bean.HeWeather;
import cn.lixyz.weatherapp.fragment.PageAFragment;
import cn.lixyz.weatherapp.fragment.PageBFragment;
import cn.lixyz.weatherapp.util.KeyUtil;
import cn.lixyz.weatherapp.util.SetBarColorUtil;


public class MainActivity extends Activity {

    private ImageView page_index, iv_img;
    private TextView tv_cityName, tv_date, tv_weahter, tv_wind;

    private float downX = 0;//用来记录手指按下时X点的坐标，
    private float upX = 0;//用来记录手指抬起时X点的坐标，
    private int flag = 1;   //用来标记当前Fragment是第一页还是第二页


    private SharedPreferences configSp;
    private SharedPreferences.Editor editor;

    private int statusNumber = 0; //用来方式更新天气之后，UI组件更新不及时造成显式混乱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SetBarColorUtil.setBarColor(getWindow());   //调用工具类，设置StatusBar和NavigationBar透明
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().add(R.id.root_layout, new PageAFragment(), "pageA").commit();
        initView();
        initIconData();

        configSp = getSharedPreferences("config", MODE_PRIVATE);
        editor = configSp.edit();

        //通过configSp判断是否是第一次进入，如果是第一次进入，则进入欢迎界面
        if (configSp.getBoolean("first", true)) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }

        /**
         * 查看configSp中的city字段是否有值，如果有值，开始查看日期，如果无值，提示用户选择城市
         */
        if (configSp.getString("cityName", null) == null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("选择城市");
            dialog.setMessage("您是第一次使用，请选择您的城市");

            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, SelectCityActivity.class);
                    startActivityForResult(intent, 1);
                }
            });
            dialog.show();
        }


        tv_cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectCityActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * 接收SelectCityActivity传递过来的城市ID并存储到SP中
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String cityID = data.getStringExtra("cityID");
            String cityName = data.getStringExtra("cityName");
            editor.putString("cityID", cityID);
            editor.putString("cityName", cityName);
            editor.commit();
        }
    }

    /**
     * 在onStart方法中更新天气和更新UI
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (configSp.getString("cityID", null) != null) {
            if (tv_cityName.getText().toString() == null && tv_date.getText().toString() == null) {
                updateWeather();
            } else {
                if (tv_cityName.getText().toString().equals(configSp.getString("cityName", null)) && tv_date.getText().toString().equals(configSp.getString("date", null))) {
                    editView();
                } else {
                    updateWeather();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 获取天气内容,更新configSp内容
     */
    private void updateWeather() {
        String cityID = configSp.getString("cityID", null);
        if (cityID != null) {
            String requestURL = "https://api.heweather.com/x3/weather?cityid=" + cityID + "&key=" + KeyUtil.WEATHER_KEY;
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(requestURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Gson gson = new Gson();
                    HeWeather heWeather = gson.fromJson(jsonObject.toString().replace("HeWeather data service 3.0", "heWeather"), HeWeather.class);
                    editor.putString("cityID", heWeather.getHeWeather().get(0).getBasic().getId());//城市ID
                    editor.putString("cityName", heWeather.getHeWeather().get(0).getBasic().getCity());//城市名称
                    editor.putString("date", heWeather.getHeWeather().get(0).getDaily_forecast().get(0).getDate());//日期
                    editor.putString("now_code", heWeather.getHeWeather().get(0).getNow().getCond().getCode());//天气代码
                    editor.putString("now_fl", heWeather.getHeWeather().get(0).getNow().getFl() + "℃");//体感温度
                    editor.putString("now_cond", heWeather.getHeWeather().get(0).getNow().getCond().getTxt());//天气描述
                    editor.putString("now_wind_dir", heWeather.getHeWeather().get(0).getNow().getWind().getDir());//风向
                    editor.putString("now_wind_sc", heWeather.getHeWeather().get(0).getNow().getWind().getSc() + "级");//风力等级
                    editor.putString("运动指数", heWeather.getHeWeather().get(0).getSuggestion().getSport().getTxt());
                    editor.putString("穿衣指数", heWeather.getHeWeather().get(0).getSuggestion().getDrsg().getTxt());
                    editor.putString("紫外线指数", heWeather.getHeWeather().get(0).getSuggestion().getUv().getTxt());
                    editor.putString("洗车指数", heWeather.getHeWeather().get(0).getSuggestion().getCw().getTxt());
                    editor.putString("旅游指数", heWeather.getHeWeather().get(0).getSuggestion().getTrav().getTxt());
                    editor.putString("感冒指数", heWeather.getHeWeather().get(0).getSuggestion().getFlu().getTxt());
                    editor.commit();
                    statusNumber++;
                    //天气更新完毕，更新UI
                    editView();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(MainActivity.this, "抱歉，更新天气失败", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
        }
    }


    /**
     * 从SP中获取天气内容，更新UI
     * 使用statusNumber来控制循环，防止UI更新混乱
     */
    private void editView() {
        final int tmpStatusNumber = statusNumber;
        tv_cityName.setText(configSp.getString("cityName", "未知"));//设置城市名称
        tv_date.setText(configSp.getString("date", "未知"));//设置日期
        iv_img.setImageResource(iconMap.get(configSp.getString("now_code", "999")));
        new Thread(new Runnable() {
            @Override
            public void run() {
                FlAndWindDir flAndCond = new FlAndWindDir();
                flAndCond.setWindDir(configSp.getString("now_wind_dir", "-"));
                flAndCond.setFl(configSp.getString("now_fl", "-"));
                CondAndWindSc condAndWindSc = new CondAndWindSc();
                condAndWindSc.setCond(configSp.getString("now_cond", "-"));
                condAndWindSc.setWindSc(configSp.getString("now_wind_sc", "-"));
                try {
                    while (tmpStatusNumber == statusNumber) {
                        Message weatherMessage = new Message();
                        weatherMessage.obj = flAndCond;
                        handler.sendMessage(weatherMessage);
                        Thread.sleep(1000);
                        Message windMessage = new Message();
                        windMessage.obj = condAndWindSc;
                        handler.sendMessage(windMessage);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Handler用来循环更新温度和风力的TextView
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object object = msg.obj;
            if (object instanceof CondAndWindSc) {
                tv_weahter.setText(((CondAndWindSc) object).getCond());
                tv_wind.setText(((CondAndWindSc) object).getWindSc());
            } else if (object instanceof FlAndWindDir) {
                tv_weahter.setText(((FlAndWindDir) object).getFl());
                tv_wind.setText(((FlAndWindDir) object).getWindDir());
            }
        }
    };

    /**
     * 初始化组件
     */
    private void initView() {
        page_index = (ImageView) findViewById(R.id.page_index);
        tv_cityName = (TextView) findViewById(R.id.tv_cityName);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_weahter = (TextView) findViewById(R.id.tv_weahter);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        iv_img = (ImageView) findViewById(R.id.iv_img);
    }


    /**
     * 重写Activity的dispatchTouchEvent方法，用来拦截滑动事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                upX = ev.getX();
                if (Math.abs(upX - downX) > 50) {
                    if (flag == 1) {
                        getFragmentManager().beginTransaction().replace(R.id.root_layout, new PageBFragment(), "pageB").commit();
                        flag = 2;
                        page_index.setImageResource(R.drawable.page_two);
                    } else if (flag == 2) {
                        getFragmentManager().beginTransaction().replace(R.id.root_layout, new PageAFragment(), "pageA").commit();
                        flag = 1;
                        page_index.setImageResource(R.drawable.page_one);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private Map<String, Integer> iconMap = new HashMap<String, Integer>();

    public void initIconData() {
        iconMap.put("100", R.drawable.w100);
        iconMap.put("101", R.drawable.w101);
        iconMap.put("102", R.drawable.w102);
        iconMap.put("103", R.drawable.w103);
        iconMap.put("104", R.drawable.w104);
        iconMap.put("200", R.drawable.w200);
        iconMap.put("201", R.drawable.w201);
        iconMap.put("202", R.drawable.w202);
        iconMap.put("203", R.drawable.w203);
        iconMap.put("204", R.drawable.w204);
        iconMap.put("205", R.drawable.w205);
        iconMap.put("206", R.drawable.w206);
        iconMap.put("207", R.drawable.w207);
        iconMap.put("208", R.drawable.w208);
        iconMap.put("209", R.drawable.w209);
        iconMap.put("210", R.drawable.w210);
        iconMap.put("211", R.drawable.w211);
        iconMap.put("212", R.drawable.w212);
        iconMap.put("213", R.drawable.w213);
        iconMap.put("300", R.drawable.w300);
        iconMap.put("301", R.drawable.w301);
        iconMap.put("302", R.drawable.w302);
        iconMap.put("303", R.drawable.w303);
        iconMap.put("304", R.drawable.w304);
        iconMap.put("305", R.drawable.w305);
        iconMap.put("306", R.drawable.w306);
        iconMap.put("307", R.drawable.w307);
        iconMap.put("308", R.drawable.w308);
        iconMap.put("309", R.drawable.w309);
        iconMap.put("310", R.drawable.w310);
        iconMap.put("311", R.drawable.w311);
        iconMap.put("312", R.drawable.w312);
        iconMap.put("313", R.drawable.w313);
        iconMap.put("400", R.drawable.w400);
        iconMap.put("401", R.drawable.w401);
        iconMap.put("402", R.drawable.w402);
        iconMap.put("403", R.drawable.w403);
        iconMap.put("404", R.drawable.w404);
        iconMap.put("405", R.drawable.w405);
        iconMap.put("406", R.drawable.w406);
        iconMap.put("407", R.drawable.w407);
        iconMap.put("501", R.drawable.w501);
        iconMap.put("502", R.drawable.w502);
        iconMap.put("504", R.drawable.w504);
        iconMap.put("507", R.drawable.w507);
        iconMap.put("508", R.drawable.w508);
        iconMap.put("900", R.drawable.w900);
        iconMap.put("901", R.drawable.w901);
        iconMap.put("999", R.drawable.w999);
    }
}


/**
 * 两个实体类，用来发送Message，Handler更新天气状况和风力状况
 */

class CondAndWindSc {
    private String cond;
    private String windSc;

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getWindSc() {
        return windSc;
    }

    public void setWindSc(String windSc) {
        this.windSc = windSc;
    }
}

class FlAndWindDir {
    private String fl;
    private String windDir;

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }
}
