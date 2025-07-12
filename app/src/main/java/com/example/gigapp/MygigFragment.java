package com.example.gigapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MygigFragment extends Fragment implements GigAdapter.OnLocationClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GigAdapter gigAdapter;
    private ArrayList<Gig> gigList;

    private FirebaseAuth mAuth;

    public MygigFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mygig, container, false);

        recyclerView = view.findViewById(R.id.recyclerMyGigs);
        progressBar = view.findViewById(R.id.progressMyGig);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gigList = new ArrayList<>();
        gigAdapter = new GigAdapter(gigList, this);
        recyclerView.setAdapter(gigAdapter);

        mAuth = FirebaseAuth.getInstance();

        fetchMyGigs();

        return view;
    }

    private void fetchMyGigs() {
        progressBar.setVisibility(View.VISIBLE);

        String currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Gigs");

        ref.orderByChild("ownerId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        gigList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Gig gig = ds.getValue(Gig.class);
                            if (gig != null) {
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
    public void onLocationClick(String location) {
        // Open Google Maps with the location string as query
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
