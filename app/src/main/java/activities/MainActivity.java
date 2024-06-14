package activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listadecompras.R;

import java.util.ArrayList;

import database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvShoppingLists;
    private Button btnAddNewList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> shoppingLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        lvShoppingLists = findViewById(R.id.lvShoppingLists);
        btnAddNewList = findViewById(R.id.btnAddNewList);
        shoppingLists = new ArrayList<>();

        loadShoppingLists();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shoppingLists);
        lvShoppingLists.setAdapter(adapter);

        btnAddNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddListActivity.class);
                startActivity(intent);
            }
        });

        lvShoppingLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String listName = shoppingLists.get(position);
                Intent intent = new Intent(MainActivity.this, EditListActivity.class);
                intent.putExtra("listName", listName);
                startActivity(intent);
            }
        });
    }

    private void loadShoppingLists() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_LIST, null);
        shoppingLists.clear();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIST_NAME));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIST_DATE));
            shoppingLists.add(name + " (" + date + ")");
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadShoppingLists();
        adapter.notifyDataSetChanged();
    }
}
