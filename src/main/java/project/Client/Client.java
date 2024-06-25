package project.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/project/customerLogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("HungryHub!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
