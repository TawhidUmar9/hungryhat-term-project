package project.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataObjects implements Serializable {
    private  List<Food> menuList;
    private  List<Restaurant> restaurantList;
    private  Map<String, Integer> restaurantToId;
    private  Map<String, List<Food>> foodsInRestaurants;
    private  Map<String, List<String>> categoryToRestaurants;

    public List<Food> getMenuList() {
        return menuList;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public Map<String, Integer> getRestaurantToId() {
        return restaurantToId;
    }

    public Map<String, List<Food>> getFoodsInRestaurants() {
        return foodsInRestaurants;
    }

    public Map<String, List<String>> getCategoryToRestaurants() {
        return categoryToRestaurants;
    }

    public DataObjects(List<Food> menuList, List<Restaurant> restaurantList,
                       Map<String, Integer> restaurantToId, Map<String, List<Food>> foodsInRestaurants,
                       Map<String, List<String>> categoryToRestaurants) {
        this.menuList = menuList;
        this.restaurantList = restaurantList;
        this.restaurantToId = restaurantToId;
        this.foodsInRestaurants = foodsInRestaurants;
        this.categoryToRestaurants = categoryToRestaurants;
    }
}
