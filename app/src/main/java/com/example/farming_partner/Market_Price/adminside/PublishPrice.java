package com.example.farming_partner.Market_Price.adminside;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Toast;

import com.example.farming_partner.AdminHomeActivity;
import com.example.farming_partner.Market_Price.Pricepost;
import com.example.farming_partner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PublishPrice extends AppCompatActivity {

    private EditText editTextCrop;
    private EditText editTextPrice;
    private Button btnPublishPrice;
    private DatabaseReference MarketPriceDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_price);

        editTextCrop = findViewById(R.id.editTextcrop);
        editTextPrice = findViewById(R.id.editTextPrice);
        btnPublishPrice = findViewById(R.id.btnPublishPrice);

        mAuth = FirebaseAuth.getInstance();
        MarketPriceDbRef = FirebaseDatabase.getInstance().getReference("MarketPrice");

        btnPublishPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublishPrice();
            }
        });
    }

    private void PublishPrice() {
        // Retrieve all the input fields
        String crop = editTextCrop.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();

        if (crop.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique key for the post
        String postId = MarketPriceDbRef.push().getKey();

        // Get the current date and time as the validity date
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String validityDate = dateFormat.format(currentDate);

        // Create a new Pricepost object with validity date
        Pricepost pricepost = new Pricepost(postId, crop, price, validityDate);

        // Save the post to Firebase Realtime Database
        MarketPriceDbRef.child(postId).setValue(pricepost)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PublishPrice.this, "PricePost published successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PublishPrice.this, AdminHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PublishPrice.this, "Error publishing the post. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
