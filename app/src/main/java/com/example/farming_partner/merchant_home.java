package com.example.farming_partner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.farming_partner.Contacts.FarmerListActivity;
import com.example.farming_partner.Market_Price.farmerside.PricePostListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

public class merchant_home extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageButton btnViewPost;

    private ImageButton btnViewPrice;
    private ImageButton btnFarmer;
    private TextView musernameTextView;
    private ImageView merchantimageview;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_home);

        mAuth = FirebaseAuth.getInstance();

        btnViewPost = findViewById(R.id.btnViewPost);
        btnViewPrice = findViewById(R.id.btnViewPrice);
        btnFarmer = findViewById(R.id.btnFarmer);
        musernameTextView = findViewById(R.id.mviewUserName);
        merchantimageview = findViewById(R.id.merchantProfilePicture);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_home) {

                    return true;
                } else if (item.getItemId() == R.id.action_fine_history) {
                    // Handle the Fine History action
                    startActivity(new Intent(merchant_home.this, PricePrediction.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.action_profile) {
                    // Handle the Profile action
                    startActivity(new Intent(merchant_home.this, farmer_profile.class));
                    finish();
                    return true;
                }
                return false;
         }
});




        btnViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(merchant_home.this, PostListActivity.class));
            }
        });

        ImageButton profileButton = findViewById(R.id.merchantProfilePicture);
        profileButton.setOnClickListener(view -> {
            startActivity(new Intent(merchant_home.this, merchant_profile.class));
        });


        btnViewPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(merchant_home.this, PricePostListActivity.class));
            }
        });


        btnFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(merchant_home.this, FarmerListActivity.class));
            }
        });



        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Retrieve the username from the database
            DatabaseReference userDbRef = FirebaseDatabase.getInstance().getReference("merchants").child(userId);
            userDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        merchants userData = dataSnapshot.getValue(merchants.class);
                        String name = userData.getName();
                        // Set the username to the TextView
                        musernameTextView.setText("Welcome, " + name + "!");




                        // Load and set the profile image from Base64 string
                        String profileImageBase64 = userData.getprofileImageBase64();
                        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                            byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                            merchantimageview.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(merchant_home.this, "Failed to fetch username: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }






}