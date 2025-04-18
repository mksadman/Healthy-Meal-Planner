package model;

import java.util.List;

public class Lunch extends Meal {
    private boolean isQuickPrep; // Quick to prepare (under 15 minutes)

    public Lunch() {
        super();
    }

    public Lunch(String name, String dietaryType, String description) {
        super(name, dietaryType, description);
    }

    public Lunch(String name, List<Ingredient> ingredients, String dietaryType,
                 String description, boolean isQuickPrep) {
        super(name, ingredients, dietaryType, description);
        this.isQuickPrep = isQuickPrep;
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
                ", Quick Prep: " + (isQuickPrep ? "Yes" : "No");
    }
}