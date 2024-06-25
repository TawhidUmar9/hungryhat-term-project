package project.util;

import java.io.Serializable;
import java.util.Arrays;

public class Restaurant implements Serializable {
    private int id;
    private String name;
    private Double score;
    private String price;
    private String zipCode;
    private String[] category;

    public Restaurant(String... c) {
        if(c.length<6) return;
        int len = c.length;
        int numCategories = len - 5;
        category = new String[numCategories];

        this.id = Integer.parseInt(c[0]);
        this.name = c[1];
        this.score = Double.parseDouble(c[2]);
        this.price = c[3];
        this.zipCode = c[4];

        System.arraycopy(c, 5, category, 0, numCategories);
    }
    Restaurant(String id, String name, String score, String price , String zipCode) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.score = Double.parseDouble(score);
        this.price = price;
        this.zipCode = zipCode;

    }

    public void setCategory(String[] category) {
        this.category = new String[category.length];
        System.arraycopy(category, 0, this.category, 0, category.length);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getScore() {
        return score;
    }

    public String getPrice() {
        return price;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String[] getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Restaurant:" +
                //"id=" + id +->implementation detail.
                "Name: " + name +
                "\nScore: " + score +
                "\nPrice: " + price +
                "\nZipCode: " + zipCode +
                "\nCategory: " + Arrays.toString(category)+
                "\n";
    }

    public String getCSV() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(id).append(",").append(name).append(",").append(score).
                append(",").append(price).append(",").append(zipCode);
        for (var category : category) {
            stringBuilder.append(",").append(category);
        }
        return stringBuilder.toString();
    }
}
