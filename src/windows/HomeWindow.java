package windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class HomeWindow {

    private Scene HomeWindowScene;

    public HomeWindow() {
        VBox base = new VBox();
        base.getChildren().add(new Label("Pictionary"));
        base.getChildren().add(getPlayerInformation());
        base.getChildren().add(getJoinHostButtons());
        base.setAlignment(Pos.CENTER);
        HomeWindowScene = new Scene(base);




    }


    public HBox getPlayerInformation(){
        HBox PlayerInfo = new HBox();
        TextField username = new TextField();
        Button LeftButton = new Button("<-");
        ImageView profileImage = new ImageView();
        Button RightButton = new Button("->");
        PlayerInfo.getChildren().addAll(username,LeftButton,profileImage,RightButton);
        PlayerInfo.setSpacing(10);
        PlayerInfo.setAlignment(Pos.CENTER);
        return PlayerInfo;
    }

    public GridPane getJoinHostButtons(){
        GridPane joinHostButtons = new GridPane();

        Button publicJoinButton = new Button("Join public game");
        Button privateJoinButton = new Button("Join private game");
        Button privateHostButton = new Button("Host private game");
        Button publicHostButton = new Button("Host public game");
        TextField privateJoinCodeTextField = new TextField();

        joinHostButtons.add(publicJoinButton,1,2);
        joinHostButtons.add(publicHostButton,1,1);

        joinHostButtons.add(privateHostButton,2,1);
        joinHostButtons.add(privateJoinButton,2,2);
        joinHostButtons.add(privateJoinCodeTextField,2,3);

        joinHostButtons.setVgap(10);
        joinHostButtons.setHgap(10);
        joinHostButtons.setAlignment(Pos.CENTER);

        return joinHostButtons;
    }

    public Scene getHomeWindowScene() {
        return HomeWindowScene;
    }
}
