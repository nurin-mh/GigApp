package com.example.gigapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // simple layout with buttons
    }

    public void openRegister(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void openLogin(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
