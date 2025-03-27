package model;

import java.util.List;

/**
 * Represents a breakfast meal with specific breakfast-related properties.
 */
public class Breakfast extends Meal {
    private boolean isHighProtein;
    private boolean containsCaffeine;
    
    /**
     * Default constructor
     */
    public Breakfast() {
        super();
    }
    
    /**
     * Constructor with basic breakfast information
     */
    public Breakfast(String name, String dietaryType, String description) {
        super(name, dietaryType, description);
    }
    
    /**
     * Full constructor with all breakfast properties
     */
    public Breakfast(String name, List<Ingredient> ingredients, String dietaryType, 
                    String description, boolean isHighProtein, boolean containsCaffeine) {
        super(name, ingredients, dietaryType, description);
        this.isHighProtein = isHighProtein;
        this.containsCaffeine = containsCaffeine;
    }
    
    // Getters and Setters for breakfast-specific properties
    public boolean isHighProtein() {
        return isHighProtein;
    }
    
    public void setHighProtein(boolean highProtein) {
        isHighProtein = highProtein;
    }
    
    public boolean containsCaffeine() {
        return containsCaffeine;
    }
    
    public void setContainsCaffeine(boolean containsCaffeine) {
        this.containsCaffeine = containsCaffeine;
    }
    
    @Override
    public String toString() {
        return "BREAKFAST: " + super.toString() + 
               "\nHigh Protein: " + (isHighProtein ? "Yes" : "No") + 
               ", Contains Caffeine: " + (containsCaffeine ? "Yes" : "No");
    }
}