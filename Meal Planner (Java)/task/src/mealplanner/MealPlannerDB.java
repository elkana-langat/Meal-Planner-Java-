package mealplanner;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MealPlannerDB {
    Connection connection;
    public MealPlannerDB() {
        try {
            String DB_URL = "jdbc:postgresql://localhost:5432/meals_db";
            String USER = "postgres";
            String PASS = "1111";
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTables() throws SQLException {
        try {
            Statement statement = connection.createStatement();

            // create the table meal
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals (" +
                    "meal_id INTEGER PRIMARY KEY NOT NULL," +
                    "meal VARCHAR(30)," +
                    "category VARCHAR(30))");

            // create table ingredients
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients (" +
                    "ingredient_id INTEGER  PRIMARY KEY NOT NULL," +
                    "ingredient VARCHAR(300)," +
                    "meal_id INTEGER)");

            // create the table plan
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plan (" +
                    "plan_id SERIAL  PRIMARY KEY NOT NULL," +
                    "meal VARCHAR(30)," +
                    "category VARCHAR(40)," +
                    "day VARCHAR(30)," +
                    "meal_id INTEGER)");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int addMeal(int meals_id, String meal, String category) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO meals (meal_id, meal, category) " +
                    "VALUES ('" + meals_id + "', '" + meal + "', '" + category + "')");
            ResultSet rs = statement.executeQuery("SELECT meal_id FROM meals ORDER BY meal_id DESC LIMIT 1");
            int meal_id = 0;
            while (rs.next()) {
                meal_id = rs.getInt("meal_id");
            }

            return meal_id;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void addIngredient(int ingredient_id, String ingredient, int meal_id) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO ingredients (ingredient_id, ingredient, meal_id) " +
                    "VALUES ('" + ingredient_id + "','" + ingredient + "', '" + meal_id +"')");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addPlan(String meal, String category, String day, int meal_id) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO plan (meal, category, day, meal_id) VALUES " +
                    "('" + meal + "', '" + category + "', '" + day + "', '" + meal_id + "')");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean containsCategory(String category) throws SQLException{
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT count(*) AS total " +
                    "FROM meals WHERE category='" + category + "'");
            int count = 0;
            while (rs.next()) {
                count = rs.getInt("total");
            }

            return (count == 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean isEmpty() throws SQLException{
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT count(*) AS total FROM meals");
            int count = 0;
            while (rs.next()) {
                count = rs.getInt("total");
            }

            return (count == 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void showMeal(String category) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            Statement statement2 = connection.createStatement();
            ResultSet rsMeal = statement.executeQuery("SELECT * FROM meals WHERE category='" + category + "'");
            System.out.println("Category: " + category);
            System.out.println();
            while(rsMeal.next()) {
                System.out.println("Name: " + rsMeal.getString("meal"));
                System.out.println("Ingredients:");
                ResultSet rsIngredients = statement2.executeQuery(String.format
                        ("SELECT * FROM ingredients WHERE meal_id = %d;", rsMeal.getInt("meal_id")));
                while(rsIngredients.next()) {
                    String[] ingredientList = rsIngredients.getString("ingredient").split(",");
                    for (String i : ingredientList) {
                        System.out.println(i.trim());
                    }
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int retrieveMealID() throws SQLException {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT meal_id FROM meals ORDER BY meal_id DESC LIMIT 1");
            if (rs.next()) {
                return rs.getInt("meal_id");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    // retrieve meal ID of certain meal
    public int getMealID(String meal, String category) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT meal_id FROM meals " +
                    "WHERE meal='" + meal + "' AND category='" + category + "'");
            if (rs.next()) {
                return rs.getInt("meal_id");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
    public ArrayList<String> retrieveMeal(String category) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT meal FROM meals " +
                    "WHERE category='"+category+"' ORDER BY meal ASC");
            ArrayList<String> returnedMeals = new ArrayList<>();
            while (rs.next()) {
                returnedMeals.add(rs.getString("meal"));
                System.out.println(rs.getString("meal"));
            }

            return returnedMeals;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void showPlan() throws SQLException {
        try {
            Statement statement = connection.createStatement();
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            for (String day : days) {
                System.out.println(day);

                String[] cat = {"breakfast", "lunch", "dinner"};
                for (String a : cat) {
                    ResultSet rs = statement.executeQuery("SELECT meal FROM plan WHERE day='" + day + "' AND category='" + a + "'");
                    while (rs.next()) {
                        System.out.printf("%s: %s\n", a, rs.getString("meal"));
                    }
                    rs.close(); // Close the ResultSet after each inner loop iteration
                }

                System.out.println();
            }
            statement.close(); // Close the Statement object after the outer loop finishes
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<String> printIngredients() throws SQLException {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("""
                    SELECT p.meal,i.ingredient AS a
                    FROM plan p
                    JOIN ingredients i ON p.meal_id = i.meal_id;""");

            ArrayList<String> allIngredients = new ArrayList<>();
            while (rs.next()) {
                String[] ingredient = rs.getString("a").split(",");
                allIngredients.addAll(Arrays.asList(ingredient));
            }
            return allIngredients;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean planSaved() throws SQLException{
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT count(*) AS total FROM plan");
            int count = 0;
            while (rs.next()) {
                count = rs.getInt("total");
            }

            return (count == 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
