package com.xmx.wenote.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by The_onE on 2015/10/23.
 */
public class SQLManager {

    SQLiteDatabase database = null;

    public SQLManager() {
        openDatabase();
    }

    static final int NOTE = 0;
    static final int PLAN = 1;

    private boolean openDatabase() {
        String d = android.os.Environment.getExternalStorageDirectory() + "/WeNote/Database";
        File dir = new File(d);
        boolean flag = dir.exists() || dir.mkdirs();

        if (flag) {
            String sqlFile = android.os.Environment.getExternalStorageDirectory() + "/WeNote/Database/note.db";
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
                    "TIME not null default(datetime('now', 'localtime')), " +
                    "TYPE integer default(" + NOTE + ")" +
                    ")";
            database.execSQL(createNoteSQL);

            String createPhotoSQL = "create table if not exists PHOTOS(" +
                    "ID integer not null primary key autoincrement, " +
                    "PHOTO text not null" +
                    ")";
            database.execSQL(createPhotoSQL);
        }
        return database != null;
    }

    private boolean checkDatabase() {
        return database != null || openDatabase();
    }

    private String insertPhotos(ArrayList<String> paths) {
        String photos = "";
        String d = android.os.Environment.getExternalStorageDirectory() + "/WeNote/Images";
        File dir = new File(d);
        boolean flag = dir.exists() || dir.mkdirs();
        if (flag) {
            for (String path : paths) {
                ContentValues content = new ContentValues();

            /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
            byte[] bytes = baos.toByteArray();*/

                int id = 0;
                String newPath = android.os.Environment.getExternalStorageDirectory() + "/WeNote/Images/" + path.hashCode();
                File newFile = new File(newPath);
                if (newFile.exists()) {
                    Cursor cursor = database.rawQuery("select ID from PHOTOS where PHOTO=?", new String[]{newPath});
                    if (cursor.moveToFirst()) {
                        id = cursor.getInt(0);
                    }
                    cursor.close();
                }
                if (id == 0) {
                    try {
                        File photo = new File(path);
                        if (photo.exists()) {
                            InputStream is = new FileInputStream(path);
                            FileOutputStream os = new FileOutputStream(newPath);
                            byte[] buffer = new byte[1444];
                            int count;
                            while ((count = is.read(buffer)) != -1) {
                                os.write(buffer, 0, count);
                            }
                            is.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    content.put("PHOTO", newPath);

                    database.insert("PHOTOS", null, content);

                    Cursor cursor = database.rawQuery("select last_insert_rowid()", null);
                    cursor.moveToFirst();
                    id = cursor.getInt(0);
                    cursor.close();
                }
                photos = photos + id + "|";
            }
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
        content.put("TYPE", NOTE);

        database.insert("NOTE", null, content);

        return true;
    }

    public boolean insertPlan(String title, String text, ArrayList<String> paths
            , int year, int month, int day, int hour, int minute) {
        if (!checkDatabase()) {
            return false;
        }
        ContentValues content = new ContentValues();
        content.put("TITLE", title);
        content.put("TEXT", text);
        String photosId = insertPhotos(paths);
        content.put("PHOTOS", photosId);
        content.put("TYPE", PLAN);
        content.put("TIME", ""+year+"-"+month+"-"+day+" "+hour+":"+minute+":00");

        database.insert("NOTE", null, content);

        return true;
    }

    private String getPhoto(int id) {
        Cursor cursor = database.rawQuery("select PHOTO from PHOTOS where ID=?", new String[]{"" + id});
        if (cursor.moveToFirst()) {
            String path = cursor.getString(0);
            cursor.close();
            return path;
        } else {
            return null;
        }
    }

    //ID TITLE TEXT PHOTO TIME
    public Cursor getCursor() {
        if (!checkDatabase()) {
            return null;
        }
        return database.rawQuery("select * from NOTE", null);
    }

    public ArrayList<String> getPhotos(String idsString) {
        if (!checkDatabase()) {
            return null;
        }
        if (idsString.isEmpty()) {
            return null;
        }
        ArrayList<String> paths = new ArrayList<>();
        String[] ids = idsString.split("\\|");
        for (String idString : ids) {
            if (!idString.equals("")) {
                int id = Integer.parseInt(idString);
                paths.add(getPhoto(id));
            }
        }
        return paths;
    }
}
