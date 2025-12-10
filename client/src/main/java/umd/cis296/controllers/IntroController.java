package umd.cis296.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import umd.cis296.ClientApplication;
import umd.cis296.MessageSocket;
import umd.cis296.message.BeaconMessage;

public class IntroController implements Initializable {

    //
    //

    private ObservableList<BeaconMessage> beaconList = FXCollections.observableArrayList();

    private Timeline scheduler;
    private DatagramChannel channel;
    private ByteBuffer messageBuffer;

    // BOOOOOOOOOO!!!!
    private void recieveBeacon() {
        try {
            InetSocketAddress from = (InetSocketAddress)channel.receive(messageBuffer);

            if (from != null) {
                ByteArrayInputStream bytesIn = new ByteArrayInputStream(messageBuffer.array());
                ObjectInputStream objIn = new ObjectInputStream(bytesIn);

                Object obj = objIn.readObject();

                if (obj instanceof BeaconMessage beacon) {
                    // ew
                    beacon.setAddress(from.getAddress());

                    if (!beaconList.contains(beacon)) {
                        beaconList.add(beacon);
                    }
                }
            }
        } catch (Exception ex) { 
            System.out.println(ex.getMessage());            
        }
    }

    //
    //

    @FXML private TableView<BeaconMessage> serverTableView;
    @FXML private TableColumn<BeaconMessage, String> serverNameColumn;
    @FXML private TableColumn<BeaconMessage, Integer> serverUsersColumn;
    @FXML private TextField usernameTextField;
    @FXML private Button connectButton;

    @FXML
    private void onConnectClicked(ActionEvent event) {
        String username = usernameTextField.getText().strip();
        BeaconMessage selected = serverTableView.getSelectionModel().getSelectedItem();

        if (username.length() == 0 || selected == null) {
            return;
        }

        try {
            MessageSocket socket = MessageSocket.fromBeacon(selected);

            if (socket == null) {
                // todo show user error
                throw new Exception();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(ClientApplication.MAIN_FXML_PATH));
            Parent root = loader.load();
            
            MainController mainController = loader.getController();
            mainController.setName(username);
            mainController.initializeSocket(socket);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("CIS-296 Final Project");
            stage.setScene(new Scene(root));
        } catch (Exception ex) { 
            System.out.println(ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.bind(new InetSocketAddress(5005));
        } catch (IOException e) { }
        
        messageBuffer = ByteBuffer.allocate(0xFFFF);

        serverNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serverUsersColumn.setCellValueFactory(new PropertyValueFactory<>("users"));

        scheduler = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            recieveBeacon();
        }));

        scheduler.setCycleCount(Timeline.INDEFINITE);
        scheduler.play();

        serverTableView.setItems(beaconList);
    }
}
