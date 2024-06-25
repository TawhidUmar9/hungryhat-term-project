package project.Client.Controller;

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
import project.util.DataObjects;
import project.util.Food;
import project.util.NetworkUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;

public class CartController implements Initializable {
    @FXML
    private TableView<Food> cartTable;
    @FXML
    private TableColumn<Food, String> foodName;
    @FXML
    private TableColumn<Food, Double> foodPrice;
    @FXML
    private Button placeOrder;
    @FXML
    private Button removeFromCart;

    @FXML
    private Button clearCart;
    @FXML
    private Label totalPrice;


    @FXML
    void clearCart(ActionEvent event) {
        selectedFood.clear();
        refreshCartTable();

    }
    NetworkUtil networkUtil;
    private String userName;
    private List<Food> selectedFood;
    DataObjects dataObjects;
    Socket socket;

    public CartController(String userName, List<Food> selectedFood) {
        this.selectedFood = selectedFood;
        this.userName = userName;

    }
    private void networkEstablish() {
        try {
            socket = new Socket("127.0.0.1", 44444);
            this.networkUtil = new NetworkUtil(socket);
            networkUtil.write(true);
            networkUtil.write(userName);
            dataObjects = (DataObjects) networkUtil.read();

        } catch (IOException e) {
            // Handle exceptions appropriately (e.g., log or show an error message)
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    private void setTotalPrice(){
        double price = 0;
        for(var x: selectedFood)
            price+= x.getFoodPrice();
        totalPrice.setText("Total Price: " + NumberFormat.getCurrencyInstance().format(price));
    }

    @FXML
    void placeOrder(ActionEvent event) throws IOException, ClassNotFoundException {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirm Order");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Are you sure you want to place this order?");
        ButtonType result = confirmationDialog.showAndWait().orElse(ButtonType.CANCEL);
        if (result == ButtonType.OK) {
            networkUtil.write(selectedFood);
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Order Placed");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Your order has been placed successfully!");
            successAlert.showAndWait();
            selectedFood.clear();
            returnHome(event);
        }
    }

    @FXML
    void removeFromCart(ActionEvent actionEvent) {
        Food temp = cartTable.getSelectionModel().getSelectedItem();
        selectedFood.remove(temp);
        setTotalPrice();
        refreshCartTable();
    }

    @FXML
    void returnHome(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/CustomerHome.fxml"));
            CustomerHome customerHomeController = new CustomerHome(userName, selectedFood);
            fxmlLoader.setController(customerHomeController);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshCartTable() {
        ObservableList<Food> foodItems = FXCollections.observableArrayList(selectedFood);
        foodName.setCellValueFactory(new PropertyValueFactory<Food, String>("name"));
        foodPrice.setCellValueFactory(new PropertyValueFactory<Food, Double>("foodPrice"));
        cartTable.setItems(foodItems);
        setTotalPrice();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        networkEstablish();
        refreshCartTable();
    }
}
