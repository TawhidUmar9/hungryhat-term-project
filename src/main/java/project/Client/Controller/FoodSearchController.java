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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import project.util.*;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class FoodSearchController implements Initializable {

    String userName;
    NetworkUtil networkUtil;
    List<Food> menuList;
    List<Restaurant> restaurantList;
    List<Food> selectedFood;
    String selectedRestaurantName = "";
    boolean includeRestaurantFlag = false;
    int criteria = 0;
    MenuManagement menuManagement;
    private List<Food> tempSelected;
    private DataObjects dataObjects;
    private Socket socket;
    private Map<String, List<Food>> foodsInRestaurants;
    @FXML
    private Button addToCart;

    @FXML
    private Button costliestFood;

    @FXML
    private TextField criteria1;

    @FXML
    private TextField criteria2;

    @FXML
    private TableColumn<Food, String> foodCatColumn;

    @FXML
    private TableColumn<Food, String> foodName;

    @FXML
    private TableColumn<Food, Double> foodPrice;

    @FXML
    private TableView<Food> foodTable;

    @FXML
    private CheckBox includeRestaurant;

    @FXML
    private ChoiceBox<String> listOfRestaurants;

    @FXML
    private Button returnHome;

    @FXML
    private Button search;

    @FXML
    private Button searchByCategory;

    @FXML
    private Button searchByName;

    @FXML
    private Button searchByPriceRange;

    @FXML
    private HBox searchFoodHbox;

    @FXML
    private Label searchLabel;

    @FXML
    private Button viewCart;

    public FoodSearchController(String userName, List<Food> selectedFood, MenuManagement menuManagement) {
        this.userName = userName;
        this.selectedFood = selectedFood;
        this.menuManagement = menuManagement;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        networkEstablish();
        poplulateChoiseBox();
        criteria1.setVisible(false);
        criteria2.setVisible(false);
        search.setVisible(false);
        includeRestaurant.setVisible(false);
        listOfRestaurants.setVisible(false);
    }

    private void poplulateChoiseBox() {
        ObservableList<String> restaurantNames = FXCollections.observableArrayList();
        for (Restaurant restaurant : restaurantList) {
            restaurantNames.add(restaurant.getName());
        }
        listOfRestaurants.setItems(restaurantNames);
    }

    public void createTable(List<Food> temp) {
        ObservableList<Food> foodItems = FXCollections.
                observableArrayList(temp);

        // Set up the column cell value factories
        foodName.setCellValueFactory(new PropertyValueFactory<Food, String>("name"));
        foodPrice.setCellValueFactory(new PropertyValueFactory<Food, Double>("foodPrice"));
        foodCatColumn.setCellValueFactory(new PropertyValueFactory<Food, String>("category"));

        // Set the items for the TableView
        foodTable.setItems(foodItems);
        foodTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
        foodsInRestaurants = dataObjects.getFoodsInRestaurants();
    }

    @FXML
    void costliestFood(ActionEvent event) {
        criteria = 4;
        search.setVisible(true);
        searchLabel.setText("$$$$");
        criteria1.clear();
        criteria1.setVisible(false);
        criteria2.clear();
        criteria2.setVisible(false);
        includeRestaurant.setText("Please Select The Restaurant");
        includeRestaurant.setVisible(true);
        listOfRestaurants.setVisible(true);
    }

    @FXML
    void includeRestaurant(ActionEvent event) {
        includeRestaurantFlag = !includeRestaurantFlag;
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
            // Handle the exception as needed, e.g., show an error message.
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle other exceptions here, if any.
        }
    }
    @FXML
    void searchClick(ActionEvent event) {
        List<Food> temp = new ArrayList<>();
        if (includeRestaurantFlag)
            selectedRestaurantName = listOfRestaurants.getValue();
        try {
            switch (criteria) {
                case 1 -> {
                    if (includeRestaurantFlag) {
                        System.out.println(true);
                        temp = menuManagement.searchByNameInRestaurant(criteria1.getText().toLowerCase().trim(),
                                selectedRestaurantName.toLowerCase());
                    } else
                        temp = menuManagement.searchByName(criteria1.getText().toLowerCase().trim());

                }
                case 2 -> {
                    if (includeRestaurantFlag) {
                        temp = menuManagement.searchByCategoryInRestaurant(criteria1.getText().toLowerCase().trim(),
                                selectedRestaurantName.toLowerCase());
                    } else
                        temp = menuManagement.searchByCategory(criteria1.getText().toLowerCase().trim());
                }
                case 3 -> {
                    if (includeRestaurantFlag) {
                        temp = menuManagement.searchByPriceRangeInRestaurant
                                (Double.parseDouble(criteria1.getText().trim()),
                                        Double.parseDouble(criteria2.getText().trim()), selectedRestaurantName.toLowerCase());
                    } else
                        temp = menuManagement.searchByPriceRange(Double.parseDouble(criteria1.getText().trim()),
                                Double.parseDouble(criteria2.getText().trim()));

                }
                case 4 -> {
                    temp = menuManagement.costliestFood(selectedRestaurantName);
                }
                default -> {
                    break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (NullPointerException e) {
            System.out.println("Invalid input. Please make sure you provide valid values.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
        createTable(temp);
    }
    @FXML
    void searchTwo(ActionEvent event) {
        List<Food> temp = new ArrayList<>();
        if (includeRestaurantFlag)
            selectedRestaurantName = listOfRestaurants.getValue();
        try {
            switch (criteria) {
                case 1 -> {
                    if (includeRestaurantFlag) {
                        System.out.println(true);
                        temp = menuManagement.searchByNameInRestaurant(criteria1.getText().toLowerCase().trim(),
                                selectedRestaurantName.toLowerCase());
                    } else
                        temp = menuManagement.searchByName(criteria1.getText().toLowerCase().trim());

                }
                case 2 -> {
                    if (includeRestaurantFlag) {
                        temp = menuManagement.searchByCategoryInRestaurant(criteria1.getText().toLowerCase().trim(),
                                selectedRestaurantName.toLowerCase());
                    } else
                        temp = menuManagement.searchByCategory(criteria1.getText().toLowerCase().trim());
                }
                case 3 -> {
                    if (includeRestaurantFlag) {
                        temp = menuManagement.searchByPriceRangeInRestaurant
                                (Double.parseDouble(criteria1.getText().trim()),
                                        Double.parseDouble(criteria2.getText().trim()), selectedRestaurantName.toLowerCase());
                    } else
                        temp = menuManagement.searchByPriceRange(Double.parseDouble(criteria1.getText().trim()),
                                Double.parseDouble(criteria2.getText().trim()));

                }
                case 4 -> {
                    temp = menuManagement.costliestFood(selectedRestaurantName);
                }
                default -> {
                    break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (NullPointerException e) {
            System.out.println("Invalid input. Please make sure you provide valid values.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
        createTable(temp);
    }
    @FXML
    void search(KeyEvent event) {
        List<Food> temp = new ArrayList<>();
        if (includeRestaurantFlag)
            selectedRestaurantName = listOfRestaurants.getValue();
        try {
            switch (criteria) {
                case 1 -> {
                    if (includeRestaurantFlag) {
                        System.out.println(true);
                        temp = menuManagement.searchByNameInRestaurant(criteria1.getText().toLowerCase().trim(),
                                selectedRestaurantName.toLowerCase());
                    } else
                        temp = menuManagement.searchByName(criteria1.getText().toLowerCase().trim());

                }

                case 3 -> {
                    if (includeRestaurantFlag) {
                        temp = menuManagement.searchByPriceRangeInRestaurant
                                (Double.parseDouble(criteria1.getText().trim()),
                                        Double.parseDouble(criteria2.getText().trim()), selectedRestaurantName.toLowerCase());
                    } else
                        temp = menuManagement.searchByPriceRange(Double.parseDouble(criteria1.getText().trim()),
                                Double.parseDouble(criteria2.getText().trim()));

                }
                case 4 -> {
                    temp = menuManagement.costliestFood(selectedRestaurantName);
                }
                default -> {
                    break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (NullPointerException e) {
            System.out.println("Invalid input. Please make sure you provide valid values.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
        createTable(temp);
    }

    @FXML
    void searchTwo(KeyEvent event) {
        List<Food> temp = new ArrayList<>();
        if (includeRestaurantFlag)
            selectedRestaurantName = listOfRestaurants.getValue();
        try {
            switch (criteria) {
                case 2 -> {
                    if (includeRestaurantFlag) {
                        temp = menuManagement.searchByCategoryInRestaurant(criteria1.getText().toLowerCase().trim(),
                                selectedRestaurantName.toLowerCase());
                    } else
                        temp = menuManagement.searchByCategory(criteria1.getText().toLowerCase().trim());
                }
                default -> {
                    break;
                }

            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (NullPointerException e) {
            System.out.println("Invalid input. Please make sure you provide valid values.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
        createTable(temp);
    }

    @FXML
    void searchByCategory(ActionEvent event) {
        criteria = 2;
        searchLabel.setText("Category");
        search.setVisible(true);
        criteria2.clear();
        criteria1.clear();
        criteria1.setVisible(true);
        criteria2.setVisible(true);
        includeRestaurant.setVisible(false);
        listOfRestaurants.setVisible(false);
    }

    @FXML
    void searchByName(ActionEvent event) {
        criteria = 1;
        search.setVisible(true);
        searchLabel.setText("Name");
        criteria1.clear();
        criteria1.setVisible(true);
        criteria1.setPromptText("Name");
        criteria2.clear();
        criteria2.setVisible(false);
        includeRestaurant.setVisible(true);
        listOfRestaurants.setVisible(true);
    }

    @FXML
    void searchByPriceRange(ActionEvent event) {
        criteria = 3;
        search.setVisible(true);
        searchLabel.setText("PriceRange");
        criteria1.clear();
        criteria1.setVisible(true);
        criteria1.setPromptText("Minimum Price");
        criteria2.clear();
        criteria2.setVisible(true);
        criteria2.setPromptText("Maximum Price");
        includeRestaurant.setVisible(true);
        listOfRestaurants.setVisible(true);
    }
    @FXML
    void showCart(ActionEvent actionEvent) throws IOException {
        //if(selectedFoodList.isEmpty()) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/cartUI.fxml"));
        var cartUi = new CartController(userName, selectedFood);
        fxmlLoader.setController(cartUi);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }
    @FXML
    void addToCart(ActionEvent event) {
        tempSelected = new ArrayList<>();
        ObservableList<Food> selectedFoods = foodTable.getSelectionModel().getSelectedItems();
        tempSelected.addAll(selectedFoods);
        selectedFood.addAll(tempSelected);
    }


}
