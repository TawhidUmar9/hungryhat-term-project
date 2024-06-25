module com.example.termproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens project to javafx.fxml;
    exports project.Server;
    opens project.Server to javafx.fxml;
    exports project.Client;
    opens project.Client to javafx.fxml;
    exports project.util;
    opens project.util to javafx.fxml;
    exports project.Client.Controller;
    opens project.Client.Controller to javafx.fxml;
}