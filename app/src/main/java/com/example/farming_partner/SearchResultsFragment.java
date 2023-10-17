package com.example.farming_partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> filteredPostList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSearchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize filteredPostList here
        filteredPostList = new ArrayList<>();

        postAdapter = new PostAdapter(requireContext(), filteredPostList);
        recyclerView.setAdapter(postAdapter);


        postAdapter.setOnGetDirectionClickListener(new PostAdapter.OnGetDirectionClickListener() {
            @Override
            public void onGetDirectionClick(Post post) {
                // Handle the "Get Direction" button click here
                // Pass the selected post's postId to the getdirection activity
                Intent intent = new Intent(requireContext(), getdirection.class);
                intent.putExtra("postId", post.getPostId());
                startActivity(intent);
            }
        });

        return view;
    }

    // Method to update the filtered list
    public void updateFilteredList(List<Post> filteredList) {
        if (filteredPostList != null) {
            filteredPostList.clear();
            filteredPostList.addAll(filteredList);
            postAdapter.notifyDataSetChanged();
        }
    }
}
