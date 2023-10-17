package com.example.farming_partner.Market_Price.farmerside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farming_partner.Market_Price.Pricepost;
import com.example.farming_partner.R;

import java.util.List;

public class PricepostAdapter extends RecyclerView.Adapter<PricepostAdapter.PricePostViewHolder> {

    private Context context;
    private List<Pricepost> PricePostList;

    public PricepostAdapter(Context context, List<Pricepost> PricePostList) {
        this.context = context;
        this.PricePostList = PricePostList;
    }

    @NonNull
    @Override
    public PricePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pricepost, parent, false);
        return new PricePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PricePostViewHolder holder, int position) {
        Pricepost pricepost = PricePostList.get(position);

        holder.textViewCrop.setText("Crop: " + pricepost.getCrop());
        String formattedPrice = String.format("Rs.%.2f", Double.parseDouble(pricepost.getPrice()));
        holder.textViewPrice.setText("Price Per Kg: " + formattedPrice);
        holder.textViewValidityDate.setText("Validity Date: " + pricepost.getValidityDate());

        // You can bind other UI elements here
    }

    @Override
    public int getItemCount() {
        return PricePostList.size();
    }

    static class PricePostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCrop, textViewPrice, textViewValidityDate;

        PricePostViewHolder(View itemView) {
            super(itemView);
            textViewCrop = itemView.findViewById(R.id.textViewCrop);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewValidityDate = itemView.findViewById(R.id.textViewValidityDate);

            // Find other views here
        }
    }
}
