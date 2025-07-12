package com.example.gigapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GigAdapter extends RecyclerView.Adapter<GigAdapter.GigViewHolder> {

    public interface OnLocationClickListener {
        void onLocationClick(String location);
    }

    private final ArrayList<Gig> gigs;
    private final OnLocationClickListener listener;

    public GigAdapter(ArrayList<Gig> gigs, OnLocationClickListener listener) {
        this.gigs = gigs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gig, parent, false);
        return new GigViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GigViewHolder holder, int position) {
        Gig gig = gigs.get(position);
        holder.tvGigName.setText(gig.getGigName());
        holder.tvGigDetails.setText(gig.getDetails());

        // Set location text clickable
        holder.tvGigLocation.setText(gig.getLocation());
        holder.tvGigLocation.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLocationClick(gig.getLocation());
            }
        });

        // Load image with Glide or placeholder
        if (gig.getPosterUrl() != null && !gig.getPosterUrl().isEmpty()) {
            Glide.with(holder.imgGigPoster.getContext())
                    .load(gig.getPosterUrl())
                    .placeholder(R.drawable.ic_default_poster) // Add default poster drawable in your resources
                    .into(holder.imgGigPoster);
        } else {
            holder.imgGigPoster.setImageResource(R.drawable.ic_default_poster);
        }
    }

    @Override
    public int getItemCount() {
        return gigs.size();
    }

    static class GigViewHolder extends RecyclerView.ViewHolder {
        TextView tvGigName, tvGigDetails, tvGigLocation;
        ImageView imgGigPoster;

        public GigViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGigName = itemView.findViewById(R.id.tvGigName);
            tvGigDetails = itemView.findViewById(R.id.tvGigDetails);
            tvGigLocation = itemView.findViewById(R.id.tvGigLocation);
            imgGigPoster = itemView.findViewById(R.id.imgGigPoster);
        }
    }
}
