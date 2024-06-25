package project.Client.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.util.DataObjects;
import project.util.Food;
import project.util.NetworkUtil;
import project.util.Restaurant;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

public class RestaurantLoginController implements Initializable {
    @FXML
    private Button loginButton;
    @FXML
    private Button loginCustomer;
    @FXML
    private TextField restaurantInputField;
    @FXML
    private PasswordField restaurantPassword;
    private NetworkUtil networkUtil;
    private Socket socket;
    private String restaurantName;
    private DataObjects dataObjects;
    List<Food> foodOrders;

    public RestaurantLoginController() {
    }

    @FXML
    void login(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        socket = new Socket("127.0.0.1", 44444);
        networkUtil = new NetworkUtil(socket);
        networkUtil.write(false);


        restaurantName = restaurantInputField.getText().toLowerCase();
        String password = restaurantPassword.getText();
        networkUtil.write(restaurantName);
        networkUtil.write(password);
        boolean flag = (boolean) networkUtil.read();
        if (flag ) {
            this.dataObjects = (DataObjects) networkUtil.read();

            try {
                Socket socket = new Socket("127.0.0.1", 45454);
                NetworkUtil networkUtil1 = new NetworkUtil(socket);
                int id = dataObjects.getRestaurantToId().get(restaurantName.toLowerCase());
                networkUtil1.write(id);
                foodOrders = (CopyOnWriteArrayList<Food>) networkUtil1.read();

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            var restaurantHome = new RestaurantHome(restaurantName, networkUtil, dataObjects, foodOrders);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/RestaurantHome.fxml"));
            fxmlLoader.setController(restaurantHome);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        } else {
            showAlert("Incorrect Password", "The entered password is incorrect.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void loginCustomer(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/customerLogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("HungryHub!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
