package com.example.gigapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddGigActivity extends AppCompatActivity {

    EditText etGigName, etGigLocation, etGigDetails;
    ImageView imgPoster;
    Button btnUpload;
    Uri imageUri;

    FirebaseAuth mAuth;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gig);

        // Initialize views
        etGigName = findViewById(R.id.etGigName);
        etGigLocation = findViewById(R.id.etGigLocation);
        etGigDetails = findViewById(R.id.etGigDetails);
        imgPoster = findViewById(R.id.imgGigPoster);
        btnUpload = findViewById(R.id.btnUploadGig);

        mAuth = FirebaseAuth.getInstance();

        // Select image
        imgPoster.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Upload gig
        btnUpload.setOnClickListener(v -> uploadGig());
    }

    private void uploadGig() {
        String gigName = etGigName.getText().toString().trim();
        String location = etGigLocation.getText().toString().trim();
        String details = etGigDetails.getText().toString().trim();

        if (TextUtils.isEmpty(gigName) || TextUtils.isEmpty(location) || TextUtils.isEmpty(details) || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select a poster", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        HashMap<String, Object> gig = new HashMap<>();
        gig.put("gigName", gigName);
        gig.put("location", location);
        gig.put("details", details);
        gig.put("ownerId", uid);
        // Poster not uploaded — only shown locally

        FirebaseDatabase.getInstance().getReference("Gigs")
                .push().setValue(gig)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Gig posted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to post gig", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgPoster.setImageURI(imageUri); // Show the selected image locally
        }
    }
}
