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

import com.xmx.wenote.ChoosePhoto.entities.PhotoAlbum;
import com.xmx.wenote.R;

import java.util.List;

public class PhotoAlbumAdapter extends BaseAdapter {
    private List<PhotoAlbum> albumList;
    private Context context;
    private ViewHolder holder;

    public PhotoAlbumAdapter(List<PhotoAlbum> list, Context context) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.choosephoto_photoalbum_item, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.photoalbum_item_image);
            holder.tv = (TextView) convertView.findViewById(R.id.photoalbum_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //通过ID 获取缩略图
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), albumList.get(position).getBitmap(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
        holder.iv.setImageBitmap(bitmap);
        holder.tv.setText(albumList.get(position).getName() + "(" + albumList.get(position).getCount() + ")");
        return convertView;
    }

    static class ViewHolder {
        ImageView iv;
        TextView tv;
    }

}
