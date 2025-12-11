package umd.cis296;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI{

    List<String> messages = new ArrayList<>();

    @FXML
    private TextField textField;

    @FXML
    private VBox messageContainer;

    @FXML
    public void initialize() {
        if (textField == null) {
            System.out.println("textField is NULL! Injection failed.");
        } else {
            System.out.println("textField injected successfully: " + textField);
            textField.setOnAction(event -> {
                String text = textField.getText();
                messages.add(text);
                displayMessages();
                textField.clear();
            });
        }
    }

    private void displayMessages()
    {
        messageContainer.getChildren().clear();

        for (String message : messages) {
            Label label = new Label(message);
            label.setFont(Font.font(18));
            label.setPadding(new Insets(10));
            //label.setTextFill(Color.BLUE);

            messageContainer.getChildren().add(label);
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

