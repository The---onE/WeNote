package com.xmx.wenote;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmx.wenote.ChoosePhoto.entities.GifImageView;
import com.xmx.wenote.ChoosePhoto.entities.ImageLoader;
import com.xmx.wenote.Database.SQLManager;

import java.util.ArrayList;

/**
 * Created by The_onE on 2015/10/11.
 */
public class TimelineFragment extends Fragment {

    SQLManager sqlManager = new SQLManager();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.timeline_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.layout);

        Cursor cursor = sqlManager.getCursor();
        if (cursor.moveToFirst()) {
            do {
                //ID TITLE TEXT PHOTO TIME
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String text = cursor.getString(2);
                String p = cursor.getString(3);
                ArrayList<String> photos = sqlManager.getPhotos(p);
                String time = cursor.getString(4);

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                parent.addView(layout);

                TextView titleTV = new TextView(getContext());
                titleTV.setText(title);
                titleTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.addView(titleTV);

                if (photos != null) {
                    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                    int width = wm.getDefaultDisplay().getWidth();

                    HorizontalScrollView sv = new HorizontalScrollView(getContext());
                    sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    layout.addView(sv);

                    LinearLayout l = new LinearLayout(getContext());
                    l.setOrientation(LinearLayout.HORIZONTAL);
                    l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    l.setVerticalGravity(Gravity.CENTER);
                    sv.addView(l);

                    for (int i = 0; i < photos.size(); ++i) {
                        String path = photos.get(i);
                        if (!path.isEmpty()) {
                            GifImageView iv = new GifImageView(getContext());
                            iv.setLayoutParams(new LinearLayout.LayoutParams(width / 4, width / 4));
                            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(path, iv);
                            iv.setImagePath(path, true);
                            l.addView(iv);
                        }
                    }
                }

                TextView timeTV = new TextView(getContext());
                timeTV.setText(time);
                timeTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.addView(timeTV);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
