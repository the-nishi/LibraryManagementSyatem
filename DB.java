package iublibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/library-register-view.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Library Register");
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        new Thread(() -> DB.getInstance().closeConnection()).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}