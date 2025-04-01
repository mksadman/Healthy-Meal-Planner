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
        System.out.println("Welcome to the Healthy Meal Planner!");
        initializeSystem();
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getUserChoice(1, 8);
            
            switch (choice) {
                case 1:
                    manageUserProfile();
                    break;
                case 2:
                    viewMealSuggestions();
                    break;
                case 3:
                    generateWeeklyPlan();
                    break;
                case 4:
                    generateShoppingList();
                    break;
                case 5:
                    analyzeNutrition();
                    break;
                case 6:
                    manageRecipes();
                    break;
                case 7:
                    displayUserInfo();
                    break;
                case 8:
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

        addSampleMeals();
        addSampleRecipes();

        if (userProfileManager.profileExists()) {
            currentUser = userProfileManager.loadUserProfile();
            System.out.println("Welcome back, " + currentUser.getName() + "!");
        } else {
            System.out.println("Please create a user profile to get started.");
            createNewProfile();
        }
    }

    private static void addSampleMeals() {
        Ingredient egg = new Ingredient("Egg", 2, "units", 140, 12, 1, 10);
        Ingredient bread = new Ingredient("Whole Wheat Bread", 2, "slices", 160, 8, 30, 2);
        Ingredient avocado = new Ingredient("Avocado", 1, "unit", 240, 3, 12, 22);
        Ingredient chicken = new Ingredient("Chicken Breast", 150, "g", 250, 45, 0, 5);
        Ingredient rice = new Ingredient("Brown Rice", 100, "g", 130, 3, 28, 1);
        Ingredient broccoli = new Ingredient("Broccoli", 100, "g", 55, 4, 11, 0.5);
        Ingredient salmon = new Ingredient("Salmon Fillet", 150, "g", 280, 40, 0, 12);
        Ingredient sweetPotato = new Ingredient("Sweet Potato", 150, "g", 130, 2, 30, 0);
        Ingredient yogurt = new Ingredient("Greek Yogurt", 150, "g", 130, 15, 6, 4);
        Ingredient berries = new Ingredient("Mixed Berries", 100, "g", 60, 1, 14, 0.5);
        Ingredient nuts = new Ingredient("Mixed Nuts", 30, "g", 180, 6, 6, 16);

        Breakfast avocadoToast = new Breakfast("Avocado Toast", "vegetarian", "Avocado on whole wheat toast with eggs");
        avocadoToast.addIngredient(egg);
        avocadoToast.addIngredient(bread);
        avocadoToast.addIngredient(avocado);
        avocadoToast.setHighProtein(true);
        avocadoToast.setContainsCaffeine(false);
        mealSuggester.addMeal(avocadoToast);

        Lunch chickenRice = new Lunch("Chicken and Rice Bowl", "none", "Grilled chicken with brown rice and broccoli");
        chickenRice.addIngredient(chicken);
        chickenRice.addIngredient(rice);
        chickenRice.addIngredient(broccoli);
        chickenRice.setLowCarb(false);
        chickenRice.setQuickPrep(true);
        mealSuggester.addMeal(chickenRice);

        Dinner salmonDinner = new Dinner("Baked Salmon", "none", "Baked salmon with sweet potato");
        salmonDinner.addIngredient(salmon);
        salmonDinner.addIngredient(sweetPotato);
        salmonDinner.setHeartHealthy(true);
        salmonDinner.setComfortFood(false);
        mealSuggester.addMeal(salmonDinner);

        Snack yogurtBerries = new Snack("Yogurt with Berries", "vegetarian", "Greek yogurt with mixed berries");
        yogurtBerries.addIngredient(yogurt);
        yogurtBerries.addIngredient(berries);
        yogurtBerries.setLowCalorie(true);
        yogurtBerries.setPortable(true);
        mealSuggester.addMeal(yogurtBerries);

        Snack mixedNuts = new Snack("Mixed Nuts", "vegan", "Assorted nuts");
        mixedNuts.addIngredient(nuts);
        mixedNuts.setLowCalorie(false);
        mixedNuts.setPortable(true);
        mealSuggester.addMeal(mixedNuts);
    }

    private static void addSampleRecipes() {
        Recipe avocadoToast = new Recipe("Avocado Toast", 5, 5, 1, "Easy");
        avocadoToast.addIngredient(new Ingredient("Whole Wheat Bread", 2, "slices", 160, 8, 30, 2));
        avocadoToast.addIngredient(new Ingredient("Avocado", 1, "unit", 240, 3, 12, 22));
        avocadoToast.addIngredient(new Ingredient("Salt", 1, "pinch", 0, 0, 0, 0));
        avocadoToast.addIngredient(new Ingredient("Pepper", 1, "pinch", 0, 0, 0, 0));
        avocadoToast.addPreparationStep("Toast the bread until golden brown.");
        avocadoToast.addPreparationStep("Mash the avocado in a bowl.");
        avocadoToast.addPreparationStep("Spread the mashed avocado on the toast.");
        avocadoToast.addPreparationStep("Season with salt and pepper.");
        recipeDatabase.add(avocadoToast);

        Recipe chickenRice = new Recipe("Chicken and Rice Bowl", 10, 20, 2, "Medium");
        chickenRice.addIngredient(new Ingredient("Chicken Breast", 300, "g", 500, 90, 0, 10));
        chickenRice.addIngredient(new Ingredient("Brown Rice", 200, "g", 260, 6, 56, 2));
        chickenRice.addIngredient(new Ingredient("Broccoli", 200, "g", 110, 8, 22, 1));
        chickenRice.addIngredient(new Ingredient("Olive Oil", 15, "ml", 120, 0, 0, 14));
        chickenRice.addIngredient(new Ingredient("Salt", 1, "tsp", 0, 0, 0, 0));
        chickenRice.addIngredient(new Ingredient("Pepper", 1, "tsp", 0, 0, 0, 0));
        chickenRice.addPreparationStep("Cook rice according to package instructions.");
        chickenRice.addPreparationStep("Season chicken with salt and pepper.");
        chickenRice.addPreparationStep("Heat olive oil in a pan and cook chicken until done.");
        chickenRice.addPreparationStep("Steam broccoli until tender.");
        chickenRice.addPreparationStep("Combine all ingredients in a bowl.");
        recipeDatabase.add(chickenRice);
    }

    private static void displayMainMenu() {
        System.out.println("\n===== HEALTHY MEAL PLANNER MENU =====");
        System.out.println("1. Manage User Profile");
        System.out.println("2. View Meal Suggestions");
        System.out.println("3. Generate Weekly Meal Plan");
        System.out.println("4. Generate Shopping List");
        System.out.println("5. Analyze Nutrition");
        System.out.println("6. Manage Recipes");
        System.out.println("7. Display User Information");
        System.out.println("8. Exit");
        System.out.print("Enter your choice (1-8): ");
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
        
        System.out.println("\n===== GENERATE WEEKLY MEAL PLAN =====");

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

        System.out.print("\nWould you like to generate a shopping list for this meal plan? (yes/no): ");
        String generateList = scanner.nextLine().toLowerCase();
        if (generateList.equals("yes")) {
            shoppingListGenerator.generateFromWeeklyPlan(weeklyPlan);
            displayShoppingList();
        }
    }
    
    /**
     * Displays the weekly meal plan
     */
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
        System.out.println("1. View All Recipes");
        System.out.println("2. View Recipe Details");
        System.out.println("3. Add New Recipe");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice (1-4): ");
        
        int choice = getUserChoice(1, 4);
        
        switch (choice) {
            case 1:
                viewAllRecipes();
                break;
            case 2:
                viewRecipeDetails();
                break;
            case 3:
                addNewRecipe();
                break;
            case 4:
                return;
        }
    }
    
    /**
     * Displays all recipes in the database
     */
    private static void viewAllRecipes() {
        System.out.println("\n===== ALL RECIPES =====");
        
        if (recipeDatabase.isEmpty()) {
            System.out.println("No recipes in the database.");
            return;
        }
        
        for (int i = 0; i < recipeDatabase.size(); i++) {
            Recipe recipe = recipeDatabase.get(i);
            System.out.println((i + 1) + ". " + recipe.getName() + 
                    " (" + recipe.getDifficultyLevel() + ", " + 
                    recipe.getTotalTimeMinutes() + " min)");
        }
    }
    
    /**
     * Displays details of a specific recipe
     */
    private static void viewRecipeDetails() {
        if (recipeDatabase.isEmpty()) {
            System.out.println("No recipes in the database.");
            return;
        }
        
        viewAllRecipes();
        
        System.out.print("\nEnter the number of the recipe to view (1-" + recipeDatabase.size() + "): ");
        int recipeIndex = getUserChoice(1, recipeDatabase.size()) - 1;
        
        Recipe recipe = recipeDatabase.get(recipeIndex);
        
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