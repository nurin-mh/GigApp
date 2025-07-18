package com.example.gigapp;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.events.Event;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Data;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔹 MODIFIED: load eventMap fragment instead of general map
        SupportMapFragment eventMapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.eventMap, eventMapFragment)
                .commit();

        eventMapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        enableUserLocation();

        //Call new method to load only upcoming event
        loadUpcomingEvent();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 14f));
                } else {
                    // 🌍 fallback if GPS fails
                    LatLng fallback = new LatLng(3.0738, 101.5183); // Shah Alam area
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallback, 12f));
                }
            });
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation(); // Retry location enabling if granted
        } else {
            Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    //Load only the upcoming event within 3 days
    private void loadUpcomingEvent() {
        DatabaseReference gigsRef = FirebaseDatabase.getInstance().getReference("Gigs");
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        gigsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DAY_OF_YEAR, 3); // +3 days
                Date threeDaysLater = cal.getTime();

                boolean foundUpcoming = false;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String posterId = snap.child("posterId").getValue(String.class);

                    if (posterId != null && posterId.equals(currentUserId)) {
                        String gigName = snap.child("gigName").getValue(String.class);
                        String location = snap.child("location").getValue(String.class);
                        String datetimeStr = snap.child("datetime").getValue(String.class);

                        try {
                            Date eventDate = sdf.parse(datetimeStr);
                            if (eventDate != null && eventDate.after(now) && eventDate.before(threeDaysLater)) {

                                //Schedule notification 1 day before event
                                Gig gig = new Gig(gigName, location, datetimeStr);
                                scheduleNotificationForEvent(gig, 1);

                                // (Optional) This part is UI related — it’s safe to leave it unchanged
                                foundUpcoming = true;
                                updateEventCardUI(gigName, datetimeStr, location);

                                List<Address> addresses = geocoder.getFromLocationName(location, 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address addr = addresses.get(0);
                                    LatLng latLng = new LatLng(addr.getLatitude(), addr.getLongitude());

                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(gigName)
                                            .snippet(location));

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                                }

                                break; // Stop after scheduling 1st upcoming event
                            }
                        } catch (Exception e) {
                            Log.e("DATE_PARSE", "Error parsing date: " + e.getMessage());
                        }
                    }
                }

                if (!foundUpcoming) {
                    updateEventCardUI("none", "", "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Updates for upcoming or fallback event
    private void updateEventCardUI(String title, String datetime, String location) {
        View view = getView();
        if (view != null) {
            TextView tvTitle = view.findViewById(R.id.tvEventTitle);
            TextView tvDatetime = view.findViewById(R.id.tvEventDateTime);

            if (title.equals("none")) {
                // Show fallback message
                tvTitle.setText("📅 No Upcoming Event");
                tvDatetime.setText("You don't have any event in the next few days.");
            } else {
                tvTitle.setText("📅 Upcoming Event: " + title);
                tvDatetime.setText("🕕 " + datetime + "\n📍 " + location);
            }
        }
    }

    private void scheduleNotificationForEvent(Gig gig, long daysBefore) {
        long delayMillis = (gig.getDateTimeMillis() - System.currentTimeMillis()) - (daysBefore * 86400000);

        if (delayMillis > 0) {
            Data data = new Data.Builder()
                    .putString("eventTitle", gig.getTitle())
                    .putString("eventDateTime", gig.getFormattedDateTime())
                    .build();

            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(Notification.class)
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build();

            WorkManager.getInstance(requireContext()).enqueue(request);
        }
    }
}
