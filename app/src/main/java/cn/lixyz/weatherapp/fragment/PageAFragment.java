package cn.lixyz.weatherapp.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lixyz.weatherapp.R;
import cn.lixyz.weatherapp.adapter.AdapterForPageA;
import cn.lixyz.weatherapp.util.AnimatorUtil;

/**
 * Created by LGB on 2016/4/4.
 */
public class PageAFragment extends Fragment {

    private List<String> iconNames = new ArrayList<String>();
    public static final int VIEW_VISIBLE = 1;
    public static final int VIEW_GONE = 0;
    private LinearLayout weather_root;
    private TextView item_message;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initList();

        float windowWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        float widownHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        weather_root = (LinearLayout) getActivity().findViewById(R.id.weather_root);
        item_message = (TextView) getActivity().findViewById(R.id.item_message);

        View view = inflater.inflate(R.layout.page_a, null, false);
        GridView gridview = (GridView) view.findViewById(R.id.grid_a);

        AdapterForPageA adapter = new AdapterForPageA(getActivity(), iconNames);

        gridview.setAdapter(adapter);

        LayoutAnimationController controller = AnimatorUtil.getGridViewAnimator(windowWidth, widownHeight);
        gridview.setLayoutAnimation(controller);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, 360);
                animator.setDuration(1000);
                animator.start();

                SharedPreferences sp = getActivity().getSharedPreferences("weatherInfo", Context.MODE_PRIVATE);
                String str = sp.getString(iconNames.get(position), "暂未获取到相关信息");

                item_message.setText(str);
                item_message.setVisibility(View.VISIBLE);

                ObjectAnimator anim1 = ObjectAnimator.ofFloat(weather_root, "translationX", 0, (0 - weather_root.getWidth()));
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(item_message, "translationX", weather_root.getWidth(), 0);
                AnimatorSet set = new AnimatorSet();
                set.setDuration(1000);
                set.play(anim1).with(anim2);
                set.start();
            }
        });
        return view;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };


    /**
     * 设置View组件是否显式
     *
     * @param status 是否显式
     * @param views  要操作的组件
     */
    public void setViewVisibility(int status, View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(status);
        }
    }

    private void initList() {
        iconNames.add("穿衣指数");
        iconNames.add("紫外线指数");
        iconNames.add("洗车指数");
        iconNames.add("旅游指数");
        iconNames.add("感冒指数");
        iconNames.add("运动指数");
    }
}
