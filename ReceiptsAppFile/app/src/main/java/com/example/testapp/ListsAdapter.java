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

    public interface OnItemClickListener {
        void onItemClick(ListData list);
    }

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
                .inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ListData list = lists.get(position);

        holder.listName.setText(list.getListName());
        holder.itemCount.setText("Items: " + list.getPurchases().split(",").length);

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

        holder.cardView.setCardBackgroundColor(color);

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
            listName = itemView.findViewById(R.id.listName);
            itemCount = itemView.findViewById(R.id.itemCount);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}