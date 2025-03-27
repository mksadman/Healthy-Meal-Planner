package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all meal types in the meal planning system.
 * Contains common properties and methods for all meals.
 */
public class Meal {
    private String name;
    private List<Ingredient> ingredients;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private String dietaryType; // vegetarian, keto, paleo, etc.
    private String description;
    
    /**
     * Default constructor
     */
    public Meal() {
        this.ingredients = new ArrayList<>();
    }
    
    /**
     * Constructor with basic meal information
     */
    public Meal(String name, String dietaryType, String description) {
        this.name = name;
        this.dietaryType = dietaryType;
        this.description = description;
        this.ingredients = new ArrayList<>();
    }
    
    /**
     * Full constructor with all meal properties
     */
    public Meal(String name, List<Ingredient> ingredients, String dietaryType, String description) {
        this.name = name;
        this.ingredients = ingredients;
        this.dietaryType = dietaryType;
        this.description = description;
        calculateNutrition();
    }
    
    /**
     * Calculates the nutritional values based on ingredients
     */
    public void calculateNutrition() {
        this.calories = 0;
        this.protein = 0;
        this.carbs = 0;
        this.fat = 0;
        
        for (Ingredient ingredient : ingredients) {
            this.calories += ingredient.getCalories();
            this.protein += ingredient.getProtein();
            this.carbs += ingredient.getCarbs();
            this.fat += ingredient.getFat();
        }
    }
    
    /**
     * Adds an ingredient to the meal and updates nutrition
     */
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        calculateNutrition();
    }
    
    /**
     * Removes an ingredient from the meal and updates nutrition
     */
    public boolean removeIngredient(Ingredient ingredient) {
        boolean removed = ingredients.remove(ingredient);
        if (removed) {
            calculateNutrition();
        }
        return removed;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        calculateNutrition();
    }
    
    public double getCalories() {
        return calories;
    }
    
    public double getProtein() {
        return protein;
    }
    
    public double getCarbs() {
        return carbs;
    }
    
    public double getFat() {
        return fat;
    }
    
    public String getDietaryType() {
        return dietaryType;
    }
    
    public void setDietaryType(String dietaryType) {
        this.dietaryType = dietaryType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return name + " - " + description + " (" + dietaryType + ")\n" +
               "Nutrition: " + calories + " cal, " + protein + "g protein, " + 
               carbs + "g carbs, " + fat + "g fat";
    }
}