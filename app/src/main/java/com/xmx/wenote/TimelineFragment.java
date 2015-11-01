package com.xmx.wenote;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmx.wenote.ChoosePhoto.BigPhotoActivity;
import com.xmx.wenote.ChoosePhoto.adapter.ShowAdapter;
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
        if (cursor.moveToLast()) {
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

                    GridView gv = new GridView(getContext());
                    gv.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

                    DisplayMetrics dm = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                    float density = dm.density;
                    gv.setHorizontalSpacing((int) (5 * density));
                    gv.setColumnWidth((int) (100 * density));

                    gv.setAdapter(new ShowAdapter(getContext(), photos));

                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), BigPhotoActivity.class);
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

                TextView timeTV = new TextView(getContext());
                timeTV.setText(time);
                timeTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.addView(timeTV);

            } while (cursor.moveToPrevious());
            cursor.close();
        }
    }
}
