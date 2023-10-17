package com.example.farming_partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity implements PostAdapter.OnGetDirectionClickListener {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<Post> filteredPostList; // New list to hold filtered results

    private DatabaseReference postsDbRef;
    private FirebaseAuth mAuth;

    private SearchResultsFragment searchResultsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        filteredPostList = new ArrayList<>(); // Initialize the filtered list

        mAuth = FirebaseAuth.getInstance();
        postsDbRef = FirebaseDatabase.getInstance().getReference("posts");

        // Set the click listener for the adapter
        postAdapter.setOnGetDirectionClickListener(this);

        // Initialize the searchResultsFragment
        searchResultsFragment = new SearchResultsFragment();

        // Handle search button click
        Button searchButton = findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSearch();
            }
        });

        // Handle clear button click
        Button clearButton = findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearchResults();
            }
        });

        // Retrieve all posts from Firebase
        postsDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        // Check if the SearchResultsFragment is added
        if (savedInstanceState == null) {
            // If savedInstanceState is null, add the fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.frameLayout, searchResultsFragment);
            transaction.commit();
        }
    }

    // Handle the search logic
    private void handleSearch() {
        EditText searchText = findViewById(R.id.editTextSearch);
        String query = searchText.getText().toString().trim();

        // Implement your search logic here
        filteredPostList.clear();
        for (Post post : postList) {
            if (post.getCrop().toLowerCase().contains(query.toLowerCase()) ||
                    post.getFarmerName().toLowerCase().contains(query.toLowerCase())) {
                filteredPostList.add(post);
            }
        }

        // Update the filtered list in the SearchResultsFragment
        searchResultsFragment.updateFilteredList(filteredPostList);
    }

    // Handle clearing the search results
    private void clearSearchResults() {
        filteredPostList.clear();

        // Update the filtered list in the SearchResultsFragment
        searchResultsFragment.updateFilteredList(filteredPostList);
    }

    // Implement the OnGetDirectionClickListener
    @Override
    public void onGetDirectionClick(Post post) {
        // Handle the "Get Direction" button click here
        // Pass the selected post's postId to the getdirection activity
        Intent intent = new Intent(this, getdirection.class);
        intent.putExtra("postId", post.getPostId());
        startActivity(intent);
    }
}
