package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for suggesting meals based on dietary preferences and nutritional goals.
 */
public class MealSuggester {
    private List<Meal> mealDatabase;
    private NutritionAnalyzer nutritionAnalyzer;
    
    /**
     * Constructor initializes an empty meal database and nutrition analyzer
     */
    public MealSuggester() {
        this.mealDatabase = new ArrayList<>();
        this.nutritionAnalyzer = new NutritionAnalyzer();
    }
    
    /**
     * Constructor with existing meal database
     */
    public MealSuggester(List<Meal> mealDatabase) {
        this.mealDatabase = mealDatabase;
        this.nutritionAnalyzer = new NutritionAnalyzer();
    }
    
    /**
     * Adds meal to the database
     */
    public void addMeal(Meal meal) {
        mealDatabase.add(meal);
    }
    
    /**
     * Removes a meal from the database
     */
    public boolean removeMeal(Meal meal) {
        return mealDatabase.remove(meal);
    }
    
    /**
     * Gets all meals in the database
     */
    public List<Meal> getAllMeals() {
        return new ArrayList<>(mealDatabase);
    }
    
    /**
     * Suggests breakfast options based on dietary type
     */
    public List<Breakfast> suggestBreakfasts(String dietaryType, boolean highProteinOnly, boolean caffeineFree) {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Breakfast)
                .map(meal -> (Breakfast) meal)
                .filter(breakfast -> dietaryType == null || breakfast.getDietaryType().equalsIgnoreCase(dietaryType))
                .filter(breakfast -> !highProteinOnly || breakfast.isHighProtein())
                .filter(breakfast -> !caffeineFree || !breakfast.containsCaffeine())
                .collect(Collectors.toList());
    }
    
    /**
     * Suggests lunch options based on dietary type
     */
    public List<Lunch> suggestLunches(String dietaryType, boolean lowCarbOnly, boolean quickPrepOnly) {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Lunch)
                .map(meal -> (Lunch) meal)
                .filter(lunch -> dietaryType == null || lunch.getDietaryType().equalsIgnoreCase(dietaryType))
                .filter(lunch -> !lowCarbOnly || lunch.isLowCarb())
                .filter(lunch -> !quickPrepOnly || lunch.isQuickPrep())
                .collect(Collectors.toList());
    }
    
    /**
     * Suggests dinner options based on dietary type
     */
    public List<Dinner> suggestDinners(String dietaryType, boolean heartHealthyOnly, boolean comfortFoodOnly) {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Dinner)
                .map(meal -> (Dinner) meal)
                .filter(dinner -> dietaryType == null || dinner.getDietaryType().equalsIgnoreCase(dietaryType))
                .filter(dinner -> !heartHealthyOnly || dinner.isHeartHealthy())
                .filter(dinner -> !comfortFoodOnly || dinner.isComfortFood())
                .collect(Collectors.toList());
    }
    
    /**
     * Suggests snack options based on dietary type
     */
    public List<Snack> suggestSnacks(String dietaryType, boolean lowCalorieOnly, boolean portableOnly) {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Snack)
                .map(meal -> (Snack) meal)
                .filter(snack -> dietaryType == null || snack.getDietaryType().equalsIgnoreCase(dietaryType))
                .filter(snack -> !lowCalorieOnly || snack.isLowCalorie())
                .filter(snack -> !portableOnly || snack.isPortable())
                .collect(Collectors.toList());
    }
    
    /**
     * Suggests meals based on calorie target
     */
    public List<Meal> suggestMealsByCalories(double minCalories, double maxCalories) {
        return mealDatabase.stream()
                .filter(meal -> meal.getCalories() >= minCalories && meal.getCalories() <= maxCalories)
                .collect(Collectors.toList());
    }
    
    /**
     * Suggests meals based on protein target
     */
    public List<Meal> suggestMealsByProtein(double minProtein) {
        return mealDatabase.stream()
                .filter(meal -> meal.getProtein() >= minProtein)
                .collect(Collectors.toList());
    }
    
    /**
     * Suggests meals that are heart-healthy
     */
    public List<Meal> suggestHeartHealthyMeals() {
        return mealDatabase.stream()
                .filter(meal -> nutritionAnalyzer.isHeartHealthy(meal))
                .collect(Collectors.toList());
    }
    
    /**
     * Suggests meals that are low-carb
     */
    public List<Meal> suggestLowCarbMeals() {
        return mealDatabase.stream()
                .filter(meal -> nutritionAnalyzer.isLowCarb(meal))
                .collect(Collectors.toList());
    }
    
    /**
     * Suggests meals based on user's nutritional goals
     * @param nutritionalGoals Map containing daily nutritional goals
     * @param mealType Type of meal to suggest (breakfast, lunch, dinner, snack)
     * @param dietaryType Dietary preference (vegetarian, keto, etc.)
     * @return List of meals that fit the nutritional goals
     */
    public List<Meal> suggestMealsByNutritionalGoals(Map<String, Double> nutritionalGoals,
                                                     String mealType, String dietaryType) {
        if (nutritionalGoals == null || mealType == null) {
            return new ArrayList<>();
        }
        
        // Get target values from nutritional goals
        double targetCalories = nutritionalGoals.getOrDefault("calories", 2000.0);
        double targetProtein = nutritionalGoals.getOrDefault("protein", 50.0);
        double targetCarbs = nutritionalGoals.getOrDefault("carbs", 250.0);
        double targetFat = nutritionalGoals.getOrDefault("fat", 70.0);
        
        // Calculate meal-specific targets (approximate distribution)
        double mealCalorieTarget;
        double mealProteinTarget;
        double mealCarbTarget;
        double mealFatTarget;
        
        switch (mealType.toLowerCase()) {
            case "breakfast":
                mealCalorieTarget = targetCalories * 0.25; // 25% of daily calories
                mealProteinTarget = targetProtein * 0.25;
                mealCarbTarget = targetCarbs * 0.25;
                mealFatTarget = targetFat * 0.25;
                break;
            case "lunch":
                mealCalorieTarget = targetCalories * 0.35; // 35% of daily calories
                mealProteinTarget = targetProtein * 0.35;
                mealCarbTarget = targetCarbs * 0.35;
                mealFatTarget = targetFat * 0.35;
                break;
            case "dinner":
                mealCalorieTarget = targetCalories * 0.30; // 30% of daily calories
                mealProteinTarget = targetProtein * 0.30;
                mealCarbTarget = targetCarbs * 0.30;
                mealFatTarget = targetFat * 0.30;
                break;
            case "snack":
                mealCalorieTarget = targetCalories * 0.10; // 10% of daily calories
                mealProteinTarget = targetProtein * 0.10;
                mealCarbTarget = targetCarbs * 0.10;
                mealFatTarget = targetFat * 0.10;
                break;
            default:
                mealCalorieTarget = targetCalories * 0.25;
                mealProteinTarget = targetProtein * 0.25;
                mealCarbTarget = targetCarbs * 0.25;
                mealFatTarget = targetFat * 0.25;
        }
        
        // Allow for some flexibility in the targets (±20%)
        double minCalories = mealCalorieTarget * 0.8;
        double maxCalories = mealCalorieTarget * 1.2;
        double minProtein = mealProteinTarget * 0.8;
        double maxProtein = mealProteinTarget * 1.2;
        
        // Filter meals based on type, dietary preference, and nutritional targets
        List<Meal> filteredMeals = mealDatabase.stream()
                .filter(meal -> {
                    // Filter by meal type
                    if (mealType.equalsIgnoreCase("breakfast") && !(meal instanceof Breakfast)) return false;
                    if (mealType.equalsIgnoreCase("lunch") && !(meal instanceof Lunch)) return false;
                    if (mealType.equalsIgnoreCase("dinner") && !(meal instanceof Dinner)) return false;
                    if (mealType.equalsIgnoreCase("snack") && !(meal instanceof Snack)) return false;
                    
                    // Filter by dietary type if specified
                    if (dietaryType != null && !meal.getDietaryType().equalsIgnoreCase(dietaryType)) return false;
                    
                    // Filter by nutritional targets
                    return meal.getCalories() >= minCalories && meal.getCalories() <= maxCalories &&
                           meal.getProtein() >= minProtein && meal.getProtein() <= maxProtein;
                })
                .collect(Collectors.toList());
        
        return filteredMeals;
    }
    
    /**
     * Suggests a balanced daily meal plan based on nutritional goals
     * @param nutritionalGoals Map containing daily nutritional goals
     * @param dietaryType Dietary preference (vegetarian, keto, etc.)
     * @return Map containing meals for each meal type
     */
    public Map<String, List<Meal>> suggestDailyMealPlan(Map<String, Double> nutritionalGoals, String dietaryType) {
        Map<String, List<Meal>> mealPlan = new HashMap<>();
        
        // Suggest meals for each meal type
        List<Meal> breakfasts = suggestMealsByNutritionalGoals(nutritionalGoals, "breakfast", dietaryType);
        List<Meal> lunches = suggestMealsByNutritionalGoals(nutritionalGoals, "lunch", dietaryType);
        List<Meal> dinners = suggestMealsByNutritionalGoals(nutritionalGoals, "dinner", dietaryType);
        List<Meal> snacks = suggestMealsByNutritionalGoals(nutritionalGoals, "snack", dietaryType);
        
        // Add to meal plan
        mealPlan.put("breakfast", breakfasts);
        mealPlan.put("lunch", lunches);
        mealPlan.put("dinner", dinners);
        mealPlan.put("snack", snacks);
        
        return mealPlan;
    }
    
    /**
     * Suggests meals based on ingredient availability
     * @param availableIngredients List of available ingredients
     * @return List of meals that can be prepared with available ingredients
     */
    public List<Meal> suggestMealsByIngredients(List<String> availableIngredients) {
        if (availableIngredients == null || availableIngredients.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Convert ingredient names to lowercase for case-insensitive comparison
        List<String> lowerCaseIngredients = availableIngredients.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        
        return mealDatabase.stream()
                .filter(meal -> {
                    // Get all ingredients in the meal
                    List<String> mealIngredientNames = meal.getIngredients().stream()
                            .map(ingredient -> ingredient.getName().toLowerCase())
                            .collect(Collectors.toList());
                    
                    // Check if all meal ingredients are in the available ingredients list
                    return lowerCaseIngredients.containsAll(mealIngredientNames);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Suggests meals that match a specific search term in name or description
     * @param searchTerm Term to search for
     * @return List of meals matching the search term
     */
    public List<Meal> searchMealsByTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        
        return mealDatabase.stream()
                .filter(meal -> {
                    String name = meal.getName() != null ? meal.getName().toLowerCase() : "";
                    String description = meal.getDescription() != null ? meal.getDescription().toLowerCase() : "";
                    
                    return name.contains(lowerCaseSearchTerm) || description.contains(lowerCaseSearchTerm);
                })
                .collect(Collectors.toList());
    }
}