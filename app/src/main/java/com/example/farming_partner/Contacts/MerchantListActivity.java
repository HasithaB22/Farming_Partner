package com.example.farming_partner.Contacts;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farming_partner.merchants;
import com.example.farming_partner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MerchantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MerchantAdapter merchantAdapter;
    private List<merchants> merchantList;

    private DatabaseReference userDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_list);

        recyclerView = findViewById(R.id.recyclerViewMerchants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        merchantList = new ArrayList<>();
        merchantAdapter = new MerchantAdapter(this, merchantList);
        recyclerView.setAdapter(merchantAdapter);

        mAuth = FirebaseAuth.getInstance();
        userDbRef = FirebaseDatabase.getInstance().getReference("merchants");

        // Retrieve merchants from Firebase
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                merchantList.clear();
                for (DataSnapshot merchantSnapshot : dataSnapshot.getChildren()) {
                    merchants merchant = merchantSnapshot.getValue(merchants.class);
                    merchantList.add(merchant);
                }
                merchantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
