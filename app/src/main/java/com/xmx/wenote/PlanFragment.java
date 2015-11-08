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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xmx.wenote.ChoosePhoto.AlbumActivity;
import com.xmx.wenote.ChoosePhoto.BigPhotoActivity;
import com.xmx.wenote.ChoosePhoto.entities.GifImageView;
import com.xmx.wenote.Database.SQLManager;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by The_onE on 2015/10/11.
 */
public class PlanFragment extends Fragment {
    ArrayList<LinearLayout> layouts = new ArrayList<>();
    ArrayList<GifImageView> images = new ArrayList<>();
    ArrayList<String> paths = new ArrayList<>();

    SQLManager sqlManager = new SQLManager();

    PlanHandler planHandler = new PlanHandler();

    Button clear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plan_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TimePicker time = (TimePicker) getActivity().findViewById(R.id.plan_time);
        time.setIs24HourView(true);

        Button choose = (Button) getActivity().findViewById(R.id.plan_choose);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AlbumActivity.class);
                startActivityForResult(i, CHOOSE_IMAGE);
            }
        });

        Button plan = (Button) getActivity().findViewById(R.id.plan);
        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTitle().isEmpty()) {
                    Toast.makeText(getContext(), "必须输入标题", Toast.LENGTH_SHORT).show();
                    return;
                }
                PlanThread t = new PlanThread();
                new Thread(t).start();

            }
        });
    }

    private String getTitle() {
        return ((EditText) getActivity().findViewById(R.id.plan_title)).getText().toString();
    }

    private String getText() {
        return ((EditText) getActivity().findViewById(R.id.plan_text)).getText().toString();
    }

    class PlanThread implements Runnable {
        @Override
        public void run() {
            DatePicker date = (DatePicker) getActivity().findViewById(R.id.plan_date);
            int year = date.getYear() - 1900;
            int month = date.getMonth();
            int day = date.getDayOfMonth();
            TimePicker time = (TimePicker) getActivity().findViewById(R.id.plan_time);
            int hour = time.getCurrentHour();
            int minute = time.getCurrentMinute();
            Date d = new Date(year, month, day);
            d.setHours(hour);
            d.setMinutes(minute);
            boolean flag = sqlManager.insertPlan(getTitle(), getText(), paths, d);
            Message msg = Message.obtain();
            Bundle b = new Bundle();
            b.putBoolean("flag", flag);
            msg.setData(b);
            PlanFragment.this.planHandler.sendMessage(msg);
        }
    }

    class PlanHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            boolean flag = msg.getData().getBoolean("flag");
            if (flag) {
                Toast.makeText(PlanFragment.this.getContext(), "记录成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PlanFragment.this.getContext(), "记录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static final int CHOOSE_IMAGE = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            paths.addAll(data.getStringArrayListExtra("paths"));

            LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.plan_layout);
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
                        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.plan_layout);
                        for (LinearLayout l : layouts) {
                            parent.removeView(l);
                        }
                        layouts.clear();
                        images.clear();
                        paths.clear();
                        ((LinearLayout) getActivity().findViewById(R.id.plan_photo_button_layout)).removeView(clear);
                        clear = null;
                    }
                });
                ((LinearLayout) getActivity().findViewById(R.id.plan_photo_button_layout)).addView(clear);
            }
        }
    }
}
