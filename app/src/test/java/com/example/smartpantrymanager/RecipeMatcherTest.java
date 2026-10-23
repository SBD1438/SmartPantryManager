package com.example.smartpantrymanager;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.example.smartpantrymanager.utils.RecipeMatcher;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
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

    // Tests whether a null recipe is rejected.
    @Test
    public void cannotMakeRecipeWhenRecipeIsNull() {

        List<RecipeIngredient> requiredIngredients = Arrays.asList(
                new RecipeIngredient(1, 1, "Eggs", 2, "pieces")
        );

        List<PantryItem> pantryItems = Arrays.asList(
                new PantryItem(1, "Eggs", 3, "pieces", null)
        );

        assertFalse(RecipeMatcher.canMakeRecipe(
                null, requiredIngredients, pantryItems));
    }

    // Tests whether only recipes with available ingredients are returned.
    @Test
    public void returnsOnlyRecipesThatCanBeMade() {

        Recipe eggsRecipe = new Recipe(
                1, "Scrambled Eggs", 10, "Mix and cook.");

        Recipe flourRecipe = new Recipe(
                2, "Flour Recipe", 15, "Mix ingredients.");

        List<Recipe> recipes = Arrays.asList(
                eggsRecipe, flourRecipe
        );

        List<PantryItem> pantryItems = Arrays.asList(
                new PantryItem(1, "Eggs", 3, "pieces", null)
        );

        List<RecipeIngredient> allRecipeIngredients = Arrays.asList(
                new RecipeIngredient(1, 1, "Eggs", 2, "pieces"),
                new RecipeIngredient(2, 2, "Flour", 500, "g")
        );

        List<Recipe> matchingRecipes = RecipeMatcher.getMatchingRecipes(
                recipes, pantryItems, allRecipeIngredients);

        assertEquals(1, matchingRecipes.size());
        assertEquals("Scrambled Eggs", matchingRecipes.get(0).getName());
    }
}

