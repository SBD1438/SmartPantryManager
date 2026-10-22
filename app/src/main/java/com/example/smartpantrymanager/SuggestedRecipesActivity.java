package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.example.smartpantrymanager.utils.RecipeMatcher;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerSuggestedRecipes;
    private TextView textRecipeCount;
    private TextView textNoRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.seedRecipesIfNeeded();

        recyclerSuggestedRecipes = findViewById(R.id.recyclerSuggestedRecipes);
        textRecipeCount = findViewById(R.id.textRecipeCount);
        textNoRecipes = findViewById(R.id.textNoRecipes);

        Button buttonBackToPantry = findViewById(R.id.buttonBackToPantry);

        recyclerSuggestedRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        buttonBackToPantry.setOnClickListener(view -> finish());

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        ArrayList<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        ArrayList<Recipe> allRecipes =
                databaseHelper.getAllRecipes();

        ArrayList<RecipeIngredient> allRecipeIngredients =
                databaseHelper.getAllRecipeIngredients();

        // Pass the lists in the order expected by RecipeMatcher.
        ArrayList<Recipe> matchingRecipes =
                RecipeMatcher.getMatchingRecipes(
                        allRecipes,
                        pantryItems,
                        allRecipeIngredients
                );

        textRecipeCount.setText(
                "Recipes you can make: " + matchingRecipes.size()
        );

        if (matchingRecipes.isEmpty()) {
            textNoRecipes.setVisibility(android.view.View.VISIBLE);
            recyclerSuggestedRecipes.setVisibility(android.view.View.GONE);
        } else {
            textNoRecipes.setVisibility(android.view.View.GONE);
            recyclerSuggestedRecipes.setVisibility(android.view.View.VISIBLE);
        }

        RecipeAdapter recipeAdapter =
                new RecipeAdapter(this, matchingRecipes);

        recyclerSuggestedRecipes.setAdapter(recipeAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
