package viewmodel;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class SpalshScreenController {

    @FXML
    public void initialize() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(event -> {
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
                Scene loginScene = new Scene(loginRoot, 900, 600);

                // Get the currently visible window (the splash window)
                Stage splashStage = (Stage) Stage.getWindows()
                        .stream()
                        .filter(Window::isShowing)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No visible window found"));

                splashStage.setScene(loginScene);
                splashStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pause.play();
    }
}
