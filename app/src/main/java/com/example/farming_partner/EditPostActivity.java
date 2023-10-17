package com.example.farming_partner;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditPostActivity extends AppCompatActivity {

    private EditText editTextCrop;
    private EditText editTextCapacity;
    private CheckBox checkBoxNegotiable;
    private EditText editTextFarmerName;
    private EditText editTextContactNumber;
    private EditText editTextAddress;
    private EditText editTextSellingLocation;
    private EditText editTextLatitude;
    private EditText editTextLongitude;
    private Button btnUpdate;
    private ProgressBar progressBar;

    private DatabaseReference postsDbRef;
    private FirebaseAuth mAuth;

    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        editTextCrop = findViewById(R.id.editTextCrop);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        checkBoxNegotiable = findViewById(R.id.checkBoxNegotiable);
        editTextFarmerName = findViewById(R.id.editTextFarmerName);
        editTextContactNumber = findViewById(R.id.editTextContactNumber);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextSellingLocation = findViewById(R.id.editTextSellingLocation);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        btnUpdate = findViewById(R.id.btnUpdate);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        postsDbRef = FirebaseDatabase.getInstance().getReference("posts");

        postId = getIntent().getStringExtra("postId");

        // Fetch the Post object associated with postId from Firebase
        postsDbRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post != null) {
                        // Populate UI fields with retrieved data
                        editTextCrop.setText(post.getCrop());
                        editTextCapacity.setText(post.getquantity());
                        checkBoxNegotiable.setChecked(post.isNegotiable());
                        editTextFarmerName.setText(post.getFarmerName());
                        editTextContactNumber.setText(post.getContactNumber());
                        editTextAddress.setText(post.getAddress());
                        editTextSellingLocation.setText(post.getSellingLocation());
                        editTextLatitude.setText(String.valueOf(post.getLatitude()));
                        editTextLongitude.setText(String.valueOf(post.getLongitude()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database error here
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePost();
            }
        });
    }

    private void updatePost() {
        // Retrieve all the input fields
        String crop = editTextCrop.getText().toString().trim();
        String quantity = editTextCapacity.getText().toString().trim();
        boolean negotiable = checkBoxNegotiable.isChecked();
        String farmerName = editTextFarmerName.getText().toString().trim();
        String contactNumber = editTextContactNumber.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String sellingLocation = editTextSellingLocation.getText().toString().trim();

        if (crop.isEmpty() || quantity.isEmpty() || farmerName.isEmpty() || contactNumber.isEmpty() || address.isEmpty() || sellingLocation.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar to indicate processing
        progressBar.setVisibility(View.VISIBLE);

        // Create a new Post object with updated data
        double latitude = Double.parseDouble(editTextLatitude.getText().toString().trim());
        double longitude = Double.parseDouble(editTextLongitude.getText().toString().trim());
        Post updatedPost = new Post(postId, mAuth.getCurrentUser().getUid(), crop, quantity, negotiable, farmerName, contactNumber, address, sellingLocation, latitude, longitude);

        // Update the post in Firebase Realtime Database
        postsDbRef.child(postId).setValue(updatedPost)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                        Toast.makeText(EditPostActivity.this, "Post updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after update
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                        Toast.makeText(EditPostActivity.this, "Error updating the post. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
