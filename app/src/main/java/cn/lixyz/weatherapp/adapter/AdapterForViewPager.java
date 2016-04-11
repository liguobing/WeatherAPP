package cn.lixyz.weatherapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * Created by LGB on 2016/4/12.
 */
public class AdapterForViewPager extends PagerAdapter {

    private List<View> mViews;
    private Context mContext;

    public AdapterForViewPager(List<View> views, Context context) {
        this.mContext = context;
        this.mViews = views;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mViews.size();
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return (arg0 == arg1);
    }

}
