package project.util;

import project.Server.Server;
import project.util.Food;
import project.util.Restaurant;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileOperation {
    private List<Restaurant> restaurantList;
    private Map<String, Integer> restaurantToId;
    private Map<String, List<String>> categoryToRestaurant;
    private Map<String, List<Food>> restaurantToMenu;
    private List<Food> menuList;

    public FileOperation(List<Restaurant> restaurantList, Map<String, Integer> restaurantToId,
                         Map<String, List<String>> categoryToRestaurant,
                         Map<String, List<Food>> restaurantToMenu, List<Food> menuList) {
        if (restaurantList == null || restaurantToId == null || categoryToRestaurant == null
                || restaurantToMenu == null || menuList == null) {
            return;
        }


        this.restaurantList = restaurantList;
        this.restaurantToId = restaurantToId;
        this.categoryToRestaurant = categoryToRestaurant;
        this.restaurantToMenu = restaurantToMenu;
        this.menuList = menuList;
    }

    public void readRestaurants(String inputFileName) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] restaurantData = line.split(",", -1);
                if (restaurantData.length < 6) return;
                Restaurant tempRestaurant = new Restaurant(restaurantData);
                String[] categories = tempRestaurant.getCategory();
                if (!restaurantToId.containsKey(tempRestaurant.getName().toLowerCase())) {
                    restaurantToId.put(tempRestaurant.getName().toLowerCase(), tempRestaurant.getId());
                }
                for (String category : categories) {
                    if (!category.isEmpty())
                        categoryToRestaurant.computeIfAbsent(category.toLowerCase(), k -> new ArrayList<>())
                                .add(tempRestaurant.getName());
                }
                restaurantList.add(tempRestaurant);
            }
        }
    }

    public void readFood(String inputFileName) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] foodData = line.split(",", -1);
                Food tempFood = new Food(foodData);
                int restaurantId = tempFood.getRestaurantId();
                String restaurantName = getRestaurantNameById(restaurantToId, restaurantId);
                restaurantToMenu.computeIfAbsent(restaurantName, k -> new ArrayList<>()).add(tempFood);
                menuList.add(tempFood);
            }
        }
    }

    public void writeFoodToFile(String outputFileName) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileName))) {
            for (Food temp : menuList) {
                bufferedWriter.write(temp.getCSV());
                bufferedWriter.write(System.lineSeparator());
            }
        }
    }

    public void writeRestaurantToFile(String outputFileName) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileName))) {
            for (Restaurant temp : restaurantList) {
                bufferedWriter.write(temp.getCSV());
                bufferedWriter.write(System.lineSeparator());
            }
        }
    }
    private String getRestaurantNameById(Map<String, Integer> restaurantToId, int id) {
        for (Map.Entry<String, Integer> entry : restaurantToId.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Restaurant Doesn't Exist");
    }



}