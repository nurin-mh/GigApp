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

    EditText etName, etPhone, etGender, etAddress;
    Button btnSave;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etEditName);
        etPhone = findViewById(R.id.etEditPhone);
        etGender = findViewById(R.id.etEditGender);
        etAddress = findViewById(R.id.etEditAddress);
        btnSave = findViewById(R.id.btnSaveProfile);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        // Load existing data
        FirebaseDatabase.getInstance().getReference("Users").child(uid)
                .get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        etName.setText(snapshot.child("fullName").getValue(String.class));
                        etPhone.setText(snapshot.child("phone").getValue(String.class));
                        etGender.setText(snapshot.child("gender").getValue(String.class));
                        etAddress.setText(snapshot.child("address").getValue(String.class));
                    }
                });

        // Save changes
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String gender = etGender.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(address)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    .child("fullName").setValue(name);
            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    .child("phone").setValue(phone);
            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    .child("gender").setValue(gender);
            FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    .child("address").setValue(address)
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
