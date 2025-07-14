package com.example.gigapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MygigFragment extends Fragment implements GigAdapter.OnGigActionListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GigAdapter gigAdapter;
    private ArrayList<Gig> gigList;
    private Uri selectedImageUri = null;
    private FirebaseAuth mAuth;
    private DatabaseReference gigsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mygig, container, false);

        recyclerView = view.findViewById(R.id.recyclerMyGigs);
        progressBar = view.findViewById(R.id.progressMyGig);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gigList = new ArrayList<>();
        gigAdapter = new GigAdapter(gigList, this);
        recyclerView.setAdapter(gigAdapter);

        mAuth = FirebaseAuth.getInstance();
        gigsRef = FirebaseDatabase.getInstance().getReference("Gigs");

        fetchMyGigs();
        return view;
    }

    private void fetchMyGigs() {
        progressBar.setVisibility(View.VISIBLE);
        String currentUserId = mAuth.getCurrentUser().getUid();

        gigsRef.orderByChild("ownerId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        gigList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Gig gig = ds.getValue(Gig.class);
                            if (gig != null) {
                                gig.setId(ds.getKey());
                                gigList.add(gig);
                            }
                        }
                        gigAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        if (gigList.isEmpty()) {
                            Toast.makeText(getContext(), "No gigs found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to load gigs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onView(Gig gig) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_view_gig, null);

        ImageView imgPoster = dialogView.findViewById(R.id.dialogImage);
        TextView[] etFields = {
                dialogView.findViewById(R.id.dialogGigName),
                dialogView.findViewById(R.id.dialogLocation),
                dialogView.findViewById(R.id.dialogDetails),
                dialogView.findViewById(R.id.dialogWorkers),
                dialogView.findViewById(R.id.dialogSalary),
        };
        TextView tvSchedule = dialogView.findViewById(R.id.dialogSchedule);

        etFields[0].setText(gig.getGigName());
        etFields[1].setText(gig.getLocation());
        etFields[2].setText(gig.getDetails());
        etFields[3].setText(gig.getWorkers());
        etFields[4].setText(gig.getSalary());
        for (TextView et : etFields) et.setEnabled(false);

        if (!TextUtils.isEmpty(gig.getPosterUrl())) {
            File file = new File(requireContext().getFilesDir(), gig.getPosterUrl());
            imgPoster.setImageURI(file.exists() ? Uri.fromFile(file) : null);
        } else {
            imgPoster.setImageResource(R.drawable.ic_default_poster);
        }

        StringBuilder scheduleBuilder = new StringBuilder();
        if (gig.getSchedule() != null && !gig.getSchedule().isEmpty()) {
            for (Map.Entry<String, String> entry : gig.getSchedule().entrySet()) {
                scheduleBuilder.append("\u2022 ")
                        .append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append("\n");
            }
        } else {
            scheduleBuilder.append("No schedule available");
        }
        tvSchedule.setText(scheduleBuilder.toString().trim());

        new AlertDialog.Builder(getContext())
                .setTitle("View Gig")
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .show();

    }

    @Override
    public void onEdit(Gig gig) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_gig, null);

        EditText etName = dialogView.findViewById(R.id.dialogGigName);
        EditText etLocation = dialogView.findViewById(R.id.dialogLocation);
        EditText etDetails = dialogView.findViewById(R.id.dialogDetails);
        EditText etWorkers = dialogView.findViewById(R.id.dialogWorkers);
        EditText etSalary = dialogView.findViewById(R.id.dialogSalary);
        ImageView imgPoster = dialogView.findViewById(R.id.dialogImage);
        LinearLayout scheduleContainer = dialogView.findViewById(R.id.scheduleContainer);
        Button btnAddSchedule = dialogView.findViewById(R.id.btnAddSchedule);

        etName.setText(gig.getGigName());
        etLocation.setText(gig.getLocation());
        etDetails.setText(gig.getDetails());
        etWorkers.setText(gig.getWorkers());
        etSalary.setText(gig.getSalary());

        if (!TextUtils.isEmpty(gig.getPosterUrl())) {
            File file = new File(requireContext().getFilesDir(), gig.getPosterUrl());
            if (file.exists()) {
                imgPoster.setImageURI(Uri.fromFile(file));
            } else {
                imgPoster.setImageResource(R.drawable.ic_default_poster);
            }
        } else {
            imgPoster.setImageResource(R.drawable.ic_default_poster);
        }

        List<EditText> dateFields = new ArrayList<>();
        List<EditText> startFields = new ArrayList<>();
        List<EditText> endFields = new ArrayList<>();

        Map<String, String> originalSchedule = gig.getSchedule();
        if (originalSchedule != null && !originalSchedule.isEmpty()) {
            for (Map.Entry<String, String> entry : originalSchedule.entrySet()) {
                View row = LayoutInflater.from(getContext()).inflate(R.layout.item_schedule_edit, scheduleContainer, false);
                EditText etDate = row.findViewById(R.id.editDate);
                EditText etStartTime = row.findViewById(R.id.editStartTime);
                EditText etEndTime = row.findViewById(R.id.editEndTime);
                Button btnDelete = row.findViewById(R.id.btnDeleteSchedule);

                etDate.setText(entry.getKey());
                if (entry.getValue().contains("-")) {
                    String[] times = entry.getValue().split("-");
                    etStartTime.setText(times[0].trim());
                    etEndTime.setText(times[1].trim());
                }

                setTimeRangePickers(etDate, etStartTime, etEndTime);

                dateFields.add(etDate);
                startFields.add(etStartTime);
                endFields.add(etEndTime);

                btnDelete.setOnClickListener(v -> {
                    int index = scheduleContainer.indexOfChild(row);
                    if (index >= 0) {
                        scheduleContainer.removeViewAt(index);
                        dateFields.remove(index);
                        startFields.remove(index);
                        endFields.remove(index);
                    }
                });

                scheduleContainer.addView(row);
            }
        }

        btnAddSchedule.setOnClickListener(v -> {
            View row = LayoutInflater.from(getContext()).inflate(R.layout.item_schedule_edit, scheduleContainer, false);
            EditText etDate = row.findViewById(R.id.editDate);
            EditText etStartTime = row.findViewById(R.id.editStartTime);
            EditText etEndTime = row.findViewById(R.id.editEndTime);
            Button btnDelete = row.findViewById(R.id.btnDeleteSchedule);

            setTimeRangePickers(etDate, etStartTime, etEndTime);

            dateFields.add(etDate);
            startFields.add(etStartTime);
            endFields.add(etEndTime);

            btnDelete.setOnClickListener(v2 -> {
                int index = scheduleContainer.indexOfChild(row);
                if (index >= 0) {
                    scheduleContainer.removeViewAt(index);
                    dateFields.remove(index);
                    startFields.remove(index);
                    endFields.remove(index);
                }
            });

            scheduleContainer.addView(row);
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Gig")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("gigName", etName.getText().toString().trim());
                    updates.put("location", etLocation.getText().toString().trim());
                    updates.put("details", etDetails.getText().toString().trim());
                    updates.put("workers", etWorkers.getText().toString().trim());
                    updates.put("salary", etSalary.getText().toString().trim());

                    Map<String, String> updatedSchedule = new HashMap<>();
                    for (int i = 0; i < dateFields.size(); i++) {
                        String date = dateFields.get(i).getText().toString().trim();
                        String start = startFields.get(i).getText().toString().trim();
                        String end = endFields.get(i).getText().toString().trim();
                        if (!date.isEmpty() && !start.isEmpty() && !end.isEmpty()) {
                            updatedSchedule.put(date, start + " - " + end);
                        }
                    }
                    updates.put("schedule", updatedSchedule);

                    gigsRef.child(gig.getId()).updateChildren(updates)
                            .addOnSuccessListener(aVoid -> fetchMyGigs())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setTimeRangePickers(EditText etDate, EditText etStartTime, EditText etEndTime) {
        etDate.setFocusable(false);
        etDate.setClickable(true);
        etStartTime.setFocusable(false);
        etStartTime.setClickable(true);
        etEndTime.setFocusable(false);
        etEndTime.setClickable(true);

        etDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view1, year, month, day) -> {
                String formattedDate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
                etDate.setText(formattedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        View.OnClickListener timeClickListener = view -> {
            EditText timeField = (EditText) view;
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(getContext(), (timePicker, hour, minute) -> {
                String formattedTime = String.format("%02d:%02d", hour, minute);
                timeField.setText(formattedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        };

        etStartTime.setOnClickListener(timeClickListener);
        etEndTime.setOnClickListener(timeClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
        }
    }

    @Override
    public void onDelete(Gig gig) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Gig")
                .setMessage("Are you sure you want to delete this gig?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    gigsRef.child(gig.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> fetchMyGigs())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onLocationClick(String location) {
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getContext(), "Google Maps app is not installed", Toast.LENGTH_SHORT).show();
        }
    }
}
