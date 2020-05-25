package windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeWindow {

    private Scene homeWindowScene;
    private int pictureIndex = 0;
    private ArrayList<String> pictureList = new ArrayList<>();
    private ImageView profileImage = new ImageView();
    private TextField username= new TextField();;

    public HomeWindow() {
        List<String> namesList = Arrays.asList("cat", "chicken", "chip", "dog", "donkey", "goldy", "owl", "pengiun", "pine", "raccoon", "robot", "rudolph", "sticktail", "union", "vampier");
        pictureList.addAll(namesList);
        VBox base = new VBox();
        base.getChildren().addAll(new Label("Pictionary"), getPlayerInformation(), getJoinHostButtons());
        base.setAlignment(Pos.CENTER);
        homeWindowScene = new Scene(base);
    }


    public HBox getPlayerInformation() {
        HBox playerInfo = new HBox();

        Button leftButton = new Button("<-");
        leftButton.setOnAction(event -> {
            if (pictureIndex > 0) {
                pictureIndex--;
            } else {
                pictureIndex = 14;
            }
            setImageView();
        });
        File file = new File("resources/pictures/cat.jpg");
        profileImage.setImage(new Image(file.toURI().toString()));
        Button rightButton = new Button("->");
        rightButton.setOnAction(event -> {
            if (pictureIndex <= 13) {
                pictureIndex++;
            } else {
                pictureIndex = 0;
            }
            setImageView();
        });
        profileImage.setFitHeight(120);
        profileImage.setFitWidth(120);

        playerInfo.getChildren().addAll(username, leftButton, profileImage, rightButton);
        playerInfo.setSpacing(10);
        playerInfo.setAlignment(Pos.CENTER);
        return playerInfo;
    }

    public void setImageView() {
        String fileName = "resources/pictures/" + pictureList.get(pictureIndex) + ".jpg";
        File newFile = new File(fileName);
        profileImage.setImage(new Image(newFile.toURI().toString()));
    }

    public GridPane getJoinHostButtons() {
        GridPane joinHostButtons = new GridPane();

        TextField privateJoinCodeTextField = new TextField();
        Button publicJoinButton = new Button("Join public game");
        publicJoinButton.setOnAction(event -> {
nameCheck();
        });
        Button privateJoinButton = new Button("Join private game");
        privateJoinButton.setOnAction(event -> {
            if (!nameCheck()){
                System.out.println("your name was null!");
            }else if (privateJoinCodeTextField.getText().trim().isEmpty()){
                System.out.println("no game room code was given");
            }
            else{
                System.out.println("the gameroom was full or failled to connect");
            }

        });
        Button privateHostButton = new Button("Host private game");
        privateHostButton.setOnAction(event -> {
            nameCheck();
        });
        Button publicHostButton = new Button("Host public game");
        publicHostButton.setOnAction(event -> {
            nameCheck();
        });


        joinHostButtons.add(publicJoinButton, 1, 2);
        joinHostButtons.add(publicHostButton, 1, 1);

        joinHostButtons.add(privateHostButton, 2, 1);
        joinHostButtons.add(privateJoinButton, 2, 2);
        joinHostButtons.add(privateJoinCodeTextField, 2, 3);

        joinHostButtons.setVgap(10);
        joinHostButtons.setHgap(10);
        joinHostButtons.setAlignment(Pos.CENTER);

        return joinHostButtons;
    }

    public boolean nameCheck (){
        if (username.getText().trim().isEmpty()){
            System.out.println("name was null");
            return false;
        }
        return true;
    }

    public Scene getHomeWindowScene() {
        return homeWindowScene;
    }
}
