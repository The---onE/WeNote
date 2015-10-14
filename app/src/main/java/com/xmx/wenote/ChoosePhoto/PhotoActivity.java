package com.xmx.wenote.ChoosePhoto;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xmx.wenote.ChoosePhoto.adapter.PhotoAdapter;
import com.xmx.wenote.ChoosePhoto.entities.PhotoAlbumItem;
import com.xmx.wenote.ChoosePhoto.entities.PhotoItem;
import com.xmx.wenote.R;

public class PhotoActivity extends Activity {
    private GridView gv, gl_bottom;
    private PhotoAlbumItem album;
    private PhotoAdapter adapter;
    private TextView tv;
    private int chooseNum = 0;
    private Button btn_sure;
    private LayoutInflater inflater;

    private ArrayList<PhotoItem> gl_arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_photo_activity);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        album = (PhotoAlbumItem) getIntent().getExtras().get("album");
        //获取已经选择的图片
        for (int i = 0; i < album.getBitList().size(); i++) {
            if (album.getBitList().get(i).isSelect()) {
                chooseNum++;
            }
        }
        gv = (GridView) findViewById(R.id.photo_gridview);
        gl_bottom = (GridView) findViewById(R.id.gl_bottom);
        adapter = new PhotoAdapter(this, album, null);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(gvItemClickListener);
        btn_sure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(PhotoActivity.this, paths.toString(), 3000).show();
                Log.e("info", paths.toString());
            }
        });
        gl_bottom.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = gl_adapter.getItem(position).getPath();
                Intent intent = new Intent(PhotoActivity.this, BigPhotoActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }

    PhotoAdapter gl_adapter = new PhotoAdapter(this, album, gl_arr);//

    private void inite(PhotoItem str, boolean isSeclect) {//初始化被选中的图片的方法  将图片添加或者删除

        if (isSeclect) {
            btn_sure.setText("确定(" + gl_arr.size() + ")");
        } else {
            btn_sure.setText("确定(" + gl_arr.size() + ")");
        }
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gl_bottom.setAdapter(gl_adapter);
        int size = gl_arr.size();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int allWidth = (int) (110 * size * density);
        int itemWidth = (int) (75 * density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                allWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gl_bottom.setLayoutParams(params);
        gl_bottom.setColumnWidth(itemWidth);
        gl_bottom.setHorizontalSpacing(10);
        gl_bottom.setStretchMode(GridView.NO_STRETCH);
        gl_bottom.setNumColumns(size);

    }

    private ArrayList<String> paths = new ArrayList<String>();
    private ArrayList<String> ids = new ArrayList<String>();
    private OnItemClickListener gvItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            PhotoItem gridItem = album.getBitList().get(position);
            if (album.getBitList().get(position).isSelect()) {
                album.getBitList().get(position).setSelect(false);
                paths.remove(album.getBitList().get(position).getPath());
                ids.remove(album.getBitList().get(position).getPhotoID() + "");
                gl_arr.remove(album.getBitList().get(position));
                chooseNum--;
                inite(album.getBitList().get(position), album.getBitList().get(position).isSelect());
            } else {
                album.getBitList().get(position).setSelect(true);
                ids.add(album.getBitList().get(position).getPhotoID() + "");
                paths.add(album.getBitList().get(position).getPath());
                gl_arr.add(album.getBitList().get(position));
                chooseNum++;
                inite(album.getBitList().get(position), album.getBitList().get(position).isSelect());
            }
            adapter.notifyDataSetChanged();
        }
    };
}
