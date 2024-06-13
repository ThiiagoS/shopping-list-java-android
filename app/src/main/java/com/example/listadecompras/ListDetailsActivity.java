package com.example.listadecompras;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ListDetailsActivity extends AppCompatActivity {

    private ListView lvListItems;
    private Button btnEditList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listItems;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        lvListItems = findViewById(R.id.lvListItems);
        btnEditList = findViewById(R.id.btnEditList);

        Intent intent = getIntent();
        listName = intent.getStringExtra("listName");

        listItems = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        lvListItems.setAdapter(adapter);

        loadListItems();

        btnEditList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement edit functionality
            }
        });
    }

    private void loadListItems() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String listQuery = "SELECT " + DatabaseHelper.COLUMN_LIST_ID + " FROM " + DatabaseHelper.TABLE_LIST +
                " WHERE " + DatabaseHelper.COLUMN_LIST_NAME + " = ?";
        Cursor cursor = db.rawQuery(listQuery, new String[]{listName});
        if (cursor.moveToFirst()) {
            int listId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIST_ID));

            String itemsQuery = "SELECT * FROM " + DatabaseHelper.TABLE_ITEMS +
                    " WHERE " + DatabaseHelper.COLUMN_ITEM_LIST_ID + " = ?";
            Cursor itemsCursor = db.rawQuery(itemsQuery, new String[]{String.valueOf(listId)});
            listItems.clear();
            while (itemsCursor.moveToNext()) {
                String itemName = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_NAME));
                double itemPrice = itemsCursor.getDouble(itemsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_PRICE));
                listItems.add(itemName + " - R$ " + String.format(Locale.getDefault(), "%.2f", itemPrice));
            }
            itemsCursor.close();
        }
        cursor.close();
        db.close();

        adapter.notifyDataSetChanged();
    }
}
