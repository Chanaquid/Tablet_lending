package com.example.tablet;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class ReceiptActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        TextView receiptTextView = findViewById(R.id.receiptTextView);

        // Get the receipt data from the intent
        String receiptData = getIntent().getStringExtra("receipt");
        if (receiptData != null) {
            try {
                // Parse the JSON object
                JSONObject receipt = new JSONObject(receiptData);
                String receiptText = "Tablet Brand: " + receipt.getString("Tablet Brand") + "\n"
                        + "Include Cable: " + receipt.getString("Include Cable") + "\n"
                        + "Name: " + receipt.getString("Name") + "\n"
                        + "Email: " + receipt.getString("Email") + "\n"
                        + "Phone: " + receipt.getString("Phone") + "\n"
                        + "Date & Time: " + receipt.getString("Date & Time");
                receiptTextView.setText(receiptText);
            } catch (Exception e) {
                receiptTextView.setText("Error displaying receipt.");
            }
        } else {
            receiptTextView.setText("No receipt data available.");
        }
    }
    //go back to home
    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}



