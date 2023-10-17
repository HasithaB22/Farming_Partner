package com.example.farming_partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class home extends AppCompatActivity {

    private Button btnFarmer;
    private Button btnMerchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnFarmer = findViewById(R.id.btnFarmer);
        btnMerchant = findViewById(R.id.btnMerchant);

        btnFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, farmer_registration.class));
            }
        });

        btnMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this, merchant_registration.class));
            }
        });
    }
}
