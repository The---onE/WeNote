package com.xmx.wenote.ChoosePhoto.entities;

import java.io.Serializable;

public class PhotoInf implements Serializable {
    private int photoID;
    private boolean select;
    private String path;

    public PhotoInf(int id, String path) {
        photoID = id;
        select = false;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public int getPhotoID() {
        return photoID;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
