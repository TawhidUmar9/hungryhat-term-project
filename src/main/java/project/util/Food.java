package project.util;

import java.io.Serializable;

public class Food implements Serializable {
    private int restaurantId;
    private String category;
    private String name;
    private double foodPrice;
    public Food(String... data){
        if (data.length != 4) return;
        restaurantId = Integer.parseInt(data[0]);
        category = data[1];
        this.name = data[2];
        foodPrice = Double.parseDouble(data[3]);
    }
    public Food(){}
    public String getCSV(){
        return restaurantId + "," + category +
                "," + name + "," + foodPrice;
    }

    @Override
    public String toString() {
        return  "Name: " + name +
                //"restaurantId=" + restaurantId + ->implementation detail.
                "\nCategory:" + category +
                "\nFoodPrice: " + foodPrice+
                "\n";
    }
    public String getName() {
        return name;
    }
    public double getFoodPrice() {
        return foodPrice;
    }
    public String getCategory() {
        return category;
    }
    public int getRestaurantId() {
        return restaurantId;
    }
}
