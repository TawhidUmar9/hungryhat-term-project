package project.Client.Controller;

import project.util.Food;
import project.util.NetworkUtil;

import java.io.IOException;
import java.util.List;

public class RestaurantReadThread implements Runnable{
    Thread t;
    NetworkUtil networkUtil;
    List<Food> foodOrders;
    OrderedFoodMenu orderedFoodMenu;
    public RestaurantReadThread(NetworkUtil networkUtil, List<Food> foodOrders, OrderedFoodMenu orderedFoodMenu) {
        this.networkUtil = networkUtil;
        this.foodOrders = foodOrders;
        this.orderedFoodMenu = orderedFoodMenu;
        t = new Thread(this);
        t.start();
    }

    @Override
    synchronized public void run() {
        while (true){
            try {
                Food temp = (Food) networkUtil.read();
                foodOrders.add(temp);
                orderedFoodMenu.createTable();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
