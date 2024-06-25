package project.Client.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import project.util.DataObjects;
import project.util.Food;
import project.util.NetworkUtil;
import project.util.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RestaurantHome implements Initializable {
    @FXML
    private TableColumn<Food, String> foodCategoryColumn;
    @FXML
    private TableColumn<Food, String> foodNameColumn;
    @FXML
    private TableColumn<Food, Double> foodPriceColumn;
    @FXML
    private TableView<Food> ownMenu;

    @FXML
    private Label categories;
    @FXML
    private Label restauranName;

    @FXML
    private Label score;

    @FXML
    private Label zipCode;

    private DataObjects dataObjects;
    private NetworkUtil networkUtil;
    private String name;
    private Map<String, List<Food>> foodsInRestaurants;
    private RestaurantReadThread restaurantReadThread;
    private List<Food> foodOrders;
    private OrderedFoodMenu orderedFoodMenu;


    public RestaurantHome(String name, NetworkUtil networkUtil,
                          DataObjects dataObjects, List<Food> foodOrders) throws IOException, ClassNotFoundException {
        System.out.println("Constructor called");
        this.name = name;
        this.networkUtil = networkUtil;
        this.dataObjects = dataObjects;
        this.foodOrders = foodOrders;
        orderedFoodMenu = new OrderedFoodMenu(name, dataObjects, networkUtil, this.foodOrders);
        restaurantReadThread = new RestaurantReadThread(networkUtil, this.foodOrders, orderedFoodMenu);
        this.foodsInRestaurants = dataObjects.getFoodsInRestaurants();

    }

    public RestaurantHome(DataObjects dataObjects, NetworkUtil networkUtil,
                          String name, RestaurantReadThread restaurantReadThread,
                          List<Food> orderedFood, OrderedFoodMenu orderedFoodMenu) {
        this.name = name;
        this.networkUtil = networkUtil;
        this.dataObjects = dataObjects;
        this.foodOrders = orderedFood;
        this.restaurantReadThread = restaurantReadThread;
        this.orderedFoodMenu = orderedFoodMenu;
        this.foodsInRestaurants = dataObjects.getFoodsInRestaurants();
    }

    private void getCount() {
        System.out.println(foodOrders.size());
        System.out.println(foodOrders);
    }

    @FXML
    void addFood(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/AddFoodUI.fxml"));
        AddFoodController addFoodController = new AddFoodController(dataObjects, networkUtil,
                name, restaurantReadThread
                , foodOrders, orderedFoodMenu);
        fxmlLoader.setController(addFoodController);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    public List<Food> getFoodOrders() {
        return foodOrders;
    }

    @FXML
    void incomingOrder(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/orderdFood.fxml"));
        fxmlLoader.setController(orderedFoodMenu);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        getCount();
    }

    @FXML
    void logout(ActionEvent actionEvent) throws IOException {
        System.exit(0);
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/customerLogin.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
//        stage.setTitle("HungryHub!");
//        stage.setScene(scene);
//        stage.setResizable(false);
//        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTable(name);
        restauranName.setText(name.toUpperCase());
        List<Restaurant> temp = dataObjects.getRestaurantList();
        Restaurant home = null;
        for(var x: temp){
            if(x.getName().equalsIgnoreCase(name))
                home = x;
        }
        zipCode.setText(home.getZipCode());
        List<String > catList = new ArrayList<>();
        for(var x: home.getCategory()){
            catList.add(x);
        }
        categories.setText(catList.toString());
        score.setText(String.valueOf(home.getScore()));
    }


    private void createTable(String name) {
        foodsInRestaurants = dataObjects.getFoodsInRestaurants();
        ObservableList<Food> foodItems = FXCollections.
                observableArrayList(foodsInRestaurants.get(name));

        foodNameColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("name"));
        foodPriceColumn.setCellValueFactory(new PropertyValueFactory<Food, Double>("foodPrice"));
        foodCategoryColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("category"));

        ownMenu.setItems(foodItems);
    }
}