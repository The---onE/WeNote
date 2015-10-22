package com.xmx.wenote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xmx.wenote.ChoosePhoto.AlbumActivity;
import com.xmx.wenote.ChoosePhoto.entities.GifImageView;

import java.util.ArrayList;

/**
 * Created by The_onE on 2015/10/11.
 */
public class NoteFragment extends Fragment {
    ArrayList<LinearLayout> layouts = new ArrayList<>();
    ArrayList<GifImageView> images = new ArrayList<>();
    ArrayList<String> paths = new ArrayList<>();
    String sqlFile = android.os.Environment.getExternalStorageDirectory() + "/WeNote/note.db";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.note_view, container, false);
    }

    static final int CHOOSE_IMAGE = 1;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button choose = (Button) getActivity().findViewById(R.id.choose);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AlbumActivity.class);
                startActivityForResult(i, CHOOSE_IMAGE);
            }
        });

        Button note = (Button) getActivity().findViewById(R.id.note);
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!paths.isEmpty()) {
                    Toast.makeText(getContext(), paths.toString(), Toast.LENGTH_LONG).show();
                    //TODO
                    Toast.makeText(getContext(), sqlFile, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            paths = data.getStringArrayListExtra("paths");

            LinearLayout parent = (LinearLayout)getActivity().findViewById(R.id.note_layout);
            for (LinearLayout l:layouts) {
                parent.removeView(l);
            }
            layouts.clear();
            images.clear();

            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();

            LinearLayout l = new LinearLayout(getContext());
            for (int i=0; i<paths.size(); ++i) {
                String path = paths.get(i);
                if (i%4 == 0) {
                    l = new LinearLayout(getContext());
                    l.setOrientation(LinearLayout.HORIZONTAL);
                    l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    l.setVerticalGravity(Gravity.CENTER);
                    parent.addView(l);
                    layouts.add(l);
                }
                GifImageView iv = new GifImageView(getActivity());
                iv.setImagePath(path);
                iv.setLayoutParams(new LinearLayout.LayoutParams(width / 4, width / 4));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                l.addView(iv);
                images.add(iv);
            }
        }
    }


}
