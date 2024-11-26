package com.example.testapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.PurchaseViewHolder> {

//    private List<Purchase> purchases;
//    private OnItemClickListener listener;
//
//    // Define the listener interface
//    public interface OnItemClickListener {
//        void onItemClick(Purchase purchase);
//    }
//
//    // Modify constructor to include the listener
//    public PurchasesAdapter(List<Purchase> purchases, OnItemClickListener listener) {
//        if (listener == null) {
//            throw new IllegalArgumentException("OnItemClickListener cannot be null");
//        }
//        this.purchases = (purchases != null) ? purchases : new ArrayList<>();
//        this.listener = listener;
//    }
//
//    public void setPurchases(List<Purchase> purchases) {
//        this.purchases = (purchases != null) ? purchases : new ArrayList<>();
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase, parent, false);
//        return new PurchaseViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
//        Purchase purchase = purchases.get(position);
//        holder.itemNameTextView.setText(purchase.getItemName());
//        holder.priceTextView.setText("Price: $" + purchase.getPrice());
//        holder.categoryTextView.setText("Category: " + purchase.getCategory());
//
//        String formattedDate = purchase.getDatePurchased().split("T")[0];
//        holder.datePurchasedTextView.setText("Date: " + formattedDate);
//
//        // Set the click listener for each item
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onItemClick(purchase);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return purchases.size();
//    }
//
//    public static class PurchaseViewHolder extends RecyclerView.ViewHolder {
//        TextView itemNameTextView, priceTextView, categoryTextView, datePurchasedTextView;
//
//        public PurchaseViewHolder(@NonNull View itemView) {
//            super(itemView);
//            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
//            priceTextView = itemView.findViewById(R.id.priceTextView);
//            categoryTextView = itemView.findViewById(R.id.categoryTextView);
//            datePurchasedTextView = itemView.findViewById(R.id.datePurchasedTextView);
//        }
//    }

    private List<Purchase> purchases;
    private OnItemClickListener listener;

    // Define the listener interface for item clicks
    public interface OnItemClickListener {
        void onItemClick(Purchase purchase);
    }

    // Constructor
    public PurchasesAdapter(List<Purchase> purchases, OnItemClickListener listener) {
        this.purchases = (purchases != null) ? purchases : new ArrayList<>();
        this.listener = listener;
    }

    // Method to update the list of purchases
    public void setPurchases(List<Purchase> purchases) {
        this.purchases = (purchases != null) ? purchases : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_purchase, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Purchase purchase = purchases.get(position);

        // Bind the purchase data to the views
        holder.itemNameTextView.setText(purchase.getItemName());
        holder.priceTextView.setText("Price: $" + purchase.getPrice());
        holder.categoryTextView.setText("Category: " + purchase.getCategory());

        String formattedDate = purchase.getDatePurchased().split("T")[0]; // Exclude the time part
        holder.datePurchasedTextView.setText("Date: " + formattedDate);

        // Handle item clicks
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(purchase);
            }
        });
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    public static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, priceTextView, categoryTextView, datePurchasedTextView;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            datePurchasedTextView = itemView.findViewById(R.id.datePurchasedTextView);
        }
    }
}