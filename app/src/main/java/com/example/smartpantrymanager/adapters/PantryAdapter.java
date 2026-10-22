package com.example.smartpantrymanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.AddEditIngredientActivity;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;

import java.util.ArrayList;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final ArrayList<PantryItem> pantryItems;

    // Receives the ingredients to display.
    public PantryAdapter(ArrayList<PantryItem> pantryItems) {
        this.pantryItems = new ArrayList<>(pantryItems);
    }

    // Creates the layout for an individual ingredient.
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    // Displays the ingredient information.
    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item = pantryItems.get(position);

        holder.name.setText(item.getName());

        holder.quantity.setText(
                item.getQuantity() + " " + item.getUnit()
        );

        if (item.getExpiryDate() == null
                || item.getExpiryDate().trim().isEmpty()) {

            holder.expiry.setText("No expiry date");

        } else {

            holder.expiry.setText(
                    "Expiry: " + item.getExpiryDate()
            );
        }

        // Open the editing screen when an ingredient is tapped.
        holder.itemView.setOnClickListener(view -> {

            Context context = view.getContext();

            Intent intent = new Intent(
                    context,
                    AddEditIngredientActivity.class
            );

            // Pass the selected ingredient's ID to the editing screen.
            intent.putExtra("item_id", item.getId());

            context.startActivity(intent);
        });
    }

    // Returns the number of ingredients in the list.
    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    // Updates the list after an ingredient is saved.
    public void updateData(ArrayList<PantryItem> newItems) {

        pantryItems.clear();
        pantryItems.addAll(newItems);

        notifyDataSetChanged();
    }

    // Holds the views for one ingredient card.
    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView quantity;
        TextView expiry;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(
                    R.id.textIngredientName
            );

            quantity = itemView.findViewById(
                    R.id.textIngredientQuantity
            );

            expiry = itemView.findViewById(
                    R.id.textIngredientExpiry
            );
        }
    }
}
