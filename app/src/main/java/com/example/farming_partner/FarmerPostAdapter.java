package com.example.farming_partner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FarmerPostAdapter extends RecyclerView.Adapter<FarmerPostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Post post);
        void onDeleteClick(Post post);
    }

    public FarmerPostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_farmer_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.textViewCrop.setText("Crop: " + post.getCrop());
        holder.textViewCapacity.setText("Quantity: " + post.getquantity());
        holder.textViewNegotiable.setText("Negotiable: " + (post.isNegotiable() ? "Yes" : "No"));
        holder.textViewFarmerName.setText("Farmer Name: " + post.getFarmerName());
        holder.textViewContactNumber.setText("Contact Number: " + post.getContactNumber());
        holder.textViewAddress.setText("Address: " + post.getAddress());
        holder.textViewSellingLocation.setText("Selling Location: " + post.getSellingLocation());

        // Set a click listener for the update button
        holder.buttonUpdatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(post);
                }
            }
        });

        // Set a click listener for the delete button
        holder.buttonDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onDeleteClick(post);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCrop, textViewCapacity, textViewNegotiable, textViewFarmerName,
                textViewContactNumber, textViewAddress, textViewSellingLocation;

        Button buttonUpdatePost, buttonDeletePost;

        PostViewHolder(View itemView) {
            super(itemView);
            textViewCrop = itemView.findViewById(R.id.textViewCrop);
            textViewCapacity = itemView.findViewById(R.id.textViewCapacity);
            textViewNegotiable = itemView.findViewById(R.id.textViewNegotiable);
            textViewFarmerName = itemView.findViewById(R.id.textViewFarmerName);
            textViewContactNumber = itemView.findViewById(R.id.textViewContactNumber);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewSellingLocation = itemView.findViewById(R.id.textViewSellingLocation);
            buttonUpdatePost = itemView.findViewById(R.id.buttonUpdatePost);
            buttonDeletePost = itemView.findViewById(R.id.buttonDeletePost);
        }
    }

    // Add a getter method for the OnItemClickListener
    public OnItemClickListener getOnItemClickListener() {
        return itemClickListener;
    }

    // Add a setter method for the OnItemClickListener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
}
