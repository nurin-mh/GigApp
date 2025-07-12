package com.example.gigapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // simple layout with buttons

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Log a test event to confirm Firebase connection
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "app_open");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
    }

    public void openRegister(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void openLogin(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
