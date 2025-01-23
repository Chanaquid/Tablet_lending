package com.example.tablet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

public class UserActivity extends AppCompatActivity {
    private Spinner brandSpinner;
    private CheckBox cableCheckBox;
    private EditText nameEditText, emailEditText, phoneEditText;
    private Button dateTimeButton, submitButton;
    private String selectedDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        brandSpinner = findViewById(R.id.brandSpinner);
        cableCheckBox = findViewById(R.id.cableCheckBox);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        dateTimeButton = findViewById(R.id.dateTimeButton);
        submitButton = findViewById(R.id.submitButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.brands, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(adapter);

        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBrand = parent.getItemAtPosition(position).toString();
                if (selectedBrand.equals("Acer")) {
                    cableCheckBox.setText("Include USB-C Cable");
                    cableCheckBox.setVisibility(View.VISIBLE);
                } else if (selectedBrand.equals("Samsung")) {
                    cableCheckBox.setText("Include Micro-USB Cable");
                    cableCheckBox.setVisibility(View.VISIBLE);
                } else {
                    cableCheckBox.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cableCheckBox.setVisibility(View.GONE);
            }
        });

        dateTimeButton.setOnClickListener(v -> selectDateTime());

        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void selectDateTime() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                String time = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
                selectedDateTime = date + " " + time;
                dateTimeButton.setText(selectedDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void validateAndSubmit() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String brand = brandSpinner.getSelectedItem().toString();
        boolean includeCable = cableCheckBox.isChecked();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || brand.equals("Select Brand") || selectedDateTime.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a JSON object for the receipt
        JSONObject receiptObject = new JSONObject();
        try {
            receiptObject.put("Tablet Brand", brand);
            receiptObject.put("Include Cable", includeCable ? "Yes" : "No");
            receiptObject.put("Name", name);
            receiptObject.put("Email", email);
            receiptObject.put("Phone", phone);
            receiptObject.put("Date & Time", selectedDateTime);

            // Save to JSON file
            saveReceiptToFile(receiptObject);

            // Pass receipt to ReceiptActivity
            Intent intent = new Intent(this, ReceiptActivity.class);
            intent.putExtra("receipt", receiptObject.toString()); // Pass receipt as a string
            startActivity(intent); // Redirect to the receipt page

        } catch (Exception e) {
            Toast.makeText(this, "Error saving receipt.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveReceiptToFile(JSONObject receiptObject) {
        try {
            File file = new File(getFilesDir(), "borrowed_tablets.json");
            JSONArray receiptArray;

            // Initialize or load the JSON array
            if (file.exists() && file.length() > 0) {
                try (FileReader reader = new FileReader(file)) {
                    StringBuilder jsonData = new StringBuilder();
                    int i;
                    while ((i = reader.read()) != -1) {
                        jsonData.append((char) i);
                    }
                    receiptArray = new JSONArray(jsonData.toString());
                } catch (Exception e) {
                    // If reading fails, initialize a new array
                    receiptArray = new JSONArray();
                }
            } else {
                receiptArray = new JSONArray();
            }

            // Add the new receipt to the array
            receiptArray.put(receiptObject);

            // Write the updated JSON array back to the file
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(receiptArray.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
