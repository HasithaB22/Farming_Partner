package com.example.farming_partner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class login extends AppCompatActivity {

    EditText userEmail;
    EditText userPassword;
    Button btnSignUp;
    Button btnLogin;
    Button btnForgotPassword; // Add the Forgot Password button

    FirebaseAuth mAuth;
    DatabaseReference farmersDbRef;
    DatabaseReference merchantsDbRef;

    SharedPreferences sharedPreferences;
    boolean firstLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.editTextEmail1);
        userPassword = findViewById(R.id.editTextPassword1);
        btnSignUp = findViewById(R.id.signUpBtn);
        btnLogin = findViewById(R.id.loginButton);
        btnForgotPassword = findViewById(R.id.btnForgotPassword); // Initialize the Forgot Password button

        mAuth = FirebaseAuth.getInstance();
        farmersDbRef = FirebaseDatabase.getInstance().getReference("farmers");
        merchantsDbRef = FirebaseDatabase.getInstance().getReference("merchants");

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        firstLaunch = sharedPreferences.getBoolean("firstLaunch", true);

        // Automatically sign out the user when the app is first launched after installation
        if (firstLaunch) {
            mAuth.signOut();
            Toast.makeText(this, "You have been signed out.", Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().putBoolean("firstLaunch", false).apply();
        }

        // Checking if the user is already logged in and automatically log in
        if (mAuth.getCurrentUser() != null && !firstLaunch) {
            checkUserTypeAndRedirect(mAuth.getCurrentUser().getUid());
        }

        btnLogin.setOnClickListener(view -> {
            loginUser();
        });

        // Clicking SignUp button will go to the Registration Form
        btnSignUp.setOnClickListener(view -> {
            startActivity(new Intent(login.this, home.class));
        });

        // Set OnClickListener for the Forgot Password button
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
    }

    private void loginUser() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Email cannot be empty");
            userEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            userPassword.setError("Password cannot be empty");
            userPassword.requestFocus();
        } else {
            // Check if the entered credentials are for an admin
            if (isAdminCredentials(email, password)) {
                // Redirect to the admin home
                redirectToAdminActivity();
            } else {
                // Attempt to log in as a regular user
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            checkUserTypeAndRedirect(user.getUid());
                        } else {
                            Toast.makeText(login.this, "Log in Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private boolean isAdminCredentials(String email, String password) {
        // Define the admin credentials (replace with your actual admin username and password)
        String adminEmail = "admin@gmail.com";
        String adminPassword = "admin12345";

        // Check if the entered email and password match the admin credentials
        return email.equals(adminEmail) && password.equals(adminPassword);
    }



    private void checkUserTypeAndRedirect(String userId) {
        farmersDbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists in "farmers" table
                    farmers userData = dataSnapshot.getValue(farmers.class);
                    // Redirect to appropriate activity for farmers
                    redirectToUserActivity(userData);
                } else {
                    // User does not exist in "farmers" table, check "merchants" table
                    merchantsDbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User exists in "merchants" table
                                merchants merchantData = snapshot.getValue(merchants.class);
                                // Redirect to appropriate activity for merchants
                                redirectToMerchantActivity(merchantData);
                            } else {
                                // Neither in "farmers" nor in "merchants" table
                                Toast.makeText(login.this, "User data not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(login.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(login.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToUserActivity(farmers userData) {
        Intent profileIntent = new Intent(getApplicationContext(), farmer_home.class);
        profileIntent.putExtra("username", userData.getUsername());
        // Add other relevant user data to the intent if needed
        startActivity(profileIntent);
    }

    private void redirectToMerchantActivity(merchants merchantData) {
        Intent profileIntent = new Intent(getApplicationContext(), merchant_home.class);
        profileIntent.putExtra("merchantName", merchantData.getUsername());
        // Add other relevant merchant data to the intent if needed
        startActivity(profileIntent);
    }

    // Method to handle the "Forgot Password" feature
    private void forgotPassword() {
        String email = userEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Please enter your registered email address.");
            userEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(login.this, "Failed to send password reset email. Please check your email address.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void redirectToAdminActivity() {
        // Add code to redirect to the admin activity
        // Example:
        Intent adminIntent = new Intent(getApplicationContext(), AdminHomeActivity.class);
        startActivity(adminIntent);
    }

}
