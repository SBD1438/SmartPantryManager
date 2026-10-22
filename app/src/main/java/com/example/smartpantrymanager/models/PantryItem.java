package com.example.smartpantrymanager.models;

public class PantryItem {

    // Stores the ingredient's ID in the database
    private int id;

    // Stores the ingredient's name
    private String name;

    // Stores how much of the ingredient is available
    private double quantity;

    // Stores the measurement unit, such as kg or cups
    private String unit;

    // Stores the optional expiry date
    private String expiryDate;

    // Constructor used to create a pantry ingredient
    public PantryItem(int id, String name, double quantity,
                      String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    // Returns the ingredient's ID
    public int getId() {
        return id;
    }

    // Returns the ingredient's name
    public String getName() {
        return name;
    }

    // Returns the available quantity
    public double getQuantity() {
        return quantity;
    }

    // Returns the measurement unit
    public String getUnit() {
        return unit;
    }

    // Returns the expiry date
    public String getExpiryDate() {
        return expiryDate;
    }

    // Updates the ingredient's name
    public void setName(String name) {
        this.name = name;
    }

    // Updates the available quantity
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    // Updates the measurement unit
    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Updates the expiry date
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}

