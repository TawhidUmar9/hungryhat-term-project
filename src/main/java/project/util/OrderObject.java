package project.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderObject implements Serializable {
    private String userName;
    private List<Food> orders = new ArrayList<>();
    private int restaurantId;

    public OrderObject(String userName, List<Food> orders, int restaurantId) {
        this.userName = userName;
        this.orders = orders;
        this.restaurantId = restaurantId;
    }
}
