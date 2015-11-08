package com.xmx.wenote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmx.wenote.ChoosePhoto.BigPhotoActivity;
import com.xmx.wenote.ChoosePhoto.adapter.ShowAdapter;
import com.xmx.wenote.Database.SQLManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        int id = getIntent().getIntExtra("id", -1);
        SQLManager sqlManager = new SQLManager();
        Cursor cursor = sqlManager.selectById(id);
        if (cursor.moveToFirst()) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.detail_layout);

            String title = cursor.getString(1);
            String text = cursor.getString(2);
            String p = cursor.getString(3);
            ArrayList<String> photos = sqlManager.getPhotos(p);
            Date date = new Date(cursor.getLong(4));

            TextView titleTV = (TextView) findViewById(R.id.detail_title);
            titleTV.setText(title);

            TextView textTV = (TextView) findViewById(R.id.detail_text);
            textTV.setText(text);

            if (photos != null) {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();

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

            TextView timeTV = new TextView(this);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timeTV.setText(df.format(date));
            timeTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(timeTV);
        }
    }
}
