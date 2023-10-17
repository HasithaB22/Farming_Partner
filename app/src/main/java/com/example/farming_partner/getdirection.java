package com.example.farming_partner;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class getdirection extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private String postId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getdirection);

        // Retrieve the postId extra from the Intent
        postId = getIntent().getStringExtra("postId");

        // Get the map fragment and register for the map callback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // Initialize the fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Fetch the latitude and longitude from Firebase based on the postId
        fetchLocationFromFirebase(postId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set up map options
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void fetchLocationFromFirebase(String postId) {
        DatabaseReference postsDbRef = FirebaseDatabase.getInstance().getReference("posts");
        postsDbRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the latitude and longitude for the post
                    double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                    // Create a LatLng object for the post
                    LatLng postLatLng = new LatLng(latitude, longitude);

                    // Update the map marker for the post
                    updateMapMarker(postLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void updateMapMarker(LatLng latLng) {
        if (mMap != null) {
            // Remove existing marker
            if (marker != null) {
                marker.remove();
            }

            // Add new marker
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            marker = mMap.addMarker(markerOptions);

            // Move camera to the selected location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        } else {
            Toast.makeText(this, "Map not ready.", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle location permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location
                onMapReady(mMap);
            } else {
                // Permission denied, handle it accordingly (e.g., show a message)
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
