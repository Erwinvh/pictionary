package windows;

import comms.Client;
import comms.Server;
import comms.ServerSettings;
import comms.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeWindow {

    private Scene homeWindowScene;
    private int pictureIndex = 0;
    private ArrayList<String> pictureList = new ArrayList<>();
    private ImageView profileImage = new ImageView();
    private TextField username = new TextField();
    private Stage PrimaryStage;
    private TextField portTextField;
    private int portNumber;
    private String filelocation;

    public HomeWindow(Stage primaryStage) {
        List<String> namesList = Arrays.asList("cat", "chicken", "chip", "dog", "donkey", "goldy", "owl", "pengiun", "pine", "raccoon", "robot", "rudolph", "sticktail", "union", "vampier");
        pictureList.addAll(namesList);
        VBox base = new VBox();
        base.getChildren().addAll(new Label("Pictionary"), getPlayerInformation(), getJoinHostButtons());
        base.setAlignment(Pos.CENTER);
        homeWindowScene = new Scene(base);
        PrimaryStage = primaryStage;
        PrimaryStage.setTitle("Pictionary - Home");
        PrimaryStage.setWidth(500);
        PrimaryStage.setHeight(400);
        PrimaryStage.setScene(homeWindowScene);
        PrimaryStage.setResizable(false);
        PrimaryStage.show();
    }

    private HBox getPlayerInformation() {
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

        filelocation = "resources/pictures/cat.jpg";
        File file = new File(filelocation);
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

    private void setImageView() {
        filelocation = "resources/pictures/" + pictureList.get(pictureIndex) + ".jpg";
        File newFile = new File(filelocation);
        profileImage.setImage(new Image(newFile.toURI().toString()));
    }

    private GridPane getJoinHostButtons() {
        GridPane joinHostButtons = new GridPane();
        portTextField = new TextField();
        Button privateJoinButton = new Button("Join game");
        privateJoinButton.setOnAction(event -> {
            if (inputCheck(false)) {
                LobbyWindow LB = new LobbyWindow(PrimaryStage);
            }
        });

        Button privateHostButton = new Button("Host game");
        privateHostButton.setOnAction(event -> {
            if (inputCheck(true)) {
                Server host = new Server(new ServerSettings(portNumber));
                LobbyWindow LB = new LobbyWindow(PrimaryStage);
            }
        });
        joinHostButtons.add(portTextField, 0, 1);
        joinHostButtons.add(privateHostButton, 1, 1);
        joinHostButtons.add(privateJoinButton, 2, 1);


        joinHostButtons.setVgap(10);
        joinHostButtons.setHgap(10);
        joinHostButtons.setAlignment(Pos.CENTER);

        return joinHostButtons;
    }

    private boolean inputCheck(boolean isHost) {
        if (username.getText().trim().isEmpty()) {
            System.out.println("name was null");
            return false;
        } else if (portTextField.getText().trim().isEmpty()) {
            System.out.println("port was null");
            return false;
        }
        try {
            portNumber = Integer.parseInt(portTextField.getText());
        } catch (Exception e) {
            System.out.println("not a port");
            return false;
        }
        Client.getInstance().setUser(new User(username.getText(), filelocation ,isHost));
        Client.getInstance().connectToServer("localhost", portNumber);
        return true;
    }
}