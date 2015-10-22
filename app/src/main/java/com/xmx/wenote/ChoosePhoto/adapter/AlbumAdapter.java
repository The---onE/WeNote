package com.xmx.wenote.ChoosePhoto.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmx.wenote.ChoosePhoto.entities.AlbumItem;
import com.xmx.wenote.R;

import java.util.List;

public class AlbumAdapter extends BaseAdapter {
    private List<AlbumItem> albumList;
    private Context context;

    public AlbumAdapter(List<AlbumItem> list, Context context) {
        this.albumList = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView iv;
        TextView tv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cp_album_item, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.album_item_image);
            holder.tv = (TextView) convertView.findViewById(R.id.album_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //通过ID 获取缩略图
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                albumList.get(position).getBitmap(), MediaStore.Images.Thumbnails.MINI_KIND, null);
        holder.iv.setImageBitmap(bitmap);
        holder.tv.setText(albumList.get(position).getName() + "(" + albumList.get(position).getCount() + ")");
        return convertView;
    }

}
