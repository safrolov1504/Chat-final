package clientChat;

import clientChat.interfaceMy.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/clientChat/resources/sample.fxml"));


        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("My Chat");
        primaryStage.setScene(scene);

        Controller primaryController = loader.getController();
        primaryStage.setOnHidden(e -> primaryController.shutdown());

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
