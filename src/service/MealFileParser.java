package service;

import model.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing meal data from text files.
 */
public class MealFileParser {

    /**
     * Parses a breakfast meal file and returns a list of Breakfast objects.
     *
     * @param filePath Path to the breakfast meal file
     * @return List of Breakfast objects
     */
    public static List<Breakfast> parseBreakfastFile(String filePath) {
        List<Breakfast> breakfasts = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            Pattern mealPattern = Pattern.compile("\\d+\\.\\s+([^\\n]+)\\s+Calories:\\s+(\\d+)\\s+Macros:\\s+([^\\n]+)\\s+Ingredients:[\\s\\S]+?Dietary notes:\\s+([^\\n]+)");
            Matcher matcher = mealPattern.matcher(content);

            while (matcher.find()) {
                String name = matcher.group(1).trim();
                double calories = Double.parseDouble(matcher.group(2).trim());
                String macrosText = matcher.group(3).trim();
                String dietaryNotes = matcher.group(4).trim();

                // Parse macros
                double protein = extractMacroValue(macrosText, "protein");
                double fat = extractMacroValue(macrosText, "fat");
                double carbs = extractMacroValue(macrosText, "carbs");
                double fiber = extractMacroValue(macrosText, "fiber");

                // Create breakfast object
                Breakfast breakfast = new Breakfast(name, dietaryNotes, "A healthy breakfast option");

                // Set nutrition values directly since we don't have detailed ingredient information
                breakfast.setDirectNutritionValues(calories, protein, carbs, fat);

                // Set breakfast-specific properties based on dietary notes or macros
                breakfast.setHighProtein(protein >= 15); // If protein is 15g or more, consider it high protein
                breakfast.setContainsCaffeine(dietaryNotes.toLowerCase().contains("caffeine") ||
                        name.toLowerCase().contains("coffee") ||
                        name.toLowerCase().contains("tea"));

                breakfasts.add(breakfast);
            }
        } catch (IOException e) {
            System.err.println("Error reading breakfast file: " + e.getMessage());
        }
        return breakfasts;
    }

    /**
     * Parses a lunch meal file and returns a list of Lunch objects.
     *
     * @param filePath Path to the lunch meal file
     * @return List of Lunch objects
     */
    public static List<Lunch> parseLunchFile(String filePath) {
        List<Lunch> lunches = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            Pattern mealPattern = Pattern.compile("\\d+\\.\\s+([^\\n]+)\\s+Calories:\\s+(\\d+)\\s+Macros:\\s+([^\\n]+)\\s+Ingredients:[\\s\\S]+?Dietary notes:\\s+([^\\n]+)");
            Matcher matcher = mealPattern.matcher(content);

            while (matcher.find()) {
                String name = matcher.group(1).trim();
                double calories = Double.parseDouble(matcher.group(2).trim());
                String macrosText = matcher.group(3).trim();
                String dietaryNotes = matcher.group(4).trim();

                // Parse macros
                double protein = extractMacroValue(macrosText, "protein");
                double fat = extractMacroValue(macrosText, "fat");
                double carbs = extractMacroValue(macrosText, "carbs");
                double fiber = extractMacroValue(macrosText, "fiber");

                // Create lunch object
                Lunch lunch = new Lunch(name, dietaryNotes, "A healthy lunch option");

                // Set nutrition values directly since we don't have detailed ingredient information
                lunch.setDirectNutritionValues(calories, protein, carbs, fat);

                // Set lunch-specific properties based on dietary notes or macros
                lunch.setLowCarb(carbs < 30); // If carbs are less than 30g, consider it low-carb
                lunch.setQuickPrep(dietaryNotes.toLowerCase().contains("quick") ||
                        dietaryNotes.toLowerCase().contains("easy") ||
                        name.toLowerCase().contains("quick"));

                lunches.add(lunch);
            }
        } catch (IOException e) {
            System.err.println("Error reading lunch file: " + e.getMessage());
        }
        return lunches;
    }

    /**
     * Parses a dinner meal file and returns a list of Dinner objects.
     *
     * @param filePath Path to the dinner meal file
     * @return List of Dinner objects
     */
    public static List<Dinner> parseDinnerFile(String filePath) {
        List<Dinner> dinners = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            Pattern mealPattern = Pattern.compile("\\d+\\.\\s+([^\\n]+)\\s+Calories:\\s+(\\d+)\\s+Macros:\\s+([^\\n]+)\\s+Ingredients:[\\s\\S]+?Dietary notes:\\s+([^\\n]+)");
            Matcher matcher = mealPattern.matcher(content);

            while (matcher.find()) {
                String name = matcher.group(1).trim();
                double calories = Double.parseDouble(matcher.group(2).trim());
                String macrosText = matcher.group(3).trim();
                String dietaryNotes = matcher.group(4).trim();

                // Parse macros
                double protein = extractMacroValue(macrosText, "protein");
                double fat = extractMacroValue(macrosText, "fat");
                double carbs = extractMacroValue(macrosText, "carbs");
                double fiber = extractMacroValue(macrosText, "fiber");

                // Create dinner object
                Dinner dinner = new Dinner(name, dietaryNotes, "A healthy dinner option");

                // Set nutrition values directly since we don't have detailed ingredient information
                dinner.setDirectNutritionValues(calories, protein, carbs, fat);

                // Set dinner-specific properties based on dietary notes or macros
                dinner.setHeartHealthy(dietaryNotes.toLowerCase().contains("heart") ||
                        fat < 15 ||
                        dietaryNotes.toLowerCase().contains("omega"));
                dinner.setComfortFood(name.toLowerCase().contains("stew") ||
                        name.toLowerCase().contains("chili") ||
                        name.toLowerCase().contains("pasta") ||
                        dietaryNotes.toLowerCase().contains("comfort"));

                dinners.add(dinner);
            }
        } catch (IOException e) {
            System.err.println("Error reading dinner file: " + e.getMessage());
        }
        return dinners;
    }

    /**
     * Parses a snack file and returns a list of Snack objects.
     *
     * @param filePath Path to the snack file
     * @return List of Snack objects
     */
    public static List<Snack> parseSnackFile(String filePath) {
        List<Snack> snacks = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            Pattern mealPattern = Pattern.compile("\\d+\\.\\s+([^\\n]+)\\s+Calories:\\s+(\\d+)\\s+Macros:\\s+([^\\n]+)\\s+Ingredients:[\\s\\S]+?Dietary notes:\\s+([^\\n]+)");
            Matcher matcher = mealPattern.matcher(content);

            while (matcher.find()) {
                String name = matcher.group(1).trim();
                double calories = Double.parseDouble(matcher.group(2).trim());
                String macrosText = matcher.group(3).trim();
                String dietaryNotes = matcher.group(4).trim();

                // Parse macros
                double protein = extractMacroValue(macrosText, "protein");
                double fat = extractMacroValue(macrosText, "fat");
                double carbs = extractMacroValue(macrosText, "carbs");
                double fiber = extractMacroValue(macrosText, "fiber");

                // Create snack object
                Snack snack = new Snack(name, dietaryNotes, "A healthy snack option");

                // Set nutrition values directly since we don't have detailed ingredient information
                snack.setDirectNutritionValues(calories, protein, carbs, fat);

                // Set snack-specific properties based on dietary notes or calories
                snack.setLowCalorie(calories <= 200); // If calories are 200 or less, consider it low calorie
                snack.setPortable(dietaryNotes.toLowerCase().contains("portable") ||
                        name.toLowerCase().contains("bar") ||
                        name.toLowerCase().contains("trail mix") ||
                        dietaryNotes.toLowerCase().contains("on-the-go"));

                snacks.add(snack);
            }
        } catch (IOException e) {
            System.err.println("Error reading snack file: " + e.getMessage());
        }
        return snacks;
    }

    /**
     * Helper method to extract macro nutrient values from the macros text.
     *
     * @param macrosText Text containing macro information (e.g., "15g protein, 10g fat, 30g carbs, 5g fiber")
     * @param macroName Name of the macro to extract (e.g., "protein", "fat", "carbs", "fiber")
     * @return The value of the specified macro, or 0 if not found
     */
    private static double extractMacroValue(String macrosText, String macroName) {
        Pattern pattern = Pattern.compile("(\\d+)g\\s+" + macroName);
        Matcher matcher = pattern.matcher(macrosText);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0;
    }
}