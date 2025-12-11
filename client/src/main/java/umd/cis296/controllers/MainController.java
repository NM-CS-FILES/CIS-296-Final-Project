package umd.cis296.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;
import umd.cis296.ClientApplication;
import umd.cis296.Message;
import umd.cis296.MessageSocket;
import umd.cis296.message.ChannelListMessage;
import umd.cis296.message.IAmMessage;
import umd.cis296.message.RequestChannelListMessage;
import umd.cis296.message.RequestUserListMessage;
import umd.cis296.message.TextMessage;
import umd.cis296.message.UserJoinMessage;
import umd.cis296.message.UserLeaveMessage;
import umd.cis296.message.UserListMessage;
import umd.cis296.objects.Channel;
import umd.cis296.objects.User;

public class MainController implements Initializable {
    @FXML private ListView<Channel> channelListView;
    @FXML private ListView<User> userListView;
    @FXML private Label channelLabel;
    @FXML private ListView<String> channelMessageListView;
    @FXML private TextField messageTextField;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Channel> channels = FXCollections.observableArrayList();
    private Map<String, ObservableList<String>> channelContent = new HashMap();
    private MessageSocket socket;
    private String name;
    private Timeline scheduler;

    public void setName(String name) {
        this.name = name;
    }

    public void initializeSocket(MessageSocket socket) {
        this.socket = socket;

        scheduler = new Timeline(new KeyFrame(Duration.millis(20), e -> {
            Message message = null;

            while ((message = socket.read()) != null) {
                handleMessage(message);
            }
        }));

        scheduler.setCycleCount(Timeline.INDEFINITE);
        scheduler.play();

        try {
            socket.send(new IAmMessage(name));
            socket.send(new RequestChannelListMessage());
            socket.send(new RequestUserListMessage());
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userListView.setItems(users);
        channelListView.setItems(channels);
        channelListView.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> channelSelectionChange());
        messageTextField.setOnKeyReleased(event -> { if (event.getCode() == KeyCode.ENTER) sendTextMessage(); });
    }

    //
    //

    private Channel selectedChannel() {
        return channelListView.getSelectionModel().getSelectedItem();
    }

    private void channelSelectionChange() {
        channelLabel.setText(selectedChannel().getName());
        channelMessageListView.setItems(channelContent.get(selectedChannel().getName()));
        messageTextField.clear();
    }

    private void sendTextMessage() {
        socket.send(new TextMessage(selectedChannel(), new User(this.name, null), messageTextField.getText()));
        messageTextField.clear();
    }

    //
    //
    
    private void handleChannelList(ChannelListMessage message) {
        message.getChannels().forEach((channel) -> {
            if (!channelContent.containsKey(channel.getName())) {
                channelContent.put(channel.getName(), FXCollections.observableArrayList());
            }
        });

        channels.setAll(message.getChannels());
        channelListView.getSelectionModel().select(0);
    }

    private void handleUserList(UserListMessage message) {
        users.setAll(message.getUsers());
    }

    private void handleUserJoin(UserJoinMessage message) {
        users.add(message.getUser());
    }

    private void handleUserLeave(UserLeaveMessage message) {
        users.remove(message.getUser());
    }

    private void handleTextMessage(TextMessage message) {
        channelContent.get(message.getChannel().getName()).add("[" + message.getUser().getName() + "]: " + message.getText());
    }

    private void handleMessage(Message in) {
        switch ((Object)in) {
            case ChannelListMessage message -> handleChannelList(message);
            case UserListMessage message    -> handleUserList(message);
            case UserJoinMessage message    -> handleUserJoin(message);
            case UserLeaveMessage message   -> handleUserLeave(message);
            case TextMessage message        -> handleTextMessage(message);
            default -> { }
        }
    }

    //
    //

    @FXML
    private void onExitServerPressed(ActionEvent event) {
        socket.close();
        
        try {
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(ClientApplication.INTRO_FXML_PATH))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
