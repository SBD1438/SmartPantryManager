package com.example.smartpantrymanager.utils;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeMatcher {

    // Checks whether all required ingredients are available.
    public static boolean canMakeRecipe(
            Recipe recipe,
            List<RecipeIngredient> requiredIngredients,
            List<PantryItem> pantryItems) {

        if (recipe == null ||
                requiredIngredients == null ||
                requiredIngredients.isEmpty() ||
                pantryItems == null) {
            return false;
        }

        // Check every ingredient required by the recipe.
        for (RecipeIngredient required : requiredIngredients) {

            boolean ingredientFound = false;

            // Search the pantry for the required ingredient.
            for (PantryItem pantryItem : pantryItems) {

                if (sameIngredient(
                        required.getIngredientName(),
                        pantryItem.getName())) {

                    // Check whether the units are compatible.
                    double requiredAmount = convertToBaseUnit(
                            required.getRequiredQuantity(),
                            required.getUnit());

                    double availableAmount = convertToBaseUnit(
                            pantryItem.getQuantity(),
                            pantryItem.getUnit());

                    String requiredUnit = getBaseUnit(required.getUnit());
                    String availableUnit = getBaseUnit(pantryItem.getUnit());

                    // Units must be compatible.
                    if (requiredUnit.equals(availableUnit)
                            && availableAmount >= requiredAmount) {

                        ingredientFound = true;
                        break;
                    }
                }
            }

            // If any required ingredient is missing,
            // the recipe cannot be made.
            if (!ingredientFound) {
                return false;
            }
        }

        // Every required ingredient is available.
        return true;
    }


    // Finds all recipes that can be made completely.
    public static ArrayList<Recipe> getMatchingRecipes(
            List<Recipe> recipes,
            List<PantryItem> pantryItems,
            List<RecipeIngredient> allRecipeIngredients) {

        ArrayList<Recipe> matchingRecipes = new ArrayList<>();

        if (recipes == null ||
                pantryItems == null ||
                allRecipeIngredients == null) {
            return matchingRecipes;
        }

        for (Recipe recipe : recipes) {

            ArrayList<RecipeIngredient> requiredIngredients =
                    new ArrayList<>();

            // Collect ingredients belonging to this recipe.
            for (RecipeIngredient ingredient : allRecipeIngredients) {

                if (ingredient.getRecipeId() == recipe.getId()) {
                    requiredIngredients.add(ingredient);
                }
            }

            // Include the recipe only if every ingredient is available.
            if (canMakeRecipe(
                    recipe,
                    requiredIngredients,
                    pantryItems)) {

                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }


    // Compares ingredient names while allowing simple plurals.
    private static boolean sameIngredient(
            String first,
            String second) {

        String name1 = normaliseName(first);
        String name2 = normaliseName(second);

        return name1.equals(name2);
    }


    // Converts names to lowercase and handles simple plural endings.
    private static String normaliseName(String name) {

        if (name == null) {
            return "";
        }

        String result = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        if (result.endsWith("ies") && result.length() > 4) {
            result = result.substring(0, result.length() - 3) + "y";
        } else if (result.endsWith("s")
                && !result.endsWith("ss")
                && result.length() > 3) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }


    // Converts compatible measurements to a common base unit.
    private static double convertToBaseUnit(
            double quantity,
            String unit) {

        String normalisedUnit = normaliseUnit(unit);

        switch (normalisedUnit) {

            case "kg":
            case "kilogram":
            case "kilograms":
                return quantity * 1000.0;

            case "g":
            case "gram":
            case "grams":
                return quantity;

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return quantity * 1000.0;

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return quantity;

            default:
                // Units such as pieces remain unchanged.
                return quantity;
        }
    }


    // Returns the standard unit used for comparisons.
    private static String getBaseUnit(String unit) {

        String normalisedUnit = normaliseUnit(unit);

        switch (normalisedUnit) {

            case "kg":
            case "kilogram":
            case "kilograms":
            case "g":
            case "gram":
            case "grams":
                return "g";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            default:
                return normalisedUnit;
        }
    }


    // Standardises common unit names.
    private static String normaliseUnit(String unit) {

        if (unit == null) {
            return "";
        }

        return unit.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}
