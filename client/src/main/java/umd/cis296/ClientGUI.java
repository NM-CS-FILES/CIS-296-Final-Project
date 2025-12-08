package umd.cis296;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        root.setSpacing(10);

        Label title = new Label("Discord Lite Client");
        Label status = new Label("Status: Not connected");

        root.getChildren().addAll(title, status);

        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("Discord Lite Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

