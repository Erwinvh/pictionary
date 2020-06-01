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

    // Stage
    private Stage primaryStage;

    // Picture
    private int pictureIndex = 0;
    private ArrayList<String> pictureList = new ArrayList<>();
    private ImageView profileImage = new ImageView();
    private String fileLocation;

    // User information
    private TextField username = new TextField();
    private TextField portTextField;

    // Launch settings
    private int portNumber;

    public HomeWindow(Stage primaryStage) {
        List<String> namesList = Arrays.asList("cat", "chicken", "chip", "dog", "donkey", "goldy", "owl", "pengiun", "pine", "raccoon", "robot", "rudolph", "sticktail", "union", "vampier");
        pictureList.addAll(namesList);

        VBox base = new VBox();
        base.getChildren().addAll(new Label("Pictionary"), getPlayerInformation(), getJoinHostButtons());
        base.setAlignment(Pos.CENTER);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Pictionary - Home");
        this.primaryStage.setWidth(500);
        this.primaryStage.setHeight(400);
        this.primaryStage.setScene(new Scene(base));
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
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

        fileLocation = "resources/pictures/cat.jpg";
        File file = new File(fileLocation);
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
        fileLocation = "resources/pictures/" + pictureList.get(pictureIndex) + ".jpg";
        File newFile = new File(fileLocation);
        profileImage.setImage(new Image(newFile.toURI().toString()));
    }

    private GridPane getJoinHostButtons() {
        GridPane joinHostButtons = new GridPane();

        portTextField = new TextField();
        portTextField.setText("10000");

        Button joinButton = new Button("Join game");
        joinButton.setOnAction(event -> {
            if (inputCheck()) {
                setupClient(false);
                new LobbyWindow(primaryStage);
            }
        });

        Button hostButton = new Button("Host game");
        hostButton.setOnAction(event -> {
            if (inputCheck()) {
                new Thread(() -> new Server(new ServerSettings(portNumber))).start();
                setupClient(true);
                new LobbyWindow(primaryStage);
            }
        });

        joinHostButtons.add(portTextField, 0, 1);
        joinHostButtons.add(hostButton, 1, 1);
        joinHostButtons.add(joinButton, 2, 1);

        joinHostButtons.setVgap(10);
        joinHostButtons.setHgap(10);
        joinHostButtons.setAlignment(Pos.CENTER);

        return joinHostButtons;
    }

    private void setupClient(boolean isHost) {
        Client.getInstance().setUser(new User(username.getText(), fileLocation, isHost));
        Client.getInstance().connectToServer("localhost", portNumber);
    }

    private boolean inputCheck() {
        if (username.getText().trim().isEmpty()) {
            System.out.println("Name was null");
            return false;
        } else if (portTextField.getText().trim().isEmpty()) {
            System.out.println("Port was null");
            return false;
        }

        try {
            portNumber = Integer.parseInt(portTextField.getText());
        } catch (Exception e) {
            System.out.println("Not a port");
            return false;
        }

        return true;
    }
}