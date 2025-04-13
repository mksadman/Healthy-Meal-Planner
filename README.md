# Healthy Meal Planner

## Overview
Healthy Meal Planner is a Java application designed to help users plan nutritious meals based on their dietary preferences, health goals, and nutritional requirements. The application provides personalized meal suggestions, generates weekly meal plans, creates shopping lists, and offers nutrition analysis to support healthy eating habits.

## Features

### User Profile Management
- Create and edit detailed user profiles
- Track personal information (weight, height, age, gender)
- Set activity levels and weight goals (maintain, lose, gain)
- Specify dietary preferences (normal, vegetarian, vegan, gluten-free, low-carb, keto)
- Record food allergies for safe meal planning

### Meal Suggestions
- Browse meal options filtered by dietary preferences
- Get personalized breakfast suggestions with options for high protein and caffeine-free meals
- Find lunch options with filters for low-carb and quick preparation
- Discover dinner ideas with heart-healthy and comfort food options
- Access snack recommendations with low-calorie and portable options

### Weekly Meal Planning
- Generate complete weekly meal plans based on your profile
- Automatically calculate daily calorie targets based on your goals
- View and manage existing meal plans
- Balanced distribution of meals throughout the week

### Shopping List Generation
- Automatically create shopping lists from your weekly meal plan
- Organize ingredients with quantities and units
- Simplify grocery shopping with consolidated lists

### Nutrition Analysis
- Analyze nutritional content of your meal plans
- Calculate daily calorie needs based on your profile
- Track protein, carbohydrates, and fat intake
- Ensure your meal plans align with your health goals

### Recipe Management
- Add and manage your own recipes
- Integrate custom meals into your planning

## Installation

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, etc.) for development

### Setup
1. Clone or download this repository
2. Open the project in your Java IDE
3. Build the project to compile all Java files
4. Run the `Main.java` file to start the application

## Usage

### Getting Started
1. When you first run the application, you'll be prompted to create a user profile
2. Enter your personal details, dietary preferences, and health goals
3. Navigate through the main menu to access different features

### Main Menu Options
1. **Display User Information** - View your current profile details
2. **Manage User Profile** - Create or edit your profile
3. **View Meal Suggestions** - Browse meal ideas filtered by your preferences
4. **Generate Weekly Meal Plan** - Create a personalized weekly meal schedule
5. **View Existing Meal Plan** - Check your current meal plan
6. **Generate Shopping List** - Create a shopping list based on your meal plan
7. **Analyze Nutrition** - Get nutritional insights about your profile and meals
8. **Manage Recipes** - Add and edit your own recipes
9. **Exit** - Close the application

## Data Files

The application uses several text files to store data:

- `breakfast.txt` - Contains breakfast meal options
- `lunch.txt` - Contains lunch meal options
- `dinner.txt` - Contains dinner meal options
- `snacks.txt` - Contains snack options
- `user_profile.txt` - Stores your user profile information
- `weekly_plan.txt` - Stores your generated weekly meal plan

### Meal Data File Format

To add your own meals to the system, you can edit the respective meal files using the following format:

```
Name: [Meal Name]
Description: [Brief description]
Calories: [Number]
Protein: [Number]
Carbs: [Number]
Fat: [Number]
Dietary Type: [normal/vegetarian/vegan/gluten-free/low-carb/keto]
[Additional meal-specific properties]
Ingredients: [Ingredient1:quantity:unit, Ingredient2:quantity:unit, ...]
```

Each meal type has specific additional properties:

- **Breakfast**: `HighProtein: [true/false]`, `CaffeineFree: [true/false]`
- **Lunch**: `LowCarb: [true/false]`, `QuickPrep: [true/false]`
- **Dinner**: `HeartHealthy: [true/false]`, `ComfortFood: [true/false]`
- **Snack**: `LowCalorie: [true/false]`, `Portable: [true/false]`

## Project Structure

### Main Components

- `Main.java` - Application entry point and user interface
- `model/` - Contains data models for meals, users, and recipes
- `service/` - Contains business logic for meal planning and analysis

### Model Classes

- `User.java` - Represents user profile with preferences and goals
- `Meal.java` - Base class for all meal types
- `Breakfast.java`, `Lunch.java`, `Dinner.java`, `Snack.java` - Specific meal types
- `Recipe.java` - Custom user recipes
- `Ingredient.java` - Food ingredients with quantities

### Service Classes

- `MealSuggester.java` - Provides meal recommendations based on preferences
- `WeeklyPlanGenerator.java` - Creates weekly meal plans
- `NutritionAnalyzer.java` - Analyzes nutritional content
- `ShoppingListGenerator.java` - Creates shopping lists from meal plans
- `UserProfileManager.java` - Manages user profile data
- `MealFileParser.java` - Parses meal data from text files

## Contributing

Contributions to improve the Healthy Meal Planner are welcome! You can contribute by:

1. Adding more meal options to the data files
2. Enhancing the nutrition analysis algorithms
3. Improving the user interface
4. Adding new features like meal preparation instructions

## License

This project is available for personal and educational use.