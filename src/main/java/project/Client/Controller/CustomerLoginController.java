package project.Client.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.util.Food;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerLoginController implements Initializable {
    @FXML
    public TextField userNameTextField;
    @FXML
    public Button userNameButton;

    @FXML
    private Button loginRestaurant;

    private String userName;


    public void submitUserName(ActionEvent actionEvent) throws IOException {
        userName = userNameTextField.getText();
        if (userName.isEmpty()) return;
        List<Food> selectedFood = new ArrayList<>();

        // Pass the userName to the CustomerHome constructor
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/CustomerHome.fxml"));
        CustomerHome customerHomeController = new CustomerHome(userName, selectedFood);
        fxmlLoader.setController(customerHomeController);

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    public String getUserName(){
        return userName;
    }
    @FXML
    void loginRestaurant(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        RestaurantLoginController restaurantLoginController = new RestaurantLoginController();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/RestaurantLogin.fxml"));
        fxmlLoader.setController(restaurantLoginController);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userNameTextField.setFocusTraversable(false);
    }
}
