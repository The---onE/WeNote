package com.xmx.wenote.ChoosePhoto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.xmx.wenote.ChoosePhoto.adapter.PhotoAlbumAdapter;
import com.xmx.wenote.ChoosePhoto.entities.PhotoAlbum;
import com.xmx.wenote.ChoosePhoto.entities.PhotoItem;
import com.xmx.wenote.R;

public class PhotoAlbumActivity extends Activity {
    private GridView albumGV;
    private List<PhotoAlbum> albumList;

    //设置获取图片的字段信息
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, //名称
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.LONGITUDE, //经度
            MediaStore.Images.Media._ID, //id
            MediaStore.Images.Media.BUCKET_ID, //dir id 目录
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME //dir name 目录名称
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosephoto_activity_photoalbum);
        albumGV = (GridView) findViewById(R.id.album_gridview);
        albumList = getPhotoAlbum();
        albumGV.setAdapter(new PhotoAlbumAdapter(albumList, this));
        albumGV.setOnItemClickListener(albumClickListener);
    }

    //相册点击事件
    OnItemClickListener albumClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(PhotoAlbumActivity.this, PhotoActivity.class);
            intent.putExtra("album", albumList.get(position));
            startActivity(intent);
        }
    };

    //按相册获取图片信息
    private List<PhotoAlbum> getPhotoAlbum() {
        List<PhotoAlbum> albumList = new ArrayList<>();
        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES,
                null, MediaStore.Images.Media.DATE_MODIFIED);
        Map<String, PhotoAlbum> countMap = new LinkedHashMap<>();
        PhotoAlbum pa;
        cursor.moveToLast();
        while (cursor.moveToPrevious()) {
            String path = cursor.getString(1);
            String id = cursor.getString(3);
            String dir_id = cursor.getString(4);
            String dir = cursor.getString(5);
            if (!countMap.containsKey(dir_id)) {
                pa = new PhotoAlbum();
                pa.setName(dir);
                pa.setBitmap(Integer.parseInt(id));
                pa.getBitList().add(new PhotoItem(Integer.valueOf(id), path));
                countMap.put(dir_id, pa);
            } else {
                pa = countMap.get(dir_id);
                pa.increaseCount();
                pa.getBitList().add(new PhotoItem(Integer.valueOf(id), path));
            }
        }
        cursor.close();
        Iterable<String> it = countMap.keySet();
        for (String key : it) {
            albumList.add(countMap.get(key));
        }
        return albumList;
    }
}
