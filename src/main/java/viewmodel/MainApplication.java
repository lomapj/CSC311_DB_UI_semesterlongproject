package viewmodel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/splashscreen.fxml"));
        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("PatTrack");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/DollarClouddatabase.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
