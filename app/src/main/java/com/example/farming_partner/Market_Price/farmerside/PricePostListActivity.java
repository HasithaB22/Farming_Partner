package com.example.farming_partner.Market_Price.farmerside;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farming_partner.Market_Price.Pricepost;
import com.example.farming_partner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PricePostListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PricepostAdapter pricepostAdapter;
    private List<Pricepost> pricepostList;

    private DatabaseReference MarketPriceDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_post_list);

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pricepostList = new ArrayList<>();
        pricepostAdapter = new PricepostAdapter(this, pricepostList);
        recyclerView.setAdapter(pricepostAdapter);

        mAuth = FirebaseAuth.getInstance();
        MarketPriceDbRef = FirebaseDatabase.getInstance().getReference("MarketPrice");

        // Retrieve posts from Firebase
        MarketPriceDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pricepostList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Pricepost pricepost = postSnapshot.getValue(Pricepost.class);
                    pricepostList.add(pricepost);
                }
                pricepostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
