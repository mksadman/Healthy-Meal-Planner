package service;

import model.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class WeeklyPlanGenerator {
    private MealSuggester mealSuggester;
    private Map<String, List<Meal>> weeklyPlan;
    private Map<String, Set<String>> usedMealNames;
    private Map<String, String> previousDayMeals;

    public WeeklyPlanGenerator(MealSuggester mealSuggester) {
         this.mealSuggester = mealSuggester;
         this.weeklyPlan = new HashMap<>();
         this.usedMealNames = new HashMap<>();
         this.previousDayMeals = new HashMap<>();
         initializeEmptyPlan();
         initializeUsedMealTracker();
     }

    private void initializeUsedMealTracker() {
        usedMealNames.put("Breakfast", new HashSet<>());
        usedMealNames.put("Lunch", new HashSet<>());
        usedMealNames.put("Dinner", new HashSet<>());
        usedMealNames.put("Snack", new HashSet<>());

        previousDayMeals.put("Lunch", "");
        previousDayMeals.put("Dinner", "");
    }

     private void initializeEmptyPlan() {
         String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
         for (String day : daysOfWeek) {
             weeklyPlan.put(day, new ArrayList<>());
         }
     }

     public void generateWeeklyPlan(String dietaryType, double dailyCalorieTarget) {
         initializeEmptyPlan();
         initializeUsedMealTracker();

         String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
         for (String day : daysOfWeek) {
             // Get meal options
             List<Breakfast> breakfastOptions = mealSuggester.suggestBreakfasts(dietaryType, false, false);
             List<Lunch> lunchOptions = mealSuggester.suggestLunches(dietaryType, false);
             List<Dinner> dinnerOptions = mealSuggester.suggestDinners(dietaryType, false, false);
             List<Snack> snackOptions = mealSuggester.suggestSnacks(dietaryType, false, false);

             // Filter out already used meals for variety
             List<Breakfast> filteredBreakfasts = breakfastOptions.stream()
                     .filter(b -> !usedMealNames.get("Breakfast").contains(b.getName()))
                     .collect(Collectors.toList());
             if (filteredBreakfasts.isEmpty()) filteredBreakfasts = breakfastOptions;

             List<Lunch> filteredLunches = lunchOptions.stream()
                     .filter(l -> !usedMealNames.get("Lunch").contains(l.getName()) && !l.getName().equals(previousDayMeals.get("Lunch")))
                     .collect(Collectors.toList());
             if (filteredLunches.isEmpty()) filteredLunches = lunchOptions.stream().filter(l -> !l.getName().equals(previousDayMeals.get("Lunch"))).collect(Collectors.toList());
             if (filteredLunches.isEmpty()) filteredLunches = lunchOptions;

             List<Dinner> filteredDinners = dinnerOptions.stream()
                     .filter(d -> !usedMealNames.get("Dinner").contains(d.getName()) && !d.getName().equals(previousDayMeals.get("Dinner")))
                     .collect(Collectors.toList());
             if (filteredDinners.isEmpty()) filteredDinners = dinnerOptions.stream().filter(d -> !d.getName().equals(previousDayMeals.get("Dinner"))).collect(Collectors.toList());
             if (filteredDinners.isEmpty()) filteredDinners = dinnerOptions;

             // Try combinations to get closest to calorie target
             MealCombination bestCombo = null;
             double minDiff = Double.MAX_VALUE;
             int breakfastLimit = Math.min(3, filteredBreakfasts.size());
             int lunchLimit = Math.min(3, filteredLunches.size());
             int dinnerLimit = Math.min(3, filteredDinners.size());
             for (int i = 0; i < breakfastLimit; i++) {
                 for (int j = 0; j < lunchLimit; j++) {
                     for (int k = 0; k < dinnerLimit; k++) {
                         Breakfast b = filteredBreakfasts.get(i);
                         Lunch l = filteredLunches.get(j);
                         Dinner d = filteredDinners.get(k);
                         double baseCalories = b.getCalories() + l.getCalories() + d.getCalories();
                         // Try with 1 or 2 snacks
                         List<Snack> filteredSnacks = snackOptions.stream().filter(s -> !usedMealNames.get("Snack").contains(s.getName())).collect(Collectors.toList());
                         if (filteredSnacks.isEmpty()) filteredSnacks = snackOptions;
                         for (int s1 = 0; s1 < Math.min(2, filteredSnacks.size()); s1++) {
                             double totalCalories = baseCalories + filteredSnacks.get(s1).getCalories();
                             double diff = Math.abs(totalCalories - dailyCalorieTarget);
                             if (diff < minDiff) {
                                 minDiff = diff;
                                 bestCombo = new MealCombination(b, l, d, Arrays.asList(filteredSnacks.get(s1)));
                             }
                             // Try with two snacks if possible
                             for (int s2 = s1 + 1; s2 < Math.min(2, filteredSnacks.size()); s2++) {
                                 double totalCalories2 = baseCalories + filteredSnacks.get(s1).getCalories() + filteredSnacks.get(s2).getCalories();
                                 double diff2 = Math.abs(totalCalories2 - dailyCalorieTarget);
                                 if (diff2 < minDiff) {
                                     minDiff = diff2;
                                     bestCombo = new MealCombination(b, l, d, Arrays.asList(filteredSnacks.get(s1), filteredSnacks.get(s2)));
                                 }
                             }
                         }
                     }
                 }
             }
             // Fallback if not enough options
             if (bestCombo == null) {
                 Breakfast b = filteredBreakfasts.isEmpty() ? null : filteredBreakfasts.get(0);
                 Lunch l = filteredLunches.isEmpty() ? null : filteredLunches.get(0);
                 Dinner d = filteredDinners.isEmpty() ? null : filteredDinners.get(0);
                 List<Snack> snacks = snackOptions.isEmpty() ? new ArrayList<>() : Arrays.asList(snackOptions.get(0));
                 bestCombo = new MealCombination(b, l, d, snacks);
             }
             // Add selected meals to plan
             List<Meal> mealsForDay = weeklyPlan.get(day);
             if (bestCombo.breakfast != null) {
                 mealsForDay.add(bestCombo.breakfast);
                 usedMealNames.get("Breakfast").add(bestCombo.breakfast.getName());
             }
             if (bestCombo.lunch != null) {
                 mealsForDay.add(bestCombo.lunch);
                 usedMealNames.get("Lunch").add(bestCombo.lunch.getName());
                 previousDayMeals.put("Lunch", bestCombo.lunch.getName());
             }
             if (bestCombo.dinner != null) {
                 mealsForDay.add(bestCombo.dinner);
                 usedMealNames.get("Dinner").add(bestCombo.dinner.getName());
                 previousDayMeals.put("Dinner", bestCombo.dinner.getName());
             }
             for (Snack snack : bestCombo.snacks) {
                 mealsForDay.add(snack);
                 usedMealNames.get("Snack").add(snack.getName());
             }
             limitUsedMealSets(2);
         }
     }

     // Helper class for meal combinations
     private static class MealCombination {
         Breakfast breakfast;
         Lunch lunch;
         Dinner dinner;
         List<Snack> snacks;
         MealCombination(Breakfast b, Lunch l, Dinner d, List<Snack> snacks) {
             this.breakfast = b;
             this.lunch = l;
             this.dinner = d;
             this.snacks = snacks;
         }
     }

     private void limitUsedMealSets(int maxSize) {
         for (String mealType : usedMealNames.keySet()) {
             Set<String> usedMeals = usedMealNames.get(mealType);
             if (usedMeals.size() > maxSize) {
                 List<String> mealList = new ArrayList<>(usedMeals);
                 usedMealNames.put(mealType, new HashSet<>(mealList.subList(mealList.size() - maxSize, mealList.size())));
             }
         }
     }

     private void adjustDailyCalories(String day, double targetCalories) {
         List<Meal> dayMeals = weeklyPlan.get(day);
         double currentCalories = dayMeals.stream().mapToDouble(Meal::getCalories).sum();

         if (Math.abs(currentCalories - targetCalories) <= targetCalories * 0.1) {
             return;
         }

         if (currentCalories < targetCalories) {
             List<Snack> snackOptions = mealSuggester.suggestSnacks(null, false, false);

             long existingSnackCount = dayMeals.stream()
                     .filter(meal -> meal instanceof Snack)
                     .count();

             while (!snackOptions.isEmpty() && currentCalories < targetCalories * 0.9 && existingSnackCount < 2) {
                 List<Snack> filteredSnacks = snackOptions.stream()
                         .filter(s -> !usedMealNames.get("Snack").contains(s.getName()))
                         .collect(Collectors.toList());

                 if (filteredSnacks.isEmpty()) {
                     filteredSnacks = snackOptions;
                 }

                 Snack snack = getRandomMeal(filteredSnacks);
                 dayMeals.add(snack);
                 currentCalories += snack.getCalories();
                 usedMealNames.get("Snack").add(snack.getName());
                 snackOptions.remove(snack);
                 existingSnackCount++;
             }
         }
         else {
             dayMeals.sort((m1, m2) -> Double.compare(m2.getCalories(), m1.getCalories()));

             for (int i = 0; i < dayMeals.size() && currentCalories > targetCalories * 1.1; i++) {
                 Meal highCalorieMeal = dayMeals.get(i);
                 List<Meal> alternatives;
                 String mealType = null;

                 if (highCalorieMeal instanceof Breakfast) {
                     alternatives = new ArrayList<>(mealSuggester.suggestBreakfasts(null, false, false));
                     mealType = "Breakfast";
                 } else if (highCalorieMeal instanceof Lunch) {
                     alternatives = new ArrayList<>(mealSuggester.suggestLunches(null, false));
                     mealType = "Lunch";
                 } else if (highCalorieMeal instanceof Dinner) {
                     alternatives = new ArrayList<>(mealSuggester.suggestDinners(null, false, false));
                     mealType = "Dinner";
                 } else { // Snack
                     alternatives = new ArrayList<>(mealSuggester.suggestSnacks(null, true, false));
                     mealType = "Snack";
                 }

                 if (mealType != null) {
                     final String finalMealType = mealType;
                     if ("Lunch".equals(finalMealType) || "Dinner".equals(finalMealType)) {
                         final String previousDayMeal = previousDayMeals.get(finalMealType);
                         alternatives = alternatives.stream()
                                 .filter(m -> !usedMealNames.get(finalMealType).contains(m.getName()) &&
                                         !m.getName().equals(previousDayMeal))
                                 .collect(Collectors.toList());
                     } else {
                         alternatives = alternatives.stream()
                                 .filter(m -> !usedMealNames.get(finalMealType).contains(m.getName()))
                                 .collect(Collectors.toList());
                     }

                     if (alternatives.isEmpty()) {
                         if ("Lunch".equals(finalMealType) || "Dinner".equals(finalMealType)) {
                             final String previousDayMeal = previousDayMeals.get(finalMealType);
                             if (highCalorieMeal instanceof Lunch) {
                                 alternatives = new ArrayList<>(mealSuggester.suggestLunches(null, false));
                                 alternatives = alternatives.stream()
                                         .filter(m -> !m.getName().equals(previousDayMeal))
                                         .collect(Collectors.toList());
                             } else if (highCalorieMeal instanceof Dinner) {
                                 alternatives = new ArrayList<>(mealSuggester.suggestDinners(null, false, false));
                                 alternatives = alternatives.stream()
                                         .filter(m -> !m.getName().equals(previousDayMeal))
                                         .collect(Collectors.toList());
                             }

                             if (alternatives.isEmpty()) {
                                 if (highCalorieMeal instanceof Lunch) {
                                     alternatives = new ArrayList<>(mealSuggester.suggestLunches(null, false));
                                 } else if (highCalorieMeal instanceof Dinner) {
                                     alternatives = new ArrayList<>(mealSuggester.suggestDinners(null, false, false));
                                 }
                             }
                         } else {
                             if (highCalorieMeal instanceof Breakfast) {
                                 alternatives = new ArrayList<>(mealSuggester.suggestBreakfasts(null, false, false));
                             } else {
                                 alternatives = new ArrayList<>(mealSuggester.suggestSnacks(null, true, false));
                             }
                         }
                     }
                 }

                 alternatives.sort(Comparator.comparingDouble(Meal::getCalories));

                 if (!alternatives.isEmpty() && alternatives.get(0).getCalories() < highCalorieMeal.getCalories()) {
                     Meal replacement = alternatives.get(0);
                     currentCalories = currentCalories - highCalorieMeal.getCalories() + replacement.getCalories();
                     dayMeals.set(i, replacement);

                     if (mealType != null) {
                         usedMealNames.get(mealType).add(replacement.getName());

                         if (("Lunch".equals(mealType) || "Dinner".equals(mealType)) && replacement instanceof Meal) {
                             previousDayMeals.put(mealType, replacement.getName());
                         }
                     }
                 }
             }
         }
     }

     private <T extends Meal> T getRandomMeal(List<T> options) {
         if (options.isEmpty()) {
             throw new IllegalArgumentException("Cannot get a random meal from an empty list");
         }
         Random random = new Random();
         return options.get(random.nextInt(options.size()));
     }

     public Map<String, List<Meal>> getWeeklyPlan() {
         return new HashMap<>(weeklyPlan);
     }

     public List<Meal> getDayPlan(String day) {
         return new ArrayList<>(weeklyPlan.getOrDefault(day, new ArrayList<>()));
     }

     public double calculateDailyCalories(String day) {
         return weeklyPlan.getOrDefault(day, new ArrayList<>()).stream()
                 .mapToDouble(Meal::getCalories)
                 .sum();
     }

     public double calculateAverageWeeklyCalories() {
         return weeklyPlan.values().stream()
                 .flatMap(List::stream)
                 .mapToDouble(Meal::getCalories)
                 .sum() / 7.0;
     }

     public String getWeeklyPlanSummary() {
         StringBuilder summary = new StringBuilder("WEEKLY MEAL PLAN\n=================\n\n");

         for (String day : Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")) {
             List<Meal> dayMeals = weeklyPlan.get(day);
             summary.append(day.toUpperCase()).append("\n");
             summary.append("Total Calories: ").append(String.format("%.1f", calculateDailyCalories(day))).append("\n\n");

             Map<String, List<Meal>> categorizedMeals = new HashMap<>();
             categorizedMeals.put("Breakfast", new ArrayList<>());
             categorizedMeals.put("Lunch", new ArrayList<>());
             categorizedMeals.put("Dinner", new ArrayList<>());
             categorizedMeals.put("Snack", new ArrayList<>());

             for (Meal meal : dayMeals) {
                 if (meal instanceof Breakfast) {
                     categorizedMeals.get("Breakfast").add(meal);
                 } else if (meal instanceof Lunch) {
                     categorizedMeals.get("Lunch").add(meal);
                 } else if (meal instanceof Dinner) {
                     categorizedMeals.get("Dinner").add(meal);
                 } else if (meal instanceof Snack) {
                     categorizedMeals.get("Snack").add(meal);
                 }
             }

             for (Map.Entry<String, List<Meal>> entry : categorizedMeals.entrySet()) {
                 if (!entry.getValue().isEmpty()) {
                     summary.append(entry.getKey()).append(": ");
                     for (Meal meal : entry.getValue()) {
                         summary.append(meal.getName()).append(" (").append(meal.getCalories()).append(" cal), ");
                     }
                     summary.delete(summary.length() - 2, summary.length()); // Remove trailing comma and space
                     summary.append("\n");
                 }
             }
             summary.append("\n");
         }

         summary.append("Weekly Average: ").append(String.format("%.1f", calculateAverageWeeklyCalories())).append(" calories per day");
         return summary.toString();
     }

     public void generateWeeklyPlanByNutritionalGoals(Map<String, Double> nutritionalGoals, String dietaryType) {
         if (nutritionalGoals == null) {
             throw new IllegalArgumentException("Nutritional goals cannot be null");
         }

         double targetCalories = nutritionalGoals.getOrDefault("calories", 2000.0);
         generateWeeklyPlan(dietaryType, targetCalories);

         double targetProtein = nutritionalGoals.getOrDefault("protein", 50.0);
         double targetCarbs = nutritionalGoals.getOrDefault("carbs", 250.0);
         double targetFat = nutritionalGoals.getOrDefault("fat", 70.0);

         optimizeMacronutrients(targetProtein, targetCarbs, targetFat);
     }

     private void optimizeMacronutrients(double targetProtein, double targetCarbs, double targetFat) {
         for (String day : weeklyPlan.keySet()) {
             List<Meal> dayMeals = weeklyPlan.get(day);

             double currentProtein = dayMeals.stream().mapToDouble(Meal::getProtein).sum();
             double currentCarbs = dayMeals.stream().mapToDouble(Meal::getCarbs).sum();
             double currentFat = dayMeals.stream().mapToDouble(Meal::getFat).sum();

             boolean proteinInRange = Math.abs(currentProtein - targetProtein) <= targetProtein * 0.15;
             boolean carbsInRange = Math.abs(currentCarbs - targetCarbs) <= targetCarbs * 0.15;
             boolean fatInRange = Math.abs(currentFat - targetFat) <= targetFat * 0.15;

             if (proteinInRange && carbsInRange && fatInRange) {
                 continue;
             }

             adjustSnacksForMacros(day, targetProtein, targetCarbs, targetFat, currentProtein, currentCarbs, currentFat);
         }
     }

     private void adjustSnacksForMacros(String day, double targetProtein, double targetCarbs, double targetFat,
                                        double currentProtein, double currentCarbs, double currentFat) {
         List<Meal> dayMeals = weeklyPlan.get(day);

         dayMeals.removeIf(meal -> meal instanceof Snack);

         currentProtein = dayMeals.stream().mapToDouble(Meal::getProtein).sum();
         currentCarbs = dayMeals.stream().mapToDouble(Meal::getCarbs).sum();
         currentFat = dayMeals.stream().mapToDouble(Meal::getFat).sum();

         double neededProtein = Math.max(0, targetProtein - currentProtein);
         double neededCarbs = Math.max(0, targetCarbs - currentCarbs);
         double neededFat = Math.max(0, targetFat - currentFat);

         List<Snack> allSnacks = mealSuggester.suggestSnacks(null, false, false);
         int snackCount = 0;

         if (neededProtein > 5 && snackCount < 2) {
             List<Snack> highProteinSnacks = allSnacks.stream()
                     .filter(snack -> snack.getProtein() > 5)
                     .sorted((s1, s2) -> Double.compare(s2.getProtein(), s1.getProtein()))
                     .collect(Collectors.toList());

             if (!highProteinSnacks.isEmpty()) {
                 Snack proteinSnack = highProteinSnacks.get(0);
                 dayMeals.add(proteinSnack);
                 allSnacks.remove(proteinSnack);
                 snackCount++;

                 neededProtein -= proteinSnack.getProtein();
                 neededCarbs -= proteinSnack.getCarbs();
                 neededFat -= proteinSnack.getFat();
             }
         }

         if (neededCarbs > 10 && snackCount < 2) {
             List<Snack> highCarbSnacks = allSnacks.stream()
                     .filter(snack -> snack.getCarbs() > 10)
                     .sorted((s1, s2) -> Double.compare(s2.getCarbs(), s1.getCarbs()))
                     .collect(Collectors.toList());

             if (!highCarbSnacks.isEmpty()) {
                 Snack carbSnack = highCarbSnacks.get(0);
                 dayMeals.add(carbSnack);
                 allSnacks.remove(carbSnack);
                 snackCount++;

                 neededProtein -= carbSnack.getProtein();
                 neededCarbs -= carbSnack.getCarbs();
                 neededFat -= carbSnack.getFat();
             }
         }

         if (neededFat > 5 && snackCount < 2) {
             List<Snack> highFatSnacks = allSnacks.stream()
                     .filter(snack -> snack.getFat() > 5)
                     .sorted((s1, s2) -> Double.compare(s2.getFat(), s1.getFat()))
                     .collect(Collectors.toList());

             if (!highFatSnacks.isEmpty()) {
                 Snack fatSnack = highFatSnacks.get(0);
                 dayMeals.add(fatSnack);
                 snackCount++;
             }
         }
     }

     public void customizeDayPlan(String day, Breakfast breakfast, Lunch lunch, Dinner dinner, List<Snack> snacks) {
         if (!weeklyPlan.containsKey(day)) {
             throw new IllegalArgumentException("Invalid day: " + day);
         }

         List<Meal> dayMeals = weeklyPlan.get(day);

         // Create a new list to hold the customized meals
         List<Meal> customizedMeals = new ArrayList<>();

         // Find existing meals by type
         Breakfast existingBreakfast = null;
         Lunch existingLunch = null;
         Dinner existingDinner = null;
         List<Snack> existingSnacks = new ArrayList<>();

         for (Meal meal : dayMeals) {
             if (meal instanceof Breakfast) {
                 existingBreakfast = (Breakfast) meal;
             } else if (meal instanceof Lunch) {
                 existingLunch = (Lunch) meal;
             } else if (meal instanceof Dinner) {
                 existingDinner = (Dinner) meal;
             } else if (meal instanceof Snack) {
                 existingSnacks.add((Snack) meal);
             }
         }

         // Add breakfast (new or existing)
         if (breakfast != null) {
             customizedMeals.add(breakfast);
         } else if (existingBreakfast != null) {
             customizedMeals.add(existingBreakfast);
         }

         // Add lunch (new or existing)
         if (lunch != null) {
             customizedMeals.add(lunch);
         } else if (existingLunch != null) {
             customizedMeals.add(existingLunch);
         }

         // Add dinner (new or existing)
         if (dinner != null) {
             customizedMeals.add(dinner);
         } else if (existingDinner != null) {
             customizedMeals.add(existingDinner);
         }

         // Add snacks (new or existing)
         if (snacks != null && !snacks.isEmpty()) {
             customizedMeals.addAll(snacks);
         } else {
             customizedMeals.addAll(existingSnacks);
         }

         // Update the day's meal plan
         weeklyPlan.put(day, customizedMeals);
     }

     public String exportWeeklyPlan() {
         StringBuilder export = new StringBuilder();

         for (String day : Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")) {
             export.append(day).append(":\n");

             List<Meal> dayMeals = weeklyPlan.get(day);
             for (Meal meal : dayMeals) {
                 String mealType = "Other";
                 if (meal instanceof Breakfast) mealType = "Breakfast";
                 if (meal instanceof Lunch) mealType = "Lunch";
                 if (meal instanceof Dinner) mealType = "Dinner";
                 if (meal instanceof Snack) mealType = "Snack";

                 export.append(mealType).append(",");
                 export.append(meal.getName()).append(",");
                 export.append(meal.getCalories()).append(",");
                 export.append(meal.getProtein()).append(",");
                 export.append(meal.getCarbs()).append(",");
                 export.append(meal.getFat()).append("\n");
             }

             export.append("\n");
         }

         return export.toString();
     }

     public boolean saveWeeklyPlan() {
         try (FileWriter writer = new FileWriter("weekly_plan.txt")) {
             writer.write(exportWeeklyPlan());
             return true;
         } catch (IOException e) {
             System.err.println("Error saving weekly plan: " + e.getMessage());
             return false;
         }
     }

     public boolean weeklyPlanExists() {
         File planFile = new File("weekly_plan.txt");
         return planFile.exists() && planFile.length() > 0;
     }

     public boolean loadWeeklyPlan() {
         File planFile = new File("weekly_plan.txt");
         if (!planFile.exists()) {
             return false;
         }

         try (BufferedReader reader = new BufferedReader(new FileReader(planFile))) {
             initializeEmptyPlan();
             initializeUsedMealTracker();

             String line;
             String currentDay = null;

             while ((line = reader.readLine()) != null) {
                 if (line.trim().isEmpty()) {
                     continue;
                 }

                 if (line.endsWith(":")) {
                     currentDay = line.substring(0, line.length() - 1);
                     continue;
                 }

                 if (currentDay != null) {
                     String[] parts = line.split(",");
                     if (parts.length >= 6) {
                         String mealType = parts[0];
                         String mealName = parts[1];
                         double calories = Double.parseDouble(parts[2]);
                         double protein = Double.parseDouble(parts[3]);
                         double carbs = Double.parseDouble(parts[4]);
                         double fat = Double.parseDouble(parts[5]);

                         Meal meal = null;

                         Meal originalMeal = null;
                         switch (mealType) {
                             case "Breakfast":
                                 originalMeal = findMealByName(mealSuggester.getAllBreakfasts(), mealName);
                                 meal = new Breakfast(mealName, "", "");
                                 break;
                             case "Lunch":
                                 originalMeal = findMealByName(mealSuggester.getAllLunches(), mealName);
                                 meal = new Lunch(mealName, "", "");
                                 break;
                             case "Dinner":
                                 originalMeal = findMealByName(mealSuggester.getAllDinners(), mealName);
                                 meal = new Dinner(mealName, "", "");
                                 break;
                             case "Snack":
                                 originalMeal = findMealByName(mealSuggester.getAllSnacks(), mealName);
                                 meal = new Snack(mealName, "", "");
                                 break;
                         }

                         if (meal != null) {
                             meal.setCalories(calories);
                             meal.setProtein(protein);
                             meal.setCarbs(carbs);
                             meal.setFat(fat);

                             if (originalMeal != null) {
                                 for (Ingredient ingredient : originalMeal.getIngredients()) {
                                     meal.addIngredient(ingredient);
                                 }
                                 meal.setDietaryType(originalMeal.getDietaryType());
                                 meal.setDescription(originalMeal.getDescription());
                             }

                             weeklyPlan.get(currentDay).add(meal);

                             if (meal instanceof Breakfast) {
                                 usedMealNames.get("Breakfast").add(meal.getName());
                             } else if (meal instanceof Lunch) {
                                 usedMealNames.get("Lunch").add(meal.getName());
                                 previousDayMeals.put("Lunch", meal.getName());
                             } else if (meal instanceof Dinner) {
                                 usedMealNames.get("Dinner").add(meal.getName());
                                 previousDayMeals.put("Dinner", meal.getName());
                             } else if (meal instanceof Snack) {
                                 usedMealNames.get("Snack").add(meal.getName());
                             }
                         }
                     }
                 }
             }

             return true;
         } catch (IOException | NumberFormatException e) {
             System.err.println("Error loading weekly plan: " + e.getMessage());
             return false;
         }
     }

     private Meal findMealByName(List<? extends Meal> meals, String name) {
         if (meals == null || name == null) {
             return null;
         }

         return meals.stream()
                 .filter(meal -> name.equals(meal.getName()))
                 .findFirst()
                 .orElse(null);
     }
}