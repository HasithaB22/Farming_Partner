package com.example.farming_partner.Market_Price.adminside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.farming_partner.Market_Price.Pricepost;
import com.example.farming_partner.R;
import java.util.List;

public class AdminPricepostAdapter extends RecyclerView.Adapter<AdminPricepostAdapter.PricePostViewHolder> {

    private Context context;
    private List<Pricepost> PricePostList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public AdminPricepostAdapter(Context context, List<Pricepost> PricePostList) {
        this.context = context;
        this.PricePostList = PricePostList;
    }

    public OnItemClickListener getOnItemClickListener() {
        return listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PricePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_adminpricepost, parent, false);
        return new PricePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PricePostViewHolder holder, int position) {
        Pricepost pricepost = PricePostList.get(position);

        holder.textViewCrop.setText("Crop: " + pricepost.getCrop());
        String formattedPrice = String.format("Rs.%.2f", Double.parseDouble(pricepost.getPrice()));
        holder.textViewPrice.setText("Price Per Kg: " + formattedPrice);
        holder.textViewValidityDate.setText("Validity Date: " + pricepost.getValidityDate());

        holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemClick(adapterPosition);
                    }
                }
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(adapterPosition);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return PricePostList.size();
    }

    static class PricePostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCrop, textViewPrice, textViewValidityDate;
        Button buttonUpdate, buttonDelete;

        PricePostViewHolder(View itemView) {
            super(itemView);
            textViewCrop = itemView.findViewById(R.id.textViewCrop);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewValidityDate = itemView.findViewById(R.id.textViewValidityDate);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdatePost);
            buttonDelete = itemView.findViewById(R.id.buttonDelete); // Added delete button
        }
    }
}
