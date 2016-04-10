package cn.lixyz.weatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.lixyz.weatherapp.R;

/**
 * Created by LGB on 2016/4/4.
 */
public class AdapterForPageB extends BaseAdapter {

    private Context mContext;
    private List<String> mList;

    private int[] icons = new int[]{R.drawable.guji, R.drawable.hour24, R.drawable.day7, R.drawable.update};

    public AdapterForPageB(Context context, List<String> list) {
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

        View view = LayoutInflater.from(mContext).inflate(R.layout.item, null, false);

        TextView iconName = (TextView) view.findViewById(R.id.icon_name);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);

        iconName.setText(mList.get(position));
        icon.setImageResource(icons[position]);


        return view;
    }
}
