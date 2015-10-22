package com.xmx.wenote.ChoosePhoto.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

import com.xmx.wenote.ChoosePhoto.entities.PhotoItem;
import com.xmx.wenote.ChoosePhoto.entities.AlbumItem;
import com.xmx.wenote.ChoosePhoto.entities.PhotoInf;

import java.util.ArrayList;

public class PhotoAdapter extends BaseAdapter {
    private Context context;
    private AlbumItem album;
    private ArrayList<PhotoInf> photos;

    public PhotoAdapter(Context context, AlbumItem album, ArrayList<PhotoInf> photos) {
        this.context = context;
        this.album = album;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        if (photos == null) {
            return album.getBitList().size();
        } else {
            return photos.size();
        }
    }

    @Override
    public PhotoInf getItem(int position) {
        if (photos == null) {
            return album.getBitList().get(position);
        } else {
            return photos.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoItem item;
        if (convertView == null) {
            item = new PhotoItem(context);
            item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            item = (PhotoItem) convertView;
        }
        // 通过ID 加载缩略图
        if (photos == null) {
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                    album.getBitList().get(position).getPhotoID(), Thumbnails.MINI_KIND, null);
            item.SetBitmap(bitmap);
            boolean flag = album.getBitList().get(position).isSelect();
            item.setChecked(flag);
        } else {
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                    photos.get(position).getPhotoID(), Thumbnails.MINI_KIND, null);
            item.SetBitmap(bitmap);
        }
        return item;
    }
}
