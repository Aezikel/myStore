package com.example.MyStore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.MyStore.data.StoreContract.StoreEntry;

import androidx.annotation.Nullable;

public class StoreDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;

    public StoreDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_STATEMENT = "CREATE TABLE " + StoreEntry.TABLE_NAME + " ("
                + StoreEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StoreEntry.PHOTO + " BLOB NOT NULL, "
                + StoreEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + StoreEntry.PRICE + " TEXT NOT NULL, "
                + StoreEntry.QUANTITY + " INTEGER  NOT NULL DEFAULT 0, "
                + StoreEntry.DESCRIPTION + " TEXT, "
                + StoreEntry.SUPPLIER + " TEXT NOT NULL, "
                + StoreEntry.CONTACT + " TEXT NOT NULL, "
                + StoreEntry.EMAIL + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE_STATEMENT);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}