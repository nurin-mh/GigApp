package com.example.gigapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    TextView textViewFullName, textViewEmail, textViewPhone, textViewGender, textViewAddress;
    Button btnLogout, btnEditProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Bind Views
        textViewFullName = view.findViewById(R.id.textView2);
        textViewEmail = view.findViewById(R.id.textEmail);
        textViewPhone = view.findViewById(R.id.textPhone);
        textViewGender = view.findViewById(R.id.textGender);
        textViewAddress = view.findViewById(R.id.textAddress);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Logout functionality
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        // Edit profile
        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Edit profile feature coming soon!", Toast.LENGTH_SHORT).show()
        );

        // Load user data from Firestore
        if (user != null) {
            String uid = user.getUid();
            DocumentReference docRef = db.collection("gig_posters").document(uid);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    textViewFullName.setText(documentSnapshot.getString("fullName"));
                    textViewEmail.setText("Email: " + documentSnapshot.getString("email"));
                    textViewPhone.setText("Phone: " + documentSnapshot.getString("phone"));
                    textViewGender.setText("Gender: " + documentSnapshot.getString("gender"));
                    textViewAddress.setText("Address: " + documentSnapshot.getString("address"));
                } else {
                    Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}
