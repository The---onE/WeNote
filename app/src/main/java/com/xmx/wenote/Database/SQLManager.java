package com.xmx.wenote.Database;

import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by The_onE on 2015/10/23.
 */
public class SQLManager {

    SQLiteDatabase database = null;

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
            if (!file.exists()) {
                database = SQLiteDatabase.openOrCreateDatabase(file, null);
                String createTableSQL = "create table NOTE" +
                        "(ID integer not null primary key" +
                        ", TIME not null default(datetime('now', 'localtime')))";
                database.execSQL(createTableSQL);

            } else {
                database = SQLiteDatabase.openOrCreateDatabase(file, null);
            }
        }
        return database != null;
    }

    private boolean checkDatabase() {
        if (database != null) {
            return true;
        } else {
            return openDatabase();
        }
    }
}
