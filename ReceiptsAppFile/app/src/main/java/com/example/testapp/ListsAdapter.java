package com.example.testapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListViewHolder> {


    private List<ListData> lists;
    private OnItemClickListener onItemClickListener;

    // Listener Interface
    public interface OnItemClickListener {
        void onItemClick(ListData list);
    }

    // Constructor
    public ListsAdapter(List<ListData> lists, OnItemClickListener onItemClickListener) {
        this.lists = lists;
        this.onItemClickListener = onItemClickListener;
    }

    public void setLists(List<ListData> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false); // Ensure your `list_item` layout is updated as per the earlier card-based layout
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ListData list = lists.get(position);

        // Set the list name and item count
        holder.listName.setText(list.getListName());
        holder.itemCount.setText("Items: " + list.getPurchases().split(",").length);

        // Alternate colors based on the position
        Context context = holder.itemView.getContext();
        int color;

        switch (position % 6) {
            case 0:
                color = ContextCompat.getColor(context, R.color.groceriesColor);
                break;
            case 1:
                color = ContextCompat.getColor(context, R.color.clothingColor);
                break;
            case 2:
                color = ContextCompat.getColor(context, R.color.electronicsColor);
                break;
            case 3:
                color = ContextCompat.getColor(context, R.color.healthColor);
                break;
            case 4:
                color = ContextCompat.getColor(context, R.color.homeColor);
                break;
            case 5:
                color = ContextCompat.getColor(context, R.color.entertainmentColor);
                break;
            default:
                color = ContextCompat.getColor(context, R.color.defaultColor);
                break;
        }

        // Apply the color to the CardView
        holder.cardView.setCardBackgroundColor(color);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(list);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists != null ? lists.size() : 0;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView listName, itemCount;
        CardView cardView;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.listName); // Replace with your TextView ID for the list name
            itemCount = itemView.findViewById(R.id.itemCount); // Replace with your TextView ID for item count
            cardView = itemView.findViewById(R.id.cardView); // Add this in your `list_item` layout
        }
    }
}