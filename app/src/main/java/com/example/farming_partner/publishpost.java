package com.example.farming_partner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class publishpost extends AppCompatActivity {

    private EditText editTextCrop;
    private EditText editTextCapacity;
    private CheckBox checkBoxNegotiable;
    private EditText editTextFarmerName;
    private EditText editTextContactNumber;
    private EditText editTextAddress;
    private EditText editTextSellingLocation;
    private EditText editTextLatitude; // Add this
    private EditText editTextLongitude; // Add this
    private Button btnPublish;
    private Button btnSelectLocation; // Add this
    private ProgressBar progressBar; // Add this

    private DatabaseReference postsDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publishpost);

        editTextCrop = findViewById(R.id.editTextcrop);
        editTextCapacity = findViewById(R.id.editTextcapacity);
        checkBoxNegotiable = findViewById(R.id.checkBoxNegotiable);
        editTextFarmerName = findViewById(R.id.editTextFarmerName);
        editTextContactNumber = findViewById(R.id.editTextContactNumber);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextSellingLocation = findViewById(R.id.editTextSellingLocation);
        editTextLatitude = findViewById(R.id.editTextLatitude); // Initialize latitude EditText
        editTextLongitude = findViewById(R.id.editTextLongitude); // Initialize longitude EditText
        btnPublish = findViewById(R.id.btnPublish);
        btnSelectLocation = findViewById(R.id.btnSelectLocation); // Initialize Select Location button
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar

        mAuth = FirebaseAuth.getInstance();
        postsDbRef = FirebaseDatabase.getInstance().getReference("posts");

        // Retrieve latitude and longitude from Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            double latitude = intent.getDoubleExtra("latitude", 0.0); // 0.0 is a default value if not found
            double longitude = intent.getDoubleExtra("longitude", 0.0); // 0.0 is a default value if not found

            // Set latitude and longitude in the EditText fields
            editTextLatitude.setText(String.valueOf(latitude));
            editTextLongitude.setText(String.valueOf(longitude));
        }

        // Check if data exists in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("tempData", MODE_PRIVATE);
        if (sharedPreferences.contains("crop")) {
            // Restore the data in EditText fields
            editTextCrop.setText(sharedPreferences.getString("crop", ""));
            editTextCapacity.setText(sharedPreferences.getString("capacity", ""));
            editTextFarmerName.setText(sharedPreferences.getString("farmerName", ""));
            editTextContactNumber.setText(sharedPreferences.getString("contactNumber", ""));
            editTextAddress.setText(sharedPreferences.getString("address", ""));
            editTextSellingLocation.setText(sharedPreferences.getString("sellingLocation", ""));
        }

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishPost();
            }
        });

        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the entered data in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("crop", editTextCrop.getText().toString());
                editor.putString("capacity", editTextCapacity.getText().toString());
                editor.putString("farmerName", editTextFarmerName.getText().toString());
                editor.putString("contactNumber", editTextContactNumber.getText().toString());
                editor.putString("address", editTextAddress.getText().toString());
                editor.putString("sellingLocation", editTextSellingLocation.getText().toString());
                editor.apply();

                // Start the MainActivity to select a location
                Intent intent = new Intent(publishpost.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void publishPost() {
        // Retrieve all the input fields
        String crop = editTextCrop.getText().toString().trim();
        String quantity = editTextCapacity.getText().toString().trim();
        boolean negotiable = checkBoxNegotiable.isChecked();
        String farmerName = editTextFarmerName.getText().toString().trim();
        String contactNumber = editTextContactNumber.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String sellingLocation = editTextSellingLocation.getText().toString().trim();
        double latitude = Double.parseDouble(editTextLatitude.getText().toString().trim()); // Get latitude
        double longitude = Double.parseDouble(editTextLongitude.getText().toString().trim()); // Get longitude

        if (crop.isEmpty() || quantity.isEmpty() || farmerName.isEmpty() || contactNumber.isEmpty() || address.isEmpty() || sellingLocation.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar to indicate processing
        progressBar.setVisibility(View.VISIBLE);

        // Generate a unique key for the post
        String postId = postsDbRef.push().getKey();
        String userId = mAuth.getCurrentUser().getUid();

        // Create a new Post object
        Post post = new Post(postId, userId, crop, quantity, negotiable, farmerName, contactNumber, address, sellingLocation, latitude, longitude);

        // Save the post to Firebase Realtime Database
        postsDbRef.child(postId).setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                        Toast.makeText(publishpost.this, "Post published successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(publishpost.this, farmer_home.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                        Toast.makeText(publishpost.this, "Error publishing the post. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
