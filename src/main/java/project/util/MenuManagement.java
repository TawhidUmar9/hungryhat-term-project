package project.util;

import project.util.Food;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuManagement {
    private  List<Food> menuList;
    public  Map<String, List<Food>> foodsInRestaurants;

    public MenuManagement(List<Food> menuList, Map<String, List<Food>> foodsInRestaurants){
        //if(menuList == null || foodsInRestaurants == null) return;
        this.menuList = menuList;
        this.foodsInRestaurants = foodsInRestaurants;
    }

    public List<Food> searchByName(String name){
        System.out.println(foodsInRestaurants.get("kfc"));
        return searchByName(name, menuList);

    }

    public List<Food> searchByNameInRestaurant(String name, String restaurantName){
        List<Food> targetList = foodsInRestaurants.get(restaurantName);
        if(targetList == null) return null;
        return  searchByName(name, targetList);
    }

    public List<Food> searchByCategory(String category){
        return searchByCategory(category, menuList);
    }

    public List<Food> searchByCategoryInRestaurant(String category, String restaurantName){
        List<Food> targetList = foodsInRestaurants.get(restaurantName);
        if(targetList == null) return null;
        return  searchByCategory(category, targetList);
    }

    public List<Food> searchByPriceRange(double priceA, double priceB){
        return searchByPriceRange(priceA,priceB, menuList);
    }

    public List<Food> searchByPriceRangeInRestaurant(double priceA, double priceB, String restaurantName){
        List<Food> targetList = foodsInRestaurants.get(restaurantName);
        if(targetList == null) return null;
        return  searchByPriceRange(priceA,priceB, targetList);
    }

    public List<Food> costliestFood(String restaurantName) {
        List<Food> tempList = new ArrayList<>();
        List<Food> targetList = foodsInRestaurants.get(restaurantName.toLowerCase());
        if(targetList == null) return null;
        double maxPrice = Double.MIN_VALUE;
        for (Food food : targetList ) {
            if (food.getFoodPrice() > maxPrice) {
                maxPrice = food.getFoodPrice();
            }
        }
        for (Food food : targetList) {
            if (food.getFoodPrice() == maxPrice) {
                tempList.add(food);
            }
        }
        return tempList;
    }

    private List<Food>searchByName(String name, List<Food> foodListScope){
        List<Food> tempList = new ArrayList<>();
        for (Food temp : foodListScope) {
            if (temp.getName().toLowerCase().contains(name.toLowerCase()))
                tempList.add(temp);
        }
        return tempList;
    }

    private List<Food> searchByCategory(String category, List<Food> foodListScope){
        List<Food> tempList = new ArrayList<>();
        for (Food temp : foodListScope) {
            if (temp.getCategory().toLowerCase().contains(category.toLowerCase()))
                tempList.add(temp);
        }
        return tempList;
    }

    private List<Food> searchByPriceRange(double priceA, double priceB, List<Food> foodListScope){
        double higher = Math.max(priceA, priceB);
        double lower = Math.min(priceA, priceB);
        List<Food> tempList = new ArrayList<>();
        for (Food temp : foodListScope) {
            if (temp.getFoodPrice() <= higher && temp.getFoodPrice() >= lower)
                tempList.add(temp);
        }
        return tempList;
    }
}