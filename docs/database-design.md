# Smart Pantry Manager: Database Design

## 1. Database Overview

Smart Pantry Manager uses SQLite to store pantry ingredients, recipe information, recipe ingredients, and application settings locally on the device.

The database is managed by the `DatabaseHelper.java` class, which extends Android's `SQLiteOpenHelper`.

* **Database name:** `SmartPantry.db`
* **Database version:** 1
* **Database management class:** `DatabaseHelper.java`

## 2. Database Tables

The database contains four tables: `PantryItems`, `Recipes`, `RecipeIngredients`, and `Settings`.

### 2.1 PantryItems Table

The `PantryItems` table stores ingredients added to the user's pantry.

**Columns:**

| Column       | Data Type | Description                                           |
| ------------ | --------- | ----------------------------------------------------- |
| `id`         | INTEGER   | Primary key that automatically generates a unique ID. |
| `name`       | TEXT      | Ingredient name. Cannot be NULL.                      |
| `quantity`   | REAL      | Amount of the ingredient available. Cannot be NULL.   |
| `unit`       | TEXT      | Measurement unit. Cannot be NULL.                     |
| `expiryDate` | TEXT      | Expiry date of the ingredient. Can be NULL.           |

**Purpose:**

This table stores pantry ingredients and their quantities, measurement units, and expiry dates.

**Database operations:**

The `DatabaseHelper.java` class provides methods to:

* Add a pantry ingredient using `addPantryItem()`.
* Retrieve all pantry ingredients using `getAllPantryItems()`.
* Update an ingredient using `updatePantryItem()`.
* Delete an ingredient using `deletePantryItem()`.

Retrieved pantry ingredients are ordered alphabetically by name.

### 2.2 Recipes Table

The `Recipes` table stores recipe information used by the recipe suggestion feature.

**Columns:**

| Column         | Data Type | Description                                            |
| -------------- | --------- | ------------------------------------------------------ |
| `id`           | INTEGER   | Primary key that automatically generates a unique ID.  |
| `name`         | TEXT      | Name of the recipe. Cannot be NULL.                    |
| `prepTime`     | INTEGER   | Recipe preparation time in minutes. Cannot be NULL.    |
| `instructions` | TEXT      | Instructions for preparing the recipe. Cannot be NULL. |

**Purpose:**

This table stores recipe names, preparation times, and cooking instructions.

**Database operations:**

The `DatabaseHelper.java` class provides methods to:

* Add a recipe using `addRecipe()`.
* Retrieve all recipes using `getAllRecipes()`.
* Retrieve one recipe using `getRecipeById()`.
* Update a recipe using `updateRecipe()`.
* Delete a recipe using `deleteRecipe()`.

Retrieved recipes are ordered alphabetically by name.

### 2.3 RecipeIngredients Table

The `RecipeIngredients` table stores the ingredients and quantities required for each recipe.

**Columns:**

| Column             | Data Type | Description                                           |
| ------------------ | --------- | ----------------------------------------------------- |
| `id`               | INTEGER   | Primary key that automatically generates a unique ID. |
| `recipeId`         | INTEGER   | Identifies the associated recipe. Cannot be NULL.     |
| `ingredientName`   | TEXT      | Name of the required ingredient. Cannot be NULL.      |
| `requiredQuantity` | REAL      | Quantity required for the recipe. Cannot be NULL.     |
| `unit`             | TEXT      | Measurement unit. Cannot be NULL.                     |

**Purpose:**

This table records which ingredients a recipe requires, including the required quantities and measurement units.

**Database operations:**

The `DatabaseHelper.java` class provides methods to:

* Add a required ingredient using `addRecipeIngredient()`.
* Retrieve the ingredients for one recipe using `getIngredientsForRecipe()`.
* Retrieve all recipe ingredients using `getAllRecipeIngredients()`.

**Relationship:**

The `recipeId` column is declared as a foreign key referencing the `id` column in the `Recipes` table. The foreign key specifies `ON DELETE CASCADE`.

One recipe can have multiple required ingredients.

The `deleteRecipe()` method also manually deletes the recipe's associated ingredients before deleting the recipe. Both operations are performed inside a database transaction.

### 2.4 Settings Table

The `Settings` table stores the application's expiry-alert setting.

**Columns:**

| Column         | Data Type | Description                                                                 |
| -------------- | --------- | --------------------------------------------------------------------------- |
| `id`           | INTEGER   | Primary key identifying the settings record.                                |
| `expiryAlerts` | INTEGER   | Indicates whether expiry alerts are enabled. Cannot be NULL. Defaults to 1. |

**Purpose:**

This table stores whether the user has enabled or disabled expiry alerts.

When the database is created, the application inserts an initial settings record with `id = 1` and `expiryAlerts = 1`.

* `1` means expiry alerts are enabled.
* `0` means expiry alerts are disabled.

**Database operations:**

The `DatabaseHelper.java` class provides methods to:

* Check whether expiry alerts are enabled using `areExpiryAlertsEnabled()`.
* Enable or disable expiry alerts using `setExpiryAlertsEnabled()`.

The application stores the setting in the database so that it can be retrieved later.

## 3. Database Relationships

The database contains a relationship between the `Recipes` and `RecipeIngredients` tables.

The `recipeId` foreign key in `RecipeIngredients` references the `id` primary key in `Recipes`.

One recipe can have multiple required ingredients. Each recipe ingredient record refers to its associated recipe.

The `deleteRecipe()` method uses a transaction to delete the associated recipe ingredients and then the recipe itself. The transaction is marked successful only after both deletion operations have completed.

**Relationship summary:**

`Recipes.id` → `RecipeIngredients.recipeId`

## 4. Initial Recipe Data

The `DatabaseHelper.java` class includes a `seedRecipesIfNeeded()` method that inserts 20 starter recipes when the `Recipes` table contains no recipes.

The starter recipes are:

1. Pancakes
2. French Toast
3. Cheese Omelette
4. Scrambled Eggs
5. Banana Pancakes
6. Tomato Pasta
7. Garlic Pasta
8. Cheese Sandwich
9. Tomato Sandwich
10. Potato Mash
11. Roasted Potatoes
12. Carrot Soup
13. Apple Smoothie
14. Banana Smoothie
15. Fruit Salad
16. Carrot Salad
17. Tomato Soup
18. Egg Fried Rice
19. Simple Rice
20. Buttered Toast

Each starter recipe includes its name, preparation time, instructions, required ingredients, quantities, and measurement units.

The `addStarterRecipe()` method inserts a recipe and its required ingredients. It checks that the ingredient names, quantities, and units have matching array lengths and throws an exception if a database insertion fails.

The starter recipes are inserted inside a database transaction to help keep the initial data consistent.

**Important:** The method `seedRecipesIfNeeded()` must be called by the application to insert these recipes. It is not automatically called by `onCreate()` in the `DatabaseHelper.java` code provided.

## 5. Database Creation and Upgrades

### 5.1 Database Creation

The `onCreate()` method creates the four database tables:

* `PantryItems`
* `Recipes`
* `RecipeIngredients`
* `Settings`

It also inserts the initial settings record with expiry alerts enabled.

### 5.2 Database Upgrades

The `onUpgrade()` method is provided for future database schema changes.

Currently, it contains a placeholder comment for future migration code. No database upgrade operations have been implemented in this method.

The database version is currently set to `1`.

## 6. Why SQLite Was Chosen

SQLite is suitable for Smart Pantry Manager because it is integrated into Android and stores data locally on the device without requiring a separate database server.

It allows the application to store and retrieve pantry ingredients, recipe information, required recipe ingredients, and application settings.

Local database storage also allows saved information to remain available when the application is reopened.

## 7. Summary

The `DatabaseHelper.java` class manages the creation and operation of the Smart Pantry Manager SQLite database.

The four tables support pantry management, recipe storage, recipe ingredient requirements, and expiry-alert settings.

The class provides methods for inserting, retrieving, updating, and deleting records, as well as inserting starter recipes and managing the expiry-alert setting.
