package com.xmx.wenote.ChoosePhoto;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.MotionEvent;

import com.xmx.wenote.ChoosePhoto.entities.GifImageView;
import com.xmx.wenote.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BigPhotoActivity extends Activity {
    GifImageView gif_view;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_big_photo_activity);

        path = getIntent().getStringExtra("path");

        gif_view = (GifImageView) findViewById(R.id.gif_view);
        gif_view.setImagePath(path);
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
