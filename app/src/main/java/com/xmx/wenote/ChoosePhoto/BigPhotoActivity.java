package com.xmx.wenote.ChoosePhoto;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.xmx.wenote.ChoosePhoto.entities.GifView;
import com.xmx.wenote.R;

public class BigPhotoActivity extends Activity {
    GifView iv_show_big_pic;
    ImageView iv_show_big_iv;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_big_photo_activity);

        path = getIntent().getStringExtra("path");
        //iv_show_big_pic.setImageBitmap(BitmapFactory.decodeFile(path));

        iv_show_big_pic = (GifView) findViewById(R.id.iv_show_big_pic);
        iv_show_big_pic.setMovieResource(path);

        //iv_show_big_iv = (ImageView) findViewById(R.id.iv_show_big_iv);
        //iv_show_big_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
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
