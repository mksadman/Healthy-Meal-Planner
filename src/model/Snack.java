package model;

import java.util.List;

/**
 * Represents a snack with specific snack-related properties.
 */
public class Snack extends Meal {
    private boolean isLowCalorie;
    private boolean isPortable; // Easy to carry around
    
    /**
     * Default constructor
     */
    public Snack() {
        super();
    }
    
    /**
     * Constructor with basic snack information
     */
    public Snack(String name, String dietaryType, String description) {
        super(name, dietaryType, description);
    }
    
    /**
     * Full constructor with all snack properties
     */
    public Snack(String name, List<Ingredient> ingredients, String dietaryType, 
                String description, boolean isLowCalorie, boolean isPortable) {
        super(name, ingredients, dietaryType, description);
        this.isLowCalorie = isLowCalorie;
        this.isPortable = isPortable;
    }
    
    // Getters and Setters for snack-specific properties
    public boolean isLowCalorie() {
        return isLowCalorie;
    }
    
    public void setLowCalorie(boolean lowCalorie) {
        isLowCalorie = lowCalorie;
    }
    
    public boolean isPortable() {
        return isPortable;
    }
    
    public void setPortable(boolean portable) {
        isPortable = portable;
    }
    
    @Override
    public String toString() {
        return "SNACK: " + super.toString() + 
               "\nLow Calorie: " + (isLowCalorie ? "Yes" : "No") + 
               ", Portable: " + (isPortable ? "Yes" : "No");
    }
}