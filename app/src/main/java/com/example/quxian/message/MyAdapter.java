package com.example.quxian.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by quxian on 2018/7/19.
 */

public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private List<Person> mPersonList;

    public MyAdapter(Context mContext, List<Person> mPersonList) {
        this.mContext = mContext;
        this.mPersonList = mPersonList;
    }

    @Override
    public int getCount() {
        return mPersonList.size();
    }

    @Override
    public Object getItem(int i) {
        return mPersonList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_listview,null);

            viewHolder.name = (TextView) view.findViewById(R.id.item_name);
            viewHolder.phone = (TextView) view.findViewById(R.id.item_phone);
            viewHolder.content = (TextView) view.findViewById(R.id.item_content);


            viewHolder.name.setText(mPersonList.get(i).getName());
            viewHolder.phone.setText(mPersonList.get(i).getPhoneNumber());
            viewHolder.content.setText(mPersonList.get(i).getContent());


            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
            viewHolder.name.setText(mPersonList.get(i).getName());
            viewHolder.phone.setText(mPersonList.get(i).getPhoneNumber());
            viewHolder.content.setText(mPersonList.get(i).getContent());
        }
        return view;
    }

    private  static class ViewHolder {
        private TextView name;
        private TextView phone;
        private TextView content;
    }
}
