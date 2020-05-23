package windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class HomeWindow {

    private Scene homeWindowScene;

    public HomeWindow() {
        VBox base = new VBox();
        base.getChildren().add(new Label("Pictionary"));
        base.getChildren().add(getPlayerInformation());
        base.getChildren().add(getJoinHostButtons());
        base.setAlignment(Pos.CENTER);
        homeWindowScene = new Scene(base);

    }


    public HBox getPlayerInformation(){
        HBox playerInfo = new HBox();
        TextField username = new TextField();
        Button leftButton = new Button("<-");
        ImageView profileImage = new ImageView();
        Button rightButton = new Button("->");
        playerInfo.getChildren().addAll(username,leftButton,profileImage,rightButton);
        playerInfo.setSpacing(10);
        playerInfo.setAlignment(Pos.CENTER);
        return playerInfo;
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
        return homeWindowScene;
    }
}
