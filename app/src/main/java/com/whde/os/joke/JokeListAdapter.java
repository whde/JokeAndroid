package com.whde.os.joke;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class JokeListAdapter extends ArrayAdapter {
    private final int resourceId;

    public JokeListAdapter(Context context, int textViewResourceId, List<JokeBean> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JokeBean jokeBean = (JokeBean) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView title = (TextView) view.findViewById(R.id.itemTitle);//获取该布局内的图片视图
        TextView date = (TextView) view.findViewById(R.id.itemDate);//获取该布局内的文本视图
        title.setText(jokeBean.getTitle());//为图片视图设置图片资源
        date.setText(jokeBean.getDate());//为文本视图设置文本内容
        return view;
    }
}
