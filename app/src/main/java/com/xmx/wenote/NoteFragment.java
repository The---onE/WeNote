package com.xmx.wenote;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.xmx.wenote.ChoosePhoto.PhotoAlbumActivity;

import java.util.ArrayList;

/**
 * Created by The_onE on 2015/10/11.
 */
public class NoteFragment extends Fragment {
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
                //Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent i = new Intent(getActivity(), PhotoAlbumActivity.class);
                startActivityForResult(i, CHOOSE_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> paths = data.getStringArrayListExtra("paths");
            Toast.makeText(getActivity(), paths.toString(), Toast.LENGTH_LONG).show();
        }
    }


}
