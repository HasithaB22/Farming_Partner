package com.example.farming_partner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MypostActivity extends AppCompatActivity {


    private Button btnMakePost;
    private Button Mangepost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost);


        btnMakePost = findViewById(R.id.btnMakePost);
        Mangepost = findViewById(R.id.btnManagepost);



        Mangepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MypostActivity.this, FarmerPostListActivity.class));
            }
        });



        btnMakePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MypostActivity.this, publishpost.class));
            }
        });


    }
}