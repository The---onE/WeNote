package com.xmx.wenote.ChoosePhoto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xmx.wenote.ChoosePhoto.entities.GifImageLoader;
import com.xmx.wenote.ChoosePhoto.entities.GifImageView;
import com.xmx.wenote.ChoosePhoto.entities.PhotoInf;
import com.xmx.wenote.R;

import java.util.ArrayList;

public class ChosenAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PhotoInf> photos;

    public ChosenAdapter(Context context, ArrayList<PhotoInf> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public PhotoInf getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        GifImageView iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cp_chosen_item, null);
            holder = new ViewHolder();
            holder.iv = (GifImageView) convertView.findViewById(R.id.chosen_item_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GifImageLoader.getInstance(3, GifImageLoader.Type.LIFO).loadImage(photos.get(position).getPath(), holder.iv, false);
        return convertView;
    }
}
