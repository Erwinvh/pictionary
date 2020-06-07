package windows;

import comms.Client;
import comms.Server;
import comms.ServerSettings;
import comms.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private TextField serverAddressTextField;
    private TextField portTextField;

    // Launch settings
    private String serverAddress;
    private int portNumber;

    public HomeWindow(Stage primaryStage) {
        List<String> namesList = Arrays.asList("cat", "chicken", "chip", "dog", "donkey", "goldy", "owl", "pengiun", "pine", "raccoon", "robot", "rudolph", "sticktail", "union", "vampier");
        pictureList.addAll(namesList);

//        BorderPane borderPane = new BorderPane();

        VBox base = new VBox();

        Label headerLabel = new Label("Pictionary");
        headerLabel.setStyle("-fx-font-size: 50pt");
//        borderPane.setTop(headerLabel);
//        borderPane.setCenter(getPlayerInformation());
//        borderPane.setBottom(getJoinHostButtons());

        base.getChildren().addAll(headerLabel, getPlayerInformation(), getJoinHostButtons());
        base.setAlignment(Pos.TOP_CENTER);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Pictionary - Home");
        this.primaryStage.setWidth(500);
        this.primaryStage.setHeight(400);
        this.primaryStage.setScene(new Scene(base));
//        this.primaryStage.setScene(new Scene(borderPane));
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

        this.fileLocation = getClass().getResource("/pictures/cat.jpg").toString();
        profileImage.setImage(new Image(this.fileLocation));

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
        fileLocation = getClass().getResource("/pictures/" + pictureList.get(pictureIndex) + ".jpg").toString();
        profileImage.setImage(new Image(fileLocation));
    }

    private GridPane getJoinHostButtons() {
        GridPane joinHostButtons = new GridPane();

        this.serverAddressTextField = new TextField();
        this.serverAddressTextField.setText("localhost");

        this.portTextField = new TextField();
        this.portTextField.setText("10000");

        Button joinButton = new Button("Join game");
        joinButton.setOnAction(event -> {
            if (inputCheck()) {
                setupClient(false);
            }
        });
        joinButton.setPrefWidth(100);

        Button hostButton = new Button("Host game");
        hostButton.setOnAction(event -> {
            if (inputCheck()) {
                new Thread(() -> new Server(new ServerSettings(portNumber))).start();
                setupClient(true);
            }
        });
        hostButton.setPrefWidth(100);

        Label serverAddressLabel = new Label("Server address:");
        Label portLabel = new Label("Port:");
        Region emptySpace = new Region();
        emptySpace.setPrefHeight(20);

        joinHostButtons.add(serverAddressLabel, 0, 0);
        joinHostButtons.add(serverAddressTextField, 0, 1);
        joinHostButtons.add(emptySpace, 0, 2);
        joinHostButtons.add(portLabel, 0, 3);
        joinHostButtons.add(portTextField, 0, 4);
        joinHostButtons.add(hostButton, 1, 1);
        joinHostButtons.add(joinButton, 1, 4);

        joinHostButtons.setHgap(70);
        joinHostButtons.setAlignment(Pos.CENTER_LEFT);
        joinHostButtons.setPadding(new Insets(0, 0, 0, 40));

        return joinHostButtons;
    }

    private void setupClient(boolean isHost) {
        Client.getInstance().setUser(new User(username.getText(), fileLocation, isHost));
        Client.getInstance().connectToServer(serverAddress, portNumber);

        new LobbyWindow(primaryStage);
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
            serverAddress = serverAddressTextField.getText();
            portNumber = Integer.parseInt(portTextField.getText());
        } catch (Exception e) {
            System.out.println("Not a port");
            return false;
        }

        return true;
    }
}