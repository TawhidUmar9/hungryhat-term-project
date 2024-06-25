package project.util;

import project.util.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class RestaurantManagement {
    private List<Restaurant> restaurantList;
    private Map<String, List<String>> categoryToRestaurants;


    public RestaurantManagement(List<Restaurant> restaurantList, Map<String, List<String>> categoryToRestaurants) {
        if (restaurantList == null || categoryToRestaurants == null) return;
        this.restaurantList = restaurantList;
        this.categoryToRestaurants = categoryToRestaurants;
    }

    public List<Restaurant> searchRestaurantByName(String targetName) {
        List<Restaurant> tempList = new ArrayList<>();
        for (Restaurant temp : restaurantList) {
            if (temp.getName().toLowerCase().contains(targetName.toLowerCase()))
                tempList.add(temp);
        }
        return tempList;
    }

    public List<Restaurant> searchRestaurantByScore(double scoreA, double scoreB) {
        double higher = Math.max(scoreA, scoreB);
        double lower = Math.min(scoreA, scoreB);
        List<Restaurant> tempList = new ArrayList<>();
        for (Restaurant temp : restaurantList) {
            if (temp.getScore() <= higher && temp.getScore() >= lower)
                tempList.add(temp);
        }
        return tempList;
    }

    public List<Restaurant> searchByRestaurantByCategory(String category) {
        List<Restaurant> tempList = new ArrayList<>();
        for (Restaurant temp : restaurantList) {
            for (var x : temp.getCategory())
                if (x.toLowerCase().contains(category.toLowerCase())) {
                    tempList.add(temp);
                    break;
                }
        }
        return tempList;
    }

    public List<Restaurant> searchRestaurantByPrice(String price) {
        List<Restaurant> tempList = new ArrayList<>();
        for (Restaurant temp : restaurantList) {
            if (temp.getPrice().equals(price))
                tempList.add(temp);
        }
        return tempList;
    }

    public List<Restaurant> searchRestaurantByZipCode(String zip) {
        List<Restaurant> tempList = new ArrayList<>();
        for (Restaurant temp : restaurantList) {
            if (temp.getZipCode().contains(zip))
                tempList.add(temp);
        }
        return tempList;
    }

    public String printRestaurantByCategory() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : categoryToRestaurants.entrySet()) {
            String category = entry.getKey();
            List<String> restaurantList = entry.getValue();

            stringBuilder.append(capitalizeFirstLetter(category)).append(":");
            for (String restaurant : restaurantList) {
                stringBuilder.append(" ").append(restaurant);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    //Messages.
    public String searchMessage() {
        return """
                Restaurant Searching Options:
                 1) By Name
                 2) By Score
                 3) By Category
                 4) By Price
                 5) By Zip Code
                 6) Different Category Wise List of Restaurants
                 7) Back to Main Menu""";
    }

    public String notFoundForName() {
        return "No Such restaurant with this name";
    }

    public String notFoundForScore() {
        return "No such restaurant with this score range";
    }

    public String notFoundForCategory() {
        return "No such restaurant with this category";
    }

    public String notFoundInPrice() {
        return "No such restaurant with this price";
    }

    public String notFoundWithZip() {
        return "No such restaurant with this zip code";
    }

    private String capitalizeFirstLetter(String input) {
        return input.isEmpty() ? input : input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}