import comms.Client;
import comms.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import windows.GameWindow;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        GameWindow gw = new GameWindow(primaryStage);

        Client.getInstance().setUser(new User("Arne", false));
        Client.getInstance().connectToServer("localhost", 10000);

        primaryStage.setTitle("Pictionary");
        Scene scene = gw.getGameWindowScene();

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Client.getInstance().disconnectFromServer();
        System.exit(0);
    }
}