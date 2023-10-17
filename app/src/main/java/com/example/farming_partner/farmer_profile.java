package com.example.farming_partner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class farmer_profile extends AppCompatActivity {

    private TextView textViewfarmername;

    private TextView textViewNIC;
    private TextView textViewEmail;
    private ImageButton btnLogout;

    private Button btnEditProfile;
    private TextView textViewCity;
    private TextView textViewContact;
    private ImageView farmerimageview; // Add ImageView for profile picture

    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_profile);

        // Initialize views
        textViewfarmername = findViewById(R.id.textViewfarmername);
        btnLogout = findViewById(R.id.btnLogout);
        textViewNIC = findViewById(R.id.textViewNIC);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewCity = findViewById(R.id.textViewCity);
        textViewContact = findViewById(R.id.textViewContact);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        farmerimageview = findViewById(R.id.farmerProfilePicture); // Initialize ImageView



        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(farmer_profile.this, updatefarmer.class));
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });



        // Initialize Firebase
        userDbRef = FirebaseDatabase.getInstance().getReference("farmers");
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
                        farmers userProfile = dataSnapshot.getValue(farmers.class);
                        if (userProfile != null) {
                            textViewfarmername.setText(userProfile.getName());
                            textViewEmail.setText(userProfile.getEmail());
                            textViewNIC.setText(userProfile.getNic());
                            textViewCity.setText(userProfile.getCity());
                            textViewContact.setText(userProfile.getContact());





                            // Load and set the profile image from Base64 string
                            String profileImageBase64 = userProfile.getprofileImageBase64();
                            if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                                byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                                farmerimageview.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
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
                        Toast.makeText(farmer_profile.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(farmer_profile.this, login.class));
        finish();
    }
}
