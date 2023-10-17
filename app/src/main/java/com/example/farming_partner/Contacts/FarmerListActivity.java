package com.example.farming_partner.Contacts;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farming_partner.farmers;
import com.example.farming_partner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FarmerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FarmerAdapter farmerAdapter;
    private List<farmers> farmerList;

    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_list);

        recyclerView = findViewById(R.id.recyclerViewFarmers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        farmerList = new ArrayList<>();
        farmerAdapter = new FarmerAdapter(this, farmerList);
        recyclerView.setAdapter(farmerAdapter);

        mAuth = FirebaseAuth.getInstance();
        userDbRef = FirebaseDatabase.getInstance().getReference("farmers");

        // Retrieve farmers from Firebase
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                farmerList.clear();
                for (DataSnapshot farmerSnapshot : dataSnapshot.getChildren()) {
                    farmers farmer = farmerSnapshot.getValue(farmers.class);
                    farmerList.add(farmer);
                }
                farmerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
