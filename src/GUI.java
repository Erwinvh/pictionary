import Windows.GameWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

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
