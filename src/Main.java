import model.*;
import service.*;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser;
    private static MealSuggester mealSuggester;
    private static WeeklyPlanGenerator weeklyPlanGenerator;
    private static NutritionAnalyzer nutritionAnalyzer;
    private static ShoppingListGenerator shoppingListGenerator;
    private static UserProfileManager userProfileManager;
    private static List<Recipe> recipeDatabase;

    public static void main(String[] args) {
        System.out.println("Welcome to the Healthy Meal Planner!\n");
        initializeSystem();

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getUserChoice(1, 9);

            switch (choice) {
                case 1:
                    displayUserInfo();
                    break;
                case 2:
                    manageUserProfile();
                    break;
                case 3:
                    viewMealSuggestions();
                    break;
                case 4:
                    generateWeeklyPlan();
                    break;
                case 5:
                    displayExistingWeeklyPlan();
                    break;
                case 6:
                    generateShoppingList();
                    break;
                case 7:
                    analyzeNutrition();
                    break;
                case 8:
                    manageRecipes();
                    break;
                case 9:
                    running = false;
                    System.out.println("Thank you for using the Healthy Meal Planner. Goodbye!");
                    break;
            }
        }

        scanner.close();
    }

    private static void initializeSystem() {
        mealSuggester = new MealSuggester();
        nutritionAnalyzer = new NutritionAnalyzer();
        weeklyPlanGenerator = new WeeklyPlanGenerator(mealSuggester);
        shoppingListGenerator = new ShoppingListGenerator();
        userProfileManager = new UserProfileManager();
        recipeDatabase = new ArrayList<>();

        // Load meals from text files instead of adding sample meals
        loadMealsFromFiles();

        // Load user profile if it exists
        if (userProfileManager.profileExists()) {
            currentUser = userProfileManager.loadUserProfile();
            System.out.println("\nWelcome back, " + currentUser.getName() + "!");
        } else {
            System.out.println("\nPlease create a user profile to get started.");
            createNewProfile();
        }

        // Load weekly meal plan if it exists
        if (weeklyPlanGenerator.weeklyPlanExists()) {
            if (weeklyPlanGenerator.loadWeeklyPlan()) {
                System.out.println("Your previous weekly meal plan has been loaded.");
            } else {
                System.out.println("Failed to load your previous weekly meal plan.");
            }
        }
    }

    /**
     * Loads meals from text files using the MealFileParser
     */
    private static void loadMealsFromFiles() {
        // Define file paths
        String breakfastFilePath = "breakfast.txt";
        String lunchFilePath = "lunch.txt";
        String dinnerFilePath = "dinner.txt";
        String snacksFilePath = "snacks.txt";

        // Parse and add breakfast meals
        List<Breakfast> breakfasts = MealFileParser.parseBreakfastFile(breakfastFilePath);
        for (Breakfast breakfast : breakfasts) {
            mealSuggester.addMeal(breakfast);
        }

        // Parse and add lunch meals
        List<Lunch> lunches = MealFileParser.parseLunchFile(lunchFilePath);
        for (Lunch lunch : lunches) {
            mealSuggester.addMeal(lunch);
        }

        // Parse and add dinner meals
        List<Dinner> dinners = MealFileParser.parseDinnerFile(dinnerFilePath);
        for (Dinner dinner : dinners) {
            mealSuggester.addMeal(dinner);
        }

        // Parse and add snack options
        List<Snack> snacks = MealFileParser.parseSnackFile(snacksFilePath);
        for (Snack snack : snacks) {
            mealSuggester.addMeal(snack);
        }

        System.out.println("Loaded " + breakfasts.size() + " breakfast options");
        System.out.println("Loaded " + lunches.size() + " lunch options");
        System.out.println("Loaded " + dinners.size() + " dinner options");
        System.out.println("Loaded " + snacks.size() + " snack options");
    }

    private static void displayMainMenu() {
        System.out.println("\n===== HEALTHY MEAL PLANNER MENU =====");
        System.out.println("1. Display User Information");
        System.out.println("2. Manage User Profile");
        System.out.println("3. View Meal Suggestions");
        System.out.println("4. Generate Weekly Meal Plan");
        System.out.println("5. View Existing Meal Plan");
        System.out.println("6. Generate Shopping List");
        System.out.println("7. Analyze Nutrition");
        System.out.println("8. Manage Recipes");
        System.out.println("9. Exit");
        System.out.print("Enter your choice (1-9): ");
    }

    private static int getUserChoice(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < min || choice > max) {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
        return choice;
    }

    private static void manageUserProfile() {
        System.out.println("\n===== MANAGE USER PROFILE =====");

        if (currentUser == null) {
            System.out.println("No user profile exists. Creating a new profile...");
            createNewProfile();
            return;
        }

        System.out.println("1. Create New Profile");
        System.out.println("2. Edit Current Profile");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice (1-3): ");

        int choice = getUserChoice(1, 3);

        switch (choice) {
            case 1:
                createNewProfile();
                break;
            case 2:
                editCurrentProfile();
                break;
            case 3:
                return;
        }
    }

    private static void createNewProfile() {
        System.out.println("\n===== CREATE NEW PROFILE =====");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your weight in kg: ");
        double weight = getDoubleInput();

        System.out.print("Enter your height in cm: ");
        double height = getDoubleInput();

        System.out.print("Enter your age in years: ");
        int age = getIntInput();

        System.out.print("Enter your gender (male/female): ");
        String gender = scanner.nextLine().toLowerCase();
        while (!gender.equals("male") && !gender.equals("female")) {
            System.out.print("Please enter 'male' or 'female': ");
            gender = scanner.nextLine().toLowerCase();
        }

        currentUser = new User(name, weight, height, age, gender);

        System.out.print("Enter your activity level (1-5, where 1 is sedentary and 5 is very active): ");
        int activityLevel = getUserChoice(1, 5);
        currentUser.setActivityLevel(activityLevel);

        System.out.print("Enter your dietary preference (none, vegetarian, vegan, keto, paleo, etc.): ");
        String dietaryPreference = scanner.nextLine().toLowerCase();
        currentUser.setDietaryPreference(dietaryPreference);

        System.out.print("Enter your weight goal (maintain, lose, gain): ");
        String weightGoal = scanner.nextLine().toLowerCase();
        while (!weightGoal.equals("maintain") && !weightGoal.equals("lose") && !weightGoal.equals("gain")) {
            System.out.print("Please enter 'maintain', 'lose', or 'gain': ");
            weightGoal = scanner.nextLine().toLowerCase();
        }
        currentUser.setWeightGoal(weightGoal);

        System.out.print("Do you have any food allergies? (yes/no): ");
        String hasAllergies = scanner.nextLine().toLowerCase();
        if (hasAllergies.equals("yes")) {
            System.out.print("Enter your food allergies separated by commas: ");
            String allergiesInput = scanner.nextLine();
            String[] allergies = allergiesInput.split(",");
            for (int i = 0; i < allergies.length; i++) {
                allergies[i] = allergies[i].trim();
            }
            currentUser.setFoodAllergies(allergies);
        }

        if (userProfileManager.saveUserProfile(currentUser)) {
            System.out.println("Profile created and saved successfully!");
        } else {
            System.out.println("Profile created successfully, but there was an error saving it.");
        }
    }

    private static void editCurrentProfile() {
        if (currentUser == null) {
            System.out.println("No user profile exists. Please create a new profile first.");
            return;
        }

        System.out.println("\n===== EDIT PROFILE =====");
        System.out.println("1. Edit Name");
        System.out.println("2. Edit Weight");
        System.out.println("3. Edit Height");
        System.out.println("4. Edit Age");
        System.out.println("5. Edit Gender");
        System.out.println("6. Edit Activity Level");
        System.out.println("7. Edit Dietary Preference");
        System.out.println("8. Edit Weight Goal");
        System.out.println("9. Edit Food Allergies");
        System.out.println("10. Back to Profile Menu");
        System.out.print("Enter your choice (1-10): ");

        int choice = getUserChoice(1, 10);

        switch (choice) {
            case 1:
                System.out.print("Enter new name: ");
                currentUser.setName(scanner.nextLine());
                break;
            case 2:
                System.out.print("Enter new weight in kg: ");
                currentUser.setWeightKg(getDoubleInput());
                break;
            case 3:
                System.out.print("Enter new height in cm: ");
                currentUser.setHeightCm(getDoubleInput());
                break;
            case 4:
                System.out.print("Enter new age in years: ");
                currentUser.setAgeYears(getIntInput());
                break;
            case 5:
                System.out.print("Enter new gender (male/female): ");
                String gender = scanner.nextLine().toLowerCase();
                while (!gender.equals("male") && !gender.equals("female")) {
                    System.out.print("Please enter 'male' or 'female': ");
                    gender = scanner.nextLine().toLowerCase();
                }
                currentUser.setGender(gender);
                break;
            case 6:
                System.out.print("Enter new activity level (1-5): ");
                currentUser.setActivityLevel(getUserChoice(1, 5));
                break;
            case 7:
                System.out.print("Enter new dietary preference: ");
                currentUser.setDietaryPreference(scanner.nextLine().toLowerCase());
                break;
            case 8:
                System.out.print("Enter new weight goal (maintain, lose, gain): ");
                String weightGoal = scanner.nextLine().toLowerCase();
                while (!weightGoal.equals("maintain") && !weightGoal.equals("lose") && !weightGoal.equals("gain")) {
                    System.out.print("Please enter 'maintain', 'lose', or 'gain': ");
                    weightGoal = scanner.nextLine().toLowerCase();
                }
                currentUser.setWeightGoal(weightGoal);
                break;
            case 9:
                System.out.print("Do you have any food allergies? (yes/no): ");
                String hasAllergies = scanner.nextLine().toLowerCase();
                if (hasAllergies.equals("yes")) {
                    System.out.print("Enter your food allergies separated by commas: ");
                    String allergiesInput = scanner.nextLine();
                    String[] allergies = allergiesInput.split(",");
                    for (int i = 0; i < allergies.length; i++) {
                        allergies[i] = allergies[i].trim();
                    }
                    currentUser.setFoodAllergies(allergies);
                } else {
                    currentUser.setFoodAllergies(new String[0]);
                }
                break;
            case 10:
                return;
        }

        if (userProfileManager.saveUserProfile(currentUser)) {
            System.out.println("Profile updated and saved successfully!");
        } else {
            System.out.println("Profile updated successfully, but there was an error saving it.");
        }
    }

    private static void viewMealSuggestions() {
        if (currentUser == null) {
            System.out.println("No user profile exists. Please create a new profile first.");
            createNewProfile();
            return;
        }

        System.out.println("\n===== MEAL SUGGESTIONS =====");
        System.out.println("1. Breakfast Suggestions");
        System.out.println("2. Lunch Suggestions");
        System.out.println("3. Dinner Suggestions");
        System.out.println("4. Snack Suggestions");
        System.out.println("5. Back to Main Menu");
        System.out.print("Enter your choice (1-5): ");

        int choice = getUserChoice(1, 5);

        switch (choice) {
            case 1:
                displayBreakfastSuggestions();
                break;
            case 2:
                displayLunchSuggestions();
                break;
            case 3:
                displayDinnerSuggestions();
                break;
            case 4:
                displaySnackSuggestions();
                break;
            case 5:
                return;
        }
    }

    private static void displayBreakfastSuggestions() {
        System.out.println("\n===== BREAKFAST SUGGESTIONS =====");
        System.out.print("High protein only? (yes/no): ");
        boolean highProtein = scanner.nextLine().equalsIgnoreCase("yes");

        System.out.print("Caffeine free only? (yes/no): ");
        boolean caffeineFree = scanner.nextLine().equalsIgnoreCase("yes");

        List<Breakfast> breakfasts = mealSuggester.suggestBreakfasts(
                currentUser.getDietaryPreference().equals("none") ? null : currentUser.getDietaryPreference(),
                highProtein,
                caffeineFree);

        if (breakfasts.isEmpty()) {
            System.out.println("No breakfast suggestions match your criteria.");
        } else {
            System.out.println("\nHere are your breakfast suggestions:");
            for (int i = 0; i < breakfasts.size(); i++) {
                Breakfast breakfast = breakfasts.get(i);
                System.out.println((i + 1) + ". " + breakfast.getName() + " - " + breakfast.getDescription());
                System.out.println("   Calories: " + breakfast.getCalories() + ", Protein: " + breakfast.getProtein() + "g");
                System.out.println("   Dietary Type: " + breakfast.getDietaryType());
                System.out.println();
            }
        }
    }

    private static void displayLunchSuggestions() {
        System.out.println("\n===== LUNCH SUGGESTIONS =====");
        System.out.print("Low carb only? (yes/no): ");
        boolean lowCarb = scanner.nextLine().equalsIgnoreCase("yes");

        System.out.print("Quick preparation only? (yes/no): ");
        boolean quickPrep = scanner.nextLine().equalsIgnoreCase("yes");

        List<Lunch> lunches = mealSuggester.suggestLunches(
                currentUser.getDietaryPreference().equals("none") ? null : currentUser.getDietaryPreference(),
                lowCarb,
                quickPrep);

        if (lunches.isEmpty()) {
            System.out.println("No lunch suggestions match your criteria.");
        } else {
            System.out.println("\nHere are your lunch suggestions:");
            for (int i = 0; i < lunches.size(); i++) {
                Lunch lunch = lunches.get(i);
                System.out.println((i + 1) + ". " + lunch.getName() + " - " + lunch.getDescription());
                System.out.println("   Calories: " + lunch.getCalories() + ", Protein: " + lunch.getProtein() + "g");
                System.out.println("   Dietary Type: " + lunch.getDietaryType());
                System.out.println();
            }
        }
    }

    private static void displayDinnerSuggestions() {
        System.out.println("\n===== DINNER SUGGESTIONS =====");
        System.out.print("Heart healthy only? (yes/no): ");
        boolean heartHealthy = scanner.nextLine().equalsIgnoreCase("yes");

        System.out.print("Comfort food only? (yes/no): ");
        boolean comfortFood = scanner.nextLine().equalsIgnoreCase("yes");

        List<Dinner> dinners = mealSuggester.suggestDinners(
                currentUser.getDietaryPreference().equals("none") ? null : currentUser.getDietaryPreference(),
                heartHealthy,
                comfortFood);

        if (dinners.isEmpty()) {
            System.out.println("No dinner suggestions match your criteria.");
        } else {
            System.out.println("\nHere are your dinner suggestions:");
            for (int i = 0; i < dinners.size(); i++) {
                Dinner dinner = dinners.get(i);
                System.out.println((i + 1) + ". " + dinner.getName() + " - " + dinner.getDescription());
                System.out.println("   Calories: " + dinner.getCalories() + ", Protein: " + dinner.getProtein() + "g");
                System.out.println("   Dietary Type: " + dinner.getDietaryType());
                System.out.println();
            }
        }
    }

    private static void displaySnackSuggestions() {
        System.out.println("\n===== SNACK SUGGESTIONS =====");
        System.out.print("Low calorie only? (yes/no): ");
        boolean lowCalorie = scanner.nextLine().equalsIgnoreCase("yes");

        System.out.print("Portable only? (yes/no): ");
        boolean portable = scanner.nextLine().equalsIgnoreCase("yes");

        List<Snack> snacks = mealSuggester.suggestSnacks(
                currentUser.getDietaryPreference().equals("none") ? null : currentUser.getDietaryPreference(),
                lowCalorie,
                portable);

        if (snacks.isEmpty()) {
            System.out.println("No snack suggestions match your criteria.");
        } else {
            System.out.println("\nHere are your snack suggestions:");
            for (int i = 0; i < snacks.size(); i++) {
                Snack snack = snacks.get(i);
                System.out.println((i + 1) + ". " + snack.getName() + " - " + snack.getDescription());
                System.out.println("   Calories: " + snack.getCalories() + ", Protein: " + snack.getProtein() + "g");
                System.out.println("   Dietary Type: " + snack.getDietaryType());
                System.out.println();
            }
        }
    }

    private static void generateWeeklyPlan() {
        if (currentUser == null) {
            System.out.println("No user profile exists. Please create a new profile first.");
            createNewProfile();
            return;
        }

        System.out.println("\n===== GENERATE NEW WEEKLY MEAL PLAN =====");
        System.out.println("Note: This will create a new meal plan and overwrite any existing plan.");
        System.out.print("Do you want to continue? (yes/no): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (!confirm.equals("yes")) {
            System.out.println("Weekly meal plan generation cancelled.");
            return;
        }

        double dailyCalorieTarget = nutritionAnalyzer.calculateDailyCalorieNeeds(
                currentUser.getWeightKg(),
                currentUser.getHeightCm(),
                currentUser.getAgeYears(),
                currentUser.getGender(),
                currentUser.getActivityLevel(),
                currentUser.getWeightGoal());

        System.out.println("Your daily calorie target is: " + Math.round(dailyCalorieTarget) + " calories");
        System.out.println("Generating your weekly meal plan...");

        weeklyPlanGenerator.generateWeeklyPlan(
                currentUser.getDietaryPreference().equals("none") ? null : currentUser.getDietaryPreference(),
                dailyCalorieTarget);

        Map<String, List<Meal>> weeklyPlan = weeklyPlanGenerator.getWeeklyPlan();
        displayWeeklyPlan(weeklyPlan);

        // Save the weekly plan
        if (weeklyPlanGenerator.saveWeeklyPlan()) {
            System.out.println("Weekly meal plan saved successfully!");
        } else {
            System.out.println("Failed to save weekly meal plan.");
        }
    }

    /**
     * Displays the existing weekly meal plan without generating a new one
     */
    private static void displayExistingWeeklyPlan() {
        if (currentUser == null) {
            System.out.println("No user profile exists. Please create a new profile first.");
            createNewProfile();
            return;
        }

        System.out.println("\n===== VIEW EXISTING MEAL PLAN =====");

        // Check if a weekly plan exists
        if (!weeklyPlanGenerator.weeklyPlanExists()) {
            System.out.println("No weekly meal plan exists. Please generate a weekly plan first.");
            return;
        }

        // If the plan hasn't been loaded yet, load it
        Map<String, List<Meal>> weeklyPlan = weeklyPlanGenerator.getWeeklyPlan();
        boolean hasWeeklyPlan = weeklyPlan.values().stream()
                .anyMatch(meals -> !meals.isEmpty());

        if (!hasWeeklyPlan) {
            System.out.println("Loading your saved weekly meal plan...");
            if (weeklyPlanGenerator.loadWeeklyPlan()) {
                weeklyPlan = weeklyPlanGenerator.getWeeklyPlan();
                System.out.println("Weekly meal plan loaded successfully!");
            } else {
                System.out.println("Failed to load weekly meal plan.");
                return;
            }
        }

        // Display the weekly plan
        displayWeeklyPlan(weeklyPlan);
    }

    private static void displayWeeklyPlan(Map<String, List<Meal>> weeklyPlan) {
        System.out.println("\n===== YOUR WEEKLY MEAL PLAN =====");

        for (String day : Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")) {
            System.out.println("\n" + day + ":");
            List<Meal> dayMeals = weeklyPlan.get(day);

            if (dayMeals.isEmpty()) {
                System.out.println("No meals planned for this day.");
                continue;
            }

            // Group meals by type
            List<Meal> breakfasts = dayMeals.stream()
                    .filter(meal -> meal instanceof Breakfast).toList();

            List<Meal> lunches = dayMeals.stream()
                    .filter(meal -> meal instanceof Lunch).toList();

            List<Meal> dinners = dayMeals.stream()
                    .filter(meal -> meal instanceof Dinner).toList();

            List<Meal> snacks = dayMeals.stream()
                    .filter(meal -> meal instanceof Snack).toList();

            // Display meals by type
            if (!breakfasts.isEmpty()) {
                System.out.println("Breakfast: " + breakfasts.getFirst().getName());
            }

            if (!lunches.isEmpty()) {
                System.out.println("Lunch: " + lunches.getFirst().getName());
            }

            if (!dinners.isEmpty()) {
                System.out.println("Dinner: " + dinners.getFirst().getName());
            }

            if (!snacks.isEmpty()) {
                System.out.println("Snacks:");
                for (Meal snack : snacks) {
                    System.out.println("  - " + snack.getName());
                }
            }

            // Calculate total calories for the day
            double totalCalories = dayMeals.stream()
                    .mapToDouble(Meal::getCalories)
                    .sum();

            System.out.println("Total calories: " + Math.round(totalCalories));
        }
    }

    /**
     * Checks if a weekly plan exists and returns it if it does
     * @return The weekly plan if it exists, or null if it doesn't
     */
    private static Map<String, List<Meal>> checkWeeklyPlanExists() {
        Map<String, List<Meal>> weeklyPlan = weeklyPlanGenerator.getWeeklyPlan();
        boolean hasWeeklyPlan = weeklyPlan.values().stream()
                .anyMatch(meals -> !meals.isEmpty());

        if (!hasWeeklyPlan) {
            System.out.println("No weekly plan exists. Please generate a weekly plan first.");
            return null;
        }

        return weeklyPlan;
    }

    /**
     * Generates a shopping list based on the current weekly plan
     */
    private static void generateShoppingList() {
        if (currentUser == null) {
            System.out.println("No user profile exists. Please create a new profile first.");
            createNewProfile();
            return;
        }

        System.out.println("\n===== GENERATE SHOPPING LIST =====");

        // Check if we have a weekly plan
        Map<String, List<Meal>> weeklyPlan = checkWeeklyPlanExists();
        if (weeklyPlan == null) {
            return;
        }

        System.out.println("Generating shopping list based on your weekly meal plan...");
        shoppingListGenerator.generateFromWeeklyPlan(weeklyPlan);

        displayShoppingList();
    }

    /**
     * Displays the current shopping list
     */
    private static void displayShoppingList() {
        System.out.println("\n===== YOUR SHOPPING LIST =====");

        Map<String, Double> shoppingList = shoppingListGenerator.getShoppingList();

        if (shoppingList.isEmpty()) {
            System.out.println("Your shopping list is empty.");
            return;
        }

        // Sort ingredients alphabetically
        List<String> sortedIngredients = new ArrayList<>(shoppingList.keySet());
        Collections.sort(sortedIngredients);

        for (String ingredient : sortedIngredients) {
            double quantity = shoppingList.get(ingredient);
            String unit = shoppingListGenerator.getUnitForIngredient(ingredient);
            System.out.printf("- %.1f %s %s%n", quantity, unit, ingredient);
        }
    }

    /**
     * Analyzes nutrition based on user profile and meal plans
     */
    private static void analyzeNutrition() {
        if (currentUser == null) {
            System.out.println("No user profile exists. Please create a new profile first.");
            createNewProfile();
            return;
        }

        System.out.println("\n===== NUTRITION ANALYSIS =====");
        System.out.println("1. Analyze User Profile");
        System.out.println("2. Analyze Weekly Plan");
        System.out.println("3. Analyze Specific Meal");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice (1-4): ");

        int choice = getUserChoice(1, 4);

        switch (choice) {
            case 1:
                analyzeUserProfile();
                break;
            case 2:
                analyzeWeeklyPlan();
                break;
            case 3:
                analyzeSpecificMeal();
                break;
            case 4:
                return;
        }
    }

    /**
     * Analyzes the user's profile for nutritional information
     */
    private static void analyzeUserProfile() {
        System.out.println("\n===== USER PROFILE ANALYSIS =====");

        // Calculate BMI and display category
        double bmi = currentUser.calculateBMI();
        String bmiCategory = currentUser.getBMICategory();

        System.out.printf("BMI: %.1f (%s)%n", bmi, bmiCategory);

        // Calculate daily calorie needs
        double dailyCalories = nutritionAnalyzer.calculateDailyCalorieNeeds(
                currentUser.getWeightKg(),
                currentUser.getHeightCm(),
                currentUser.getAgeYears(),
                currentUser.getGender(),
                currentUser.getActivityLevel(),
                currentUser.getWeightGoal());

        System.out.printf("Daily Calorie Needs: %.0f calories%n", dailyCalories);

        // Calculate macronutrient distribution
        Map<String, Double> macros = nutritionAnalyzer.calculateMacronutrientDistribution(dailyCalories);

        System.out.println("\nRecommended Daily Macronutrient Distribution:");
        System.out.printf("Protein: %.0f g (%.0f calories)%n",
                macros.get("proteinGrams"), macros.get("proteinCalories"));
        System.out.printf("Carbohydrates: %.0f g (%.0f calories)%n",
                macros.get("carbGrams"), macros.get("carbCalories"));
        System.out.printf("Fat: %.0f g (%.0f calories)%n",
                macros.get("fatGrams"), macros.get("fatCalories"));
    }

    /**
     * Analyzes the weekly plan for nutritional information
     */
    private static void analyzeWeeklyPlan() {
        Map<String, List<Meal>> weeklyPlan = checkWeeklyPlanExists();
        if (weeklyPlan == null) {
            return;
        }

        System.out.println("\n===== WEEKLY PLAN ANALYSIS =====");
        String analysis = nutritionAnalyzer.analyzeWeeklyPlan(weeklyPlan);
        System.out.println(analysis);
    }

    /**
     * Analyzes a specific meal for nutritional information
     */
    private static void analyzeSpecificMeal() {
        System.out.println("\n===== MEAL ANALYSIS =====");

        // Get all meals from the meal suggester
        List<Meal> allMeals = mealSuggester.getAllMeals();

        if (allMeals.isEmpty()) {
            System.out.println("No meals available for analysis.");
            return;
        }

        // Display all meals
        System.out.println("Available meals:");
        for (int i = 0; i < allMeals.size(); i++) {
            System.out.println((i + 1) + ". " + allMeals.get(i).getName());
        }

        System.out.print("\nEnter the number of the meal to analyze (1-" + allMeals.size() + "): ");
        int mealIndex = getUserChoice(1, allMeals.size()) - 1;

        Meal selectedMeal = allMeals.get(mealIndex);
        String analysis = nutritionAnalyzer.getNutritionalSummary(selectedMeal);

        System.out.println("\n" + analysis);
    }

    /**
     * Manages recipes in the recipe database
     */
    private static void manageRecipes() {
        System.out.println("\n===== MANAGE RECIPES =====");
        System.out.println("1. View Breakfast Recipes");
        System.out.println("2. View Lunch Recipes");
        System.out.println("3. View Dinner Recipes");
        System.out.println("4. View Snack Recipes");
        System.out.println("5. Back to Main Menu");
        System.out.print("Enter your choice (1-5): ");

        int choice = getUserChoice(1, 5);

        switch (choice) {
            case 1:
                viewBreakfastRecipes();
                break;
            case 2:
                viewLunchRecipes();
                break;
            case 3:
                viewDinnerRecipes();
                break;
            case 4:
                viewSnackRecipes();
                break;
            case 5:
                return;
        }
    }

    /**
     * Displays all breakfast recipes
     */
    private static void viewBreakfastRecipes() {
        System.out.println("\n===== BREAKFAST RECIPES =====");
        List<Recipe> breakfastRecipes = recipeDatabase.stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains("breakfast") ||
                        recipe.getName().toLowerCase().contains("toast") ||
                        recipe.getName().toLowerCase().contains("cereal") ||
                        recipe.getName().toLowerCase().contains("oatmeal") ||
                        recipe.getName().toLowerCase().contains("pancake") ||
                        recipe.getName().toLowerCase().contains("waffle"))
                .collect(Collectors.toList());

        displayRecipeList(breakfastRecipes, "breakfast");
    }

    /**
     * Displays all lunch recipes
     */
    private static void viewLunchRecipes() {
        System.out.println("\n===== LUNCH RECIPES =====");
        List<Recipe> lunchRecipes = recipeDatabase.stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains("lunch") ||
                        recipe.getName().toLowerCase().contains("sandwich") ||
                        recipe.getName().toLowerCase().contains("salad") ||
                        recipe.getName().toLowerCase().contains("soup") ||
                        recipe.getName().toLowerCase().contains("wrap"))
                .collect(Collectors.toList());

        displayRecipeList(lunchRecipes, "lunch");
    }

    /**
     * Displays all dinner recipes
     */
    private static void viewDinnerRecipes() {
        System.out.println("\n===== DINNER RECIPES =====");
        List<Recipe> dinnerRecipes = recipeDatabase.stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains("dinner") ||
                        recipe.getName().toLowerCase().contains("pasta") ||
                        recipe.getName().toLowerCase().contains("steak") ||
                        recipe.getName().toLowerCase().contains("chicken") ||
                        recipe.getName().toLowerCase().contains("fish") ||
                        recipe.getName().toLowerCase().contains("salmon"))
                .collect(Collectors.toList());

        displayRecipeList(dinnerRecipes, "dinner");
    }

    /**
     * Displays all snack recipes
     */
    private static void viewSnackRecipes() {
        System.out.println("\n===== SNACK RECIPES =====");
        List<Recipe> snackRecipes = recipeDatabase.stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains("snack") ||
                        recipe.getName().toLowerCase().contains("nuts") ||
                        recipe.getName().toLowerCase().contains("yogurt") ||
                        recipe.getName().toLowerCase().contains("fruit") ||
                        recipe.getName().toLowerCase().contains("bar"))
                .collect(Collectors.toList());

        displayRecipeList(snackRecipes, "snack");
    }

    /**
     * Helper method to display a list of recipes and allow user to select one
     */
    private static void displayRecipeList(List<Recipe> recipes, String mealType) {
        if (recipes.isEmpty()) {
            System.out.println("No " + mealType + " recipes available in the database.");
            return;
        }

        // Display all recipes
        System.out.println("Select a recipe to view:");
        for (int i = 0; i < recipes.size(); i++) {
            System.out.println((i + 1) + ". " + recipes.get(i).getName());
        }

        // Get user selection
        System.out.print("\nEnter the number of the recipe to view (1-" + recipes.size() + "): ");
        int recipeIndex = getUserChoice(1, recipes.size()) - 1;

        // Display the selected recipe
        Recipe selectedRecipe = recipes.get(recipeIndex);
        displayRecipeDetails(selectedRecipe);
    }

    /**
     * Displays details of a specific recipe
     */
    private static void displayRecipeDetails(Recipe recipe) {
        System.out.println("\n===== " + recipe.getName().toUpperCase() + " =====");
        System.out.println("Difficulty: " + recipe.getDifficultyLevel());
        System.out.println("Preparation Time: " + recipe.getPreparationTimeMinutes() + " minutes");
        System.out.println("Cooking Time: " + recipe.getCookingTimeMinutes() + " minutes");
        System.out.println("Total Time: " + recipe.getTotalTimeMinutes() + " minutes");
        System.out.println("Servings: " + recipe.getServings());

        System.out.println("\nIngredients:");
        for (Ingredient ingredient : recipe.getIngredients()) {
            System.out.printf("- %.1f %s %s%n",
                    ingredient.getQuantity(),
                    ingredient.getUnit(),
                    ingredient.getName());
        }

        System.out.println("\nPreparation Steps:");
        List<String> steps = recipe.getPreparationSteps();
        for (int i = 0; i < steps.size(); i++) {
            System.out.println((i + 1) + ". " + steps.get(i));
        }

        // Calculate nutritional information
        double totalCalories = recipe.getIngredients().stream()
                .mapToDouble(Ingredient::getCalories)
                .sum();

        double totalProtein = recipe.getIngredients().stream()
                .mapToDouble(Ingredient::getProtein)
                .sum();

        double totalCarbs = recipe.getIngredients().stream()
                .mapToDouble(Ingredient::getCarbs)
                .sum();

        double totalFat = recipe.getIngredients().stream()
                .mapToDouble(Ingredient::getFat)
                .sum();

        System.out.println("\nNutritional Information (per serving):");
        System.out.printf("Calories: %.0f%n", totalCalories / recipe.getServings());
        System.out.printf("Protein: %.1fg%n", totalProtein / recipe.getServings());
        System.out.printf("Carbohydrates: %.1fg%n", totalCarbs / recipe.getServings());
        System.out.printf("Fat: %.1fg%n", totalFat / recipe.getServings());
    }

    /**
     * Adds a new recipe to the database
     */
    private static void addNewRecipe() {
        System.out.println("\n===== ADD NEW RECIPE =====");

        System.out.print("Enter recipe name: ");
        String name = scanner.nextLine();

        System.out.print("Enter preparation time (minutes): ");
        int prepTime = getIntInput();

        System.out.print("Enter cooking time (minutes): ");
        int cookTime = getIntInput();

        System.out.print("Enter number of servings: ");
        int servings = getIntInput();

        System.out.print("Enter difficulty level (Easy, Medium, Hard): ");
        String difficulty = scanner.nextLine();

        Recipe newRecipe = new Recipe(name, prepTime, cookTime, servings, difficulty);

        // Add ingredients
        boolean addingIngredients = true;
        System.out.println("\nAdd ingredients (enter 'done' when finished):");

        while (addingIngredients) {
            System.out.print("Enter ingredient name (or 'done'): ");
            String ingredientName = scanner.nextLine();

            if (ingredientName.equalsIgnoreCase("done")) {
                addingIngredients = false;
                continue;
            }

            System.out.print("Enter quantity: ");
            double quantity = getDoubleInput();

            System.out.print("Enter unit (g, ml, tsp, tbsp, etc.): ");
            String unit = scanner.nextLine();

            System.out.print("Enter calories: ");
            double calories = getDoubleInput();

            System.out.print("Enter protein (g): ");
            double protein = getDoubleInput();

            System.out.print("Enter carbs (g): ");
            double carbs = getDoubleInput();

            System.out.print("Enter fat (g): ");
            double fat = getDoubleInput();

            Ingredient ingredient = new Ingredient(ingredientName, quantity, unit, calories, protein, carbs, fat);
            newRecipe.addIngredient(ingredient);

            System.out.println("Ingredient added: " + ingredientName);
        }

        // Add preparation steps
        boolean addingSteps = true;
        System.out.println("\nAdd preparation steps (enter 'done' when finished):");
        int stepNumber = 1;

        while (addingSteps) {
            System.out.print("Enter step " + stepNumber + " (or 'done'): ");
            String step = scanner.nextLine();

            if (step.equalsIgnoreCase("done")) {
                addingSteps = false;
                continue;
            }

            newRecipe.addPreparationStep(step);
            stepNumber++;
        }

        recipeDatabase.add(newRecipe);
        System.out.println("\nRecipe '" + name + "' added successfully!");
    }

    /**
     * Displays information about the current user
     */
    private static void displayUserInfo() {
        if (currentUser == null) {
            System.out.println("No user profile exists. Please create a new profile first.");
            createNewProfile();
            return;
        }

        System.out.println("\n===== USER INFORMATION =====");
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Age: " + currentUser.getAgeYears() + " years");
        System.out.println("Gender: " + currentUser.getGender());
        System.out.println("Weight: " + currentUser.getWeightKg() + " kg");
        System.out.println("Height: " + currentUser.getHeightCm() + " cm");
        System.out.println("BMI: " + String.format("%.1f", currentUser.calculateBMI()) +
                " (" + currentUser.getBMICategory() + ")");
        System.out.println("Activity Level: " + currentUser.getActivityLevel() + "/5");
        System.out.println("Dietary Preference: " + currentUser.getDietaryPreference());
        System.out.println("Weight Goal: " + currentUser.getWeightGoal());

        if (currentUser.hasFoodAllergies()) {
            System.out.println("Food Allergies: " + String.join(", ", currentUser.getFoodAllergies()));
        } else {
            System.out.println("Food Allergies: None");
        }
    }

    /**
     * Helper method to get a valid double input from the user
     */
    private static double getDoubleInput() {
        double value = -1;
        while (value < 0) {
            try {
                value = Double.parseDouble(scanner.nextLine());
                if (value < 0) {
                    System.out.print("Please enter a non-negative number: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
        return value;
    }

    /**
     * Helper method to get a valid integer input from the user
     */
    private static int getIntInput() {
        int value = -1;
        while (value < 0) {
            try {
                value = Integer.parseInt(scanner.nextLine());
                if (value < 0) {
                    System.out.print("Please enter a non-negative number: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
        return value;
    }
}