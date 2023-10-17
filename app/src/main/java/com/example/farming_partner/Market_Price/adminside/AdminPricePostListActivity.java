package com.example.farming_partner.Market_Price.adminside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
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

public class AdminPricePostListActivity extends AppCompatActivity implements AdminPricepostAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AdminPricepostAdapter pricepostAdapter;
    private List<Pricepost> pricepostList;

    private DatabaseReference MarketPriceDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_price_post_list);

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pricepostList = new ArrayList<>();
        pricepostAdapter = new AdminPricepostAdapter(this, pricepostList);
        recyclerView.setAdapter(pricepostAdapter);

        mAuth = FirebaseAuth.getInstance();
        MarketPriceDbRef = FirebaseDatabase.getInstance().getReference("MarketPrice");

        // Retrieve all posts from Firebase
        MarketPriceDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pricepostList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Pricepost pricepost = postSnapshot.getValue(Pricepost.class);
                    if (pricepost != null) {
                        pricepostList.add(pricepost);
                    }
                }
                pricepostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(AdminPricePostListActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add a click listener to the RecyclerView items to handle updates and deletes
        pricepostAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        // Open the UpdatePriceActivity with the selected price's data
        Intent intent = new Intent(AdminPricePostListActivity.this, UpdatePrice.class);
        intent.putExtra("priceId", pricepostList.get(position).getPriceId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        // Get the priceId of the selected item
        String priceId = pricepostList.get(position).getPriceId();

        // Show a confirmation dialog before deleting the post
        showDeleteConfirmationDialog(priceId, position);
    }

    private void showDeleteConfirmationDialog(final String priceId, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this post?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the selected post from Firebase Realtime Database
                DatabaseReference priceRef = MarketPriceDbRef.child(priceId);
                priceRef.removeValue();

                // Optionally, remove the item from the RecyclerView and update the UI
                pricepostList.remove(position);
                pricepostAdapter.notifyItemRemoved(position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel the deletion and close the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
