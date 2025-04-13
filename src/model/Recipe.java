package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recipe with ingredients and preparation instructions.
 */
public class Recipe {
    private String name;
    private List<Ingredient> ingredients;
    private List<String> preparationSteps;
    private int preparationTimeMinutes;
    private int cookingTimeMinutes;
    private int servings;
    private String difficultyLevel; // Easy, Medium, Hard

    /**
     * Default constructor
     */
    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.preparationSteps = new ArrayList<>();
    }

    /**
     * Constructor with basic recipe information
     */
    public Recipe(String name, int preparationTimeMinutes, int cookingTimeMinutes, int servings, String difficultyLevel) {
        this.name = name;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.cookingTimeMinutes = cookingTimeMinutes;
        this.servings = servings;
        this.difficultyLevel = difficultyLevel;
        this.ingredients = new ArrayList<>();
        this.preparationSteps = new ArrayList<>();
    }

    /**
     * Full constructor with all recipe properties
     */
    public Recipe(String name, List<Ingredient> ingredients, List<String> preparationSteps,
                  int preparationTimeMinutes, int cookingTimeMinutes, int servings, String difficultyLevel) {
        this.name = name;
        this.ingredients = ingredients;
        this.preparationSteps = preparationSteps;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.cookingTimeMinutes = cookingTimeMinutes;
        this.servings = servings;
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * Adds an ingredient to the recipe
     */
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    /**
     * Removes an ingredient from the recipe
     */
    public boolean removeIngredient(Ingredient ingredient) {
        return ingredients.remove(ingredient);
    }

    /**
     * Adds a preparation step to the recipe
     */
    public void addPreparationStep(String step) {
        preparationSteps.add(step);
    }

    /**
     * Removes a preparation step from the recipe
     */
    public boolean removePreparationStep(String step) {
        return preparationSteps.remove(step);
    }

    /**
     * Calculates the total preparation and cooking time
     */
    public int getTotalTimeMinutes() {
        return preparationTimeMinutes + cookingTimeMinutes;
    }

    /**
     * Scales the recipe for a different number of servings
     * @param newServings The new number of servings
     * @return A new Recipe object scaled for the new number of servings
     */
    public Recipe scaleRecipe(int newServings) {
        if (newServings <= 0 || newServings == this.servings) {
            return this;
        }

        double scaleFactor = (double) newServings / this.servings;
        List<Ingredient> scaledIngredients = new ArrayList<>();

        // Scale each ingredient
        for (Ingredient ingredient : this.ingredients) {
            Ingredient scaledIngredient = new Ingredient(
                    ingredient.getName(),
                    ingredient.getQuantity() * scaleFactor,
                    ingredient.getUnit(),
                    ingredient.getCalories() * scaleFactor,
                    ingredient.getProtein() * scaleFactor,
                    ingredient.getCarbs() * scaleFactor,
                    ingredient.getFat() * scaleFactor
            );
            scaledIngredients.add(scaledIngredient);
        }

        // Create a new recipe with scaled ingredients
        Recipe scaledRecipe = new Recipe(
                this.name + " (scaled)",
                scaledIngredients,
                new ArrayList<>(this.preparationSteps),
                this.preparationTimeMinutes,
                this.cookingTimeMinutes,
                newServings,
                this.difficultyLevel
        );

        return scaledRecipe;
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
    }

    public List<String> getPreparationSteps() {
        return preparationSteps;
    }

    public void setPreparationSteps(List<String> preparationSteps) {
        this.preparationSteps = preparationSteps;
    }

    public int getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(int preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public int getCookingTimeMinutes() {
        return cookingTimeMinutes;
    }

    public void setCookingTimeMinutes(int cookingTimeMinutes) {
        this.cookingTimeMinutes = cookingTimeMinutes;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * Generates a formatted recipe card
     */
    public String getRecipeCard() {
        StringBuilder card = new StringBuilder();
        card.append("RECIPE: ").append(name.toUpperCase()).append("\n");
        card.append("===========================================\n\n");

        card.append("Servings: ").append(servings).append("\n");
        card.append("Difficulty: ").append(difficultyLevel).append("\n");
        card.append("Prep Time: ").append(preparationTimeMinutes).append(" minutes\n");
        card.append("Cook Time: ").append(cookingTimeMinutes).append(" minutes\n");
        card.append("Total Time: ").append(getTotalTimeMinutes()).append(" minutes\n\n");

        card.append("INGREDIENTS:\n");
        for (Ingredient ingredient : ingredients) {
            card.append("- ").append(ingredient.getQuantity()).append(" ")
                    .append(ingredient.getUnit()).append(" of ")
                    .append(ingredient.getName()).append("\n");
        }
        card.append("\n");

        card.append("INSTRUCTIONS:\n");
        for (int i = 0; i < preparationSteps.size(); i++) {
            card.append(i + 1).append(". ").append(preparationSteps.get(i)).append("\n");
        }

        return card.toString();
    }

    @Override
    public String toString() {
        return name + " - " + difficultyLevel + " difficulty, " +
                getTotalTimeMinutes() + " minutes total time, " +
                servings + " servings";
    }
}