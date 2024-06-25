package project.Server;

import project.util.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private final List<Food> menuList = new CopyOnWriteArrayList<>();
    private final List<Restaurant> restaurantList = new CopyOnWriteArrayList<>();
    private final Map<String, Integer> restaurantToId = new ConcurrentHashMap<>();
    private final Map<String, List<Food>> foodsInRestaurants = new ConcurrentHashMap<>();
    private final Map<String, List<String>> categoryToRestaurants = new ConcurrentHashMap<>();
    private final Map<Integer, NetworkUtil> idToNetworkUtil = new ConcurrentHashMap<>();
    private final FileOperation fileOperation = new FileOperation(restaurantList,
            restaurantToId, categoryToRestaurants, foodsInRestaurants, menuList);
    private Map<Integer, List<Food>> storedOrder = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        Server obj = new Server();
        obj.readFromFile();

        Runnable mainMenu = obj::mainMenu;
        Thread mainMenuThread = new Thread(mainMenu, "Admin");
        mainMenuThread.start();

        Runnable addFood = () -> {
            try {
                obj.addFood();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
        Thread addFoodThread = new Thread(addFood, "Add FOOD");
        addFoodThread.start();

        Runnable serverNetwork = obj::serverNetwork;
        Thread serverThread = new Thread(serverNetwork, "ServerNetwork");
        serverThread.start();

        Runnable storingThread = () -> {
            try {
                obj.storeOrderThread();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
        Thread storingServerThread = new Thread(storingThread);
        storingServerThread.start();


        Runnable getReadRequests = () -> {
            try {
                obj.sendStoredOrders();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
        Thread getReadRequestsThread = new Thread(getReadRequests);
        getReadRequestsThread.start();

    }

     private List<Food> sendStoredOrders() throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(45454);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Restaurant Accepted");
            NetworkUtil networkUtil = new NetworkUtil(socket);
            int id = (int) networkUtil.read();
            if (storedOrder.get(id) == null) {
                List<Food> foods = new CopyOnWriteArrayList<>();
                networkUtil.write(foods);

            }
            else{
                networkUtil.write(new CopyOnWriteArrayList<>(storedOrder.get(id)));
                storedOrder.remove(id, storedOrder.get(id));
            }

        }

    }

     private void storeOrderThread() throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(55555);
        while (true) {
            Socket server = serverSocket.accept();
            NetworkUtil networkUtil = new NetworkUtil(server);
            Food incoming = (Food) networkUtil.read();
            int id = incoming.getRestaurantId();
            if (storedOrder.get(id) == null)
                storedOrder.put(id, new CopyOnWriteArrayList<>());
            storedOrder.get(id).add(incoming);
            System.out.println(incoming);

        }
    }

    private String getRestaurantData() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Restaurant temp : restaurantList) {
            List<Food> tempFoodList = foodsInRestaurants.get(temp.getName().toLowerCase());
            stringBuilder.append(temp.getName()).append(": ").append(tempFoodList.size());
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private void readFromFile() throws IOException {
        String INPUT_FILE_RESTAURANTS = "V:\\Offline\\TermProject\\src\\main\\java\\project\\Server\\restaurant.txt";
        String INPUT_FILE_MENU = "V:\\Offline\\TermProject\\src\\main\\java\\project\\Server\\menu.txt";
        fileOperation.readRestaurants(INPUT_FILE_RESTAURANTS);
        fileOperation.readFood(INPUT_FILE_MENU);
    }

     private void addFood() throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(33333);
        while (true) {
            Socket socket = serverSocket.accept();
            NetworkUtil networkUtil = new NetworkUtil(socket);
            Food temp = (Food) networkUtil.read();
            addFoodItem(temp);
            networkUtil.write(new DataObjects(menuList, restaurantList, restaurantToId,
                    foodsInRestaurants, categoryToRestaurants));
        }
    }

     private void addFoodItem(Food tempFood) throws IllegalArgumentException {
        String restaurantName = getRestaurantNameById(restaurantToId, tempFood.getRestaurantId());
        // Check if the restaurantName is present in the restaurantToMenu map
        if (foodsInRestaurants.containsKey(restaurantName)) {
            List<Food> menuForRestaurant = foodsInRestaurants.get(restaurantName);
            // Iterate through the menu for the restaurant and check for duplicates
            for (Food existingFood : menuForRestaurant) {
                if (existingFood != null && existingFood.getName().equalsIgnoreCase(tempFood.getName())
                        && existingFood.getCategory().equalsIgnoreCase(tempFood.getCategory())) {
                    throw new IllegalStateException();
                }
            }
            // If no duplicate found, add the new food item to the menu
            menuForRestaurant.add(tempFood);
        } else {
            // If the restaurant is not in the map, create a new entry
            List<Food> newMenu = new ArrayList<>();
            newMenu.add(tempFood);
            foodsInRestaurants.put(restaurantName, newMenu);
        }
        // Also add the food item to the general menu list
        menuList.add(tempFood);
    }

    private String getRestaurantNameById(Map<String, Integer> restaurantToId, int id) {
        for (Map.Entry<String, Integer> entry : restaurantToId.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Restaurant Doesn't Exist");
    }

    public void addRestaurant() {
        System.out.println("Please enter the restaurant name:");
        Scanner scanner = new Scanner(System.in);
        String restaurantName = scanner.nextLine().trim();
        if (restaurantToId.containsKey(restaurantName.toLowerCase())) {
            throw new IllegalStateException();
        }
        System.out.println("Please enter the restaurant id: ");
        String restaurantId = scanner.nextLine().trim();
        if (restaurantToId.containsValue(Integer.parseInt(restaurantId))) throw new IllegalStateException();

        System.out.println("Please enter the Score: ");
        String score = scanner.nextLine().trim();

        System.out.println("Please enter the price: ($-format) ");
        String price = scanner.nextLine().trim();

        System.out.println("Please enter the zip code: ");
        String zipCode = scanner.nextLine().trim();

        System.out.println("How Many Category do you want to add: ");
        int categoryCount = Integer.parseInt(scanner.nextLine().trim());

        if (categoryCount <= 0 || categoryCount > 3) throw new IllegalArgumentException();
        String[] categoryArray = new String[categoryCount];
        for (int i = 0; i < categoryCount; i++) {
            categoryArray[i] = scanner.nextLine().trim();
        }

        var tempRestaurant = new Restaurant(restaurantId, restaurantName, score, price, zipCode);
        tempRestaurant.setCategory(categoryArray);
        restaurantToId.put(tempRestaurant.getName().toLowerCase(), tempRestaurant.getId());
        for (String category : tempRestaurant.getCategory()) {
            if (!category.isEmpty())
                categoryToRestaurants.computeIfAbsent(category.toLowerCase(),
                        k -> new ArrayList<>()).add(tempRestaurant.getName());
        }
        restaurantList.add(tempRestaurant);
    }

    private void mainMenu() {
        while (true) {
            try {
                System.out.println(menuMessage());
                Scanner scanner = new Scanner(System.in);
                int option = Integer.parseInt(scanner.nextLine().trim());
                switch (option) {
                    case 1 -> addRestaurant();
                    case 2 -> {
                        writeToFile();
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid Option");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input!");
            } catch (IllegalStateException e) {
                System.out.println("Item already Exists");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid Arguments");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeToFile() throws IOException {

        String OUTPUT_FILE_MENU = "V:\\Offline\\TermProject\\src\\main\\java\\project\\Server\\menuOut.txt";
        String OUTPUT_FILE_RESTAURANTS = "V:\\Offline\\TermProject\\src\\main\\java\\project\\Server\\restaurantOut.txt";
        fileOperation.writeRestaurantToFile(OUTPUT_FILE_RESTAURANTS);
        fileOperation.writeFoodToFile(OUTPUT_FILE_MENU);

    }

    private String menuMessage() {
        return """
                1) Add Restaurant
                2) Exit System
                """;
    }

    private void serverNetwork() {
        try {
            ServerSocket serverSocket = new ServerSocket(44444);
            while (true) {
                Socket client = serverSocket.accept();
                NetworkUtil networkUtil = new NetworkUtil(client);
                DataObjects temp = new DataObjects(menuList, restaurantList, restaurantToId,
                        foodsInRestaurants, categoryToRestaurants);
                boolean flag = (Boolean) networkUtil.read();
                if (flag) {
                    String read = (String) networkUtil.read();
                    networkUtil.write(temp);
                } else {
                    String name = (String) networkUtil.read();
                    int resId = restaurantToId.get(name);
                    String pass = (String) networkUtil.read();

                    if(pass.equals(name.toUpperCase() + resId)) {
                        System.out.println("Restaurant Ok");
                        networkUtil.write(true);
                        networkUtil.write(temp);
                        idToNetworkUtil.put(resId, networkUtil);
                    }else
                        break;
                }
                new ServerReadThread(networkUtil, idToNetworkUtil);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
