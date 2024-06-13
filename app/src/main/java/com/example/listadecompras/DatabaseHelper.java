package com.example.listadecompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shopping.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LIST = "shopping_list";
    public static final String COLUMN_LIST_ID = "id";
    public static final String COLUMN_LIST_NAME = "name";
    public static final String COLUMN_LIST_DATE = "date";

    public static final String TABLE_ITEMS = "shopping_items";
    public static final String COLUMN_ITEM_ID = "id";
    public static final String COLUMN_ITEM_LIST_ID = "list_id";
    public static final String COLUMN_ITEM_NAME = "name";
    public static final String COLUMN_ITEM_PRICE = "price";
    public static final String COLUMN_ITEM_QUANTITY = "quantity";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createListTable = "CREATE TABLE " + TABLE_LIST + " (" +
                COLUMN_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LIST_NAME + " TEXT, " +
                COLUMN_LIST_DATE + " TEXT)";
        db.execSQL(createListTable);

        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_LIST_ID + " INTEGER, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_ITEM_PRICE + " REAL, " +
                COLUMN_ITEM_QUANTITY + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_ITEM_LIST_ID + ") REFERENCES " + TABLE_LIST + "(" + COLUMN_LIST_ID + "))";
        db.execSQL(createItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        onCreate(db);
    }

    // CRUD operations for shopping lists and items go here
}
