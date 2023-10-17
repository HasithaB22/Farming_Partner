package com.example.farming_partner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.farming_partner.Market_Price.adminside.AdminPricePostListActivity;
import com.example.farming_partner.Market_Price.farmerside.PricePostListActivity;
import com.example.farming_partner.Market_Price.adminside.PublishPrice;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ImageButton btnAdminLogout;
    private ImageButton btnAddCropPrice;
    private ImageButton btnViewprice;


//Main


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        mAuth = FirebaseAuth.getInstance();
        btnAdminLogout = findViewById(R.id.btnAdminLogout);
        btnAddCropPrice = findViewById(R.id.btnAddCropPrice);

        btnViewprice = findViewById(R.id.btnViewprice);


        btnAdminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        btnAddCropPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this, PublishPrice.class));
            }
        });



        btnViewprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this, AdminPricePostListActivity.class));
            }
        });



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
        startActivity(new Intent(AdminHomeActivity.this, home.class));
        finish();
    }
}
