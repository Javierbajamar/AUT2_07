package com.example.aut2_07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {
    private Context context;
    private List<Item> itemList;
    private OnItemListener onItemListener;

    public interface OnItemListener {
        void onItemClicked(Item item);
    }

    public ItemsAdapter(Context context, List<Item> itemList, OnItemListener onItemListener) {
        this.context = context;
        this.itemList = itemList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());

        holder.itemView.setOnClickListener(v -> onItemListener.onItemClicked(item));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public ItemViewHolder(View itemView, OnItemListener onItemListener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.itemTitle);
            descriptionTextView = itemView.findViewById(R.id.itemDescription);

            itemView.setOnClickListener(v -> onItemListener.onItemClicked((Item) itemView.getTag()));
        }
    }
}