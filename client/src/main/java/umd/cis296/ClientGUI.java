package umd.cis296;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI{

    List<String> messages = new ArrayList<>();

    @FXML
    private TextField textField;

    @FXML
    public void initialize() {
        if (textField == null) {
            System.out.println("textField is NULL! Injection failed.");
        } else {
            System.out.println("textField injected successfully: " + textField);
            textField.setOnAction(event -> {
                String text = textField.getText();
                messages.add(text);
                messages.forEach(System.out::println);
                textField.clear();
            });
        }
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

