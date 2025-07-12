package com.example.gigapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gigapp.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LandingActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default fragment
        replaceFragment(new HomeFragment());

        // Remove background on BottomNavigationView
        binding.bottomNavigationView.setBackground(null);

        // Handle bottom nav selection
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.myGiG) {
                replaceFragment(new MygigFragment());
            } else if (itemId == R.id.applicant) {
                replaceFragment(new ApplicantFragment());
            } else if (itemId == R.id.account) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        // FAB opens AddGigActivity
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingActivity.this, AddGigActivity.class);
                startActivity(intent);
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}
