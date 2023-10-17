package com.example.farming_partner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.util.Base64;

public class updatemerchant extends AppCompatActivity {

    private EditText textViewmerchantname, textViewNIC, textViewEmail, textViewCity, textViewContact;
    private Button saveButton, selectImageButton;
    private ImageView profileImageView;

    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatemerchant);

        // Initialize views and Firebase

        textViewmerchantname = findViewById(R.id.merchantname);
        textViewNIC = findViewById(R.id.merchantNIC);
        textViewEmail = findViewById(R.id.merchantEmail);
        textViewCity = findViewById(R.id.merchantCity);
        textViewContact = findViewById(R.id.merchantNumber);
        saveButton = findViewById(R.id.buttonSave);
        selectImageButton = findViewById(R.id.selectImageButton);
        profileImageView = findViewById(R.id.profileImageView);

        userDbRef = FirebaseDatabase.getInstance().getReference("merchants");
        mAuth = FirebaseAuth.getInstance();

        // Set a click listener for the select image button
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        // Set a click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        // Retrieve the user's profile data and populate the EditText fields and profile image
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            userDbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve user data from the database

                        String currentname = dataSnapshot.child("name").getValue(String.class);
                        String currentNIC = dataSnapshot.child("nic").getValue(String.class);
                        String currentEmail = dataSnapshot.child("email").getValue(String.class);
                        String currentCity = dataSnapshot.child("city").getValue(String.class);
                        String currentContactNumber = dataSnapshot.child("contact").getValue(String.class);

                        // Populate EditText fields with the retrieved data

                        textViewmerchantname.setText(currentname);
                        textViewNIC.setText(currentNIC);
                        textViewEmail.setText(currentEmail);
                        textViewCity.setText(currentCity);
                        textViewContact.setText(currentContactNumber);

                        // Retrieve and set the profile image
                        String currentProfileImageBase64 = dataSnapshot.child("profileImageBase64").getValue(String.class);
                        if (currentProfileImageBase64 != null) {
                            byte[] decodedImage = android.util.Base64.decode(currentProfileImageBase64, android.util.Base64.DEFAULT);
                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                            profileImageView.setImageBitmap(decodedBitmap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle specific types of errors
                    if (databaseError.getCode() == DatabaseError.PERMISSION_DENIED) {
                        // Handle permission denied error
                        Toast.makeText(updatemerchant.this, "Permission Denied: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle other errors
                        Toast.makeText(updatemerchant.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void selectImage() {
        // You can use an image picker library or launch an Intent to open the gallery
        // For simplicity, I'm using an Intent to open the gallery here

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1); // 1 is a requestCode, you can choose any number
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                // Get the selected image as a Bitmap
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);

                // Set the selected image to your ImageView
                profileImageView.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Get values from EditText fields
            String newmerchantname = textViewmerchantname.getText().toString();
            String newNIC = textViewNIC.getText().toString();
            String newEmail = textViewEmail.getText().toString();
            String newCity = textViewCity.getText().toString();
            String newContact = textViewContact.getText().toString();

            // Create an AlertDialog for password input
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Current Password");

            // Inflate the dialog_password.xml layout
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_password, null);
            final EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
            builder.setView(dialogView);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String currentPassword = passwordEditText.getText().toString();

                    // Verify the current password
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                    user.reauthenticate(credential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Password verification successful, proceed with the update
                                    // Convert the selected image (Bitmap) to a base64 string
                                    String imageBase64 = "";
                                    if (selectedImageBitmap != null) {
                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
                                        imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                    }else {
                                        // If no new image is selected, use the current image if available
                                        Bitmap currentImageBitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
                                        if (currentImageBitmap != null) {
                                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                            currentImageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                                            byte[] imageBytes = byteArrayOutputStream.toByteArray();
                                            imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                        }
                                    }

                                    // Update the user's profile data in the database
                                    userDbRef.child(userId).child("name").setValue(newmerchantname);
                                    userDbRef.child(userId).child("nic").setValue(newNIC);
                                    userDbRef.child(userId).child("email").setValue(newEmail);
                                    userDbRef.child(userId).child("city").setValue(newCity);
                                    userDbRef.child(userId).child("contact").setValue(newContact);
                                    userDbRef.child(userId).child("profileImageBase64").setValue(imageBase64);
                                    userDbRef.child(userId).child("timestamp").setValue(ServerValue.TIMESTAMP);

                                    // Optionally, you can show a success message to the user
                                    Toast.makeText(updatemerchant.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                                    // Redirect to the profile page after updating
                                    startActivity(new Intent(updatemerchant.this, merchant_profile.class));
                                    finish(); // Close this activity to prevent going back to the update page
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Password verification failed, show an error message
                                    Toast.makeText(updatemerchant.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }
}
