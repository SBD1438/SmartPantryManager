package com.example.smartpantrymanager;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.example.smartpantrymanager.utils.RecipeMatcher;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecipeMatcherTest {

    // Tests whether a recipe can be made when all ingredients are available.
    @Test
    public void canMakeRecipeWhenIngredientsAreAvailable() {

        Recipe recipe = new Recipe(1, "Scrambled Eggs", 10, "Mix and cook.");

        List<RecipeIngredient> requiredIngredients = Arrays.asList(
                new RecipeIngredient(1, 1, "Eggs", 2, "pieces")
        );

        List<PantryItem> pantryItems = Arrays.asList(
                new PantryItem(1, "Eggs", 3, "pieces", null)
        );

        assertTrue(RecipeMatcher.canMakeRecipe(
                recipe, requiredIngredients, pantryItems));
    }

    // Tests whether insufficient ingredient quantities are rejected.
    @Test
    public void cannotMakeRecipeWhenQuantityIsInsufficient() {

        Recipe recipe = new Recipe(1, "Scrambled Eggs", 10, "Mix and cook.");

        List<RecipeIngredient> requiredIngredients = Arrays.asList(
                new RecipeIngredient(1, 1, "Eggs", 2, "pieces")
        );

        List<PantryItem> pantryItems = Arrays.asList(
                new PantryItem(1, "Eggs", 1, "piece", null)
        );

        assertFalse(RecipeMatcher.canMakeRecipe(
                recipe, requiredIngredients, pantryItems));
    }

    // Tests whether kilograms and grams are compared correctly.
    @Test
    public void canMatchCompatibleWeightUnits() {

        Recipe recipe = new Recipe(1, "Flour Recipe", 15, "Mix ingredients.");

        List<RecipeIngredient> requiredIngredients = Arrays.asList(
                new RecipeIngredient(1, 1, "Flour", 500, "g")
        );

        List<PantryItem> pantryItems = Arrays.asList(
                new PantryItem(1, "Flour", 0.5, "kg", null)
        );

        assertTrue(RecipeMatcher.canMakeRecipe(
                recipe, requiredIngredients, pantryItems));
    }

    // Tests whether a recipe with no required ingredients is rejected.
    @Test
    public void cannotMakeRecipeWhenRequiredIngredientsAreEmpty() {

        Recipe recipe = new Recipe(1, "Test Recipe", 5, "Test instructions.");

        assertFalse(RecipeMatcher.canMakeRecipe(
                recipe,
                Collections.emptyList(),
                Collections.emptyList()));
    }

    // Tests whether ingredient names match regardless of capitalisation.
    @Test
    public void canMatchIngredientNamesWithDifferentCapitalisation() {

        Recipe recipe = new Recipe(1, "Flour Recipe", 15, "Mix ingredients.");

        List<RecipeIngredient> requiredIngredients = Arrays.asList(
                new RecipeIngredient(1, 1, "Flour", 500, "g")
        );

        List<PantryItem> pantryItems = Arrays.asList(
                new PantryItem(1, "flour", 500, "g", null)
        );

        assertTrue(RecipeMatcher.canMakeRecipe(
                recipe, requiredIngredients, pantryItems));
    }
}

