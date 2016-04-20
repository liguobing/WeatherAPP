package cn.lixyz.weatherapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
    private LinearLayout weather_root;
    private TextView item_message;
    private ObjectAnimator weatherRootOut;
    private ObjectAnimator weatherRootIn;
    private ObjectAnimator item_messageIn;
    private ObjectAnimator item_messageOut;
    private boolean animRunning = false;


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

        //item飞入动画
        LayoutAnimationController controller = AnimatorUtil.getGridViewAnimator(windowWidth, widownHeight);
        gridview.setLayoutAnimation(controller);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!animRunning) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, 360);
                    animator.setDuration(1000);
                    animator.start();

                    SharedPreferences sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
                    final String str = sp.getString(iconNames.get(position), "暂未获取到相关信息");


                    weatherRootOut = ObjectAnimator.ofFloat(weather_root, "translationX", 0, (0 - weather_root.getWidth()));    //天气信息划出屏幕
                    weatherRootIn = ObjectAnimator.ofFloat(weather_root, "translationX", weather_root.getWidth(), 0);//天气信息划入屏幕
                    item_messageIn = ObjectAnimator.ofFloat(item_message, "translationX", weather_root.getWidth(), 0);//item指数信息划入屏幕
                    item_messageOut = ObjectAnimator.ofFloat(item_message, "translationX", 0, (0 - weather_root.getWidth()));//item指数信息划出屏幕

                    weatherRootOut.setDuration(500);
                    weatherRootIn.setDuration(500);
                    item_messageIn.setDuration(1500);
                    item_messageOut.setDuration(1500);

                    /**
                     * 设置点击Item时候，指数信息的进出和天气信息的进出，因为要给指数信息设置内容已经设置是否隐藏，所以没有办法使用AnimatorSet
                     */

                    weatherRootOut.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            animRunning = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            item_message.setVisibility(View.VISIBLE);
                            item_message.setText(str);
                            item_messageIn.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    item_messageIn.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            item_messageOut.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    item_messageOut.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            item_message.setVisibility(View.GONE);
                            weatherRootIn.start();

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    weatherRootIn.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animRunning = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    weatherRootOut.start();
                }
            }
        });
        return view;
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
