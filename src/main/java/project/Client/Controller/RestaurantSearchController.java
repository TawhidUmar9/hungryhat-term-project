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
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import project.util.*;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

public class RestaurantSearchController implements Initializable {
    RestaurantManagement restaurantManagement;
    private List<Restaurant> restaurantList;
    private Map<String, List<String>> categoryToRestaurants;
    private List<Food> selectedFoods;
    private int criteria = -1;
    private String userName;
    private  String selectedRestaurantName;
    private  DataObjects dataObjects;
    private Socket socket;
    private NetworkUtil networkUtil;

    public RestaurantSearchController(String userName,List<Food> selectedFoodList) {
        this.userName = userName;
        this.selectedFoods = selectedFoodList;
    }

    @FXML
    void selectRestaurant(ActionEvent actionEvent) throws IOException {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirm Restaurant!");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Are you sure you want to proceed?");
        confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        ButtonType userResponse = confirmationDialog.showAndWait().orElse(ButtonType.CANCEL);
        if (userResponse == ButtonType.OK) {
            Restaurant selected = searchRestaurantTable.getSelectionModel().getSelectedItem();
            selectedRestaurantName = selected.getName();
            System.out.println(selectedRestaurantName);
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/CustomerHome.fxml"));
                CustomerHome customerHomeController = new CustomerHome(userName, selectedFoods, selectedRestaurantName);
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
    }
    @FXML
    void returnHome(ActionEvent actionEvent) throws IOException {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/CustomerHome.fxml"));
                CustomerHome customerHomeController = new CustomerHome(userName, selectedFoods);
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

    @FXML
    void showCart(ActionEvent actionEvent) throws IOException {
        //if(selectedFoodList.isEmpty()) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/cartUI.fxml"));
        var cartUi = new CartController(userName, selectedFoods);
        fxmlLoader.setController(cartUi);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }
    @FXML
    void searchWithLetter(KeyEvent event) {
        String input = searchTextField.getText();
        List<Restaurant> temp = new CopyOnWriteArrayList<>();
        if (!input.equals("")) {
            try {
                switch (criteria) {
                    case 1 -> temp = restaurantManagement.searchRestaurantByName(input.toLowerCase().trim());
                    case 2 -> {
                        String[] arr = input.split(",", -1);
                        temp = restaurantManagement.searchRestaurantByScore
                                (Double.parseDouble(arr[0].trim()),
                                        Double.parseDouble(arr[1].trim()));
                    }
                    case 3 -> temp = restaurantManagement.searchByRestaurantByCategory(input.toLowerCase().trim());
                    case 4 -> temp = restaurantManagement.searchRestaurantByPrice(input.trim());

                    case 5 -> temp = restaurantManagement.searchRestaurantByZipCode(input.trim());
                    //case 6 -> System.out.print(searchRestaurants.printRestaurantByCategory());
                    default -> System.out.println("Invalid Option!");

                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (NullPointerException e) {
                System.out.println("Invalid input. Please make sure you provide valid values.");
            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.");
            }
        }
        ObservableList<Restaurant> restaurants = FXCollections.
                observableArrayList(temp);

        // Set up the column cell value factories
        restaurantNameColumn.setCellValueFactory(new PropertyValueFactory<Restaurant, String>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("price"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<Restaurant, Double>("score"));
        zipColum.setCellValueFactory(new PropertyValueFactory<Restaurant, String>("zipCode"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<Restaurant, String[]>("category"));
        categoryColumn.setCellFactory(column -> {
            return new TableCell<Restaurant, String[]>() {
                @Override
                protected void updateItem(String[] categories, boolean empty) {
                    super.updateItem(categories, empty);
                    if (categories == null || empty) {
                        setText(null);
                    } else {
                        // Join the categories with a comma and space
                        String categoryString = String.join(", ", categories);
                        setText(categoryString);
                    }
                }
            };
        });

        // Set the items for the TableView
        searchRestaurantTable.setItems(restaurants);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        networkEstablish();
        searchButton.setVisible(false);
        searchTextField.setVisible(false);
        restaurantManagement = new RestaurantManagement(restaurantList, categoryToRestaurants);
    }

    private void networkEstablish() {
        try {
            socket = new Socket("127.0.0.1", 44444);
            networkUtil = new NetworkUtil(socket);
            networkUtil.write(true);
            networkUtil.write(userName);
            dataObjects = (DataObjects) networkUtil.read();
            readFromDataObject(dataObjects);

        } catch (IOException e) {
            // Handle exceptions appropriately (e.g., log or show an error message)
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void readFromDataObject(DataObjects dataObjects) {
        restaurantList = dataObjects.getRestaurantList();
        categoryToRestaurants = dataObjects.getCategoryToRestaurants();
    }
    @FXML
    private Button addToCart;
    @FXML
    private TableColumn<Restaurant, String[]> categoryColumn;
    @FXML
    private TableColumn<Food, String> priceColumn;
    @FXML
    private TableColumn<Restaurant, String> restaurantNameColumn;
    @FXML
    private TableColumn<Restaurant, Double> scoreColumn;
    @FXML
    private Button searchButton;
    @FXML
    private Button searchByCategory;
    @FXML
    private Button searchByName;
    @FXML
    private Button searchByPrice;
    @FXML
    private Button searchByScore;
    @FXML
    private Button searchByZop;
    @FXML
    private TableView<Restaurant> searchRestaurantTable;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button showCart;
    @FXML
    private Label searchLabel;
    @FXML
    private Button showRestaurantsByCat;
    @FXML
    private TableColumn<Restaurant, String> zipColum;
    @FXML
    void search(ActionEvent actionEvent){

    }
    @FXML
    void searchByName(ActionEvent event) {
        criteria = 1;
        setText("Searching By Name", "Enter Name");
    }
    @FXML
    void searchByScore(ActionEvent event) {
        criteria = 2;
        setText("Searching By Score", "Enter Score Range (min, max)");
    }
    @FXML
    void searchByCategory(ActionEvent event) {
        criteria = 3;
        setText("Searching By Category", "Enter Category");
    }
    @FXML
    void searchByPrice(ActionEvent event) {
        criteria = 4;
        setText("Searching By Price", "Enter Price Range" );

    }
    @FXML
    void searchByZip(ActionEvent event) {
        criteria = 5;
        setText("Searching By ZipCode", "Enter ZipCode");
    }
    @FXML
    void showRestaurantsByCat(ActionEvent event) {
        criteria = 6;
        setText("Lol", "I Dont Know");
    }
    private void setText(String text, String prompt){
        searchButton.setVisible(true);
        searchTextField.clear();
        searchTextField.setVisible(true);
        searchLabel.setText(text);
        searchTextField.setPromptText(prompt);
    }

}
