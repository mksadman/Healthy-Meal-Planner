package model;

import java.io.Serializable;

/**
 * Represents a user of the meal planning system with personal information and dietary preferences.
 */
public class User implements Serializable {
    private String name;
    private double weightKg;
    private double heightCm;
    private int ageYears;
    private String gender; // "male" or "female"
    private int activityLevel; // 1-5, where 1 is sedentary and 5 is very active
    private String dietaryPreference; // vegetarian, keto, paleo, etc.
    private String weightGoal; // "maintain", "lose", or "gain"
    private boolean hasFoodAllergies;
    private String[] foodAllergies;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor with basic user information
     */
    public User(String name, double weightKg, double heightCm, int ageYears, String gender) {
        this.name = name;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.ageYears = ageYears;
        this.gender = gender;
        this.activityLevel = 1; // Default to sedentary
        this.dietaryPreference = "none"; // Default to no specific preference
        this.weightGoal = "maintain"; // Default to maintain weight
        this.hasFoodAllergies = false;
        this.foodAllergies = new String[0];
    }

    /**
     * Full constructor with all user properties
     */
    public User(String name, double weightKg, double heightCm, int ageYears, String gender,
                int activityLevel, String dietaryPreference, String weightGoal,
                boolean hasFoodAllergies, String[] foodAllergies) {
        this.name = name;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.ageYears = ageYears;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.dietaryPreference = dietaryPreference;
        this.weightGoal = weightGoal;
        this.hasFoodAllergies = hasFoodAllergies;
        this.foodAllergies = foodAllergies;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public int getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(int ageYears) {
        this.ageYears = ageYears;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        if (activityLevel < 1 || activityLevel > 5) {
            throw new IllegalArgumentException("Activity level must be between 1 and 5");
        }
        this.activityLevel = activityLevel;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }

    public String getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(String weightGoal) {
        if (!weightGoal.equals("maintain") && !weightGoal.equals("lose") && !weightGoal.equals("gain")) {
            throw new IllegalArgumentException("Weight goal must be 'maintain', 'lose', or 'gain'");
        }
        this.weightGoal = weightGoal;
    }

    public boolean hasFoodAllergies() {
        return hasFoodAllergies;
    }

    public void setHasFoodAllergies(boolean hasFoodAllergies) {
        this.hasFoodAllergies = hasFoodAllergies;
    }

    public String[] getFoodAllergies() {
        return foodAllergies;
    }

    public void setFoodAllergies(String[] foodAllergies) {
        this.foodAllergies = foodAllergies;
        this.hasFoodAllergies = (foodAllergies != null && foodAllergies.length > 0);
    }

    /**
     * Calculates the Body Mass Index (BMI) of the user
     * @return The BMI value
     */
    public double calculateBMI() {
        // BMI = weight(kg) / (height(m) * height(m))
        double heightInMeters = heightCm / 100.0;
        return weightKg / (heightInMeters * heightInMeters);
    }

    /**
     * Determines the BMI category of the user
     * @return A string representing the BMI category
     */
    public String getBMICategory() {
        double bmi = calculateBMI();

        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    /**
     * Checks if a specific food is in the user's allergy list
     * @param food The food to check
     * @return True if the user is allergic to the food, false otherwise
     */
    public boolean isAllergicTo(String food) {
        if (!hasFoodAllergies || foodAllergies == null) {
            return false;
        }

        for (String allergy : foodAllergies) {
            if (allergy.equalsIgnoreCase(food)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return name + " - " + ageYears + "y, " +
                weightKg + "kg, " + heightCm + "cm, " +
                gender + ", activity level: " + activityLevel +
                ", goal: " + weightGoal +
                ", diet: " + dietaryPreference;
    }
}