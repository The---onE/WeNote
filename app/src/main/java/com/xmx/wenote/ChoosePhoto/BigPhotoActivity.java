package com.xmx.wenote.ChoosePhoto;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.xmx.wenote.ChoosePhoto.entities.GifImageView;
import com.xmx.wenote.R;

import java.util.ArrayList;

public class BigPhotoActivity extends Activity {
    LinearLayout layout;
    ViewPager vp;
    GifImageView gif_view;
    String path;
    ArrayList<String> paths;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_big_photo_activity);

        index = getIntent().getIntExtra("index", -1);

        if (index == -1) {
            path = getIntent().getStringExtra("path");

            gif_view = new GifImageView(this);
            gif_view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            gif_view.setImageByPath(path, false);

            layout = (LinearLayout) (findViewById(R.id.photo_layout));
            layout.addView(gif_view);
        } else {
            paths = getIntent().getStringArrayListExtra("paths");


            //TODO
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                finish();
                break;
        }
        return super.onTouchEvent(event);
    }
}
