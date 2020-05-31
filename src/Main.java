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
        Client.getInstance().setUser(new User("Arne","resources/pictures/cat.jpg", false));
        Client.getInstance().connectToServer("localhost", 10000);
        GameWindow Application = new GameWindow(primaryStage);



    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Client.getInstance().disconnectFromServer();
//        System.exit(0);
//        Runtime.getRuntime().exit(0);
    }
}