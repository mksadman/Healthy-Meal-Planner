package service;

import model.*;
import java.util.*;

/**
 * Service class for generating shopping lists based on meal plans.
 */
public class ShoppingListGenerator {
    private Map<String, Double> shoppingList; // Ingredient name -> quantity
    private Map<String, String> unitMap; // Ingredient name -> unit
    
    /**
     * Constructor initializes empty shopping list
     */
    public ShoppingListGenerator() {
        this.shoppingList = new HashMap<>();
        this.unitMap = new HashMap<>();
    }
    
    /**
     * Generates a shopping list from a weekly meal plan
     */
    public void generateFromWeeklyPlan(Map<String, List<Meal>> weeklyPlan) {
        // Clear any existing shopping list
        shoppingList.clear();
        unitMap.clear();
        
        // Process all meals in the weekly plan
        for (List<Meal> dayMeals : weeklyPlan.values()) {
            for (Meal meal : dayMeals) {
                addMealToShoppingList(meal);
            }
        }
    }

    public void addMealToShoppingList(Meal meal) {
        for (Ingredient ingredient : meal.getIngredients()) {
            addIngredientToShoppingList(ingredient);
        }
    }
    
    /**
     * Adds a single ingredient to the shopping list, combining quantities if it already exists
     */
    public void addIngredientToShoppingList(Ingredient ingredient) {
        String name = ingredient.getName();
        double quantity = ingredient.getQuantity();
        String unit = ingredient.getUnit();
        
        // If ingredient already exists in the list, add to its quantity
        if (shoppingList.containsKey(name)) {
            // Check if units match
            if (!unitMap.get(name).equals(unit)) {
                System.out.println("Warning: Unit mismatch for " + name + ". Using " + unitMap.get(name) + " instead of " + unit);
            } else {
                shoppingList.put(name, shoppingList.get(name) + quantity);
            }
        } else {
            // Add new ingredient to the list
            shoppingList.put(name, quantity);
            unitMap.put(name, unit);
        }
    }
    
    /**
     * Gets the current shopping list
     */
    public Map<String, Double> getShoppingList() {
        return new HashMap<>(shoppingList);
    }
    
    /**
     * Gets the unit for a specific ingredient
     */
    public String getUnitForIngredient(String ingredientName) {
        return unitMap.get(ingredientName);
    }
    
    /**
     * Removes an ingredient from the shopping list
     */
    public boolean removeIngredientFromList(String ingredientName) {
        if (shoppingList.containsKey(ingredientName)) {
            shoppingList.remove(ingredientName);
            unitMap.remove(ingredientName);
            return true;
        }
        return false;
    }
    
    /**
     * Updates the quantity of an ingredient in the shopping list
     */
    public void updateIngredientQuantity(String ingredientName, double newQuantity) {
        if (shoppingList.containsKey(ingredientName)) {
            shoppingList.put(ingredientName, newQuantity);
        }
    }
    
    /**
     * Generates a formatted string representation of the shopping list
     */
    public String getFormattedShoppingList() {
        if (shoppingList.isEmpty()) {
            return "Shopping list is empty.";
        }
        
        StringBuilder formattedList = new StringBuilder("SHOPPING LIST\n=============\n\n");
        
        // Sort ingredients alphabetically
        List<String> sortedIngredients = new ArrayList<>(shoppingList.keySet());
        Collections.sort(sortedIngredients);
        
        for (String ingredient : sortedIngredients) {
            double quantity = shoppingList.get(ingredient);
            String unit = unitMap.get(ingredient);
            formattedList.append(String.format("%.2f %s of %s\n", quantity, unit, ingredient));
        }
        
        return formattedList.toString();
    }
    
    /**
     * Categorizes shopping list items (e.g., produce, dairy, grains)
     * This implementation categorizes ingredients based on common food groups.
     */
    public Map<String, List<String>> getCategorizedShoppingList() {
        Map<String, List<String>> categorizedList = new HashMap<>();
        
        // Initialize categories
        categorizedList.put("Produce", new ArrayList<>());
        categorizedList.put("Dairy", new ArrayList<>());
        categorizedList.put("Meat and Protein", new ArrayList<>());
        categorizedList.put("Grains and Bread", new ArrayList<>());
        categorizedList.put("Canned Goods", new ArrayList<>());
        categorizedList.put("Frozen Foods", new ArrayList<>());
        categorizedList.put("Condiments and Spices", new ArrayList<>());
        categorizedList.put("Beverages", new ArrayList<>());
        categorizedList.put("Snacks", new ArrayList<>());
        categorizedList.put("Other", new ArrayList<>());
        
        // Categorize each ingredient
        for (String ingredient : shoppingList.keySet()) {
            String category = categorizeIngredient(ingredient);
            categorizedList.get(category).add(ingredient);
        }
        
        // Remove empty categories
        categorizedList.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        
        return categorizedList;
    }
    
    /**
     * Determines the category for an ingredient based on its name
     * This is a simplified implementation using common ingredient names
     */
    private String categorizeIngredient(String ingredient) {
        String lowerCaseIngredient = ingredient.toLowerCase();
        
        // Produce (fruits and vegetables)
        if (lowerCaseIngredient.contains("apple") || lowerCaseIngredient.contains("banana") ||
            lowerCaseIngredient.contains("orange") || lowerCaseIngredient.contains("grape") ||
            lowerCaseIngredient.contains("berry") || lowerCaseIngredient.contains("melon") ||
            lowerCaseIngredient.contains("lettuce") || lowerCaseIngredient.contains("spinach") ||
            lowerCaseIngredient.contains("carrot") || lowerCaseIngredient.contains("potato") ||
            lowerCaseIngredient.contains("onion") || lowerCaseIngredient.contains("tomato") ||
            lowerCaseIngredient.contains("pepper") || lowerCaseIngredient.contains("broccoli") ||
            lowerCaseIngredient.contains("cucumber") || lowerCaseIngredient.contains("avocado")) {
            return "Produce";
        }
        
        // Dairy
        if (lowerCaseIngredient.contains("milk") || lowerCaseIngredient.contains("cheese") ||
            lowerCaseIngredient.contains("yogurt") || lowerCaseIngredient.contains("butter") ||
            lowerCaseIngredient.contains("cream") || lowerCaseIngredient.contains("egg")) {
            return "Dairy";
        }
        
        // Meat and Protein
        if (lowerCaseIngredient.contains("chicken") || lowerCaseIngredient.contains("beef") ||
            lowerCaseIngredient.contains("pork") || lowerCaseIngredient.contains("fish") ||
            lowerCaseIngredient.contains("seafood") || lowerCaseIngredient.contains("tofu") ||
            lowerCaseIngredient.contains("bean") || lowerCaseIngredient.contains("lentil") ||
            lowerCaseIngredient.contains("turkey") || lowerCaseIngredient.contains("lamb")) {
            return "Meat and Protein";
        }
        
        // Grains and Bread
        if (lowerCaseIngredient.contains("bread") || lowerCaseIngredient.contains("rice") ||
            lowerCaseIngredient.contains("pasta") || lowerCaseIngredient.contains("cereal") ||
            lowerCaseIngredient.contains("oat") || lowerCaseIngredient.contains("flour") ||
            lowerCaseIngredient.contains("tortilla") || lowerCaseIngredient.contains("quinoa")) {
            return "Grains and Bread";
        }
        
        // Canned Goods
        if (lowerCaseIngredient.contains("canned") || lowerCaseIngredient.contains("soup") ||
            lowerCaseIngredient.contains("sauce") || lowerCaseIngredient.contains("broth")) {
            return "Canned Goods";
        }
        
        // Frozen Foods
        if (lowerCaseIngredient.contains("frozen")) {
            return "Frozen Foods";
        }
        
        // Condiments and Spices
        if (lowerCaseIngredient.contains("salt") || lowerCaseIngredient.contains("pepper") ||
            lowerCaseIngredient.contains("spice") || lowerCaseIngredient.contains("herb") ||
            lowerCaseIngredient.contains("oil") || lowerCaseIngredient.contains("vinegar") ||
            lowerCaseIngredient.contains("sauce") || lowerCaseIngredient.contains("dressing") ||
            lowerCaseIngredient.contains("mayo") || lowerCaseIngredient.contains("ketchup") ||
            lowerCaseIngredient.contains("mustard")) {
            return "Condiments and Spices";
        }
        
        // Beverages
        if (lowerCaseIngredient.contains("water") || lowerCaseIngredient.contains("juice") ||
            lowerCaseIngredient.contains("soda") || lowerCaseIngredient.contains("coffee") ||
            lowerCaseIngredient.contains("tea") || lowerCaseIngredient.contains("drink")) {
            return "Beverages";
        }
        
        // Snacks
        if (lowerCaseIngredient.contains("chip") || lowerCaseIngredient.contains("cracker") ||
            lowerCaseIngredient.contains("nut") || lowerCaseIngredient.contains("cookie") ||
            lowerCaseIngredient.contains("candy") || lowerCaseIngredient.contains("chocolate") ||
            lowerCaseIngredient.contains("snack")) {
            return "Snacks";
        }
        
        // Default to Other if no category matches
        return "Other";
    }
    
    /**
     * Generates a formatted shopping list organized by categories
     */
    public String getFormattedCategorizedShoppingList() {
        if (shoppingList.isEmpty()) {
            return "Shopping list is empty.";
        }
        
        StringBuilder formattedList = new StringBuilder("CATEGORIZED SHOPPING LIST\n========================\n\n");
        Map<String, List<String>> categorizedList = getCategorizedShoppingList();
        
        // Sort categories alphabetically
        List<String> sortedCategories = new ArrayList<>(categorizedList.keySet());
        Collections.sort(sortedCategories);
        
        for (String category : sortedCategories) {
            formattedList.append(category.toUpperCase()).append(":\n");
            
            // Sort ingredients within each category
            List<String> ingredients = categorizedList.get(category);
            Collections.sort(ingredients);
            
            for (String ingredient : ingredients) {
                double quantity = shoppingList.get(ingredient);
                String unit = unitMap.get(ingredient);
                formattedList.append(String.format("  - %.2f %s of %s\n", quantity, unit, ingredient));
            }
            
            formattedList.append("\n");
        }
        
        return formattedList.toString();
    }
    
    /**
     * Exports the shopping list to a format that can be saved to a file
     */
    public String exportShoppingList() {
        if (shoppingList.isEmpty()) {
            return "Shopping list is empty.";
        }
        
        StringBuilder export = new StringBuilder();
        Map<String, List<String>> categorizedList = getCategorizedShoppingList();
        
        for (String category : categorizedList.keySet()) {
            export.append(category).append(":\n");
            
            for (String ingredient : categorizedList.get(category)) {
                double quantity = shoppingList.get(ingredient);
                String unit = unitMap.get(ingredient);
                export.append(String.format("%.2f,%s,%s\n", quantity, unit, ingredient));
            }
            
            export.append("\n");
        }
        
        return export.toString();
    }
}