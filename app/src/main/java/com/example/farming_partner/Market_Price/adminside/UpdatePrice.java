package com.example.farming_partner.Market_Price.adminside;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.farming_partner.Market_Price.Pricepost;
import com.example.farming_partner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdatePrice extends AppCompatActivity {

    private EditText editTextCropUpdate;
    private EditText editTextPriceUpdate;
    private Button buttonUpdatePrice;

    private DatabaseReference MarketPriceDbRef;
    private FirebaseAuth mAuth;

    private String priceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_price);

        editTextCropUpdate = findViewById(R.id.editTextCropUpdate);
        editTextPriceUpdate = findViewById(R.id.editTextPriceUpdate);
        buttonUpdatePrice = findViewById(R.id.buttonUpdatePrice);

        mAuth = FirebaseAuth.getInstance();
        MarketPriceDbRef = FirebaseDatabase.getInstance().getReference("MarketPrice");

        priceId = getIntent().getStringExtra("priceId");

        loadPriceData(priceId);

        buttonUpdatePrice.setOnClickListener(view -> updatePrice());
    }

    private void loadPriceData(String priceId) {
        DatabaseReference priceRef = MarketPriceDbRef.child(priceId);
        priceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Pricepost pricepost = dataSnapshot.getValue(Pricepost.class);
                    if (pricepost != null) {
                        // Populate the EditText fields with the retrieved data
                        editTextCropUpdate.setText(pricepost.getCrop());
                        editTextPriceUpdate.setText(pricepost.getPrice());
                    }
                } else {
                    Toast.makeText(UpdatePrice.this, "Price not found.", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity if the price is not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(UpdatePrice.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePrice() {
        String crop = editTextCropUpdate.getText().toString().trim();
        String price = editTextPriceUpdate.getText().toString().trim();

        if (TextUtils.isEmpty(crop) || TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference priceRef = MarketPriceDbRef.child(priceId);
        priceRef.child("crop").setValue(crop);
        priceRef.child("price").setValue(price);

        Toast.makeText(this, "Price updated successfully!", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity and return to the previous one
    }
}
