package com.example.tablet;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private ListView borrowedItemsListView;
    private EditText filterEditText;
    private Button filterButton;
    private Button deleteButton;
    private ArrayAdapter<String> adapter;
    private List<String> items;
    private JSONArray receiptArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        borrowedItemsListView = findViewById(R.id.borrowedItemsListView);
        filterEditText = findViewById(R.id.filterEditText);
        filterButton = findViewById(R.id.filterButton);
        deleteButton = findViewById(R.id.deleteButton);

        items = new ArrayList<>();
        File file = new File(getFilesDir(), "borrowed_tablets.json");

        if (file.exists()) {
            try {
                FileReader reader = new FileReader(file);
                StringBuilder jsonData = new StringBuilder();
                int i;
                while ((i = reader.read()) != -1) {
                    jsonData.append((char) i);
                }
                reader.close();

                receiptArray = new JSONArray(jsonData.toString());
                updateListView(receiptArray);

            } catch (Exception e) {
                Toast.makeText(this, "Error loading data.", Toast.LENGTH_SHORT).show();
            }
        } else {
            receiptArray = new JSONArray();
        }

        filterButton.setOnClickListener(v -> filterReceipts());
        deleteButton.setOnClickListener(v -> deleteSelectedReceipt());
    }

    private void updateListView(JSONArray receiptArray) {
        items.clear();
        for (int j = 0; j < receiptArray.length(); j++) {
            try {
                JSONObject receipt = receiptArray.getJSONObject(j);
                String item = "Brand: " + receipt.getString("Tablet Brand") + ", Includes cable: " + receipt.getString("Include Cable") + ", Name: " + receipt.getString("Name") + ", Date: " + receipt.getString("Date & Time");
                items.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, items);
        borrowedItemsListView.setAdapter(adapter);
        borrowedItemsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void filterReceipts() {
        String query = filterEditText.getText().toString().trim().toLowerCase();

        if (query.isEmpty()) {
            updateListView(receiptArray);
            return;
        }

        JSONArray filteredArray = new JSONArray();
        for (int j = 0; j < receiptArray.length(); j++) {
            try {
                JSONObject receipt = receiptArray.getJSONObject(j);
                String name = receipt.getString("Name").toLowerCase();
                String brand = receipt.getString("Tablet Brand").toLowerCase();
                String dateTime = receipt.getString("Date & Time").toLowerCase();

                if (brand.contains(query) || dateTime.contains(query)|| name.contains(query)) {
                    filteredArray.put(receipt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        updateListView(filteredArray);
    }

    private void deleteSelectedReceipt() {
        int position = borrowedItemsListView.getCheckedItemPosition();
        if (position == ListView.INVALID_POSITION) {
            Toast.makeText(this, "Please select an item to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        items.remove(position);
        receiptArray.remove(position);

        // Update the JSON file
        try {
            File file = new File(getFilesDir(), "borrowed_tablets.json");
            FileWriter writer = new FileWriter(file);
            writer.write(receiptArray.toString());
            writer.close();

            Toast.makeText(this, "Receipt deleted successfully.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error deleting receipt.", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }
}


