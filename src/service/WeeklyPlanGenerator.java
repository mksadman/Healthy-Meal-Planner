package service;

import model.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

/**
 * Service class for generating weekly meal plans based on user preferences.
 */
public class WeeklyPlanGenerator {
    private MealSuggester mealSuggester;
    private Map<String, List<Meal>> weeklyPlan; // Day -> List of meals for that day
    private Map<String, Set<String>> usedMealNames; // Tracks used meal names by meal type
    private Map<String, String> previousDayMeals; // Tracks previous day's lunch and dinner

    /**
     * Constructor initializes with a meal suggester
     */
    public WeeklyPlanGenerator(MealSuggester mealSuggester) {
        this.mealSuggester = mealSuggester;
        this.weeklyPlan = new HashMap<>();
        this.usedMealNames = new HashMap<>();
        this.previousDayMeals = new HashMap<>();
        initializeEmptyPlan();
        initializeUsedMealTracker();
    }

    /**
     * Initializes the used meal tracker with empty sets for each meal type
     */
    private void initializeUsedMealTracker() {
        usedMealNames.put("Breakfast", new HashSet<>());
        usedMealNames.put("Lunch", new HashSet<>());
        usedMealNames.put("Dinner", new HashSet<>());
        usedMealNames.put("Snack", new HashSet<>());

        // Initialize previous day meals tracking
        previousDayMeals.put("Lunch", "");
        previousDayMeals.put("Dinner", "");
    }

    /**
     * Initializes an empty weekly plan with all days of the week
     */
    private void initializeEmptyPlan() {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : daysOfWeek) {
            weeklyPlan.put(day, new ArrayList<>());
        }
    }

    /**
     * Generates a complete weekly plan based on dietary preferences
     */
    public void generateWeeklyPlan(String dietaryType, double dailyCalorieTarget) {
        // Clear any existing plan
        initializeEmptyPlan();
        // Reset used meal tracker
        initializeUsedMealTracker();

        // For each day of the week
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : daysOfWeek) {
            // Add breakfast
            List<Breakfast> breakfastOptions = mealSuggester.suggestBreakfasts(dietaryType, false, false);
            if (!breakfastOptions.isEmpty()) {
                // Filter out recently used breakfasts to ensure variety
                List<Breakfast> filteredBreakfasts = breakfastOptions.stream()
                        .filter(b -> !usedMealNames.get("Breakfast").contains(b.getName()))
                        .collect(Collectors.toList());

                // If all options have been used, reset the filter
                if (filteredBreakfasts.isEmpty()) {
                    filteredBreakfasts = breakfastOptions;
                }

                Breakfast selectedBreakfast = getRandomMeal(filteredBreakfasts);
                weeklyPlan.get(day).add(selectedBreakfast);
                usedMealNames.get("Breakfast").add(selectedBreakfast.getName());
            }

            // Add lunch
            List<Lunch> lunchOptions = mealSuggester.suggestLunches(dietaryType, false, false);
            if (!lunchOptions.isEmpty()) {
                // Filter out recently used lunches and the previous day's lunch to ensure variety
                List<Lunch> filteredLunches = lunchOptions.stream()
                        .filter(l -> !usedMealNames.get("Lunch").contains(l.getName()) &&
                                !l.getName().equals(previousDayMeals.get("Lunch")))
                        .collect(Collectors.toList());

                // If all options have been used, reset the filter but still avoid previous day's lunch
                if (filteredLunches.isEmpty()) {
                    filteredLunches = lunchOptions.stream()
                            .filter(l -> !l.getName().equals(previousDayMeals.get("Lunch")))
                            .collect(Collectors.toList());
                }

                // If still empty (only one lunch option available), use all options
                if (filteredLunches.isEmpty()) {
                    filteredLunches = lunchOptions;
                }

                Lunch selectedLunch = getRandomMeal(filteredLunches);
                weeklyPlan.get(day).add(selectedLunch);
                usedMealNames.get("Lunch").add(selectedLunch.getName());
                previousDayMeals.put("Lunch", selectedLunch.getName());
            }

            // Add dinner
            List<Dinner> dinnerOptions = mealSuggester.suggestDinners(dietaryType, false, false);
            if (!dinnerOptions.isEmpty()) {
                // Filter out recently used dinners and the previous day's dinner to ensure variety
                List<Dinner> filteredDinners = dinnerOptions.stream()
                        .filter(d -> !usedMealNames.get("Dinner").contains(d.getName()) &&
                                !d.getName().equals(previousDayMeals.get("Dinner")))
                        .collect(Collectors.toList());

                // If all options have been used, reset the filter but still avoid previous day's dinner
                if (filteredDinners.isEmpty()) {
                    filteredDinners = dinnerOptions.stream()
                            .filter(d -> !d.getName().equals(previousDayMeals.get("Dinner")))
                            .collect(Collectors.toList());
                }

                // If still empty (only one dinner option available), use all options
                if (filteredDinners.isEmpty()) {
                    filteredDinners = dinnerOptions;
                }

                Dinner selectedDinner = getRandomMeal(filteredDinners);
                weeklyPlan.get(day).add(selectedDinner);
                usedMealNames.get("Dinner").add(selectedDinner.getName());
                previousDayMeals.put("Dinner", selectedDinner.getName());
            }

            // Add snacks (2 per day)
            List<Snack> snackOptions = mealSuggester.suggestSnacks(dietaryType, false, false);
            if (!snackOptions.isEmpty()) {
                // Filter out recently used snacks to ensure variety
                List<Snack> filteredSnacks = snackOptions.stream()
                        .filter(s -> !usedMealNames.get("Snack").contains(s.getName()))
                        .collect(Collectors.toList());

                // If all options have been used, reset the filter
                if (filteredSnacks.isEmpty()) {
                    filteredSnacks = snackOptions;
                }

                // Add first snack
                Snack selectedSnack1 = getRandomMeal(filteredSnacks);
                weeklyPlan.get(day).add(selectedSnack1);
                usedMealNames.get("Snack").add(selectedSnack1.getName());

                // Add second snack if available
                if (filteredSnacks.size() > 1) {
                    // Remove the first selected snack from options
                    filteredSnacks.remove(selectedSnack1);

                    // Add another snack
                    Snack selectedSnack2 = getRandomMeal(filteredSnacks);
                    weeklyPlan.get(day).add(selectedSnack2);
                    usedMealNames.get("Snack").add(selectedSnack2.getName());
                }
            }

            // Adjust plan to meet calorie target
            adjustDailyCalories(day, dailyCalorieTarget);

            // Limit the size of used meal sets to prevent them from growing too large
            // This ensures that meals can be reused after a few days
            limitUsedMealSets(2); // Limit to the 2 most recently used meals of each type
        }
    }

    /**
     * Limits the size of used meal sets to ensure meals can be reused after a certain period
     *
     * @param maxSize Maximum number of meal names to keep in each set
     */
    private void limitUsedMealSets(int maxSize) {
        for (String mealType : usedMealNames.keySet()) {
            Set<String> usedMeals = usedMealNames.get(mealType);
            if (usedMeals.size() > maxSize) {
                List<String> mealList = new ArrayList<>(usedMeals);
                usedMealNames.put(mealType, new HashSet<>(mealList.subList(mealList.size() - maxSize, mealList.size())));
            }
        }
    }

    /**
     * Adjusts the meals for a day to meet the calorie target
     */
    private void adjustDailyCalories(String day, double targetCalories) {
        List<Meal> dayMeals = weeklyPlan.get(day);
        double currentCalories = dayMeals.stream().mapToDouble(Meal::getCalories).sum();

        // If we're already close to the target (within 10%), don't adjust
        if (Math.abs(currentCalories - targetCalories) <= targetCalories * 0.1) {
            return;
        }

        // If we need more calories, add snacks
        if (currentCalories < targetCalories) {
            List<Snack> snackOptions = mealSuggester.suggestSnacks(null, false, false);

            // Count existing snacks
            long existingSnackCount = dayMeals.stream()
                    .filter(meal -> meal instanceof Snack)
                    .count();

            // Only add more snacks if we have less than 2
            while (!snackOptions.isEmpty() && currentCalories < targetCalories * 0.9 && existingSnackCount < 2) {
                // Filter out recently used snacks to ensure variety
                List<Snack> filteredSnacks = snackOptions.stream()
                        .filter(s -> !usedMealNames.get("Snack").contains(s.getName()))
                        .collect(Collectors.toList());

                // If all options have been used, reset the filter
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
        // If we need fewer calories, replace high-calorie meals with lower-calorie alternatives
        else {
            // Sort meals by calories (highest first)
            dayMeals.sort((m1, m2) -> Double.compare(m2.getCalories(), m1.getCalories()));

            // Replace the highest calorie meals with lower calorie alternatives of the same type
            for (int i = 0; i < dayMeals.size() && currentCalories > targetCalories * 1.1; i++) {
                Meal highCalorieMeal = dayMeals.get(i);
                List<Meal> alternatives;
                String mealType = null;

                if (highCalorieMeal instanceof Breakfast) {
                    alternatives = new ArrayList<>(mealSuggester.suggestBreakfasts(null, false, false));
                    mealType = "Breakfast";
                } else if (highCalorieMeal instanceof Lunch) {
                    alternatives = new ArrayList<>(mealSuggester.suggestLunches(null, false, false));
                    mealType = "Lunch";
                } else if (highCalorieMeal instanceof Dinner) {
                    alternatives = new ArrayList<>(mealSuggester.suggestDinners(null, false, false));
                    mealType = "Dinner";
                } else { // Snack
                    alternatives = new ArrayList<>(mealSuggester.suggestSnacks(null, true, false));
                    mealType = "Snack";
                }

                // Filter out recently used meals and previous day's meal (for lunch and dinner) to ensure variety
                if (mealType != null) {
                    final String finalMealType = mealType;
                    if ("Lunch".equals(finalMealType) || "Dinner".equals(finalMealType)) {
                        // For lunch and dinner, also avoid previous day's meal
                        final String previousDayMeal = previousDayMeals.get(finalMealType);
                        alternatives = alternatives.stream()
                                .filter(m -> !usedMealNames.get(finalMealType).contains(m.getName()) &&
                                        !m.getName().equals(previousDayMeal))
                                .collect(Collectors.toList());
                    } else {
                        // For breakfast and snacks, just avoid recently used meals
                        alternatives = alternatives.stream()
                                .filter(m -> !usedMealNames.get(finalMealType).contains(m.getName()))
                                .collect(Collectors.toList());
                    }

                    // If all options have been used, reset the filter but still avoid previous day's meal for lunch and dinner
                    if (alternatives.isEmpty()) {
                        if ("Lunch".equals(finalMealType) || "Dinner".equals(finalMealType)) {
                            final String previousDayMeal = previousDayMeals.get(finalMealType);
                            if (highCalorieMeal instanceof Lunch) {
                                alternatives = new ArrayList<>(mealSuggester.suggestLunches(null, false, false));
                                alternatives = alternatives.stream()
                                        .filter(m -> !m.getName().equals(previousDayMeal))
                                        .collect(Collectors.toList());
                            } else if (highCalorieMeal instanceof Dinner) {
                                alternatives = new ArrayList<>(mealSuggester.suggestDinners(null, false, false));
                                alternatives = alternatives.stream()
                                        .filter(m -> !m.getName().equals(previousDayMeal))
                                        .collect(Collectors.toList());
                            }

                            // If still empty (only one option available), use all options
                            if (alternatives.isEmpty()) {
                                if (highCalorieMeal instanceof Lunch) {
                                    alternatives = new ArrayList<>(mealSuggester.suggestLunches(null, false, false));
                                } else if (highCalorieMeal instanceof Dinner) {
                                    alternatives = new ArrayList<>(mealSuggester.suggestDinners(null, false, false));
                                }
                            }
                        } else {
                            if (highCalorieMeal instanceof Breakfast) {
                                alternatives = new ArrayList<>(mealSuggester.suggestBreakfasts(null, false, false));
                            } else { // Snack
                                alternatives = new ArrayList<>(mealSuggester.suggestSnacks(null, true, false));
                            }
                        }
                    }
                }

                // Sort alternatives by calories (lowest first)
                alternatives.sort(Comparator.comparingDouble(Meal::getCalories));

                // Replace with a lower calorie alternative if available
                if (!alternatives.isEmpty() && alternatives.get(0).getCalories() < highCalorieMeal.getCalories()) {
                    Meal replacement = alternatives.get(0);
                    currentCalories = currentCalories - highCalorieMeal.getCalories() + replacement.getCalories();
                    dayMeals.set(i, replacement);

                    // Add the replacement meal to the used meals tracker
                    if (mealType != null) {
                        usedMealNames.get(mealType).add(replacement.getName());

                        // Update previous day meal tracking for lunch and dinner replacements
                        if (("Lunch".equals(mealType) || "Dinner".equals(mealType)) && replacement instanceof Meal) {
                            previousDayMeals.put(mealType, replacement.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets a random meal from a list of options
     */
    private <T extends Meal> T getRandomMeal(List<T> options) {
        if (options.isEmpty()) {
            throw new IllegalArgumentException("Cannot get a random meal from an empty list");
        }
        Random random = new Random();
        return options.get(random.nextInt(options.size()));
    }

    /**
     * Gets the complete weekly plan
     */
    public Map<String, List<Meal>> getWeeklyPlan() {
        return new HashMap<>(weeklyPlan);
    }

    /**
     * Gets the plan for a specific day
     */
    public List<Meal> getDayPlan(String day) {
        return new ArrayList<>(weeklyPlan.getOrDefault(day, new ArrayList<>()));
    }

    /**
     * Calculates the total calories for a day
     */
    public double calculateDailyCalories(String day) {
        return weeklyPlan.getOrDefault(day, new ArrayList<>()).stream()
                .mapToDouble(Meal::getCalories)
                .sum();
    }

    /**
     * Calculates the average daily calories for the week
     */
    public double calculateAverageWeeklyCalories() {
        return weeklyPlan.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Meal::getCalories)
                .sum() / 7.0;
    }

    /**
     * Generates a formatted string representation of the weekly plan
     */
    public String getWeeklyPlanSummary() {
        StringBuilder summary = new StringBuilder("WEEKLY MEAL PLAN\n=================\n\n");

        for (String day : Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")) {
            List<Meal> dayMeals = weeklyPlan.get(day);
            summary.append(day.toUpperCase()).append("\n");
            summary.append("Total Calories: ").append(String.format("%.1f", calculateDailyCalories(day))).append("\n\n");

            // Categorize meals
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

            // Print each category
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

    /**
     * Generates a weekly plan based on specific nutritional goals
     * @param nutritionalGoals Map containing daily nutritional goals
     * @param dietaryType Dietary preference (vegetarian, keto, etc.)
     */
    public void generateWeeklyPlanByNutritionalGoals(Map<String, Double> nutritionalGoals, String dietaryType) {
        if (nutritionalGoals == null) {
            throw new IllegalArgumentException("Nutritional goals cannot be null");
        }

        double targetCalories = nutritionalGoals.getOrDefault("calories", 2000.0);
        generateWeeklyPlan(dietaryType, targetCalories);

        // Further optimize the plan to meet protein, carb, and fat goals
        double targetProtein = nutritionalGoals.getOrDefault("protein", 50.0);
        double targetCarbs = nutritionalGoals.getOrDefault("carbs", 250.0);
        double targetFat = nutritionalGoals.getOrDefault("fat", 70.0);

        optimizeMacronutrients(targetProtein, targetCarbs, targetFat);
    }

    /**
     * Optimizes the weekly plan to meet macronutrient targets
     */
    private void optimizeMacronutrients(double targetProtein, double targetCarbs, double targetFat) {
        // For each day of the week
        for (String day : weeklyPlan.keySet()) {
            List<Meal> dayMeals = weeklyPlan.get(day);

            // Calculate current macronutrients
            double currentProtein = dayMeals.stream().mapToDouble(Meal::getProtein).sum();
            double currentCarbs = dayMeals.stream().mapToDouble(Meal::getCarbs).sum();
            double currentFat = dayMeals.stream().mapToDouble(Meal::getFat).sum();

            // If we're already close to the targets (within 15%), don't adjust
            boolean proteinInRange = Math.abs(currentProtein - targetProtein) <= targetProtein * 0.15;
            boolean carbsInRange = Math.abs(currentCarbs - targetCarbs) <= targetCarbs * 0.15;
            boolean fatInRange = Math.abs(currentFat - targetFat) <= targetFat * 0.15;

            if (proteinInRange && carbsInRange && fatInRange) {
                continue;
            }

            // Adjust snacks to optimize macronutrients
            adjustSnacksForMacros(day, targetProtein, targetCarbs, targetFat, currentProtein, currentCarbs, currentFat);
        }
    }

    /**
     * Adjusts snacks in a day's meal plan to better meet macronutrient targets
     */
    private void adjustSnacksForMacros(String day, double targetProtein, double targetCarbs, double targetFat,
                                       double currentProtein, double currentCarbs, double currentFat) {
        List<Meal> dayMeals = weeklyPlan.get(day);

        // Remove existing snacks
        dayMeals.removeIf(meal -> meal instanceof Snack);

        // Recalculate current macros without snacks
        currentProtein = dayMeals.stream().mapToDouble(Meal::getProtein).sum();
        currentCarbs = dayMeals.stream().mapToDouble(Meal::getCarbs).sum();
        currentFat = dayMeals.stream().mapToDouble(Meal::getFat).sum();

        // Calculate what we need from snacks
        double neededProtein = Math.max(0, targetProtein - currentProtein);
        double neededCarbs = Math.max(0, targetCarbs - currentCarbs);
        double neededFat = Math.max(0, targetFat - currentFat);

        // Get all available snacks
        List<Snack> allSnacks = mealSuggester.suggestSnacks(null, false, false);
        int snackCount = 0;

        // Add high-protein snacks if needed and if we haven't reached the snack limit
        if (neededProtein > 5 && snackCount < 2) { // Only adjust if we need at least 5g more protein
            List<Snack> highProteinSnacks = allSnacks.stream()
                    .filter(snack -> snack.getProtein() > 5) // Snacks with decent protein
                    .sorted((s1, s2) -> Double.compare(s2.getProtein(), s1.getProtein())) // Highest protein first
                    .collect(Collectors.toList());

            if (!highProteinSnacks.isEmpty()) {
                Snack proteinSnack = highProteinSnacks.get(0);
                dayMeals.add(proteinSnack);
                allSnacks.remove(proteinSnack);
                snackCount++;

                // Update needed macros
                neededProtein -= proteinSnack.getProtein();
                neededCarbs -= proteinSnack.getCarbs();
                neededFat -= proteinSnack.getFat();
            }
        }

        // Add high-carb snacks if needed and if we haven't reached the snack limit
        if (neededCarbs > 10 && snackCount < 2) { // Only adjust if we need at least 10g more carbs
            List<Snack> highCarbSnacks = allSnacks.stream()
                    .filter(snack -> snack.getCarbs() > 10) // Snacks with decent carbs
                    .sorted((s1, s2) -> Double.compare(s2.getCarbs(), s1.getCarbs())) // Highest carbs first
                    .collect(Collectors.toList());

            if (!highCarbSnacks.isEmpty()) {
                Snack carbSnack = highCarbSnacks.get(0);
                dayMeals.add(carbSnack);
                allSnacks.remove(carbSnack);
                snackCount++;

                // Update needed macros
                neededProtein -= carbSnack.getProtein();
                neededCarbs -= carbSnack.getCarbs();
                neededFat -= carbSnack.getFat();
            }
        }

        // Add high-fat snacks if needed and if we haven't reached the snack limit
        if (neededFat > 5 && snackCount < 2) { // Only adjust if we need at least 5g more fat
            List<Snack> highFatSnacks = allSnacks.stream()
                    .filter(snack -> snack.getFat() > 5) // Snacks with decent fat
                    .sorted((s1, s2) -> Double.compare(s2.getFat(), s1.getFat())) // Highest fat first
                    .collect(Collectors.toList());

            if (!highFatSnacks.isEmpty()) {
                Snack fatSnack = highFatSnacks.get(0);
                dayMeals.add(fatSnack);
                snackCount++;
            }
        }
    }

    /**
     * Customizes a specific day in the weekly plan
     * @param day The day to customize
     * @param breakfast The breakfast to set (null to keep existing)
     * @param lunch The lunch to set (null to keep existing)
     * @param dinner The dinner to set (null to keep existing)
     * @param snacks List of snacks to set (null or empty to keep existing)
     */
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

    /**
     * Exports the weekly plan to a format that can be saved to a file
     */
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

    /**
     * Saves the current weekly plan to a file
     * @return true if the plan was saved successfully, false otherwise
     */
    public boolean saveWeeklyPlan() {
        try (FileWriter writer = new FileWriter("weekly_plan.txt")) {
            writer.write(exportWeeklyPlan());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving weekly plan: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a saved weekly plan exists
     * @return true if a saved plan exists, false otherwise
     */
    public boolean weeklyPlanExists() {
        File planFile = new File("weekly_plan.txt");
        return planFile.exists() && planFile.length() > 0;
    }

    /**
     * Loads a weekly plan from a file
     * @return true if the plan was loaded successfully, false otherwise
     */
    public boolean loadWeeklyPlan() {
        File planFile = new File("weekly_plan.txt");
        if (!planFile.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(planFile))) {
            // Clear existing plan
            initializeEmptyPlan();
            initializeUsedMealTracker();

            String line;
            String currentDay = null;

            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Check if this is a day marker
                if (line.endsWith(":")) {
                    currentDay = line.substring(0, line.length() - 1);
                    continue;
                }

                // If we have a current day, parse the meal
                if (currentDay != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        String mealType = parts[0];
                        String mealName = parts[1];
                        double calories = Double.parseDouble(parts[2]);
                        double protein = Double.parseDouble(parts[3]);
                        double carbs = Double.parseDouble(parts[4]);
                        double fat = Double.parseDouble(parts[5]);

                        // Create the appropriate meal type
                        Meal meal = null;

                        // Try to find the meal in the meal suggester to get ingredients
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

                        // Set nutritional values
                        if (meal != null) {
                            meal.setCalories(calories);
                            meal.setProtein(protein);
                            meal.setCarbs(carbs);
                            meal.setFat(fat);

                            // Copy ingredients from original meal if found
                            if (originalMeal != null) {
                                for (Ingredient ingredient : originalMeal.getIngredients()) {
                                    meal.addIngredient(ingredient);
                                }
                                meal.setDietaryType(originalMeal.getDietaryType());
                                meal.setDescription(originalMeal.getDescription());
                            }

                            // Add to the weekly plan
                            weeklyPlan.get(currentDay).add(meal);

                            // Update used meal trackers
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

    /**
     * Finds a meal by name in a list of meals
     * @param meals List of meals to search through
     * @param name Name of the meal to find
     * @return The meal if found, null otherwise
     */
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