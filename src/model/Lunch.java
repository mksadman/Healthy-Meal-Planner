package model;

import java.util.List;

/**
 * Represents a lunch meal with specific lunch-related properties.
 */
public class Lunch extends Meal {
    private boolean isLowCarb;
    private boolean isQuickPrep; // Quick to prepare (under 15 minutes)
    
    /**
     * Default constructor
     */
    public Lunch() {
        super();
    }
    
    /**
     * Constructor with basic lunch information
     */
    public Lunch(String name, String dietaryType, String description) {
        super(name, dietaryType, description);
    }
    
    /**
     * Full constructor with all lunch properties
     */
    public Lunch(String name, List<Ingredient> ingredients, String dietaryType, 
                String description, boolean isLowCarb, boolean isQuickPrep) {
        super(name, ingredients, dietaryType, description);
        this.isLowCarb = isLowCarb;
        this.isQuickPrep = isQuickPrep;
    }
    
    // Getters and Setters for lunch-specific properties
    public boolean isLowCarb() {
        return isLowCarb;
    }
    
    public void setLowCarb(boolean lowCarb) {
        isLowCarb = lowCarb;
    }
    
    public boolean isQuickPrep() {
        return isQuickPrep;
    }
    
    public void setQuickPrep(boolean quickPrep) {
        isQuickPrep = quickPrep;
    }
    
    @Override
    public String toString() {
        return "LUNCH: " + super.toString() + 
               "\nLow Carb: " + (isLowCarb ? "Yes" : "No") + 
               ", Quick Prep: " + (isQuickPrep ? "Yes" : "No");
    }
}