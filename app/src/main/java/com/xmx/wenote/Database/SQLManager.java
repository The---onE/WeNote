package com.xmx.wenote.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by The_onE on 2015/10/23.
 */
public class SQLManager {

    SQLiteDatabase database = null;

    public SQLManager() {
        openDatabase();
    }

    private boolean openDatabase() {
        boolean flag;
        String d = android.os.Environment.getExternalStorageDirectory() + "/WeNote";
        File dir = new File(d);
        if (!dir.exists()) {
            flag = dir.mkdirs();
        } else {
            flag = true;
        }

        if (flag) {
            String sqlFile = android.os.Environment.getExternalStorageDirectory() + "/WeNote/note.db";
            File file = new File(sqlFile);
            database = SQLiteDatabase.openOrCreateDatabase(file, null);
            if (database == null)
                return false;
            // ID TITLE TEXT PHOTO TIME
            String createNoteSQL = "create table if not exists NOTE(" +
                    "ID integer not null primary key autoincrement, " +
                    "TITLE text not null, " +
                    "TEXT text, " +
                    "PHOTOS text, " +
                    "TIME not null default(datetime('now', 'localtime')))";
            database.execSQL(createNoteSQL);

            String createPhotoSQL = "create table if not exists PHOTOS(" +
                    "ID integer not null primary key autoincrement, " +
                    "PHOTO blob not null)";
            database.execSQL(createPhotoSQL);
        }
        return database != null;
    }

    private boolean checkDatabase() {
        return database != null || openDatabase();
    }

    public String insertPhotos(ArrayList<String> paths) {
        String photos = new String();
        for (String path : paths) {
            ContentValues content = new ContentValues();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, baos);
            byte[] bytes = baos.toByteArray();

            content.put("PHOTO", bytes);

            database.insert("PHOTOS", null, content);

            Cursor cursor = database.rawQuery("select last_insert_rowid()", null);
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            photos = photos + id + "|";
        }
        return photos;
    }

    public boolean insertNote(String title, String text, ArrayList<String> paths) {
        if (!checkDatabase()) {
            return false;
        }
        ContentValues content = new ContentValues();
        content.put("TITLE", title);
        content.put("TEXT", text);
        String photosId = insertPhotos(paths);
        content.put("PHOTOS", photosId);

        database.insert("NOTE", null, content);

        return true;
    }

    public Bitmap getPhoto(int id) {
        if (!checkDatabase()) {
            return null;
        }
        Cursor cursor = database.rawQuery("select PHOTO from PHOTOS where ID=?", new String[]{"" + id});
        cursor.moveToFirst();
        byte[] bytes = cursor.getBlob(0);
        if (bytes.length != 0) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }
}
