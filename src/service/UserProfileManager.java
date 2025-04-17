package service;

import model.User;
import java.io.*;
import java.util.Arrays;

/**
 * Manages user profiles by providing functionality to save and load profiles from a file.
 */
public class UserProfileManager {
    private static final String PROFILE_FILE = "user_profile.txt";
    private static final String DELIMITER = ",";
    private static final String ALLERGY_DELIMITER = ";";

    /**
     * Converts a User object to a CSV line
     */
    private String userToCsv(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(escape(user.getName())).append(DELIMITER)
          .append(user.getWeightKg()).append(DELIMITER)
          .append(user.getHeightCm()).append(DELIMITER)
          .append(user.getAgeYears()).append(DELIMITER)
          .append(escape(user.getGender())).append(DELIMITER)
          .append(user.getActivityLevel()).append(DELIMITER)
          .append(escape(user.getDietaryPreference())).append(DELIMITER)
          .append(escape(user.getWeightGoal())).append(DELIMITER)
          .append(user.hasFoodAllergies()).append(DELIMITER);
        if (user.getFoodAllergies() != null && user.getFoodAllergies().length > 0) {
            sb.append(escape(String.join(ALLERGY_DELIMITER, user.getFoodAllergies())));
        } else {
            sb.append("");
        }
        return sb.toString();
    }

    /**
     * Converts a CSV line to a User object
     */
    private User csvToUser(String line) {
        String[] parts = splitCsv(line);
        User user = new User();
        int i = 0;
        user.setName(unescape(parts[i++]));
        user.setWeightKg(Double.parseDouble(parts[i++]));
        user.setHeightCm(Double.parseDouble(parts[i++]));
        user.setAgeYears(Integer.parseInt(parts[i++]));
        user.setGender(unescape(parts[i++]));
        user.setActivityLevel(Integer.parseInt(parts[i++]));
        user.setDietaryPreference(unescape(parts[i++]));
        user.setWeightGoal(unescape(parts[i++]));
        user.setHasFoodAllergies(Boolean.parseBoolean(parts[i++]));
        if (parts.length > i && !parts[i].isEmpty()) {
            user.setFoodAllergies(unescape(parts[i]).split(ALLERGY_DELIMITER));
        } else {
            user.setFoodAllergies(new String[0]);
        }
        return user;
    }

    /**
     * Escapes a string for CSV (handles commas, quotes, and newlines)
     */
    private String escape(String s) {
        if (s == null) return "";
        if (s.contains(DELIMITER) || s.contains("\"") || s.contains("\n") || s.contains("\r") || s.contains(ALLERGY_DELIMITER)) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    /**
     * Unescapes a CSV field
     */
    private String unescape(String s) {
        if (s == null) return "";
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    /**
     * Splits a CSV line into fields, handling quoted fields
     */
    private String[] splitCsv(String line) {
        // Simple CSV parser for single-line, non-nested CSV
        if (line == null || line.isEmpty()) return new String[0];
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * Saves the user profile to a file
     * @param user The user profile to save
     * @return true if the profile was saved successfully, false otherwise
     */
    public boolean saveUserProfile(User user) {
        try (Writer writer = new FileWriter(PROFILE_FILE)) {
            String csv = userToCsv(user);
            writer.write(csv);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads a user profile from a file
     * @return The loaded user profile, or null if no profile exists or an error occurred
     */
    public User loadUserProfile() {
        File profileFile = new File(PROFILE_FILE);
        if (!profileFile.exists()) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(PROFILE_FILE))) {
            String line = reader.readLine();
            if (line == null || line.isEmpty()) return null;
            return csvToUser(line);
        } catch (IOException e) {
            System.err.println("Error loading user profile: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if a user profile exists
     * @return true if a profile exists, false otherwise
     */
    public boolean profileExists() {
        File profileFile = new File(PROFILE_FILE);
        return profileFile.exists() && profileFile.length() > 0;
    }
}
