package com.example.listadecompras;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditListActivity extends AppCompatActivity {

    private EditText etListName;
    private ListView lvItems;
    private TextView tvTotalPrice;
    private Button btnSaveList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;
    private ArrayList<String> selectedItems;
    private double totalPrice = 0.0;
    private int listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        etListName = findViewById(R.id.etListName);
        lvItems = findViewById(R.id.lvItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnSaveList = findViewById(R.id.btnSaveList);

        items = new ArrayList<>();
        selectedItems = new ArrayList<>();

        items.add("Arroz 1 Kg (R$ 2.69)");
        items.add("Leite longa vida (R$ 2.70)");
        items.add("Carne Friboi (R$ 16.70)");
        items.add("Feijão carioquinha 1 Kg (R$ 3.38)");
        items.add("Refrigerante coca-cola 2 litros (R$ 3.00)");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, items);
        lvItems.setAdapter(adapter);
        lvItems.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Intent intent = getIntent();
        String listName = intent.getStringExtra("listName");
        etListName.setText(listName);

        loadExistingItems(listName);

        lvItems.setOnItemClickListener((parent, view, position, id) -> {
            String item = items.get(position);
            String priceStr = item.substring(item.lastIndexOf("R$") + 2, item.length() - 1);
            double price = Double.parseDouble(priceStr.replace(',', '.'));
            if (selectedItems.contains(item)) {
                selectedItems.remove(item);
                totalPrice -= price;
            } else {
                selectedItems.add(item);
                totalPrice += price;
            }
            tvTotalPrice.setText(String.format(Locale.getDefault(), "Total: R$ %.2f", totalPrice));
        });

        btnSaveList.setOnClickListener(v -> {
            String newListName = etListName.getText().toString();
            if (newListName.isEmpty() || selectedItems.isEmpty()) {
                // Show error message
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(EditListActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues listValues = new ContentValues();
            listValues.put(DatabaseHelper.COLUMN_LIST_NAME, newListName);
            listValues.put(DatabaseHelper.COLUMN_LIST_DATE, new Date().toString());
            db.update(DatabaseHelper.TABLE_LIST, listValues, DatabaseHelper.COLUMN_LIST_ID + " = ?", new String[]{String.valueOf(listId)});

            db.delete(DatabaseHelper.TABLE_ITEMS, DatabaseHelper.COLUMN_ITEM_LIST_ID + " = ?", new String[]{String.valueOf(listId)});

            for (String item : selectedItems) {
                String[] itemParts = item.split(" \\(R\\$ ");
                String itemName = itemParts[0];
                double itemPrice = Double.parseDouble(itemParts[1].substring(0, itemParts[1].length() - 1).replace(',', '.'));

                ContentValues itemValues = new ContentValues();
                itemValues.put(DatabaseHelper.COLUMN_ITEM_LIST_ID, listId);
                itemValues.put(DatabaseHelper.COLUMN_ITEM_NAME, itemName);
                itemValues.put(DatabaseHelper.COLUMN_ITEM_PRICE, itemPrice);
                itemValues.put(DatabaseHelper.COLUMN_ITEM_QUANTITY, 1);  // Default quantity
                db.insert(DatabaseHelper.TABLE_ITEMS, null, itemValues);
            }

            db.close();
            finish();
        });
    }

    private void loadExistingItems(String listName) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String listQuery = "SELECT " + DatabaseHelper.COLUMN_LIST_ID + " FROM " + DatabaseHelper.TABLE_LIST +
                " WHERE " + DatabaseHelper.COLUMN_LIST_NAME + " = ?";
        Cursor cursor = db.rawQuery(listQuery, new String[]{listName});
        if (cursor.moveToFirst()) {
            listId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIST_ID));

            String itemsQuery = "SELECT * FROM " + DatabaseHelper.TABLE_ITEMS +
                    " WHERE " + DatabaseHelper.COLUMN_ITEM_LIST_ID + " = ?";
            Cursor itemsCursor = db.rawQuery(itemsQuery, new String[]{String.valueOf(listId)});
            selectedItems.clear();
            while (itemsCursor.moveToNext()) {
                String itemName = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_NAME));
                double itemPrice = itemsCursor.getDouble(itemsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_PRICE));
                selectedItems.add(itemName + " (R$ " + String.format(Locale.getDefault(), "%.2f", itemPrice) + ")");

                totalPrice += itemPrice;
            }
            itemsCursor.close();
        }
        cursor.close();
        db.close();

        for (int i = 0; i < items.size(); i++) {
            if (selectedItems.contains(items.get(i))) {
                lvItems.setItemChecked(i, true);
            }
        }

        tvTotalPrice.setText(String.format(Locale.getDefault(), "Total: R$ %.2f", totalPrice));
    }
}