package com.example.farming_partner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class merchant_profile extends AppCompatActivity {

    private TextView textViewmerchantname;
    private Button btnEditProfile;
    private ImageButton btnLogout;
    private TextView textViewmerchantNIC;
    private TextView textViewmerchantEmail;
    private TextView textViewmerchantCity;
    private TextView textViewmerchantContact;
    private ImageView merchantimageview; // Add ImageView for profile picture

    private BottomNavigationView bottomNavigationView;
    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_profile);

        // Initialize views
        textViewmerchantname = findViewById(R.id.textViewmerchantname);
        btnLogout = findViewById(R.id.btnLogout);
        textViewmerchantNIC = findViewById(R.id.textViewmerchantNIC);
        textViewmerchantEmail = findViewById(R.id.textViewmerchantEmail);
        textViewmerchantCity = findViewById(R.id.textViewmerchantCity);
        textViewmerchantContact = findViewById(R.id.textViewmerchantContact);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        merchantimageview = findViewById(R.id.merchantProfilePicture); // Initialize ImageView


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@org.checkerframework.checker.nullness.qual.NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_home) {
                    startActivity(new Intent(merchant_profile.this, merchant_home.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.action_fine_history) {
                    // Handle the Fine History action
                    startActivity(new Intent(merchant_profile.this, merchant_profile.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.action_profile) {
                    // Handle the Profile action

                    return true;
                }
                return false;
            }
        });

        // Set the selected item to be "Profile"
        bottomNavigationView.setSelectedItemId(R.id.action_profile);


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(merchant_profile.this, updatemerchant.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });




        // Initialize Firebase
        userDbRef = FirebaseDatabase.getInstance().getReference("merchants");
        mAuth = FirebaseAuth.getInstance();

        // Fetch and display user profile data
        displayUserProfile();
    }

    private void displayUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            userDbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        merchants userProfile = dataSnapshot.getValue(merchants.class);
                        if (userProfile != null) {
                            textViewmerchantname.setText(userProfile.getName());
                            textViewmerchantEmail.setText(userProfile.getEmail());
                            textViewmerchantNIC.setText(userProfile.getNic());
                            textViewmerchantCity.setText(userProfile.getCity());
                            textViewmerchantContact.setText(userProfile.getContact());

                            // Load and set the profile image from Base64 string
                            String profileImageBase64 = userProfile.getprofileImageBase64();
                            if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                                byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                                merchantimageview.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    if (databaseError != null) {
                        // Log the error message
                        Log.e("DatabaseError", "Error: " + databaseError.getMessage());

                        // Show a Toast message to the user
                        Toast.makeText(merchant_profile.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                performLogout();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void performLogout() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging out...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.signOut();
        progressDialog.dismiss();

        Toast.makeText(this, "Logout Successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(merchant_profile.this, login.class));
        finish();
    }
}
