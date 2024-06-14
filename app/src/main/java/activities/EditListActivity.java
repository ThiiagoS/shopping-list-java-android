package activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listadecompras.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import database.DatabaseHelper;
import utils.Dialog;

public class EditListActivity extends AppCompatActivity {

    private EditText etListName;
    private ListView lvItems;
    private TextView tvTotalPrice;
    private Button btnSaveList;
    private Button btnDeleteList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;
    private ArrayList<String> selectedItems;
    private String previusListName;
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
        btnDeleteList = findViewById(R.id.btnDeleteList);

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
        listName = listName.replaceAll("\\(.*?\\)", "").trim();
        previusListName = listName;
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
            DatabaseHelper dbHelper = new DatabaseHelper(EditListActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String newListName = etListName.getText().toString();
            if (newListName.isEmpty()) {
                Dialog.showErrorDialog(this, "Erro", "O nome da lista não pode ser vazio.");
                return;
            }

            if (selectedItems.isEmpty()) {
                Dialog.showErrorDialog(this, "Erro", "Deve ser selecionado pelo menos um item na lista.");
                return;
            }

            String listQuery = "SELECT " + DatabaseHelper.COLUMN_LIST_ID + " FROM " + DatabaseHelper.TABLE_LIST + " WHERE " + DatabaseHelper.COLUMN_LIST_NAME + " = ?";
            Cursor cursor = db.rawQuery(listQuery, new String[]{newListName});

            if(cursor.getCount() > 0 && !previusListName.equals(newListName))  {
                Dialog.showErrorDialog(this, "Erro", "Este nome de lista já esta sendo utilizado.");
                return;
            }

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

        btnDeleteList.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(EditListActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(DatabaseHelper.TABLE_ITEMS, DatabaseHelper.COLUMN_ITEM_LIST_ID + " = ?", new String[]{String.valueOf(listId)});
            db.delete(DatabaseHelper.TABLE_LIST, DatabaseHelper.COLUMN_LIST_ID + " = ?", new String[]{String.valueOf(listId)});
            db.close();
            finish();
        });

    }

    private void loadExistingItems(String listName) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String listQuery = "SELECT " + DatabaseHelper.COLUMN_LIST_ID + " FROM " + DatabaseHelper.TABLE_LIST + " WHERE " + DatabaseHelper.COLUMN_LIST_NAME + " = ?";
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
