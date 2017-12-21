package sample;

/**
 * Create by: maciejziolkowski on 21 gru 2017
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Controller.Controller;

public class Main extends Application {
    Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI/GUI.fxml"));
        primaryStage.setTitle("Serial Communication");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        controller.init();
    }

    @Override
    public void stop() throws Exception {

        controller.disconnectSTM32();
        super.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
