package com.xmx.wenote.ChoosePhoto;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.xmx.wenote.ChoosePhoto.adapter.ChosenAdapter;
import com.xmx.wenote.ChoosePhoto.adapter.PhotoAdapter;
import com.xmx.wenote.ChoosePhoto.entities.AlbumItem;
import com.xmx.wenote.ChoosePhoto.entities.PhotoInf;
import com.xmx.wenote.R;

public class PhotoActivity extends Activity {
    private GridView chosen_gridview;
    private AlbumItem album;
    private PhotoAdapter adapter;
    private ChosenAdapter chosen_adapter;

    private ArrayList<PhotoInf> chosen = new ArrayList<>();

    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_photo_activity);

        album = (AlbumItem) getIntent().getExtras().get("album");

        GridView gv = (GridView) findViewById(R.id.photo_gridview);
        adapter = new PhotoAdapter(this, album);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoInf gridItem = album.getBitList().get(position);
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
        });

        chosen_gridview = (GridView) findViewById(R.id.chosen_gridview);
        chosen_adapter = new ChosenAdapter(this, chosen);
        chosen_gridview.setAdapter(chosen_adapter);
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

        findViewById(R.id.btn_sure).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!paths.isEmpty()) {
                    Intent i = new Intent(PhotoActivity.this, AlbumActivity.class);
                    i.putExtra("paths", paths);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        });
    }

    private void processChosen(boolean isSelect) {
        int size = chosen.size();
        Button btn_sure = (Button) findViewById(R.id.btn_sure);
        if (isSelect) {
            btn_sure.setText("确定(" + size + ")");
        } else {
            btn_sure.setText("确定(" + size + ")");
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int allWidth = (int) ((100 + 5) * density * size - 10);
        int itemWidth = (int) (100 * density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                allWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        chosen_gridview.setLayoutParams(params);
        chosen_gridview.setColumnWidth(itemWidth);
        chosen_gridview.setNumColumns(size);
    }
}
