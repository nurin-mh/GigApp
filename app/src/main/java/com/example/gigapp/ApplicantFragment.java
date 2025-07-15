package com.example.gigapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ApplicantFragment extends Fragment {

    public ApplicantFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applicant, container, false);

        // Card 1 View button
        Button viewBtn1 = view.findViewById(R.id.viewButton1);
        viewBtn1.setOnClickListener(v -> showApplicantDialog(
                getContext(),
                R.drawable.aina,
                "Nurul Aina Firzanah Binti Rosnan",
                "2023388953",
                "Project Manager"
        ));

        // Card 2 View button
        Button viewBtn2 = view.findViewById(R.id.viewButton2);
        viewBtn2.setOnClickListener(v -> showApplicantDialog(
                getContext(),
                R.drawable.nurin,
                "Nurin Mardiana Humaira Binti Ruslan",
                "2023189725",
                "Tester"
        ));

        // Card 3 View button
        Button viewBtn3 = view.findViewById(R.id.viewButton3);
        viewBtn3.setOnClickListener(v -> showApplicantDialog(
                getContext(),
                R.drawable.musya,
                "Muhammad Musyahafizi Bin Mustapa",
                "2024903077",
                "Developer"
        ));

        // Card 4 View button
        Button viewBtn4 = view.findViewById(R.id.viewButton4);
        viewBtn4.setOnClickListener(v -> showApplicantDialog(
                getContext(),
                R.drawable.nad,
                "Nurul Nadzifah Binti Zamzuri",
                "2023125495",
                "Designer"
        ));

        return view;
    }

    private void showApplicantDialog(Context context, int imageResId, String name, String studentId, String role) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_applicant_info, null);

        ImageView imageProfile = dialogView.findViewById(R.id.imageProfile);
        TextView textName = dialogView.findViewById(R.id.textName);
        TextView textStudentId = dialogView.findViewById(R.id.textStudentId);
        TextView textRole = dialogView.findViewById(R.id.textRole);

        imageProfile.setImageResource(imageResId);
        textName.setText(name);
        textStudentId.setText("Student ID: " + studentId);
        textRole.setText("Role: " + role);

        builder.setView(dialogView);
        builder.setPositiveButton("Close", null);
        builder.show();
    }
}
