package com.xmx.wenote;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmx.wenote.Database.SQLManager;

/**
 * Created by The_onE on 2015/10/11.
 */
public class TimelineFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SQLManager sqlManager = new SQLManager();

        return inflater.inflate(R.layout.timeline_view, container, false);
    }

}
