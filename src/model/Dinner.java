package model;

import java.util.List;

/**
 * Represents a dinner meal with specific dinner-related properties.
 */
public class Dinner extends Meal {
    private boolean isHeartHealthy;
    private boolean isComfortFood;
    
    /**
     * Default constructor
     */
    public Dinner() {
        super();
    }
    
    /**
     * Constructor with basic dinner information
     */
    public Dinner(String name, String dietaryType, String description) {
        super(name, dietaryType, description);
    }
    
    /**
     * Full constructor with all dinner properties
     */
    public Dinner(String name, List<Ingredient> ingredients, String dietaryType, 
                 String description, boolean isHeartHealthy, boolean isComfortFood) {
        super(name, ingredients, dietaryType, description);
        this.isHeartHealthy = isHeartHealthy;
        this.isComfortFood = isComfortFood;
    }
    
    // Getters and Setters for dinner-specific properties
    public boolean isHeartHealthy() {
        return isHeartHealthy;
    }
    
    public void setHeartHealthy(boolean heartHealthy) {
        isHeartHealthy = heartHealthy;
    }
    
    public boolean isComfortFood() {
        return isComfortFood;
    }
    
    public void setComfortFood(boolean comfortFood) {
        isComfortFood = comfortFood;
    }
    
    @Override
    public String toString() {
        return "DINNER: " + super.toString() + 
               "\nHeart Healthy: " + (isHeartHealthy ? "Yes" : "No") + 
               ", Comfort Food: " + (isComfortFood ? "Yes" : "No");
    }
}