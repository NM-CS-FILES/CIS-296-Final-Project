package umd.cis296;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientGUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientGUI.class.getResource("/Layout.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Chad Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Stage getStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    private void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = getStage(event);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void switchToServer(ActionEvent event) throws IOException {
        switchScene(event, "/Server_Layout.fxml");
    }
}

