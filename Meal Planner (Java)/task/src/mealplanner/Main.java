package mealplanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws SQLException, FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        MealPlannerDB mealPlanner = new MealPlannerDB();

        // check if tables are empty
        int meals_id = 0;
        if (mealPlanner.isEmpty()) {
        } else {
            meals_id = mealPlanner.retrieveMealID();
        }
        // check if the plan has been made
        boolean madePlan = false;

        label:
        while (true) {
            System.out.println("What would you like to do (add, show, plan, save, exit)?");
            String action = scanner.nextLine().trim();

            switch (action) {
                case "exit":
                    System.out.println("Bye!");
                    break label;
                case "save":
                    if (!mealPlanner.planSaved()) {
                        System.out.println("Input a filename:");
                        String fileName = scanner.nextLine().trim();

                        File file = new File(fileName);
                        try (PrintWriter printWriter = new PrintWriter(file)) {
                            ArrayList<String> returnedIngredients = mealPlanner.printIngredients();

                            // Create a HashMap to store the ingredient counts
                            Map<String, Integer> ingredientCounts = new HashMap<>();

                            // Iterate through the ingredients array and count the occurrences
                            for (String item : returnedIngredients) {
                                ingredientCounts.put(item, ingredientCounts.getOrDefault(item, 0) + 1);
                            }

                            // Print the ingredients and their counts
                            for (Map.Entry<String, Integer> entry : ingredientCounts.entrySet()) {
                                String ingredient = entry.getKey();
                                int count = entry.getValue();
                                printWriter.println(ingredient + (count > 1 ? " x" + count : ""));
                            }

                            System.out.println("Saved!");
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Unable to save. Plan your meals first.");
                    }
                    break;
                case "plan":
                    String[] dayOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                    for (String day : dayOfWeek) {
                        System.out.println(day);
                        String[] dailyMeals = {"breakfast", "lunch", "dinner"};
                        for (String i : dailyMeals) {
                            ArrayList<String> returnedMeal = new ArrayList<>(mealPlanner.retrieveMeal(i));
                            System.out.printf("Choose the %s for %s from the list above:\n", i, day);
                            String selectedMeal;
                            while (true) {
                                selectedMeal = scanner.nextLine().trim();
                                if (returnedMeal.contains(selectedMeal)) {
                                    break;
                                } else {
                                    System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                                }
                            }
                            int id_meal = mealPlanner.getMealID(selectedMeal, i);
                            mealPlanner.addPlan(selectedMeal, i, day, id_meal);
                            if (i.equals("dinner")) {
                                System.out.printf("Yeah! We planned the meals for %s.\n", day);
                            }
                            if (i.equals("dinner") && day.equals("Sunday")) {
                                System.out.println();
                                mealPlanner.showPlan();
                                madePlan = true;
                            }
                        }
                        System.out.println();
                    }
                    break;
                case "add":
                    System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
                    String meal = scanner.nextLine().trim();

                    while (!meal.equals("breakfast") && !meal.equals("lunch") && !meal.equals("dinner")) {
                        System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                        meal = scanner.nextLine().trim();
                    }

                    System.out.println("Input the meal's name:");
                    // the meal's name can only contain letters
                    String mealName = scanner.nextLine().trim();

                    // check if the mealName contains only letters
                    Pattern pattern = Pattern.compile("^[a-zA-Z]+( [a-zA-Z]+)*$");
                    boolean containsLettersOnly = pattern.matcher(mealName).matches();

                    if (!containsLettersOnly) {
                        do {
                            System.out.println("Wrong format. Use letters only!");
                            mealName = scanner.nextLine().trim();
                            containsLettersOnly = pattern.matcher(mealName).matches();
                        } while (!containsLettersOnly);
                    }

                    meals_id++;
                    //store data in the meals table and return the meal_id of the inserted meal
                    int retrieved_id = mealPlanner.addMeal(meals_id, mealName, meal);

                    System.out.println("Input the ingredients: ");

                    // bring ingredientsList array to main
                    String[] ingredientsList;

                    String ingredients;
                    while (true) {
                        ingredients = scanner.nextLine();

                        //insert to list
                        ingredientsList = ingredients.split(",");
                        boolean valid = true;

                        for (String ingredient : ingredientsList) {
                            if (!ingredient.trim().matches("^[a-zA-Z]+( [a-zA-Z]+)*$")) {
                                valid = false;
                                break;
                            }
                        }

                        if (valid) {
                            break;
                        } else {
                            System.out.println("Wrong format. Use letters only!");
                        }
                    }

                    // store the ingredients in the ingredients table
                    mealPlanner.addIngredient(meals_id, ingredients, retrieved_id);
                    System.out.println("The meal has been added!");
                    break;
                case "show":
                    // check if the meal set is empty
                    System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
                    String mealCategory;
                    while (true) {
                        mealCategory = scanner.nextLine().trim();
                        if (mealCategory.equals("breakfast") || mealCategory.equals("lunch") || mealCategory.equals("dinner")) {
                            break;
                        }
                        System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                    }

                    if (mealPlanner.containsCategory(mealCategory)) {
                        System.out.println("No meals found.");
                    } else {
                        mealPlanner.showMeal(mealCategory);
                    }
                    break;
            }
        }
    }

}