package model;

/**
 * Represents a food ingredient with nutritional information.
 */
public class Ingredient {
    private String name;
    private double quantity;
    private String unit; // e.g., grams, cups, tablespoons
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    
    /**
     * Default constructor
     */
    public Ingredient() {
    }
    
    /**
     * Constructor with basic ingredient information
     */
    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
    
    /**
     * Full constructor with all nutritional information
     */
    public Ingredient(String name, double quantity, String unit, 
                     double calories, double protein, double carbs, double fat) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public double getCalories() {
        return calories;
    }
    
    public void setCalories(double calories) {
        this.calories = calories;
    }
    
    public double getProtein() {
        return protein;
    }
    
    public void setProtein(double protein) {
        this.protein = protein;
    }
    
    public double getCarbs() {
        return carbs;
    }
    
    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }
    
    public double getFat() {
        return fat;
    }
    
    public void setFat(double fat) {
        this.fat = fat;
    }
    
    @Override
    public String toString() {
        return quantity + " " + unit + " of " + name + 
               " (" + calories + " cal, " + protein + "g protein, " + 
               carbs + "g carbs, " + fat + "g fat)";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Ingredient that = (Ingredient) obj;
        return name.equals(that.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}