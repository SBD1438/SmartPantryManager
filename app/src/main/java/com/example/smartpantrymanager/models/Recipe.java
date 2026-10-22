package com.example.smartpantrymanager.models;

// Stores information about a recipe.
public class Recipe {

    private int id;
    private String name;
    private int prepTime;
    private String instructions;

    // Creates a new recipe.
    public Recipe(int id, String name, int prepTime,
                  String instructions) {
        this.id = id;
        this.name = name;
        this.prepTime = prepTime;
        this.instructions = instructions;
    }

    // Returns the recipe ID.
    public int getId() {
        return id;
    }

    // Returns the recipe name.
    public String getName() {
        return name;
    }

    // Returns the preparation time.
    public int getPrepTime() {
        return prepTime;
    }

    // Returns the cooking instructions.
    public String getInstructions() {
        return instructions;
    }

    // Updates the recipe name.
    public void setName(String name) {
        this.name = name;
    }

    // Updates the preparation time.
    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    // Updates the cooking instructions.
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}