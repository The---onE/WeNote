package com.xmx.wenote.ChoosePhoto.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

import com.xmx.wenote.ChoosePhoto.PhotoGridItem;
import com.xmx.wenote.ChoosePhoto.entities.PhotoAlbum;
import com.xmx.wenote.ChoosePhoto.entities.PhotoItem;

public class PhotoAdapter extends BaseAdapter {
    private Context context;
    private PhotoAlbum album;
    private ArrayList<PhotoItem> gl_arr;

    public PhotoAdapter(Context context, PhotoAlbum album, ArrayList<PhotoItem> gl_arr) {
        this.context = context;
        this.album = album;
        this.gl_arr = gl_arr;
    }

    @Override
    public int getCount() {
        if (gl_arr == null) {
            return album.getBitList().size();
        } else {
            return gl_arr.size();
        }

    }

    @Override
    public PhotoItem getItem(int position) {
        if (gl_arr == null) {
            return album.getBitList().get(position);
        } else {
            return gl_arr.get(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoGridItem item;
        if (convertView == null) {
            item = new PhotoGridItem(context);
            item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        } else {
            item = (PhotoGridItem) convertView;
        }
        // 通过ID 加载缩略图
        if (gl_arr == null) {
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), album.getBitList().get(position).getPhotoID(), Thumbnails.MICRO_KIND, null);
            item.SetBitmap(bitmap);
            boolean flag = album.getBitList().get(position).isSelect();
            item.setChecked(flag);
        } else {
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), gl_arr.get(position).getPhotoID(), Thumbnails.MICRO_KIND, null);
            item.SetBitmap(bitmap);
        }
        return item;
    }
}
