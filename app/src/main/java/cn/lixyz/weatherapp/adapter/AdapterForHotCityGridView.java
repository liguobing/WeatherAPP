package cn.lixyz.weatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.lixyz.weatherapp.R;

/**
 * Created by LGB on 2016/4/10.
 */
public class AdapterForHotCityGridView extends BaseAdapter {

    private Context mContext;
    private List<String> mList;

    public AdapterForHotCityGridView(Context context, List<String> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.hot_city_item, null, false);
        ((TextView) view.findViewById(R.id.hot_city_name)).setText(mList.get(position));
        return view;
    }


}
