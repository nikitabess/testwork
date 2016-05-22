package com.bessmertniyy.nikita.testwork.db.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bessmertniyy.nikita.testwork.db.tables.LinkTable;
import com.bessmertniyy.nikita.testwork.db.utils.LinkDatabaseHelper;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Working on 20.05.2016.
 */
public class LinkDatabaseContentProvider extends ContentProvider {

    private LinkDatabaseHelper linkDatabaseHelper;

    private static final int LINKS   = 1;
    private static final int LINK_ID = 2;

    private static final String URI_AUTHORITY = "com.bessmertniyy.nikita.testwork.db.contentprovider";

    private static final String URI_BASE_PATH = "links";

    public static final Uri URI_CONTENT = Uri.parse("content://" +  URI_AUTHORITY + "/" + URI_BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/links";

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/link";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(URI_AUTHORITY, URI_BASE_PATH, LINKS);
        sURIMatcher.addURI(URI_AUTHORITY, URI_BASE_PATH + "/#", LINK_ID);
    }

    @Override
    public boolean onCreate() {
        linkDatabaseHelper = new LinkDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        checkForExistingColumns(projection);

        sqLiteQueryBuilder.setTables(LinkTable.TABLE_TITLE);

        int uriType = sURIMatcher.match(uri);

            switch (uriType){
                case LINKS:
                    break;
                case LINK_ID:
                    sqLiteQueryBuilder.appendWhere(LinkTable.TABLE_LINK_COLUMN_ID
                            + "="
                            + uri.getLastPathSegment());
                    break;

                default: throw new IllegalArgumentException("Wrong URI: " + uri);
            }

        SQLiteDatabase sqLiteDatabase = linkDatabaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase database = linkDatabaseHelper.getWritableDatabase();

        long id = 0;

            switch (uriType){
                case LINKS:
                    id = database.insert(LinkTable.TABLE_TITLE, null, values);
                    break;

                default: throw new IllegalArgumentException("Not defined URI: " + uri);
            }

        getContext().getContentResolver().notifyChange(uri, null);

        return uri.parse(URI_BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase database = linkDatabaseHelper.getWritableDatabase();

        int rowsDeleted = 0;

            switch (uriType){
                case LINKS:
                    rowsDeleted = database.delete(LinkTable.TABLE_TITLE, selection, selectionArgs);
                    break;
                case LINK_ID:
                    String id = uri.getLastPathSegment();
                        if (TextUtils.isEmpty(selection)){
                            rowsDeleted = database.delete(LinkTable.TABLE_TITLE, LinkTable.TABLE_LINK_COLUMN_ID + "=" + id, null);
                        }else{
                            rowsDeleted = database.delete(LinkTable.TABLE_TITLE, LinkTable.TABLE_LINK_COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                        }
                    break;
                default: throw new IllegalArgumentException("Not defined URI: " + uri);
            }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase database = linkDatabaseHelper.getWritableDatabase();

        int rowsUpdated = 0;

            switch (uriType){
                case LINKS:
                    rowsUpdated = database.update(LinkTable.TABLE_TITLE, values, selection, selectionArgs);
                    break;
                case LINK_ID:
                    String id = uri.getLastPathSegment();
                        if (TextUtils.isEmpty(selection)){
                            rowsUpdated = database.update(LinkTable.TABLE_TITLE, values, LinkTable.TABLE_LINK_COLUMN_ID + "=" + id, null);
                        }else{
                            rowsUpdated = database.update(LinkTable.TABLE_TITLE, values, LinkTable.TABLE_LINK_COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                        }
                    break;

                default: throw new IllegalArgumentException("Not defined URI: " + uri);
            }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    private void checkForExistingColumns(String[] projection){

        String[] definedColumns = {LinkTable.TABLE_LINK_COLUMN_ID, LinkTable.TABLE_LINK_COLUMN_URL,
                LinkTable.TABLE_LINK_COLUMN_STATUS, LinkTable.TABLE_LINK_COLUMN_ADD_DATE};

        if (projection != null){
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(definedColumns));

            if(!requestedColumns.containsAll(availableColumns)){
                throw new IllegalArgumentException("Not defined columns");
            }
        }


    }

}
