package com.example.farming_partner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> postList;
    private OnGetDirectionClickListener onGetDirectionClickListener;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    public void setOnGetDirectionClickListener(OnGetDirectionClickListener listener) {
        this.onGetDirectionClickListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
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

        // Add an OnClickListener to the "Get Direction" button
        holder.buttonGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGetDirectionClickListener != null) {
                    onGetDirectionClickListener.onGetDirectionClick(post);
                }
            }
        });

        // Add an OnClickListener to the "Call" button
        holder.buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the dialer with the phone number
                String phoneNumber = post.getContactNumber();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));

                // Check if the app has permission to make a phone call
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Start the phone call activity
                    context.startActivity(intent);
                } else {
                    // You don't have permission, request it from the user
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 0);
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
        ImageButton buttonGetDirection, buttonCall;

        PostViewHolder(View itemView) {
            super(itemView);
            textViewCrop = itemView.findViewById(R.id.textViewCrop);
            textViewCapacity = itemView.findViewById(R.id.textViewCapacity);
            textViewNegotiable = itemView.findViewById(R.id.textViewNegotiable);
            textViewFarmerName = itemView.findViewById(R.id.textViewFarmerName);
            textViewContactNumber = itemView.findViewById(R.id.textViewContactNumber);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewSellingLocation = itemView.findViewById(R.id.textViewSellingLocation);
            buttonGetDirection = itemView.findViewById(R.id.buttonGetDirection);
            buttonCall = itemView.findViewById(R.id.buttonCall);
        }
    }

    public interface OnGetDirectionClickListener {
        void onGetDirectionClick(Post post);
    }
}
