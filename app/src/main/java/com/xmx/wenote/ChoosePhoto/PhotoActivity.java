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
    private GridView chosen_gridview;
    private PhotoAlbumItem album;
    private PhotoAdapter adapter;

    private ArrayList<PhotoItem> chosen = new ArrayList<>();

    PhotoAdapter chosen_adapter = new PhotoAdapter(this, album, chosen);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_photo_activity);
        album = (PhotoAlbumItem) getIntent().getExtras().get("album");
        chosen_gridview = (GridView) findViewById(R.id.chosen_gridview);
        GridView gv = (GridView) findViewById(R.id.photo_gridview);
        adapter = new PhotoAdapter(this, album, null);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(gvItemClickListener);
        findViewById(R.id.btn_sure).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(PhotoActivity.this, paths.toString(), Toast.LENGTH_LONG).show();

                //TODO
            }
        });
        chosen_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = chosen_adapter.getItem(position).getPath();
                Intent intent = new Intent(PhotoActivity.this, BigPhotoActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);

                //TODO
            }
        });
    }

    //

    private void processChosen(boolean isSelect) {
        int size = chosen.size();
        Button btn_sure = (Button) findViewById(R.id.btn_sure);
        if (isSelect) {
            btn_sure.setText("确定(" + size + ")");
        } else {
            btn_sure.setText("确定(" + size + ")");
        }
        chosen_gridview.setAdapter(chosen_adapter);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int allWidth = (int) (90 * density * size - 10);
        int itemWidth = (int) (80 * density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                allWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        chosen_gridview.setLayoutParams(params);
        chosen_gridview.setColumnWidth(itemWidth);
        chosen_gridview.setHorizontalSpacing(10);
        chosen_gridview.setStretchMode(GridView.STRETCH_SPACING);
        chosen_gridview.setNumColumns(size);

    }

    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    private OnItemClickListener gvItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PhotoItem gridItem = album.getBitList().get(position);
            if (gridItem.isSelect()) {
                gridItem.setSelect(false);
                ids.remove(gridItem.getPhotoID() + "");
                paths.remove(gridItem.getPath());
                chosen.remove(gridItem);
                processChosen(false);
            } else {
                gridItem.setSelect(true);
                ids.add(gridItem.getPhotoID() + "");
                paths.add(gridItem.getPath());
                chosen.add(gridItem);
                processChosen(true);
            }
            adapter.notifyDataSetChanged();
        }
    };
}
