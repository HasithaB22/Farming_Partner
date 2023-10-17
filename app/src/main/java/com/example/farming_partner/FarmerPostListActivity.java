package com.example.farming_partner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FarmerPostListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FarmerPostAdapter fpostAdapter;
    private List<Post> postList;

    private DatabaseReference postsDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_post_list);

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        fpostAdapter = new FarmerPostAdapter(this, postList);
        recyclerView.setAdapter(fpostAdapter);

        mAuth = FirebaseAuth.getInstance();
        postsDbRef = FirebaseDatabase.getInstance().getReference("posts");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Retrieve posts from Firebase for the current farmer
            postsDbRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        postList.add(post);
                    }
                    fpostAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Toast.makeText(FarmerPostListActivity.this, "Failed to retrieve posts", Toast.LENGTH_SHORT).show();
                }
            });

            // Set an item click listener for the adapter
            fpostAdapter.setOnItemClickListener(new FarmerPostAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Post post) {
                    // Open the EditPostActivity and pass the postId
                    Intent intent = new Intent(FarmerPostListActivity.this, EditPostActivity.class);
                    intent.putExtra("postId", post.getPostId());
                    startActivity(intent);
                }

                @Override
                public void onDeleteClick(Post post) {
                    // Handle post deletion here
                    deletePost(post.getPostId());
                }
            });
        }
    }

    private void deletePost(String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this post?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference postRef = postsDbRef.child(postId);
                postRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Handle successful deletion
                                Toast.makeText(FarmerPostListActivity.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle deletion failure
                                Toast.makeText(FarmerPostListActivity.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Cancel deletion
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
