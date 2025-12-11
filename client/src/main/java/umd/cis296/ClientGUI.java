package umd.cis296;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ClientGUI extends Application {

    private final ObservableList<String> messages = FXCollections.observableArrayList();
    private ListView<String> messageList;
    private TextField inputField;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #202225;");

        VBox serverBar = buildServerBar();
        root.setLeft(serverBar);

        VBox channelList = buildChannelList();
        root.setLeft(new HBox(serverBar, channelList));

        BorderPane chatArea = buildChatArea();
        root.setCenter(chatArea);

        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("Discord-Style Chat Client");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildServerBar() {
        VBox box = new VBox();
        box.setPadding(new Insets(10, 8, 10, 8));
        box.setSpacing(10);
        box.setStyle("-fx-background-color: #202225;");
        box.setPrefWidth(70);
        box.setAlignment(Pos.TOP_CENTER);

        StackPane serverIcon = new StackPane();
        Circle circle = new Circle(24, Color.web("#5865F2"));
        Label initials = new Label("CIS");
        initials.setTextFill(Color.WHITE);
        initials.setFont(Font.font(14));
        serverIcon.getChildren().addAll(circle, initials);

        box.getChildren().add(serverIcon);
        return box;
    }

    private VBox buildChannelList() {
        VBox box = new VBox();
        box.setPadding(new Insets(12));
        box.setSpacing(10);
        box.setStyle("-fx-background-color: #2f3136;");
        box.setPrefWidth(220);

        Label serverTitle = new Label("CIS-296 Server");
        serverTitle.setTextFill(Color.WHITE);
        serverTitle.setFont(Font.font(16));

        Label section = new Label("TEXT CHANNELS");
        section.setTextFill(Color.web("#b9bbbe"));
        section.setFont(Font.font(11));

        VBox channels = new VBox();
        channels.setSpacing(6);

        Label general = buildChannelLabel("# general");
        Label homework = buildChannelLabel("# homework");
        Label random = buildChannelLabel("# random");

        channels.getChildren().addAll(general, homework, random);

        box.getChildren().addAll(serverTitle, section, channels);
        return box;
    }

    private Label buildChannelLabel(String name) {
        Label label = new Label(name);
        label.setTextFill(Color.web("#b9bbbe"));
        label.setFont(Font.font(13));
        label.setPadding(new Insets(4, 8, 4, 8));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle("-fx-background-radius: 4;");

        label.setOnMouseEntered(e ->
                label.setStyle("-fx-background-radius: 4; -fx-background-color: #3a3c43;"));
        label.setOnMouseExited(e ->
                label.setStyle("-fx-background-radius: 4;"));
        return label;
    }

    private BorderPane buildChatArea() {
        BorderPane chat = new BorderPane();
        chat.setStyle("-fx-background-color: #36393f;");

        HBox header = new HBox();
        header.setPadding(new Insets(8, 12, 8, 12));
        header.setSpacing(8);
        header.setStyle("-fx-background-color: #2f3136;");

        Label hash = new Label("#");
        hash.setTextFill(Color.WHITE);
        hash.setFont(Font.font(18));

        Label channelName = new Label("general");
        channelName.setTextFill(Color.WHITE);
        channelName.setFont(Font.font(16));

        header.getChildren().addAll(hash, channelName);
        chat.setTop(header);

        messageList = new ListView<>(messages);
        messageList.setStyle(
                "-fx-control-inner-background: #36393f;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 0;" +
                        "-fx-text-fill: #dcddde;"
        );
        chat.setCenter(messageList);

        HBox inputBar = new HBox();
        inputBar.setPadding(new Insets(8, 12, 12, 12));
        inputBar.setSpacing(8);

        inputField = new TextField();
        inputField.setPromptText("Message #general");
        inputField.setStyle(
                "-fx-background-color: #40444b;" +
                        "-fx-text-fill: #dcddde;" +
                        "-fx-prompt-text-fill: #72767d;" +
                        "-fx-background-radius: 6;"
        );
        HBox.setHgrow(inputField, Priority.ALWAYS);

        Button sendButton = new Button("Send");
        sendButton.setStyle(
                "-fx-background-color: #5865F2;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;"
        );

        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        inputBar.getChildren().addAll(inputField, sendButton);
        chat.setBottom(inputBar);

        messages.add("Welcome to #general!");
        messages.add("This is a Discord-style chat mockup connected to your CIS-296 client.");

        return chat;
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        messages.add("You: " + text);
        inputField.clear();
        messageList.scrollTo(messages.size() - 1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
