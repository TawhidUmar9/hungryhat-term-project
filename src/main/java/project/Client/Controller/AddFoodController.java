package project.Client.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.util.DataObjects;
import project.util.Food;
import project.util.NetworkUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AddFoodController implements Initializable {
    private Socket socket;
    private NetworkUtil networkUtil;
    private Map<String, Integer> restaurantToId;
    private Map<String, List<Food>> foodsInRestaurants;
    private List<Food> menuList;
    private DataObjects dataObjects;
    private String resName;
    @FXML
    private Button addFood;

    @FXML
    private TextField foodCat;

    @FXML
    private TextField foodName;

    @FXML
    private TextField foodPrice;

    @FXML
    private Label restaurantName;

    @FXML
    private Label score;

    @FXML
    private Label totalOrder;
    private final RestaurantReadThread restaurantReadThread;
    private List<Food> orderedFood;
    private OrderedFoodMenu orderedFoodMenu;


    public AddFoodController(DataObjects dataObjects, NetworkUtil networkUtil,
                             String name, RestaurantReadThread restaurantReadThread,
                             List<Food> orderedFood, OrderedFoodMenu orderedFoodMenu) {
        this.resName = name;
        this.networkUtil = networkUtil;
        this.dataObjects = dataObjects;
        this.restaurantReadThread = restaurantReadThread;
        this.orderedFoodMenu = orderedFoodMenu;
        this.orderedFood = orderedFood;
    }

    @FXML
    void addFood(ActionEvent event) throws IOException, ClassNotFoundException {
        addFoodToMenu();
        returnHome(event);
    }

    synchronized private void addFoodToMenu() throws IOException, ClassNotFoundException {
        String name = foodName.getText().trim();
        String category = foodCat.getText().trim();
        String price = foodPrice.getText().trim();
        String id = String.valueOf(restaurantToId.get(resName));
        if(name.isEmpty() || category.isEmpty()|| price.isEmpty()|| id.isEmpty()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Invalid Inputs!");

            errorAlert.showAndWait();
            return;
        }

        if (foodsInRestaurants.containsKey(restaurantName)) {
            List<Food> menuForRestaurant = foodsInRestaurants.get(restaurantName);
            // Iterate through the menu for the restaurant and check for duplicates
            for (Food existingFood : menuForRestaurant) {
                if (existingFood != null && existingFood.getName().equalsIgnoreCase(name)
                        && existingFood.getCategory().equalsIgnoreCase(category)) {
                    // Show a pop-up error message for duplicate food
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Food with the same name and category already exists!");

                    errorAlert.showAndWait();
                    return;
                }
            }
        }

        Food temp = new Food(id, category, name, price);
        Socket socket1 = new Socket("127.0.0.1", 33333);
        NetworkUtil networkUtil1 = new NetworkUtil(socket1);

        networkUtil1.write(temp);
        dataObjects = (DataObjects) networkUtil1.read();

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Food added successfully!");

        successAlert.showAndWait();
    }



    synchronized private void readFromDataObject(DataObjects dataObjects) {
        menuList = dataObjects.getMenuList();
        foodsInRestaurants = dataObjects.getFoodsInRestaurants();
        restaurantToId = dataObjects.getRestaurantToId();
    }

    @FXML
    void returnHome(ActionEvent actionEvent) throws IOException {
        var restaurantHome = new RestaurantHome(dataObjects, networkUtil, resName, restaurantReadThread,
                orderedFood, orderedFoodMenu);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/RestaurantHome.fxml"));
        fxmlLoader.setController(restaurantHome);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readFromDataObject(dataObjects);
        restaurantName.setText(resName.toUpperCase());
    }
}
