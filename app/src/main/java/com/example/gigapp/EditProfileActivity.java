package com.example.gigapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    EditText etName, etPhone, etCompanyName, etPosition;
    Button btnSave;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etEditName);
        etPhone = findViewById(R.id.etEditPhone);
        etCompanyName = findViewById(R.id.etEditCompanyName);
        etPosition = findViewById(R.id.etEditPosition);
        btnSave = findViewById(R.id.btnSaveProfile);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        // Load existing data
        FirebaseDatabase.getInstance().getReference("Users").child(uid)
                .get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        etName.setText(snapshot.child("fullName").getValue(String.class));
                        etPhone.setText(snapshot.child("phone").getValue(String.class));
                        etCompanyName.setText(snapshot.child("companyName").getValue(String.class));
                        etPosition.setText(snapshot.child("position").getValue(String.class));
                    }
                });

        // Save changes
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String companyName = etCompanyName.getText().toString().trim();
            String position = etPosition.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(companyName) || TextUtils.isEmpty(position)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    .child("fullName").setValue(name);
            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    .child("phone").setValue(phone);
            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    .child("companyName").setValue(companyName);
            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    .child("position").setValue(position)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                            finish(); // go back to profile fragment
                        } else {
                            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
