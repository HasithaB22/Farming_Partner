package com.example.farming_partner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class farmer_registration extends AppCompatActivity {

    private EditText farmeruserName;
    private EditText farmerName;
    private EditText farmeruserNIC;
    private EditText farmeruserRegEmail;
    private EditText farmeruserCity;
    private EditText farmeruserNumber;
    private EditText farmeruserRegPassword;
    private EditText farmeruserCPass;
    private Button btnSignIn;
    private Button btnRegister;
    private ImageView  farmerProfileImage;
    private Button selectImageButton;
    private Bitmap selectedImageBitmap;

    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);

        farmerName = findViewById(R.id.farmername);
        farmeruserName = findViewById(R.id.farmerEmail);
        farmeruserNIC = findViewById(R.id.farmerNIC);
        farmeruserRegEmail = findViewById(R.id.farmerEmail);
        farmeruserCity = findViewById(R.id.farmerCity);
        farmeruserNumber = findViewById(R.id.farmerNumber);
        farmeruserRegPassword = findViewById(R.id.farmerPassword);
        farmeruserCPass = findViewById(R.id.farmerConfirmPassword);
        btnSignIn = findViewById(R.id.signInBtn);
        btnRegister = findViewById(R.id.farmerregisterButton);
        farmerProfileImage = findViewById(R.id.farmerprofileImage);
        selectImageButton = findViewById(R.id.selectImageButton);

        userDbRef = FirebaseDatabase.getInstance().getReference("farmers");
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(farmer_registration.this, login.class));
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open image selection intent
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // Load the selected image into a Bitmap
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // Set the ImageView to display the selected image
                farmerProfileImage.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createUser() {
        String name = farmerName.getText().toString().trim();
        String username = farmeruserName.getText().toString().trim();
        String email = farmeruserRegEmail.getText().toString().trim();
        String password = farmeruserRegPassword.getText().toString().trim();
        String confirmPassword = farmeruserCPass.getText().toString().trim();
        String city = farmeruserCity.getText().toString().trim();
        String nic = farmeruserNIC.getText().toString().trim();
        String contact = farmeruserNumber.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            farmerName.setError("Name cannot be empty");
            farmerName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            farmeruserName.setError("Username cannot be empty");
            farmeruserName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            farmeruserRegEmail.setError("Email cannot be empty");
            farmeruserRegEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nic)) {
            farmeruserNIC.setError("NIC cannot be empty");
            farmeruserNIC.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(city)) {
            farmeruserCity.setError("City cannot be empty");
            farmeruserCity.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contact)) {
            farmeruserNumber.setError("Contact number cannot be empty");
            farmeruserNumber.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            farmeruserRegPassword.setError("Password cannot be empty");
            farmeruserRegPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            farmeruserCPass.setError("Confirm password cannot be empty");
            farmeruserCPass.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(farmer_registration.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            farmers user = new farmers(userId, name, username, nic, email, city, contact);

                            if (selectedImageBitmap != null) {
                                String imageBase64 = convertBitmapToBase64(selectedImageBitmap);
                                user.setProfileImageBase64(imageBase64);
                            }

                            userDbRef.child(userId).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(farmer_registration.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                                startActivity(new Intent(farmer_registration.this, login.class));
                                            } else {
                                                Toast.makeText(farmer_registration.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(farmer_registration.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
