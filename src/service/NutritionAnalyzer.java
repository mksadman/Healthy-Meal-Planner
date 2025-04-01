package service;

import model.Meal;
import model.Ingredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for analyzing nutritional content of meals and ingredients.
 */
public class NutritionAnalyzer {
    
    /**
     * Calculates the total calories for a meal
     */
    public double calculateTotalCalories(Meal meal) {
        return meal.getCalories();
    }
    
    /**
     * Calculates the protein to carb ratio for a meal
     */
    public double calculateProteinCarbRatio(Meal meal) {
        if (meal.getCarbs() == 0) {
            return 0; // Avoid division by zero
        }
        return meal.getProtein() / meal.getCarbs();
    }
    
    /**
     * Determines if a meal is considered low-calorie (under 300 calories)
     */
    public boolean isLowCalorie(Meal meal) {
        return meal.getCalories() < 300;
    }
    
    /**
     * Determines if a meal is considered high-protein (over 20g protein)
     */
    public boolean isHighProtein(Meal meal) {
        return meal.getProtein() > 20;
    }
    
    /**
     * Determines if a meal is considered low-carb (under 20g carbs)
     */
    public boolean isLowCarb(Meal meal) {
        return meal.getCarbs() < 20;
    }
    
    /**
     * Determines if a meal is considered heart-healthy (low fat, high fiber)
     * Note: This is a simplified version. In reality, more factors would be considered.
     */
    public boolean isHeartHealthy(Meal meal) {
        return meal.getFat() < 10;
    }
    
    /**
     * Provides a nutritional summary of a meal
     */
    public String getNutritionalSummary(Meal meal) {
        StringBuilder summary = new StringBuilder();
        summary.append("Nutritional Summary for ").append(meal.getName()).append(":\n");
        summary.append("Calories: ").append(meal.getCalories()).append(" cal\n");
        summary.append("Protein: ").append(meal.getProtein()).append("g\n");
        summary.append("Carbs: ").append(meal.getCarbs()).append("g\n");
        summary.append("Fat: ").append(meal.getFat()).append("g\n");
        
        // Add qualitative assessments
        if (isLowCalorie(meal)) {
            summary.append("This is a low-calorie meal.\n");
        }
        if (isHighProtein(meal)) {
            summary.append("This is a high-protein meal.\n");
        }
        if (isLowCarb(meal)) {
            summary.append("This is a low-carb meal.\n");
        }
        if (isHeartHealthy(meal)) {
            summary.append("This is a heart-healthy meal.\n");
        }
        
        return summary.toString();
    }
    
    /**
     * Analyzes the nutritional content of a weekly meal plan
     * @param weeklyPlan Map containing daily meal plans
     * @return A summary of the weekly nutritional intake
     */
    public String analyzeWeeklyPlan(Map<String, List<Meal>> weeklyPlan) {
        if (weeklyPlan == null || weeklyPlan.isEmpty()) {
            return "No meal plan data available for analysis.";
        }
        
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        int totalMeals = 0;
        
        StringBuilder analysis = new StringBuilder("WEEKLY NUTRITION ANALYSIS\n========================\n\n");
        
        // Analyze each day
        for (String day : weeklyPlan.keySet()) {
            List<Meal> dayMeals = weeklyPlan.get(day);
            double dayCalories = 0;
            double dayProtein = 0;
            double dayCarbs = 0;
            double dayFat = 0;
            
            for (Meal meal : dayMeals) {
                dayCalories += meal.getCalories();
                dayProtein += meal.getProtein();
                dayCarbs += meal.getCarbs();
                dayFat += meal.getFat();
                totalMeals++;
            }
            
            analysis.append(day.toUpperCase()).append(":\n");
            analysis.append(String.format("Calories: %.1f cal, Protein: %.1fg, Carbs: %.1fg, Fat: %.1fg\n\n", 
                    dayCalories, dayProtein, dayCarbs, dayFat));
            
            totalCalories += dayCalories;
            totalProtein += dayProtein;
            totalCarbs += dayCarbs;
            totalFat += dayFat;
        }
        
        // Add weekly summary
        analysis.append("WEEKLY TOTALS:\n");
        analysis.append(String.format("Total Calories: %.1f cal\n", totalCalories));
        analysis.append(String.format("Total Protein: %.1fg\n", totalProtein));
        analysis.append(String.format("Total Carbs: %.1fg\n", totalCarbs));
        analysis.append(String.format("Total Fat: %.1fg\n\n", totalFat));
        
        // Add daily averages
        int days = weeklyPlan.size();
        analysis.append("DAILY AVERAGES:\n");
        analysis.append(String.format("Average Calories: %.1f cal\n", totalCalories / days));
        analysis.append(String.format("Average Protein: %.1fg\n", totalProtein / days));
        analysis.append(String.format("Average Carbs: %.1fg\n", totalCarbs / days));
        analysis.append(String.format("Average Fat: %.1fg\n", totalFat / days));
        
        return analysis.toString();
    }
    
    /**
     * Calculates daily nutritional goals based on user profile
     * @param weight User's weight in kg
     * @param height User's height in cm
     * @param age User's age in years
     * @param gender User's gender ("male" or "female")
     * @param activityLevel Activity level (1-5, where 1 is sedentary and 5 is very active)
     * @param goal Weight goal ("maintain", "lose", or "gain")
     * @return Map containing daily nutritional goals
     */
    public Map<String, Double> calculateDailyNutritionalGoals(double weight, double height, 
                                                            int age, String gender, 
                                                            int activityLevel, String goal) {
        Map<String, Double> nutritionalGoals = new HashMap<>();
        
        // Calculate Basal Metabolic Rate (BMR) using Mifflin-St Jeor Equation
        double bmr;
        if (gender.equalsIgnoreCase("male")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        
        // Apply activity multiplier
        double activityMultiplier;
        switch (activityLevel) {
            case 1: activityMultiplier = 1.2; break;  // Sedentary
            case 2: activityMultiplier = 1.375; break; // Lightly active
            case 3: activityMultiplier = 1.55; break;  // Moderately active
            case 4: activityMultiplier = 1.725; break; // Very active
            case 5: activityMultiplier = 1.9; break;   // Extra active
            default: activityMultiplier = 1.2;
        }
        
        double maintenanceCalories = bmr * activityMultiplier;
        
        // Adjust calories based on goal
        double targetCalories;
        if (goal.equalsIgnoreCase("lose")) {
            targetCalories = maintenanceCalories - 500; // 500 calorie deficit for weight loss
        } else if (goal.equalsIgnoreCase("gain")) {
            targetCalories = maintenanceCalories + 500; // 500 calorie surplus for weight gain
        } else {
            targetCalories = maintenanceCalories; // Maintain weight
        }
        
        // Calculate macronutrient goals (using standard ratios)
        // Protein: 30%, Carbs: 40%, Fat: 30% of total calories
        double proteinCalories = targetCalories * 0.3;
        double carbCalories = targetCalories * 0.4;
        double fatCalories = targetCalories * 0.3;
        
        // Convert calories to grams
        // 1g protein = 4 calories, 1g carbs = 4 calories, 1g fat = 9 calories
        double proteinGrams = proteinCalories / 4;
        double carbGrams = carbCalories / 4;
        double fatGrams = fatCalories / 9;
        
        // Store results
        nutritionalGoals.put("calories", targetCalories);
        nutritionalGoals.put("protein", proteinGrams);
        nutritionalGoals.put("carbs", carbGrams);
        nutritionalGoals.put("fat", fatGrams);
        
        return nutritionalGoals;
    }
    
    /**
     * Analyzes a meal's nutritional content in detail
     * @param meal The meal to analyze
     * @return Detailed nutritional analysis
     */
    public String getDetailedNutritionalAnalysis(Meal meal) {
        if (meal == null) {
            return "No meal data available for analysis.";
        }
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("DETAILED NUTRITIONAL ANALYSIS FOR ").append(meal.getName().toUpperCase()).append("\n");
        analysis.append("==========================================================\n\n");
        
        // Basic nutritional information
        analysis.append("BASIC NUTRITION:\n");
        analysis.append(String.format("Calories: %.1f cal\n", meal.getCalories()));
        analysis.append(String.format("Protein: %.1fg\n", meal.getProtein()));
        analysis.append(String.format("Carbs: %.1fg\n", meal.getCarbs()));
        analysis.append(String.format("Fat: %.1fg\n\n", meal.getFat()));
        
        // Macronutrient ratios
        double totalMacros = meal.getProtein() + meal.getCarbs() + meal.getFat();
        if (totalMacros > 0) {
            double proteinPercentage = (meal.getProtein() / totalMacros) * 100;
            double carbsPercentage = (meal.getCarbs() / totalMacros) * 100;
            double fatPercentage = (meal.getFat() / totalMacros) * 100;
            
            analysis.append("MACRONUTRIENT RATIOS:\n");
            analysis.append(String.format("Protein: %.1f%%\n", proteinPercentage));
            analysis.append(String.format("Carbs: %.1f%%\n", carbsPercentage));
            analysis.append(String.format("Fat: %.1f%%\n\n", fatPercentage));
        }
        
        // Calorie sources
        double proteinCalories = meal.getProtein() * 4; // 4 calories per gram of protein
        double carbCalories = meal.getCarbs() * 4;      // 4 calories per gram of carbs
        double fatCalories = meal.getFat() * 9;         // 9 calories per gram of fat
        
        analysis.append("CALORIE SOURCES:\n");
        analysis.append(String.format("From Protein: %.1f cal (%.1f%%)\n", 
                proteinCalories, (proteinCalories / meal.getCalories()) * 100));
        analysis.append(String.format("From Carbs: %.1f cal (%.1f%%)\n", 
                carbCalories, (carbCalories / meal.getCalories()) * 100));
        analysis.append(String.format("From Fat: %.1f cal (%.1f%%)\n\n", 
                fatCalories, (fatCalories / meal.getCalories()) * 100));
        
        // Nutritional assessment
        analysis.append("NUTRITIONAL ASSESSMENT:\n");
        if (isLowCalorie(meal)) {
            analysis.append("- This is a low-calorie meal (under 300 calories).\n");
        }
        if (isHighProtein(meal)) {
            analysis.append("- This is a high-protein meal (over 20g protein).\n");
        }
        if (isLowCarb(meal)) {
            analysis.append("- This is a low-carb meal (under 20g carbs).\n");
        }
        if (isHeartHealthy(meal)) {
            analysis.append("- This is a heart-healthy meal (low in fat).\n");
        }
        
        // Protein to carb ratio
        double proteinCarbRatio = calculateProteinCarbRatio(meal);
        analysis.append(String.format("- Protein to carb ratio: %.2f\n", proteinCarbRatio));
        
        if (proteinCarbRatio > 1) {
            analysis.append("  (High protein relative to carbs, suitable for low-carb diets)\n");
        } else if (proteinCarbRatio > 0.5) {
            analysis.append("  (Balanced protein and carbs, suitable for balanced diets)\n");
        } else {
            analysis.append("  (Higher in carbs relative to protein, suitable for high-energy needs)\n");
        }
        
        return analysis.toString();
    }
    
    /**
     * Compares a meal's nutrition to daily goals
     * @param meal The meal to analyze
     * @param dailyGoals Map containing daily nutritional goals
     * @return Analysis of how the meal contributes to daily goals
     */
    public String compareMealToDailyGoals(Meal meal, Map<String, Double> dailyGoals) {
        if (meal == null || dailyGoals == null) {
            return "Cannot compare meal to goals: missing data.";
        }
        
        StringBuilder comparison = new StringBuilder();
        comparison.append("MEAL CONTRIBUTION TO DAILY GOALS\n");
        comparison.append("================================\n\n");
        
        // Get daily goals
        double calorieGoal = dailyGoals.getOrDefault("calories", 2000.0);
        double proteinGoal = dailyGoals.getOrDefault("protein", 50.0);
        double carbGoal = dailyGoals.getOrDefault("carbs", 250.0);
        double fatGoal = dailyGoals.getOrDefault("fat", 70.0);
        
        // Calculate percentages
        double caloriePercentage = (meal.getCalories() / calorieGoal) * 100;
        double proteinPercentage = (meal.getProtein() / proteinGoal) * 100;
        double carbPercentage = (meal.getCarbs() / carbGoal) * 100;
        double fatPercentage = (meal.getFat() / fatGoal) * 100;
        
        // Add to comparison
        comparison.append(String.format("Calories: %.1f of %.1f cal (%.1f%%)\n", 
                meal.getCalories(), calorieGoal, caloriePercentage));
        comparison.append(String.format("Protein: %.1fg of %.1fg (%.1f%%)\n", 
                meal.getProtein(), proteinGoal, proteinPercentage));
        comparison.append(String.format("Carbs: %.1fg of %.1fg (%.1f%%)\n", 
                meal.getCarbs(), carbGoal, carbPercentage));
        comparison.append(String.format("Fat: %.1fg of %.1fg (%.1f%%)\n\n", 
                meal.getFat(), fatGoal, fatPercentage));
        
        // Add assessment
        comparison.append("ASSESSMENT:\n");
        if (caloriePercentage > 40) {
            comparison.append("- This meal provides a significant portion of your daily calories.\n");
        }
        if (proteinPercentage > 30) {
            comparison.append("- This meal is high in protein relative to your daily goal.\n");
        }
        if (carbPercentage > 30) {
            comparison.append("- This meal is high in carbs relative to your daily goal.\n");
        }
        if (fatPercentage > 30) {
            comparison.append("- This meal is high in fat relative to your daily goal.\n");
        }
        
        return comparison.toString();
    }
    
    /**
     * Calculates the macronutrient distribution (protein, carbs, fat) as percentages for a meal
     * @param meal The meal to analyze
     * @return Map containing the percentage distribution of each macronutrient
     */
    public Map<String, Double> calculateMacronutrientDistribution(Meal meal) {
        if (meal == null) {
            throw new IllegalArgumentException("Meal cannot be null");
        }
        
        Map<String, Double> macroDistribution = new HashMap<>();
        
        // Calculate total macronutrients in grams
        double totalMacros = meal.getProtein() + meal.getCarbs() + meal.getFat();
        
        if (totalMacros <= 0) {
            // Handle case where there are no macronutrients
            macroDistribution.put("protein", 0.0);
            macroDistribution.put("carbs", 0.0);
            macroDistribution.put("fat", 0.0);
            return macroDistribution;
        }
        
        // Calculate percentage distribution
        double proteinPercentage = (meal.getProtein() / totalMacros) * 100;
        double carbsPercentage = (meal.getCarbs() / totalMacros) * 100;
        double fatPercentage = (meal.getFat() / totalMacros) * 100;
        
        // Store results rounded to one decimal place
        macroDistribution.put("protein", Math.round(proteinPercentage * 10) / 10.0);
        macroDistribution.put("carbs", Math.round(carbsPercentage * 10) / 10.0);
        macroDistribution.put("fat", Math.round(fatPercentage * 10) / 10.0);
        
        return macroDistribution;
    }
    
    /**
     * Calculates the macronutrient distribution based on daily calorie needs
     * @param dailyCalories The daily calorie needs
     * @return Map containing macronutrient information in grams and calories
     */
    public Map<String, Double> calculateMacronutrientDistribution(double dailyCalories) {
        if (dailyCalories <= 0) {
            throw new IllegalArgumentException("Daily calories must be a positive value");
        }
        
        Map<String, Double> macros = new HashMap<>();
        
        // Standard macronutrient distribution
        // Protein: 30%, Carbs: 40%, Fat: 30% of total calories
        double proteinCalories = dailyCalories * 0.3;
        double carbCalories = dailyCalories * 0.4;
        double fatCalories = dailyCalories * 0.3;
        
        // Convert calories to grams
        // 1g protein = 4 calories, 1g carbs = 4 calories, 1g fat = 9 calories
        double proteinGrams = proteinCalories / 4;
        double carbGrams = carbCalories / 4;
        double fatGrams = fatCalories / 9;
        
        // Store results
        macros.put("proteinCalories", proteinCalories);
        macros.put("carbCalories", carbCalories);
        macros.put("fatCalories", fatCalories);
        macros.put("proteinGrams", proteinGrams);
        macros.put("carbGrams", carbGrams);
        macros.put("fatGrams", fatGrams);
        
        return macros;
    }
    
    /**
     * Calculates the daily calorie needs for a user based on their profile information
     * @param weight User's weight in kg
     * @param height User's height in cm
     * @param age User's age in years
     * @param gender User's gender ("male" or "female")
     * @param activityLevel Activity level (1-5, where 1 is sedentary and 5 is very active)
     * @param weightGoal Weight goal ("maintain", "lose", or "gain")
     * @return The calculated daily calorie needs in calories
     */
    public double calculateDailyCalorieNeeds(double weight, double height, int age, String gender, int activityLevel, String weightGoal) {
        if (weight <= 0 || height <= 0 || age <= 0) {
            throw new IllegalArgumentException("Weight, height, and age must be positive values");
        }
        
        // Used Mifflin St Jeor Equation for calculating BMR
        double bmr;
        if (gender.equalsIgnoreCase("male")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        
        // Apply activity multiplier
        double activityMultiplier;
        switch (activityLevel) {
            case 1: activityMultiplier = 1.2; break;  // Sedentary
            case 2: activityMultiplier = 1.375; break; // Lightly active
            case 3: activityMultiplier = 1.55; break;  // Moderately active
            case 4: activityMultiplier = 1.725; break; // Very active
            case 5: activityMultiplier = 1.9; break;   // Extra active
            default: activityMultiplier = 1.2;
        }
        
        // Calculate maintenance calories
        double maintenanceCalories = bmr * activityMultiplier;
        
        // Adjust based on weight goal
        if (weightGoal.equalsIgnoreCase("lose")) {
            return maintenanceCalories - 500; // 500 calorie deficit for weight loss
        } else if (weightGoal.equalsIgnoreCase("gain")) {
            return maintenanceCalories + 500; // 500 calorie surplus for weight gain
        } else {
            return maintenanceCalories; // Maintain weight
        }
    }
}