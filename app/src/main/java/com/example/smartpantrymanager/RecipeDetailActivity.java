package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView textDetailRecipeName;
    private TextView textDetailPrepTime;
    private TextView textDetailIngredients;
    private TextView textDetailInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        textDetailRecipeName = findViewById(R.id.textDetailRecipeName);
        textDetailPrepTime = findViewById(R.id.textDetailPrepTime);
        textDetailIngredients = findViewById(R.id.textDetailIngredients);
        textDetailInstructions = findViewById(R.id.textDetailInstructions);

        Button buttonBackToRecipes = findViewById(R.id.buttonBackToRecipes);

        buttonBackToRecipes.setOnClickListener(view -> finish());

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId == -1) {
            showRecipeNotFound();
            return;
        }

        displayRecipe(recipeId);
    }

    private void displayRecipe(int recipeId) {

        Recipe recipe = databaseHelper.getRecipeById(recipeId);

        if (recipe == null) {
            showRecipeNotFound();
            return;
        }

        textDetailRecipeName.setText(recipe.getName());

        textDetailPrepTime.setText(
                "Preparation time: " + recipe.getPrepTime() + " minutes"
        );

        textDetailInstructions.setText(recipe.getInstructions());

        ArrayList<RecipeIngredient> ingredients =
                databaseHelper.getIngredientsForRecipe(recipeId);

        StringBuilder ingredientText = new StringBuilder();

        for (RecipeIngredient ingredient : ingredients) {

            ingredientText.append("• ")
                    .append(ingredient.getIngredientName())
                    .append(" - ")
                    .append(formatQuantity(ingredient.getRequiredQuantity()))
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }

        if (ingredientText.length() == 0) {
            ingredientText.append("No ingredients have been added for this recipe yet.");
        }

        textDetailIngredients.setText(ingredientText.toString());
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.format(Locale.getDefault(), "%.0f", quantity);
        }

        return String.format(Locale.getDefault(), "%s", quantity);
    }

    private void showRecipeNotFound() {

        textDetailRecipeName.setText("Recipe not found");
        textDetailPrepTime.setText("");
        textDetailIngredients.setText(
                "The recipe could not be found in the database."
        );
        textDetailInstructions.setText(
                "Please return to Suggested Recipes and select a recipe."
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

}
