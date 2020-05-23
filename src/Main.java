import comms.Client;
import windows.GameWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        Client.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Pictionary");
        GameWindow gw = new GameWindow(primaryStage);
        Scene scene = gw.getGameWindowScene();

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.show();
    }
}