package com.example.farming_partner;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private TextView currentLocationTextView;
    private Button nextButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the map fragment and register for the map callback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // Initialize the fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize TextViews
        currentLocationTextView = findViewById(R.id.currentLocationTextView);

        // Initialize Next button
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if a location is selected
                if (marker != null) {
                    // Get the selected location's latitude and longitude
                    LatLng selectedLatLng = marker.getPosition();
                    double latitude = selectedLatLng.latitude;
                    double longitude = selectedLatLng.longitude;

                    // Start publishpost activity and pass latitude and longitude as extras
                    Intent intent = new Intent(MainActivity.this, publishpost.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please select a location on the map.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set up map options
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Set a click listener on the map to allow users to manually select a location
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Update the marker on the map
                updateMapMarker(latLng);

                // Display the latitude and longitude in TextView
                setCurrentLocationTextView(latLng.latitude, latLng.longitude);
            }
        });

        // Get the user's last known location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Update the marker on the map
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            updateMapMarker(latLng);

                            // Display the current location in TextView
                            setCurrentLocationTextView(location.getLatitude(), location.getLongitude());
                        }
                    })
                    .addOnFailureListener(this, e ->
                            Toast.makeText(MainActivity.this, "Failed to get location.", Toast.LENGTH_SHORT).show());
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }

    private void updateMapMarker(LatLng latLng) {
        // Remove existing marker
        if (marker != null) {
            marker.remove();
        }

        // Add new marker
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        marker = mMap.addMarker(markerOptions);

        // Move camera to the selected location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
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

    private void setCurrentLocationTextView(double latitude, double longitude) {
        // Display the latitude and longitude in TextView
        String locationText = "Latitude: " + latitude + "\nLongitude: " + longitude;
        currentLocationTextView.setText(locationText);
    }
}
