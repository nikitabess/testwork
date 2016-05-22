package com.bessmertniyy.nikita.testwork.db.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bessmertniyy.nikita.testwork.db.tables.LinkTable;

/**
 * Created by Working on 20.05.2016.
 */
public class LinkDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_TITLE = "linkDatabase.db";
    private static final int DB_VERSION  = 1;

    public LinkDatabaseHelper(Context context){
        super (context, DB_TITLE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        LinkTable.create(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LinkTable.upgrade(db, oldVersion, newVersion);
    }
}
