package com.example.tablet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        passwordEditText = findViewById(R.id.passwordEditText);
    }

    public void login(View view) {
        String password = passwordEditText.getText().toString().trim();

        if (password.equals("admin123")) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
        }
    }
}