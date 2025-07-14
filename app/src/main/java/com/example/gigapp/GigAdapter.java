package com.example.gigapp;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class GigAdapter extends RecyclerView.Adapter<GigAdapter.GigViewHolder> {

    public interface OnGigActionListener {
        void onView(Gig gig);
        void onEdit(Gig gig);
        void onDelete(Gig gig);
        void onLocationClick(String location);
    }

    private final ArrayList<Gig> gigs;
    private final OnGigActionListener listener;

    public GigAdapter(ArrayList<Gig> gigs, OnGigActionListener listener) {
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
        holder.tvGigLocation.setText(gig.getLocation());

        // Set nearest upcoming date
        Map<String, String> schedule = gig.getSchedule();
        if (schedule != null && !schedule.isEmpty()) {
            String nearestDate = null;
            long minDiff = Long.MAX_VALUE;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            // Normalize today's date (00:00:00)
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date today = calendar.getTime();

            for (String dateStr : schedule.keySet()) {
                try {
                    Date date = sdf.parse(dateStr);
                    if (date != null && !date.before(today)) {
                        long diff = date.getTime() - today.getTime();
                        if (diff < minDiff) {
                            minDiff = diff;
                            nearestDate = dateStr;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.tvGigDate.setText(nearestDate != null ? nearestDate : "No upcoming date");
        } else {
            holder.tvGigDate.setText("N/A");
        }

        // Load poster from internal storage
        if (gig.getPosterUrl() != null && !gig.getPosterUrl().isEmpty()) {
            File imgFile = new File(holder.itemView.getContext().getFilesDir(), gig.getPosterUrl());
            if (imgFile.exists()) {
                holder.imgGigPoster.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            } else {
                holder.imgGigPoster.setImageResource(R.drawable.ic_default_poster);
            }
        } else {
            holder.imgGigPoster.setImageResource(R.drawable.ic_default_poster);
        }

        // Action listeners
        holder.itemView.setOnClickListener(v -> listener.onView(gig));
        holder.tvGigLocation.setOnClickListener(v -> listener.onLocationClick(gig.getLocation()));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(gig));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(gig));
    }

    @Override
    public int getItemCount() {
        return gigs.size();
    }

    static class GigViewHolder extends RecyclerView.ViewHolder {
        TextView tvGigName, tvGigLocation, tvGigDate;
        ImageView imgGigPoster;
        Button btnEdit, btnDelete;

        public GigViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGigName = itemView.findViewById(R.id.tvGigName);
            tvGigLocation = itemView.findViewById(R.id.tvGigLocation);
            tvGigDate = itemView.findViewById(R.id.tvGigDate);
            imgGigPoster = itemView.findViewById(R.id.imgGigPoster);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
