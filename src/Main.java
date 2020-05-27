import comms.Client;
import comms.Server;
import comms.ServerSettings;
import comms.User;
import windows.GameWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import windows.HomeWindow;
import windows.LobbyWindow;

public class Main extends Application {

    public static void main(String[] args) {
        //Client.getInstance();

//        new Thread(() -> {
//            ServerSettings serverSettings = new ServerSettings(2, 10, 10, "English", "localhost", 10000);
//            new Server(serverSettings);
//        }).start();


        //Client.getInstance();
//        Client.getInstance().setUser(new User("Arne", null, false));
        Client.getInstance().setUser(new User("Arne",false));
        Client.getInstance().connectToServer("localhost", 10000);
        launch();
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