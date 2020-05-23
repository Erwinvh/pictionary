import windows.GameWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import windows.HomeWindow;
import windows.LobbyWindow;

public class GUI extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Pictionary");
        LobbyWindow gw = new LobbyWindow();
        Scene scene = gw.getLobbyWindowScene();

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.show();
    }
}
