package cn.lixyz.weatherapp.fragment;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.lixyz.weatherapp.R;
import cn.lixyz.weatherapp.adapter.AdapterForPageB;
import cn.lixyz.weatherapp.util.AnimatorUtil;

/**
 * Created by LGB on 2016/4/4.
 */
public class PageBFragment extends Fragment {

    private List<String> iconNames = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initList();
        float windowWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        float widownHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();

        View view = inflater.inflate(R.layout.page_b, null, false);
        GridView gridview = (GridView) view.findViewById(R.id.grid_b);

        AdapterForPageB adapter = new AdapterForPageB(getActivity(), iconNames);
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
                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void initList() {
        iconNames.add("景点天气");
        iconNames.add("24小时");
        iconNames.add("未来七天");
        iconNames.add("更新天气");
    }
}
