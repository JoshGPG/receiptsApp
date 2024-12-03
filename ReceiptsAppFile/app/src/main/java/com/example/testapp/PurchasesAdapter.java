package com.example.testapp;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.PurchaseViewHolder>{
    private List<Purchase> purchases;

    // Constructor with a default empty list
    public PurchasesAdapter(List<Purchase> purchases) {
        this.purchases = (purchases != null) ? purchases : new ArrayList<>();
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = (purchases != null) ? purchases : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Purchase purchase = purchases.get(position);
        holder.itemNameTextView.setText(purchase.getItemName());
        holder.priceTextView.setText("Price: $" + purchase.getPrice());
        holder.categoryTextView.setText("Category: " + purchase.getCategory());

        String originalDate = purchase.getDatePurchased();
        String formattedDate = originalDate.split("T")[0];  // Get only the date part before 'T'
        holder.datePurchasedTextView.setText("Date: " + formattedDate);
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    // ViewHolder class
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
