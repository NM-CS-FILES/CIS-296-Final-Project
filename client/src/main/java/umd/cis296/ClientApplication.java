package umd.cis296;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    public static final String INTRO_FXML_PATH = "/umd/cis296/IntroView.fxml";
    public static final String MAIN_FXML_PATH = "/umd/cis296/MainView.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource(INTRO_FXML_PATH)));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("CIS-296 Final Project");
        stage.show();
    }
}
