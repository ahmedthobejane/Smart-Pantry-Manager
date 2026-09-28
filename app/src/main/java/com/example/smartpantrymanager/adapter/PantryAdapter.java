package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.Pantryitem;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Pantryitem item);
        void onDeleteClick(Pantryitem item);
    }

    private List<Pantryitem> items;
    private OnItemClickListener listener;

    public PantryAdapter(List<Pantryitem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        Pantryitem item = items.get(position);
        holder.textName.setText(item.getName());

        String qtyText = item.getQuantity() + " " + item.getUnit();
        holder.textQuantity.setText(qtyText);

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.textExpiry.setText("Exp: " + item.getExpiryDate());
            holder.textExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.textExpiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textQuantity, textExpiry;
        ImageButton btnDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_item_name);
            textQuantity = itemView.findViewById(R.id.text_item_quantity);
            textExpiry = itemView.findViewById(R.id.text_item_expiry);
            btnDelete = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}