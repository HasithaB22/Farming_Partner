package com.example.farming_partner;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.farming_partner.Contacts.MerchantListActivity;
import com.example.farming_partner.Market_Price.farmerside.PricePostListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class farmer_home extends AppCompatActivity {

    private FirebaseAuth mAuth;


    private ImageButton btnmypost;
    private ImageButton btnWeather;
    private ImageButton btnFertilizershop;
    private ImageButton btnHelp;
    private ImageButton btnprices;
    private ImageButton btnmerchants;
    private TextView fusernameTextView;
    private ImageView farmerimageview;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmerhome);

        mAuth = FirebaseAuth.getInstance();

        btnmypost = findViewById(R.id.btnmypost);
        btnWeather = findViewById(R.id.btnWeather);
        btnFertilizershop = findViewById(R.id.btnFertilizershop);
        btnHelp = findViewById(R.id.btnHelp);
        btnprices = findViewById(R.id.btnprices);
        btnmerchants = findViewById(R.id.btnMerchants);
        fusernameTextView = findViewById(R.id.fviewUserName);
        farmerimageview = findViewById(R.id.farmerProfilePicture);



        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@org.checkerframework.checker.nullness.qual.NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_home) {

                    return true;
                } else if (item.getItemId() == R.id.action_fine_history) {
                    // Handle the Fine History action
                    startActivity(new Intent(farmer_home.this, PricePrediction.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.action_profile) {
                    // Handle the Profile action
                    startActivity(new Intent(farmer_home.this,farmer_profile.class));
                    finish();
                    return true;
                }
                return false;
            }
        });








        btnmypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(farmer_home.this, MypostActivity.class));
            }
        });

        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(farmer_home.this, WeatherActivity.class));
            }
        });

        btnFertilizershop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(farmer_home.this, FertilizerShop.class));
            }
        });

        ImageButton profileButton = findViewById(R.id.farmerProfilePicture);
        profileButton.setOnClickListener(view -> {
            startActivity(new Intent(farmer_home.this, farmer_profile.class));
        });





        btnprices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(farmer_home.this, PricePostListActivity.class));
            }
        });


        btnmerchants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(farmer_home.this, MerchantListActivity.class));
            }
        });






        // Set an OnClickListener for the Help button
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for permission to make a phone call
                if (ContextCompat.checkSelfPermission(farmer_home.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission if it's not granted
                    ActivityCompat.requestPermissions(farmer_home.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);
                } else {
                    // Permission is already granted, so make the phone call
                    makePhoneCall();
                }
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Retrieve the username from the database
            DatabaseReference userDbRef = FirebaseDatabase.getInstance().getReference("farmers").child(userId);
            userDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        farmers userData = dataSnapshot.getValue(farmers.class);
                        String name = userData.getName();
                        // Set the username to the TextView
                        fusernameTextView.setText("Welcome " + name + "");


                        // Load and set the profile image from Base64 string
                        String profileImageBase64 = userData.getprofileImageBase64();
                        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                            byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                            farmerimageview.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                        }



                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(farmer_home.this, "Failed to fetch username: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to make the phone call
    private void makePhoneCall() {
        // Define the phone number to call
        String phoneNumber = "0375610090";

        // Create an intent to dial the phone number
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        // Check if the device can handle the intent (dialer app is available)
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the phone call
            startActivity(intent);
        } else {
            // The device can't handle phone calls, show an error message
            Toast.makeText(this, "Phone call not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }



    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the phone call
                makePhoneCall();
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Phone call permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

