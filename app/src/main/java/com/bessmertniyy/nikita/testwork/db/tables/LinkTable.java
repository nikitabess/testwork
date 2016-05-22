package com.bessmertniyy.nikita.testwork.db.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Working on 20.05.2016.
 */
public class LinkTable {

    public static final String TABLE_TITLE = "links";
    public static final String TABLE_LINK_COLUMN_ID = "_id";
    public static final String TABLE_LINK_COLUMN_URL = "link_URL";
    public static final String TABLE_LINK_COLUMN_STATUS = "link_status";
    public static final String TABLE_LINK_COLUMN_ADD_DATE = "link_add_date";

    public static final String TABLE_SORT_ORDER_BY_STATUS = TABLE_LINK_COLUMN_STATUS + " DESC ";
    public static final String TABLE_SORT_ORDER_BY_DATE = TABLE_LINK_COLUMN_ADD_DATE + " DESC ";

    private static final String TABLE_LINK_CREATE_SQL_STATEMENT = "create table "
            + TABLE_TITLE
            + " ("
            + TABLE_LINK_COLUMN_ID
            + " integer primary key autoincrement, "
            + TABLE_LINK_COLUMN_URL
            + " text not null, "
            + TABLE_LINK_COLUMN_ADD_DATE
            + " integer, "
            + TABLE_LINK_COLUMN_STATUS
            + " integer "
            + ");";

    public static void create (SQLiteDatabase database){

        database.execSQL(TABLE_LINK_CREATE_SQL_STATEMENT);

    }

    public static void upgrade (SQLiteDatabase database, int previousVersion, int latestVersion){

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TITLE);
        create(database);

    }


}
