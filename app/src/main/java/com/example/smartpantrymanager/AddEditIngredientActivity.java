package com.example.smartpantrymanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;

import java.util.ArrayList;

public class AddEditIngredientActivity extends Activity {

    private EditText nameInput;
    private EditText quantityInput;
    private EditText unitInput;
    private EditText expiryInput;

    private DatabaseHelper databaseHelper;
    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect this Activity to its XML layout.
        setContentView(R.layout.activity_add_edit_ingredient);

        // Connect Java to the database.
        databaseHelper = new DatabaseHelper(this);

        // Find the input fields.
        nameInput = findViewById(R.id.editIngredientName);
        quantityInput = findViewById(R.id.editIngredientQuantity);
        unitInput = findViewById(R.id.editIngredientUnit);
        expiryInput = findViewById(R.id.editIngredientExpiry);

        // Find the buttons and heading.
        Button saveButton = findViewById(R.id.buttonSaveIngredient);

        Button deleteButton =
                findViewById(R.id.buttonDeleteIngredient);

        TextView formHeading =
                findViewById(R.id.textIngredientFormHeading);

        // Check whether an existing ingredient is being edited.
        ingredientId = getIntent().getIntExtra("item_id", -1);

        if (ingredientId != -1) {

            // Change the heading when editing.
            formHeading.setText("Edit your pantry ingredient");

            // Show the Delete button when editing.
            deleteButton.setVisibility(android.view.View.VISIBLE);

            // Load the existing ingredient.
            loadIngredient();

        } else {

            // Hide the Delete button when adding a new ingredient.
            deleteButton.setVisibility(android.view.View.GONE);
        }

        // Save the ingredient when the button is clicked.
        saveButton.setOnClickListener(view -> saveIngredient());

        // Delete the ingredient when the button is clicked.
        deleteButton.setOnClickListener(view -> confirmDelete());
    }

    // Loads an existing ingredient into the form.
    private void loadIngredient() {

        ArrayList<PantryItem> items =
                databaseHelper.getAllPantryItems();

        for (PantryItem item : items) {

            if (item.getId() == ingredientId) {

                nameInput.setText(item.getName());

                quantityInput.setText(
                        String.valueOf(item.getQuantity())
                );

                unitInput.setText(item.getUnit());

                expiryInput.setText(
                        item.getExpiryDate() == null
                                ? ""
                                : item.getExpiryDate()
                );

                return;
            }
        }

        Toast.makeText(
                this,
                "Ingredient not found.",
                Toast.LENGTH_LONG
        ).show();

        finish();
    }

    // Validates and saves the ingredient.
    private void saveIngredient() {

        String name = nameInput.getText().toString().trim();

        String quantityText =
                quantityInput.getText().toString().trim();

        String unit = unitInput.getText().toString().trim();

        String expiryDate =
                expiryInput.getText().toString().trim();

        // Check required fields.
        if (name.isEmpty()
                || quantityText.isEmpty()
                || unit.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please complete all required fields.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);

            if (!Double.isFinite(quantity) || quantity <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Enter a valid quantity greater than zero.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create the pantry ingredient.
        PantryItem item = new PantryItem(
                ingredientId,
                name,
                quantity,
                unit,
                expiryDate.isEmpty() ? null : expiryDate
        );

        // Add a new ingredient or update an existing one.
        if (ingredientId == -1) {

            long result = databaseHelper.addPantryItem(item);

            if (result == -1) {
                showSaveError();
                return;
            }

        } else {

            int result = databaseHelper.updatePantryItem(item);

            if (result == 0) {
                showSaveError();
                return;
            }
        }

        Toast.makeText(
                this,
                "Ingredient saved successfully!",
                Toast.LENGTH_SHORT
        ).show();

        // Close the form after saving.
        finish();
    }

    // Ask for confirmation before deleting an ingredient.
    private void confirmDelete() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + nameInput.getText().toString().trim()
                                + " from your pantry?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteIngredient();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    // Deletes the selected ingredient from SQLite.
    private void deleteIngredient() {

        if (ingredientId == -1) {
            return;
        }

        int result = databaseHelper.deletePantryItem(ingredientId);

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Ingredient deleted successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            // Return to the pantry after deletion.
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Could not delete the ingredient. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void showSaveError() {

        Toast.makeText(
                this,
                "Could not save the ingredient. Please try again.",
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}

