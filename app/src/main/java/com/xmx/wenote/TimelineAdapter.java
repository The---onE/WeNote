package com.xmx.wenote;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmx.wenote.ChoosePhoto.BigPhotoActivity;
import com.xmx.wenote.ChoosePhoto.adapter.ShowAdapter;
import com.xmx.wenote.Database.SQLManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by The_onE on 2015/11/15.
 */
public class TimelineAdapter extends BaseExpandableListAdapter {
    Context context;
    private SQLManager database = new SQLManager();
    private float density;
    Map<Integer, Integer> ids = new HashMap<>();

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getItem(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getItemId(childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.timeline_group, null);
        }
        TextView text = (TextView) convertView.findViewById(R.id.timeline_group);
        text.setText(" ");

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return getView(childPosition, convertView, parent);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewHolder {
        TextView titleTV;
        TextView timeTV;
        GridView photos;
    }

    TimelineAdapter(Context context, float density) {
        this.context = context;
        this.density = density;
    }

    public int getCount() {
        return database.getCount();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return ids.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.timeline_item, null);
            holder = new ViewHolder();
            holder.titleTV = (TextView) convertView.findViewById(R.id.timeline_title);
            holder.timeTV = (TextView) convertView.findViewById(R.id.timeline_time);
            holder.photos = (GridView) convertView.findViewById(R.id.timeline_photos);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Cursor cursor = database.selectOneNoteOrderByTime(position);
        if (cursor.moveToFirst()) {
            //ID TITLE TEXT PHOTO TIME
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String text = cursor.getString(2);
            String p = cursor.getString(3);
            ArrayList<String> photos = database.getPhotos(p);
            Date date = new Date(cursor.getLong(4));

            ids.put(position, id);

            holder.titleTV.setText(title);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            holder.timeTV.setText(df.format(date));

            if (photos != null) {
                holder.photos.setHorizontalSpacing((int) (5 * density));
                holder.photos.setColumnWidth((int) (100 * density));

                holder.photos.setAdapter(new ShowAdapter(parent.getContext(), photos));

                holder.photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(view.getContext(), BigPhotoActivity.class);
                        ShowAdapter a = (ShowAdapter) parent.getAdapter();
                        ArrayList<String> paths = a.getPaths();
                        intent.putExtra("paths", paths);
                        intent.putExtra("index", position);
                        parent.getContext().startActivity(intent);
                    }
                });

                int size = photos.size();

                int allWidth = (int) ((100 + 5) * density * size - 10);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        allWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                holder.photos.setLayoutParams(params);
                holder.photos.setNumColumns(size);
            } else {
                holder.photos.setAdapter(null);
            }

            cursor.close();
            return convertView;
        }
        return null;
    }
}
