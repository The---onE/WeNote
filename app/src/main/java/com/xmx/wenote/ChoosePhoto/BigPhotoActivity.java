package com.xmx.wenote.ChoosePhoto;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xmx.wenote.ChoosePhoto.entities.BigGifImageLoader;
import com.xmx.wenote.ChoosePhoto.entities.GifImageView;
import com.xmx.wenote.ChoosePhoto.entities.JazzyViewPager.JazzyViewPager;
import com.xmx.wenote.R;

import java.util.ArrayList;

public class BigPhotoActivity extends Activity {
    LinearLayout layout;
    //JazzyViewPager vp;
    ViewPager vp;
    GifImageView gif_view;
    String path;
    ArrayList<String> paths;
    int index;
    boolean flipFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_big_photo_activity);
        layout = (LinearLayout) (findViewById(R.id.photo_layout));

        index = getIntent().getIntExtra("index", -1);

        if (index == -1) {
            flipFlag = false;
            path = getIntent().getStringExtra("path");

            gif_view = new GifImageView(this);
            gif_view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            BigGifImageLoader.getInstance(3, BigGifImageLoader.Type.LIFO).loadImage(path, gif_view, false);

            layout.addView(gif_view);
        } else {
            flipFlag = true;
            paths = getIntent().getStringArrayListExtra("paths");

            //vp = new JazzyViewPager(this);
            vp = new ViewPager(this);
            vp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            //vp.setTransitionEffect(JazzyViewPager.TransitionEffect.Accordion);
            vp.setAdapter(new PagerAdapter() {
                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == arg1;
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView((View) object);
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    LinearLayout l = new LinearLayout(BigPhotoActivity.this);
                    l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    l.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    l.setOrientation(LinearLayout.VERTICAL);

                    GifImageView iv = new GifImageView(BigPhotoActivity.this);
                    iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    //iv.setImageByPath(paths.get(position), false);
                    BigGifImageLoader.getInstance(3, BigGifImageLoader.Type.LIFO).loadImage(paths.get(position), iv, false);
                    l.addView(iv);
                    container.addView(l);
                    //vp.setObjectForPosition(l, position);
                    return l;
                }

                @Override
                public int getCount() {
                    return paths.size();
                }
            });
            layout.addView(vp);
            vp.setCurrentItem(index);
        }
        //TODO
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
