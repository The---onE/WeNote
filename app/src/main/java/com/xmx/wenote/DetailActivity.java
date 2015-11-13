package com.xmx.wenote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmx.wenote.ChoosePhoto.BigPhotoActivity;
import com.xmx.wenote.ChoosePhoto.adapter.ShowAdapter;
import com.xmx.wenote.Database.SQLManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailActivity extends Activity {
    private String title;
    private String text;
    private ArrayList<String> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        int id = getIntent().getIntExtra("id", -1);
        SQLManager sqlManager = new SQLManager();
        Cursor cursor = sqlManager.selectById(id);
        if (cursor.moveToFirst()) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.detail_layout);

            title = cursor.getString(1);
            text = cursor.getString(2);
            String p = cursor.getString(3);
            photos = sqlManager.getPhotos(p);
            Date date = new Date(cursor.getLong(4));

            TextView titleTV = (TextView) findViewById(R.id.detail_title);
            titleTV.setText(title);

            TextView textTV = (TextView) findViewById(R.id.detail_text);
            textTV.setText(text);

            TextView timeTV = (TextView) findViewById(R.id.detail_time);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timeTV.setText(df.format(date));

            Button share = (Button) findViewById(R.id.detail_share);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (photos != null) {
                        intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        intent.setType("image/*");
                        ArrayList<Uri> uris = new ArrayList<>();
                        for (String path : photos) {
                            File f = new File(path);
                            Uri uri = Uri.fromFile(f);
                            uris.add(uri);
                        }
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    }
                    else {
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                    }

                    intent.putExtra(Intent.EXTRA_SUBJECT, "share");
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                    intent.putExtra(Intent.EXTRA_TITLE, title);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getTitle()));
                }
            });

            if (photos != null) {
                HorizontalScrollView sv = new HorizontalScrollView(this);
                sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.addView(sv);

                LinearLayout l = new LinearLayout(this);
                l.setOrientation(LinearLayout.HORIZONTAL);
                l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                l.setVerticalGravity(Gravity.CENTER);
                sv.addView(l);

                GridView gv = new GridView(this);
                gv.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                float density = dm.density;
                gv.setHorizontalSpacing((int) (5 * density));
                gv.setColumnWidth((int) (100 * density));

                gv.setAdapter(new ShowAdapter(this, photos));

                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(DetailActivity.this, BigPhotoActivity.class);
                        ShowAdapter a = (ShowAdapter) parent.getAdapter();
                        ArrayList<String> paths = a.getPaths();
                        intent.putExtra("paths", paths);
                        intent.putExtra("index", position);
                        startActivity(intent);
                    }
                });

                int size = photos.size();

                int allWidth = (int) ((100 + 5) * density * size - 10);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        allWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                gv.setLayoutParams(params);
                gv.setNumColumns(size);

                l.addView(gv);
            }
        }
    }
}
