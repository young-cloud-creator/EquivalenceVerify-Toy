package toy.equivalence.verify.equivalenceverifytoy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        final String windowTitle = "程序等价性确认工具";
        final int windowWidth = 1024;
        final int windowHeight = 640;

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("entry-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), windowWidth, windowHeight);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}