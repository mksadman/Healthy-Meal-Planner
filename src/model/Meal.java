package model;

import java.util.ArrayList;
import java.util.List;

public class Meal {
    private String name;
    private List<Ingredient> ingredients;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private String dietaryType; // vegetarian, keto, paleo, etc.
    private String description;

    public Meal() {
        this.ingredients = new ArrayList<>();
    }

    public Meal(String name, String dietaryType, String description) {
        this.name = name;
        this.dietaryType = dietaryType;
        this.description = description;
        this.ingredients = new ArrayList<>();
    }

    public Meal(String name, List<Ingredient> ingredients, String dietaryType, String description) {
        this.name = name;
        this.ingredients = ingredients;
        this.dietaryType = dietaryType;
        this.description = description;
        calculateNutrition();
    }

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

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        calculateNutrition();
    }

    public boolean removeIngredient(Ingredient ingredient) {
        boolean removed = ingredients.remove(ingredient);
        if (removed) {
            calculateNutrition();
        }
        return removed;
    }

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

    /**
     * Sets nutrition values directly without calculating from ingredients.
     * Used when loading meals from text files where we have the total values.
     *
     * @param calories Total calories
     * @param protein Total protein in grams
     * @param carbs Total carbohydrates in grams
     * @param fat Total fat in grams
     */
    public void setDirectNutritionValues(double calories, double protein, double carbs, double fat) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    @Override
    public String toString() {
        return name + " - " + description + " (" + dietaryType + ")\n" +
                "Nutrition: " + calories + " cal, " + protein + "g protein, " +
                carbs + "g carbs, " + fat + "g fat";
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }
}