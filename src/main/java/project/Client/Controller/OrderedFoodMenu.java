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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import project.util.DataObjects;
import project.util.Food;
import project.util.NetworkUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderedFoodMenu implements Initializable {
    DataObjects dataObjects;
    NetworkUtil networkUtil;
    List<Food> orderedFood;
    RestaurantReadThread restaurantReadThread;
    String name;

    // Wrap the orderedFood list in an ObservableList
    ObservableList<Food> observableOrderedFood;
    @FXML
    private TableColumn<Food, String> foodNameColumn;
    @FXML
    private TableColumn<Food, String> foodCategoryColumn;
    @FXML
    private TableColumn<Food, Double> foodPriceColumn;
    @FXML
    private TableView<Food> ownMenu;
    @FXML
    private Label restauranName;
    @FXML
    private Label restaurantScore;
    @FXML
    private Label restaurantZipCode;

    public OrderedFoodMenu(String name, DataObjects dataObjects, NetworkUtil networkUtil, List<Food> orderedFood) {
        this.dataObjects = dataObjects;
        this.networkUtil = networkUtil;
        this.orderedFood = orderedFood;
        this.name = name;
        //this.restaurantReadThread = restaurantReadThread;
    }

    @FXML
    void returnHome(ActionEvent actionEvent) throws IOException {
        var restaurantHome = new RestaurantHome(dataObjects, networkUtil, name, restaurantReadThread, orderedFood, this);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/RestaurantHome.fxml"));
        fxmlLoader.setController(restaurantHome);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }


    @FXML
    void foodDeliverd(ActionEvent event) {
        List<Food> temp = ownMenu.getSelectionModel().getSelectedItems();
        orderedFood.removeAll(temp);
        createTable();
    }

    public void createTable() {
        ObservableList<Food> foodItems = FXCollections.observableArrayList(orderedFood);
        foodNameColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("name"));
        foodPriceColumn.setCellValueFactory(new PropertyValueFactory<Food, Double>("foodPrice"));
        foodCategoryColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("category"));
        ownMenu.setItems(foodItems);
        ownMenu.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        restaurantScore.setText(String.valueOf(orderedFood.size()));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTable();
        restauranName.setText(name.toUpperCase());
    }
}
