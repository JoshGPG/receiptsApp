package com.example.testapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
                .inflate(R.layout.list_item, parent, false); // Replace with your list item layout
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ListData list = lists.get(position);
        holder.listName.setText(list.getListName());
        holder.itemCount.setText("Items: " + list.getPurchases().split(",").length);

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

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.listName); // Replace with your TextView ID for the list name
            itemCount = itemView.findViewById(R.id.itemCount); // Replace with your TextView ID for item count
        }
    }
}