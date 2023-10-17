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

public class merchant_registration extends AppCompatActivity {

    private EditText merchantuserName;
    private EditText merchantName;
    private EditText merchantuserNIC;
    private EditText merchantuserRegEmail;
    private EditText merchantuserCity;
    private EditText merchantuserNumber;
    private EditText merchantuserRegPassword;
    private EditText merchantuserCPass;
    private Button MbtnSignIn;
    private Button MbtnRegister;
    private ImageView  merchantProfileImage;
    private Button selectImageButton;
    private Bitmap selectedImageBitmap;

    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_registration);

        merchantName = findViewById(R.id.merchantname);
        merchantuserName = findViewById(R.id.merchantEmail);
        merchantuserNIC = findViewById(R.id.merchantNIC);
        merchantuserRegEmail = findViewById(R.id.merchantEmail);
        merchantuserCity = findViewById(R.id.merchantCity);
        merchantuserNumber = findViewById(R.id.merchantNumber);
        merchantuserRegPassword = findViewById(R.id.merchantPassword);
        merchantuserCPass = findViewById(R.id.merchantConfirmPassword);
        MbtnSignIn = findViewById(R.id.merchantsignInBtn);
        MbtnRegister = findViewById(R.id.merchantregisterButton);
        merchantProfileImage = findViewById(R.id.merchantprofileImage);
        selectImageButton = findViewById(R.id.selectImageButton);

        userDbRef = FirebaseDatabase.getInstance().getReference("merchants");
        mAuth = FirebaseAuth.getInstance();

        MbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        MbtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(merchant_registration.this, login.class));
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
                merchantProfileImage.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createUser() {
        String name = merchantName.getText().toString().trim();
        String username = merchantuserName.getText().toString().trim();
        String email = merchantuserRegEmail.getText().toString().trim();
        String password = merchantuserRegPassword.getText().toString().trim();
        String confirmPassword = merchantuserCPass.getText().toString().trim();
        String city = merchantuserCity.getText().toString().trim();
        String nic = merchantuserNIC.getText().toString().trim();
        String contact = merchantuserNumber.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            merchantName.setError("Name cannot be empty");
            merchantName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            merchantuserName.setError("Username cannot be empty");
            merchantuserName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            merchantuserRegEmail.setError("Email cannot be empty");
            merchantuserRegEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nic)) {
            merchantuserNIC.setError("NIC cannot be empty");
            merchantuserNIC.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(city)) {
            merchantuserCity.setError("City cannot be empty");
            merchantuserCity.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contact)) {
            merchantuserNumber.setError("Contact number cannot be empty");
            merchantuserNumber.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            merchantuserRegPassword.setError("Password cannot be empty");
            merchantuserRegPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            merchantuserCPass.setError("Confirm password cannot be empty");
            merchantuserCPass.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(merchant_registration.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            merchants user = new merchants(userId, name, username, nic, email, city, contact);

                            if (selectedImageBitmap != null) {
                                String imageBase64 = convertBitmapToBase64(selectedImageBitmap);
                                user.setProfileImageBase64(imageBase64);
                            }

                            userDbRef.child(userId).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(merchant_registration.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                                startActivity(new Intent(merchant_registration.this, login.class));
                                            } else {
                                                Toast.makeText(merchant_registration.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(merchant_registration.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
