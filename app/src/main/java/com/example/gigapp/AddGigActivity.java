// Updated AddGigActivity.java
package com.example.gigapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddGigActivity extends AppCompatActivity {

    private EditText etGigName, etGigLocation, etGigDetails, etGigWorkers, etGigSalary, etGigDays;
    private LinearLayout datesContainer;
    private ImageView imgPoster;
    private Button btnUpload;
    private Uri imageUri;
    private String savedImageFileName;
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth mAuth;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gig);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etGigName = findViewById(R.id.etGigName);
        etGigLocation = findViewById(R.id.etGigLocation);
        etGigDetails = findViewById(R.id.etGigDetails);
        etGigWorkers = findViewById(R.id.etGigWorkers);
        etGigSalary = findViewById(R.id.etGigSalary);
        etGigDays = findViewById(R.id.etGigDays);
        datesContainer = findViewById(R.id.datesContainer);
        imgPoster = findViewById(R.id.imgGigPoster);
        btnUpload = findViewById(R.id.btnUploadGig);

        inflater = LayoutInflater.from(this);
        mAuth = FirebaseAuth.getInstance();

        etGigDays.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                generateDayInputs();
            }
        });

        imgPoster.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnUpload.setOnClickListener(v -> uploadGig());
    }

    private void generateDayInputs() {
        datesContainer.removeAllViews();

        int numDays;
        try {
            numDays = Integer.parseInt(etGigDays.getText().toString().trim());
        } catch (NumberFormatException e) {
            return;
        }

        for (int i = 0; i < numDays; i++) {
            View dayView = inflater.inflate(R.layout.item_gig_day, null);

            TextView tvDayLabel = dayView.findViewById(R.id.tvDayLabel);
            EditText etDate = dayView.findViewById(R.id.etDate);
            EditText etTimeRange = dayView.findViewById(R.id.etTimeRange);

            tvDayLabel.setText("Day " + (i + 1));

            etDate.setOnClickListener(v -> showDatePicker(etDate));
            etTimeRange.setOnClickListener(v -> showTimeRangePicker(etTimeRange));

            datesContainer.addView(dayView);
        }
    }

    private void showDatePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            target.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimeRangePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, startHour, startMinute) -> {
            String startTime = String.format("%02d:%02d", startHour, startMinute);
            new TimePickerDialog(this, (view1, endHour, endMinute) -> {
                String endTime = String.format("%02d:%02d", endHour, endMinute);
                target.setText(startTime + " - " + endTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void uploadGig() {
        String name = etGigName.getText().toString().trim();
        String location = etGigLocation.getText().toString().trim();
        String details = etGigDetails.getText().toString().trim();
        String workers = etGigWorkers.getText().toString().trim();
        String salary = etGigSalary.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location) || TextUtils.isEmpty(details)
                || TextUtils.isEmpty(workers) || TextUtils.isEmpty(salary) || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> schedule = new HashMap<>();
        for (int i = 0; i < datesContainer.getChildCount(); i++) {
            View view = datesContainer.getChildAt(i);
            EditText etDate = view.findViewById(R.id.etDate);
            EditText etTime = view.findViewById(R.id.etTimeRange);

            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
                Toast.makeText(this, "Fill date/time for all days", Toast.LENGTH_SHORT).show();
                return;
            }
            schedule.put(date, time);
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            savedImageFileName = "gig_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), savedImageFileName);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = FirebaseDatabase.getInstance().getReference("Gigs").push().getKey();
        Gig gig = new Gig(id, name, location, details, savedImageFileName,
                mAuth.getCurrentUser().getUid(), salary, workers, new HashMap<>(schedule));

        FirebaseDatabase.getInstance().getReference("Gigs").child(id).setValue(gig)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Gig added", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add gig", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPoster.setImageURI(imageUri);
        }
    }
}
