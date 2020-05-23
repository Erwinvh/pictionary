package windows;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class HomeWindow {

    public HomeWindow() {
        VBox base = new VBox();
        base.getChildren().add(new Label("Pictionary"));
        base.getChildren().add(getPlayerInformation());
        base.getChildren().add(getJoinHostButtons());





    }


    public HBox getPlayerInformation(){
        HBox PlayerInfo = new HBox();
        TextField username = new TextField();
        Button LeftButton = new Button();
        ImageView profileImage = new ImageView();
        Button RightButton = new Button();
        PlayerInfo.getChildren().addAll(username,LeftButton,profileImage,RightButton);
        PlayerInfo.setSpacing(10);
        return PlayerInfo;
    }

    public GridPane getJoinHostButtons(){
        GridPane joinHostButtons = new GridPane();



        return joinHostButtons;
    }



}
