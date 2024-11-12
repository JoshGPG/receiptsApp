package com.example.testapp;//package com.example.testapp;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.PurchaseViewHolder>{
//    private List<Purchase> purchases;
//
//    // Constructor with a default empty list
//    public PurchasesAdapter(List<Purchase> purchases) {
//        this.purchases = (purchases != null) ? purchases : new ArrayList<>();
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
//        String originalDate = purchase.getDatePurchased();
//        String formattedDate = originalDate.split("T")[0];  // Get only the date part before 'T'
//        holder.datePurchasedTextView.setText("Date: " + formattedDate);
//    }
//
//    @Override
//    public int getItemCount() {
//        return purchases.size();
//    }
//
//    // ViewHolder class
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
//}
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.PurchaseViewHolder> {

    private List<Purchase> purchases;
    private OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(Purchase purchase);
    }

    // Modify constructor to include the listener
    public PurchasesAdapter(List<Purchase> purchases, OnItemClickListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("OnItemClickListener cannot be null");
        }
        this.purchases = (purchases != null) ? purchases : new ArrayList<>();
        this.listener = listener;
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

        String formattedDate = purchase.getDatePurchased().split("T")[0];
        holder.datePurchasedTextView.setText("Date: " + formattedDate);

        // Set the click listener for each item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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