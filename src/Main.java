import comms.Client;
import comms.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import windows.GameWindow;
import windows.HomeWindow;
import windows.LobbyWindow;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        new HomeWindow(primaryStage);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (Client.getInstance().isConnected()){
            Client.getInstance().disconnectFromServer();
        }

        super.stop();
    }
}