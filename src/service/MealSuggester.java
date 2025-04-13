package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MealSuggester {
    private List<Meal> mealDatabase;
    private NutritionAnalyzer nutritionAnalyzer;

    public MealSuggester() {
        this.mealDatabase = new ArrayList<>();
        this.nutritionAnalyzer = new NutritionAnalyzer();
    }

    public MealSuggester(List<Meal> mealDatabase) {
        this.mealDatabase = mealDatabase;
        this.nutritionAnalyzer = new NutritionAnalyzer();
    }

    public void addMeal(Meal meal) {
        mealDatabase.add(meal);
    }

    public boolean removeMeal(Meal meal) {
        return mealDatabase.remove(meal);
    }

    public List<Meal> getAllMeals() {
        return new ArrayList<>(mealDatabase);
    }

    public List<Breakfast> suggestBreakfasts(String dietaryType, boolean highProteinOnly, boolean caffeineFree) {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Breakfast)
                .map(meal -> (Breakfast) meal)
                .filter(breakfast -> dietaryType == null || breakfast.getDietaryType().equalsIgnoreCase(dietaryType))
                .filter(breakfast -> !highProteinOnly || breakfast.isHighProtein())
                .filter(breakfast -> !caffeineFree || !breakfast.containsCaffeine())
                .collect(Collectors.toList());
    }

    public List<Lunch> suggestLunches(String dietaryType, boolean lowCarbOnly, boolean quickPrepOnly) {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Lunch)
                .map(meal -> (Lunch) meal)
                .filter(lunch -> dietaryType == null || lunch.getDietaryType().equalsIgnoreCase(dietaryType))
                .filter(lunch -> !lowCarbOnly || lunch.isLowCarb())
                .filter(lunch -> !quickPrepOnly || lunch.isQuickPrep())
                .collect(Collectors.toList());
    }

    public List<Dinner> suggestDinners(String dietaryType, boolean heartHealthyOnly, boolean comfortFoodOnly) {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Dinner)
                .map(meal -> (Dinner) meal)
                .filter(dinner -> dietaryType == null || dinner.getDietaryType().equalsIgnoreCase(dietaryType))
                .filter(dinner -> !heartHealthyOnly || dinner.isHeartHealthy())
                .filter(dinner -> !comfortFoodOnly || dinner.isComfortFood())
                .collect(Collectors.toList());
    }

    public List<Snack> suggestSnacks(String dietaryType, boolean lowCalorieOnly, boolean portableOnly) {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Snack)
                .map(meal -> (Snack) meal)
                .filter(snack -> dietaryType == null || snack.getDietaryType().equalsIgnoreCase(dietaryType))
                .filter(snack -> !lowCalorieOnly || snack.isLowCalorie())
                .filter(snack -> !portableOnly || snack.isPortable())
                .collect(Collectors.toList());
    }

    public List<Meal> suggestMealsByCalories(double minCalories, double maxCalories) {
        return mealDatabase.stream()
                .filter(meal -> meal.getCalories() >= minCalories && meal.getCalories() <= maxCalories)
                .collect(Collectors.toList());
    }

    public List<Meal> suggestMealsByProtein(double minProtein) {
        return mealDatabase.stream()
                .filter(meal -> meal.getProtein() >= minProtein)
                .collect(Collectors.toList());
    }

    public List<Meal> suggestHeartHealthyMeals() {
        return mealDatabase.stream()
                .filter(meal -> nutritionAnalyzer.isHeartHealthy(meal))
                .collect(Collectors.toList());
    }

    public List<Meal> suggestLowCarbMeals() {
        return mealDatabase.stream()
                .filter(meal -> nutritionAnalyzer.isLowCarb(meal))
                .collect(Collectors.toList());
    }

    public List<Meal> suggestMealsByNutritionalGoals(Map<String, Double> nutritionalGoals,
                                                     String mealType, String dietaryType) {
        if (nutritionalGoals == null || mealType == null) {
            return new ArrayList<>();
        }

        double targetCalories = nutritionalGoals.getOrDefault("calories", 2000.0);
        double targetProtein = nutritionalGoals.getOrDefault("protein", 50.0);
        double targetCarbs = nutritionalGoals.getOrDefault("carbs", 250.0);
        double targetFat = nutritionalGoals.getOrDefault("fat", 70.0);

        double mealCalorieTarget;
        double mealProteinTarget;
        double mealCarbTarget;
        double mealFatTarget;

        switch (mealType.toLowerCase()) {
            case "breakfast":
                mealCalorieTarget = targetCalories * 0.25;
                mealProteinTarget = targetProtein * 0.25;
                mealCarbTarget = targetCarbs * 0.25;
                mealFatTarget = targetFat * 0.25;
                break;
            case "lunch":
                mealCalorieTarget = targetCalories * 0.35;
                mealProteinTarget = targetProtein * 0.35;
                mealCarbTarget = targetCarbs * 0.35;
                mealFatTarget = targetFat * 0.35;
                break;
            case "dinner":
                mealCalorieTarget = targetCalories * 0.30;
                mealProteinTarget = targetProtein * 0.30;
                mealCarbTarget = targetCarbs * 0.30;
                mealFatTarget = targetFat * 0.30;
                break;
            case "snack":
                mealCalorieTarget = targetCalories * 0.10;
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

        // Acceptable ranges for calories and protein
        double minCalories = mealCalorieTarget * 0.8;
        double maxCalories = mealCalorieTarget * 1.2;
        double minProtein = mealProteinTarget * 0.8;
        double maxProtein = mealProteinTarget * 1.2;

        List<Meal> filteredMeals = mealDatabase.stream()
                .filter(meal -> {
                    if (mealType.equalsIgnoreCase("breakfast") && !(meal instanceof Breakfast)) return false;
                    if (mealType.equalsIgnoreCase("lunch") && !(meal instanceof Lunch)) return false;
                    if (mealType.equalsIgnoreCase("dinner") && !(meal instanceof Dinner)) return false;
                    if (mealType.equalsIgnoreCase("snack") && !(meal instanceof Snack)) return false;

                    if (dietaryType != null && !meal.getDietaryType().equalsIgnoreCase(dietaryType)) return false;

                    return meal.getCalories() >= minCalories && meal.getCalories() <= maxCalories &&
                            meal.getProtein() >= minProtein && meal.getProtein() <= maxProtein;
                })
                .collect(Collectors.toList());

        return filteredMeals;
    }

    public Map<String, List<Meal>> suggestDailyMealPlan(Map<String, Double> nutritionalGoals, String dietaryType) {
        Map<String, List<Meal>> mealPlan = new HashMap<>();

        List<Meal> breakfasts = suggestMealsByNutritionalGoals(nutritionalGoals, "breakfast", dietaryType);
        List<Meal> lunches = suggestMealsByNutritionalGoals(nutritionalGoals, "lunch", dietaryType);
        List<Meal> dinners = suggestMealsByNutritionalGoals(nutritionalGoals, "dinner", dietaryType);
        List<Meal> snacks = suggestMealsByNutritionalGoals(nutritionalGoals, "snack", dietaryType);

        mealPlan.put("breakfast", breakfasts);
        mealPlan.put("lunch", lunches);
        mealPlan.put("dinner", dinners);
        mealPlan.put("snack", snacks);

        return mealPlan;
    }

    public List<Meal> suggestMealsByIngredients(List<String> availableIngredients) {
        if (availableIngredients == null || availableIngredients.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> lowerCaseIngredients = availableIngredients.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return mealDatabase.stream()
                .filter(meal -> {
                    List<String> mealIngredientNames = meal.getIngredients().stream()
                            .map(ingredient -> ingredient.getName().toLowerCase())
                            .collect(Collectors.toList());

                    return lowerCaseIngredients.containsAll(mealIngredientNames);
                })
                .collect(Collectors.toList());
    }

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

    /**
     * Gets all breakfast meals
     * @return List of all breakfast meals
     */
    public List<Breakfast> getAllBreakfasts() {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Breakfast)
                .map(meal -> (Breakfast) meal)
                .collect(Collectors.toList());
    }

    /**
     * Gets all lunch meals
     * @return List of all lunch meals
     */
    public List<Lunch> getAllLunches() {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Lunch)
                .map(meal -> (Lunch) meal)
                .collect(Collectors.toList());
    }

    /**
     * Gets all dinner meals
     * @return List of all dinner meals
     */
    public List<Dinner> getAllDinners() {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Dinner)
                .map(meal -> (Dinner) meal)
                .collect(Collectors.toList());
    }

    /**
     * Gets all snack meals
     * @return List of all snack meals
     */
    public List<Snack> getAllSnacks() {
        return mealDatabase.stream()
                .filter(meal -> meal instanceof Snack)
                .map(meal -> (Snack) meal)
                .collect(Collectors.toList());
    }
}