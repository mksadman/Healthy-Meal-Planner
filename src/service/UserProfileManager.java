package service;

import model.User;

import java.io.*;
import java.util.Arrays;

/**
 * Manages user profiles by providing functionality to save and load profiles from a file.
 */
public class UserProfileManager {
    private static final String PROFILE_FILE = "user_profile.txt";

    /**
     * Saves the user profile to a file
     * @param user The user profile to save
     * @return true if the profile was saved successfully, false otherwise
     */
    public boolean saveUserProfile(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(PROFILE_FILE))) {
            oos.writeObject(user);
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

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(PROFILE_FILE))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
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
