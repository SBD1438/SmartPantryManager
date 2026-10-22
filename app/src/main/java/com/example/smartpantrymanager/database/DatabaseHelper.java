package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creates the database tables.
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE PantryItems (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "expiryDate TEXT)"
        );

        db.execSQL(
                "CREATE TABLE Recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "prepTime INTEGER NOT NULL, " +
                        "instructions TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE RecipeIngredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipeId INTEGER NOT NULL, " +
                        "ingredientName TEXT NOT NULL, " +
                        "requiredQuantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipeId) REFERENCES Recipes(id) " +
                        "ON DELETE CASCADE)"
        );

        db.execSQL(
                "CREATE TABLE Settings (" +
                        "id INTEGER PRIMARY KEY, " +
                        "expiryAlerts INTEGER NOT NULL DEFAULT 1)"
        );

        db.execSQL(
                "INSERT INTO Settings (id, expiryAlerts) VALUES (1, 1)"
        );
    }

    // Handles future database upgrades.
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {
        // Add database migration code here when the schema changes.
    }


    // =====================================================
    // PANTRY METHODS
    // =====================================================

    // Adds a pantry ingredient.
    public long addPantryItem(PantryItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiryDate", item.getExpiryDate());

        return db.insert("PantryItems", null, values);
    }

    // Retrieves all pantry ingredients.
    public ArrayList<PantryItem> getAllPantryItems() {

        ArrayList<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM PantryItems ORDER BY name ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {

                items.add(new PantryItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiryDate"))
                ));
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    // Updates a pantry ingredient.
    public int updatePantryItem(PantryItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("quantity", item.getQuantity());
        values.put("unit", item.getUnit());
        values.put("expiryDate", item.getExpiryDate());

        return db.update(
                "PantryItems",
                values,
                "id = ?",
                new String[]{String.valueOf(item.getId())}
        );
    }

    // Deletes a pantry ingredient.
    public int deletePantryItem(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                "PantryItems",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }


    // =====================================================
    // RECIPE METHODS
    // =====================================================

    // Adds a recipe and returns its database ID.
    public long addRecipe(
            String name,
            int prepTime,
            String instructions) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("prepTime", prepTime);
        values.put("instructions", instructions);

        return db.insert("Recipes", null, values);
    }

    // Retrieves all recipes.
    public ArrayList<Recipe> getAllRecipes() {

        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Recipes ORDER BY name ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {

                recipes.add(new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("prepTime")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
                ));
            }
        } finally {
            cursor.close();
        }

        return recipes;
    }

    // Retrieves one recipe by ID.
    public Recipe getRecipeById(int recipeId) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        try {
            if (cursor.moveToFirst()) {
                return new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("prepTime")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
                );
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    // Updates a recipe.
    public int updateRecipe(Recipe recipe) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", recipe.getName());
        values.put("prepTime", recipe.getPrepTime());
        values.put("instructions", recipe.getInstructions());

        return db.update(
                "Recipes",
                values,
                "id = ?",
                new String[]{String.valueOf(recipe.getId())}
        );
    }

    // Deletes a recipe and its required ingredients.
    public int deleteRecipe(int recipeId) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(
                    "RecipeIngredients",
                    "recipeId = ?",
                    new String[]{String.valueOf(recipeId)}
            );

            int deletedRows = db.delete(
                    "Recipes",
                    "id = ?",
                    new String[]{String.valueOf(recipeId)}
            );

            db.setTransactionSuccessful();
            return deletedRows;

        } finally {
            db.endTransaction();
        }
    }


    // =====================================================
    // RECIPE INGREDIENT METHODS
    // =====================================================

    // Adds a required ingredient to a recipe.
    public long addRecipeIngredient(
            int recipeId,
            String ingredientName,
            double requiredQuantity,
            String unit) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("recipeId", recipeId);
        values.put("ingredientName", ingredientName);
        values.put("requiredQuantity", requiredQuantity);
        values.put("unit", unit);

        return db.insert("RecipeIngredients", null, values);
    }

    // Retrieves all required ingredients for one recipe.
    public ArrayList<RecipeIngredient> getIngredientsForRecipe(int recipeId) {

        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM RecipeIngredients WHERE recipeId = ?",
                new String[]{String.valueOf(recipeId)}
        );

        try {
            while (cursor.moveToNext()) {

                ingredients.add(new RecipeIngredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("recipeId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ingredientName")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("requiredQuantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        } finally {
            cursor.close();
        }

        return ingredients;
    }

    // Retrieves all required ingredients for all recipes.
    public ArrayList<RecipeIngredient> getAllRecipeIngredients() {

        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM RecipeIngredients ORDER BY recipeId ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {

                ingredients.add(new RecipeIngredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("recipeId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ingredientName")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("requiredQuantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        } finally {
            cursor.close();
        }

        return ingredients;
    }


    // =====================================================
    // STARTER RECIPE SEEDING
    // =====================================================

    // Inserts the 20 starter recipes only when none exist.
    public void seedRecipesIfNeeded() {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Recipes",
                null
        );

        try {
            cursor.moveToFirst();

            if (cursor.getInt(0) > 0) {
                return;
            }
        } finally {
            cursor.close();
        }

        db.beginTransaction();

        try {
            addStarterRecipe("Pancakes", 20,
                    "1. Mix flour, milk and egg. 2. Fry portions in a lightly oiled pan until golden.",
                    new String[]{"flour", "milk", "egg"},
                    new double[]{150, 200, 1},
                    new String[]{"g", "ml", "piece"});

            addStarterRecipe("French Toast", 10,
                    "1. Beat egg and milk. 2. Dip bread into the mixture. 3. Fry until golden.",
                    new String[]{"bread", "egg", "milk"},
                    new double[]{2, 1, 50},
                    new String[]{"slices", "piece", "ml"});

            addStarterRecipe("Cheese Omelette", 10,
                    "1. Beat the eggs. 2. Cook in a pan. 3. Add cheese, fold and cook until set.",
                    new String[]{"egg", "cheese"},
                    new double[]{2, 30},
                    new String[]{"pieces", "g"});

            addStarterRecipe("Scrambled Eggs", 8,
                    "1. Beat eggs with milk. 2. Stir gently in a pan until cooked.",
                    new String[]{"egg", "milk"},
                    new double[]{2, 30},
                    new String[]{"pieces", "ml"});

            addStarterRecipe("Banana Pancakes", 15,
                    "1. Mash the banana. 2. Mix with egg and flour. 3. Fry until cooked.",
                    new String[]{"banana", "egg", "flour"},
                    new double[]{1, 1, 50},
                    new String[]{"piece", "piece", "g"});

            addStarterRecipe("Tomato Pasta", 25,
                    "1. Cook pasta. 2. Fry chopped onion and tomato. 3. Mix with pasta.",
                    new String[]{"pasta", "tomato", "onion"},
                    new double[]{150, 2, 1},
                    new String[]{"g", "pieces", "piece"});

            addStarterRecipe("Garlic Pasta", 20,
                    "1. Cook pasta. 2. Gently fry garlic in oil. 3. Toss the pasta in the garlic oil.",
                    new String[]{"pasta", "garlic", "oil"},
                    new double[]{150, 2, 15},
                    new String[]{"g", "cloves", "ml"});

            addStarterRecipe("Cheese Sandwich", 5,
                    "1. Place cheese between bread slices. 2. Serve cold or toast until golden.",
                    new String[]{"bread", "cheese"},
                    new double[]{2, 30},
                    new String[]{"slices", "g"});

            addStarterRecipe("Tomato Sandwich", 5,
                    "1. Slice the tomato. 2. Place between bread slices and serve.",
                    new String[]{"bread", "tomato"},
                    new double[]{2, 1},
                    new String[]{"slices", "piece"});

            addStarterRecipe("Potato Mash", 25,
                    "1. Boil potatoes until soft. 2. Mash with milk and butter.",
                    new String[]{"potato", "milk", "butter"},
                    new double[]{300, 50, 15},
                    new String[]{"g", "ml", "g"});

            addStarterRecipe("Roasted Potatoes", 40,
                    "1. Cut potatoes into chunks. 2. Toss with oil. 3. Roast at 200 degrees Celsius until golden.",
                    new String[]{"potato", "oil"},
                    new double[]{300, 15},
                    new String[]{"g", "ml"});

            addStarterRecipe("Carrot Soup", 30,
                    "1. Chop carrots and onion. 2. Boil until soft. 3. Blend until smooth.",
                    new String[]{"carrot", "onion", "water"},
                    new double[]{200, 1, 500},
                    new String[]{"g", "piece", "ml"});

            addStarterRecipe("Apple Smoothie", 5,
                    "1. Chop the apple. 2. Blend with milk until smooth.",
                    new String[]{"apple", "milk"},
                    new double[]{1, 200},
                    new String[]{"piece", "ml"});

            addStarterRecipe("Banana Smoothie", 5,
                    "1. Blend banana and milk until smooth.",
                    new String[]{"banana", "milk"},
                    new double[]{1, 200},
                    new String[]{"piece", "ml"});

            addStarterRecipe("Fruit Salad", 10,
                    "1. Wash and chop apple, banana and orange. 2. Mix and serve.",
                    new String[]{"apple", "banana", "orange"},
                    new double[]{1, 1, 1},
                    new String[]{"piece", "piece", "piece"});

            addStarterRecipe("Carrot Salad", 10,
                    "1. Grate the carrots. 2. Add lemon juice and mix.",
                    new String[]{"carrot", "lemon"},
                    new double[]{150, 1},
                    new String[]{"g", "piece"});

            addStarterRecipe("Tomato Soup", 25,
                    "1. Chop tomatoes and onion. 2. Simmer with water until soft. 3. Blend.",
                    new String[]{"tomato", "onion", "water"},
                    new double[]{3, 1, 300},
                    new String[]{"pieces", "piece", "ml"});

            addStarterRecipe("Egg Fried Rice", 20,
                    "1. Cook rice. 2. Scramble the egg. 3. Mix together in a pan.",
                    new String[]{"rice", "egg", "oil"},
                    new double[]{150, 1, 10},
                    new String[]{"g", "piece", "ml"});

            addStarterRecipe("Simple Rice", 20,
                    "1. Rinse rice. 2. Add water and simmer until tender.",
                    new String[]{"rice", "water"},
                    new double[]{150, 300},
                    new String[]{"g", "ml"});

            addStarterRecipe("Buttered Toast", 5,
                    "1. Toast the bread. 2. Spread butter over the warm toast.",
                    new String[]{"bread", "butter"},
                    new double[]{2, 10},
                    new String[]{"slices", "g"});

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    // Inserts one recipe and all its required ingredients.
    private void addStarterRecipe(
            String name,
            int prepTime,
            String instructions,
            String[] ingredientNames,
            double[] quantities,
            String[] units) {

        if (ingredientNames.length != quantities.length ||
                ingredientNames.length != units.length) {
            throw new SQLException("Recipe ingredient data does not match.");
        }

        long recipeId = addRecipe(name, prepTime, instructions);

        if (recipeId == -1) {
            throw new SQLException("Could not save recipe: " + name);
        }

        for (int i = 0; i < ingredientNames.length; i++) {

            long ingredientId = addRecipeIngredient(
                    (int) recipeId,
                    ingredientNames[i],
                    quantities[i],
                    units[i]
            );

            if (ingredientId == -1) {
                throw new SQLException(
                        "Could not save ingredients for: " + name
                );
            }
        }
    }


    // =====================================================
    // SETTINGS METHODS
    // =====================================================

    // Checks whether expiry alerts are enabled.
    public boolean areExpiryAlertsEnabled() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT expiryAlerts FROM Settings WHERE id = 1",
                null
        );

        try {
            return cursor.moveToFirst() && cursor.getInt(0) == 1;
        } finally {
            cursor.close();
        }
    }

    // Enables or disables expiry alerts.
    public void setExpiryAlertsEnabled(boolean enabled) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("expiryAlerts", enabled ? 1 : 0);

        db.update(
                "Settings",
                values,
                "id = 1",
                null
        );
    }
}

