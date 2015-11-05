package com.xmx.wenote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xmx.wenote.ChoosePhoto.AlbumActivity;
import com.xmx.wenote.ChoosePhoto.BigPhotoActivity;
import com.xmx.wenote.ChoosePhoto.entities.GifImageView;
import com.xmx.wenote.Database.SQLManager;

import java.util.ArrayList;

/**
 * Created by The_onE on 2015/10/11.
 */
public class NoteFragment extends Fragment {
    ArrayList<LinearLayout> layouts = new ArrayList<>();
    ArrayList<GifImageView> images = new ArrayList<>();
    ArrayList<String> paths = new ArrayList<>();

    SQLManager sqlManager = new SQLManager();

    NoteHandler noteHandler = new NoteHandler();

    Button clear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.note_view, container, false);
    }

    private String getTitle() {
        return ((EditText) getActivity().findViewById(R.id.title)).getText().toString();
    }

    private String getText() {
        return ((EditText) getActivity().findViewById(R.id.text)).getText().toString();
    }

    class NoteThread implements Runnable {
        @Override
        public void run() {
            boolean flag = sqlManager.insertNote(getTitle(), getText(), paths);
            Message msg = Message.obtain();
            Bundle b = new Bundle();
            b.putBoolean("flag", flag);
            msg.setData(b);
            NoteFragment.this.noteHandler.sendMessage(msg);
        }
    }

    class NoteHandler extends Handler {
        public NoteHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            boolean flag = msg.getData().getBoolean("flag");
            if (flag) {
                Toast.makeText(NoteFragment.this.getContext(), "记录成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NoteFragment.this.getContext(), "记录失败", Toast.LENGTH_SHORT).show();
            }
        }
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
                if (getTitle().isEmpty()) {
                    Toast.makeText(getContext(), "必须输入标题", Toast.LENGTH_SHORT).show();
                    return;
                }
                NoteThread t = new NoteThread();
                new Thread(t).start();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            paths.addAll(data.getStringArrayListExtra("paths"));

            LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.note_layout);
            for (LinearLayout l : layouts) {
                parent.removeView(l);
            }
            layouts.clear();
            images.clear();

            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();

            LinearLayout l = new LinearLayout(getContext());
            for (int i = 0; i < paths.size(); ++i) {
                String path = paths.get(i);
                if (i % 4 == 0) {
                    l = new LinearLayout(getContext());
                    l.setOrientation(LinearLayout.HORIZONTAL);
                    l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    l.setVerticalGravity(Gravity.CENTER);
                    parent.addView(l);
                    layouts.add(l);
                }
                GifImageView iv = new GifImageView(getActivity());
                iv.setImageResource(R.drawable.pic_loading);
                iv.setImageByPathLoader(path);
                iv.setLayoutParams(new LinearLayout.LayoutParams(width / 4, width / 4));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), BigPhotoActivity.class);
                        //intent.putExtra("path", path);
                        intent.putExtra("paths", paths);
                        intent.putExtra("index", paths.indexOf(((GifImageView) v).getPath()));
                        startActivity(intent);
                    }
                });
                l.addView(iv);
                images.add(iv);
            }

            if (clear == null) {
                clear = new Button(getActivity());
                clear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                clear.setText(R.string.clear);
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.note_layout);
                        for (LinearLayout l : layouts) {
                            parent.removeView(l);
                        }
                        layouts.clear();
                        images.clear();
                        paths.clear();
                        ((LinearLayout) getActivity().findViewById(R.id.photo_button_layout)).removeView(clear);
                        clear = null;
                    }
                });
                ((LinearLayout) getActivity().findViewById(R.id.photo_button_layout)).addView(clear);
            }
        }
    }


}
