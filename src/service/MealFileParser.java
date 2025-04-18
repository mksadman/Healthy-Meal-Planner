package service;

import model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MealFileParser {

    public static List<Breakfast> parseBreakfastFile(String filePath) {
        List<Breakfast> breakfasts = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            Pattern sectionPattern = Pattern.compile("\\[(.+?)\\](.*?)(?=\\n\\[|\\z)", Pattern.DOTALL);
            Matcher sectionMatcher = sectionPattern.matcher(content);
            while (sectionMatcher.find()) {
                String dietaryType = sectionMatcher.group(1).trim();
                String sectionContent = sectionMatcher.group(2);
                Pattern mealPattern = Pattern.compile("\\d+\\.\\s+([^\\n]+)\\s+Calories:\\s+(\\d+)\\s+Macros:\\s+([^\\n]+)\\s+Ingredients:[\\s\\S]+?Dietary notes:\\s+([^\\n]+)");
                Matcher matcher = mealPattern.matcher(sectionContent);
                while (matcher.find()) {
                    String name = matcher.group(1).trim();
                    double calories = Double.parseDouble(matcher.group(2).trim());
                    String macrosText = matcher.group(3).trim();
                    String dietaryNotes = matcher.group(4).trim();

                    double protein = extractMacroValue(macrosText, "protein");
                    double fat = extractMacroValue(macrosText, "fat");
                    double carbs = extractMacroValue(macrosText, "carbs");
                    double fiber = extractMacroValue(macrosText, "fiber");

                    Breakfast breakfast = new Breakfast(name, dietaryType, "A healthy breakfast option");

                    breakfast.setDirectNutritionValues(calories, protein, carbs, fat);

                    breakfast.setHighProtein(protein >= 15);
                    breakfast.setContainsCaffeine(dietaryNotes.toLowerCase().contains("caffeine") ||
                            name.toLowerCase().contains("coffee") ||
                            name.toLowerCase().contains("tea"));

                    breakfasts.add(breakfast);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading breakfast file: " + e.getMessage());
        }
        return breakfasts;
    }

    public static List<Lunch> parseLunchFile(String filePath) {
        List<Lunch> lunches = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            Pattern sectionPattern = Pattern.compile("\\[(.+?)\\](.*?)(?=\\n\\[|\\z)", Pattern.DOTALL);
            Matcher sectionMatcher = sectionPattern.matcher(content);
            while (sectionMatcher.find()) {
                String dietaryType = sectionMatcher.group(1).trim();
                String sectionContent = sectionMatcher.group(2);
                Pattern mealPattern = Pattern.compile("\\d+\\.\\s+([^\\n]+)\\s+Calories:\\s+(\\d+)\\s+Macros:\\s+([^\\n]+)\\s+Ingredients:[\\s\\S]+?Dietary notes:\\s+([^\\n]+)");
                Matcher matcher = mealPattern.matcher(sectionContent);
                while (matcher.find()) {
                    String name = matcher.group(1).trim();
                    double calories = Double.parseDouble(matcher.group(2).trim());
                    String macrosText = matcher.group(3).trim();
                    String dietaryNotes = matcher.group(4).trim();

                    double protein = extractMacroValue(macrosText, "protein");
                    double fat = extractMacroValue(macrosText, "fat");
                    double carbs = extractMacroValue(macrosText, "carbs");
                    double fiber = extractMacroValue(macrosText, "fiber");

                    Lunch lunch = new Lunch(name, dietaryType, "A healthy lunch option");

                    lunch.setDirectNutritionValues(calories, protein, carbs, fat);

                    lunch.setQuickPrep(dietaryNotes.toLowerCase().contains("quick") ||
                            dietaryNotes.toLowerCase().contains("easy") ||
                            name.toLowerCase().contains("quick"));

                    lunches.add(lunch);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading lunch file: " + e.getMessage());
        }
        return lunches;
    }

    public static List<Dinner> parseDinnerFile(String filePath) {
        List<Dinner> dinners = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            Pattern sectionPattern = Pattern.compile("\\[(.+?)\\](.*?)(?=\\n\\[|\\z)", Pattern.DOTALL);
            Matcher sectionMatcher = sectionPattern.matcher(content);
            while (sectionMatcher.find()) {
                String dietaryType = sectionMatcher.group(1).trim();
                String sectionContent = sectionMatcher.group(2);
                Pattern mealPattern = Pattern.compile("\\d+\\.\\s+([^\\n]+)\\s+Calories:\\s+(\\d+)\\s+Macros:\\s+([^\\n]+)\\s+Ingredients:[\\s\\S]+?Dietary notes:\\s+([^\\n]+)");
                Matcher matcher = mealPattern.matcher(sectionContent);
                while (matcher.find()) {
                    String name = matcher.group(1).trim();
                    double calories = Double.parseDouble(matcher.group(2).trim());
                    String macrosText = matcher.group(3).trim();
                    String dietaryNotes = matcher.group(4).trim();

                    double protein = extractMacroValue(macrosText, "protein");
                    double fat = extractMacroValue(macrosText, "fat");
                    double carbs = extractMacroValue(macrosText, "carbs");
                    double fiber = extractMacroValue(macrosText, "fiber");

                    Dinner dinner = new Dinner(name, dietaryType, "A healthy dinner option");

                    dinner.setDirectNutritionValues(calories, protein, carbs, fat);

                    dinner.setHeartHealthy(dietaryNotes.toLowerCase().contains("heart") ||
                            fat < 15 ||
                            dietaryNotes.toLowerCase().contains("omega"));
                    dinner.setComfortFood(name.toLowerCase().contains("stew") ||
                            name.toLowerCase().contains("chili") ||
                            name.toLowerCase().contains("pasta") ||
                            dietaryNotes.toLowerCase().contains("comfort"));

                    dinners.add(dinner);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading dinner file: " + e.getMessage());
        }
        return dinners;
    }

    public static List<Snack> parseSnackFile(String filePath) {
        List<Snack> snacks = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            Pattern sectionPattern = Pattern.compile("\\[(.+?)\\](.*?)(?=\\n\\[|\\z)", Pattern.DOTALL);
            Matcher sectionMatcher = sectionPattern.matcher(content);
            while (sectionMatcher.find()) {
                String dietaryType = sectionMatcher.group(1).trim();
                String sectionContent = sectionMatcher.group(2);
                Pattern mealPattern = Pattern.compile("\\d+\\.\\s+([^\\n]+)\\s+Calories:\\s+(\\d+)\\s+Macros:\\s+([^\\n]+)\\s+Ingredients:[\\s\\S]+?Dietary notes:\\s+([^\\n]+)");
                Matcher matcher = mealPattern.matcher(sectionContent);
                while (matcher.find()) {
                    String name = matcher.group(1).trim();
                    double calories = Double.parseDouble(matcher.group(2).trim());
                    String macrosText = matcher.group(3).trim();
                    String dietaryNotes = matcher.group(4).trim();

                    double protein = extractMacroValue(macrosText, "protein");
                    double fat = extractMacroValue(macrosText, "fat");
                    double carbs = extractMacroValue(macrosText, "carbs");
                    double fiber = extractMacroValue(macrosText, "fiber");

                    Snack snack = new Snack(name, dietaryType, "A healthy snack option");

                    snack.setDirectNutritionValues(calories, protein, carbs, fat);

                    snack.setLowCalorie(calories <= 200);
                    snack.setPortable(dietaryNotes.toLowerCase().contains("portable") ||
                            name.toLowerCase().contains("bar") ||
                            name.toLowerCase().contains("trail mix") ||
                            dietaryNotes.toLowerCase().contains("on-the-go"));

                    snacks.add(snack);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading snack file: " + e.getMessage());
        }
        return snacks;
    }

    private static double extractMacroValue(String macrosText, String macroName) {
        Pattern pattern = Pattern.compile("(\\d+)g\\s+" + macroName);
        Matcher matcher = pattern.matcher(macrosText);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0;
    }
}