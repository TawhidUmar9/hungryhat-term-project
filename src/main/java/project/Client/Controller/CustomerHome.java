package project.Client.Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import project.util.*;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerHome implements Initializable {
    private String userName;
    @FXML
    private Button reloadData;
    @FXML
    private TableView<Food> homeFoodMenu;
    @FXML
    private Button label;
    @FXML
    private ChoiceBox<String> listOfRestaurants = new ChoiceBox<>();
    @FXML
    private Button searchFoodButton;
    @FXML
    private Button searchHomeButton;
    @FXML
    private Button showCart;
    @FXML
    private Label welcomeLabel;
    @FXML
    private TableColumn<Food, String> foodNameColumn;
    @FXML
    private TableColumn<Food, Double> foodPriceColumn;
    private List<Food> selectedFoodList;

    private List<Food> menuList;
    private List<Restaurant> restaurantList;
    private Map<String, List<Food>> foodsInRestaurants;//-> home e thakbe.
    private NetworkUtil networkUtil;
    private String selectedRestaurantName = "";

    @FXML
    void reloadData(ActionEvent actionEvent) {
        try {
            //show popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/CustomerHome.fxml"));
            CustomerHome customerHomeController = new CustomerHome(userName, selectedFoodList);
            fxmlLoader.setController(customerHomeController);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed, e.g., show an error message.
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle other exceptions here, if any.
        }
    }
    public CustomerHome(String userName, List<Food> selectedFoodList) throws IOException {
        this.userName = userName;
        this.selectedFoodList = selectedFoodList;
    }

    public CustomerHome(String userName, List<Food> selectedFoodList, String selectedRestaurantName) throws IOException {
        this.userName = userName;
        this.selectedFoodList = selectedFoodList;
        this.selectedRestaurantName = selectedRestaurantName;
    }

    @FXML
    public void logout(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/customerLogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("HungryHub!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void searchFood(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/searchFoodUI.fxml"));
        var foodSearch = new FoodSearchController(userName, selectedFoodList, new MenuManagement(menuList, foodsInRestaurants));
        fxmlLoader.setController(foodSearch);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    @FXML
    public void searchRestaurants(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/searchRestaurantUI.fxml"));
        var restaurantSearch = new RestaurantSearchController(userName, selectedFoodList);
        fxmlLoader.setController(restaurantSearch);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());

        stage.setScene(scene);
    }

    @FXML
    void showCart(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/cartUI.fxml"));
        var cartUi = new CartController(userName, selectedFoodList);
        fxmlLoader.setController(cartUi);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        System.out.println(networkUtil);
    }

    @FXML
    void selectRestaurants() {
        homeFoodMenu.setVisible(true);
        String name = listOfRestaurants.getValue();
        createTable(name.toLowerCase());
    }

    @FXML
    private void addToCart() {
        ObservableList<Food> selectedFoods = homeFoodMenu.getSelectionModel().getSelectedItems();
        selectedFoodList.addAll(selectedFoods);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        networkEstablish();
        welcomeLabel.setText("Welcome " + userName + "!");
        populateChoiceBox();
        homeFoodMenu.setVisible(false);
        if (!selectedRestaurantName.isEmpty()) {
            System.out.println("Not Empty");
            createTable(selectedRestaurantName.toLowerCase());
            listOfRestaurants.setValue(selectedRestaurantName);
            homeFoodMenu.setVisible(true);
        }
    }

    private void networkEstablish() {
        try {
            Socket socket = new Socket("127.0.0.1", 44444);
            networkUtil = new NetworkUtil(socket);
            networkUtil.write(true);
            networkUtil.write(userName);
            DataObjects dataObjects = (DataObjects) networkUtil.read();
            readFromDataObject(dataObjects);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromDataObject(DataObjects dataObjects) {
        menuList = dataObjects.getMenuList();
        restaurantList = dataObjects.getRestaurantList();
        foodsInRestaurants = dataObjects.getFoodsInRestaurants();
    }

    private void populateChoiceBox() {
        ObservableList<String> restaurantNames = FXCollections.observableArrayList();
        for (Restaurant restaurant : restaurantList) {
            restaurantNames.add(restaurant.getName());
        }

        // Set the items of your ChoiceBox to the ObservableList
        listOfRestaurants.setItems(restaurantNames);

        // Add a listener to the ChoiceBox's valueProperty
        listOfRestaurants.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // This method is executed when a choice is made
                System.out.println("Selected choice: " + newValue);

                // Call the selectRestaurants function with the selected value
                selectRestaurants();
            }
        });
    }

    private void createTable(String name) {
        ObservableList<Food> foodItems = FXCollections.
                observableArrayList(foodsInRestaurants.get(name));

        // Set up the column cell value factories
        foodNameColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("name"));
        foodPriceColumn.setCellValueFactory(new PropertyValueFactory<Food, Double>("foodPrice"));

        // Set the items for the TableView
        homeFoodMenu.setItems(foodItems);
        homeFoodMenu.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

}
