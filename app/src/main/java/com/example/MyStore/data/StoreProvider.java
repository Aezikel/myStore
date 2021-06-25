package com.example.MyStore.data;

import android.content.ContentProvider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.MyStore.data.StoreContract.StoreEntry;

public class StoreProvider extends ContentProvider {

    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.STORE_PATH, INVENTORY);

        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.STORE_PATH + "/#", INVENTORY_ID);
    }

    private StoreDbHelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper = new StoreDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query( Uri uri, String[] projection,  String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch(match){
            case INVENTORY:
                cursor = db.query(StoreEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;

            case INVENTORY_ID:
                selection = StoreEntry.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(StoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default: throw new IllegalArgumentException("Unable to query URI");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    public Uri insert( Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long rowId;
        switch (match){
            case INVENTORY:
                rowId = db.insert(StoreEntry.TABLE_NAME,null, values);
               if(rowId == -1){
                   return null;
               }


               else
                   getContext().getContentResolver().notifyChange(uri, null);
                   return ContentUris.withAppendedId(uri, rowId);


            default: throw new IllegalArgumentException("Unable to insert at " + uri);
        }


    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch(match){
            case INVENTORY:
                getContext().getContentResolver().notifyChange(uri, null);
                return db.delete(StoreEntry.TABLE_NAME, null, null);

            case INVENTORY_ID:
                getContext().getContentResolver().notifyChange(uri, null);
                return db.delete(StoreEntry.TABLE_NAME, selection, selectionArgs);

            default:throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch(match){
            case INVENTORY:
                getContext().getContentResolver().notifyChange(uri, null);
                return db.update(StoreEntry.TABLE_NAME,values, null, null);
            case INVENTORY_ID:
                selection = StoreEntry.ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return db.update(StoreEntry.TABLE_NAME,values, selection, selectionArgs);

            default:throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }


}
