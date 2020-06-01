package windows;

import comms.Client;
import comms.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class LobbyWindow {

    private ArrayList<User> lobbyArrayList;
    private Stage primaryStage;
    private VBox lobbyList;

    LobbyWindow(Stage primaryStage) {
        HBox base = new HBox();
        base.setSpacing(40);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMaxWidth(1000);
        scrollPane.setContent(getLobbyListBox());

        base.getChildren().addAll(getGameSettingsBox(), scrollPane);
        this.primaryStage = primaryStage;
        this.primaryStage.setScene(new Scene(base));
        this.primaryStage.setTitle("Pictionary - Lobby");
        this.primaryStage.setHeight(600);
        this.primaryStage.setWidth(500);
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
    }

    private VBox getGameSettingsBox() {
        VBox gameSettingsBox = new VBox();
        gameSettingsBox.setAlignment(Pos.CENTER_LEFT);
        gameSettingsBox.setSpacing(10);

        Label amountOfRoundsLabel = new Label("Amount of rounds:");
        ComboBox<Integer> roundsComboBox = getComboBox(1, 50, 1, 4);

        Label languageLabel = new Label("Language:");
        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll("English", "Dutch");
        languageComboBox.getSelectionModel().selectFirst();

        Label timePerRoundLabel = new Label("Time per Round in seconds");
        ComboBox<Integer> timePerRoundComboBox = getComboBox(10, 120, 10, 5);

        Label maxAmountPlayersLabel = new Label("Max. amount of players:");
        ComboBox<Integer> maxAmountPlayersComboBox = getComboBox(2, 20, 1, 0);

        Label lobbyCodeLabel = new Label("Lobby Code");

        Button startGameButton = new Button("Start game");
        startGameButton.setOnAction(event -> {
            // TODO: 27/05/2020 Launch the GameWindow (if settings are valid)
            if (getLobbySize() <= maxAmountPlayersComboBox.getSelectionModel().getSelectedItem()) {
                GameWindow gameWindow = new GameWindow(primaryStage);
            } else {
                System.out.println("You have too many players");
            }
        });

        gameSettingsBox.getChildren().addAll(amountOfRoundsLabel, roundsComboBox, languageLabel, languageComboBox, timePerRoundLabel, timePerRoundComboBox, maxAmountPlayersLabel, maxAmountPlayersComboBox, lobbyCodeLabel, startGameButton);
        return gameSettingsBox;
    }

    private VBox getLobbyListBox() {
        lobbyList = new VBox();
        lobbyList.setFillWidth(true);
        lobbyList.setAlignment(Pos.CENTER);

//        for (User user: A list of users on the server){
//            lobbyList.getChildren().add(playerMaker(user));
//        }
        lobbyList.getChildren().addAll(playerMaker(new User("tester1", "resources/pictures/cat.jpg", false)), playerMaker(new User("tester1", "resources/pictures/cat.jpg", false)), playerMaker(Client.getInstance().getUser()));
        return lobbyList;
    }

    private HBox playerMaker(User user) {
        HBox hBox = new HBox();
        ImageView imageView = new ImageView();
        File file = new File(user.getProfileImage());
        imageView.setImage(new Image(file.toURI().toString()));
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);

        Label label = new Label(user.getName());

        hBox.getChildren().addAll(imageView, label);
        hBox.setAlignment(Pos.CENTER);

        return hBox;
    }

    private ComboBox<Integer> getComboBox(int min, int limit, int stepSize, int selectIndex) {
        ComboBox<Integer> comboBox = new ComboBox<>();
        for (int i = min; i <= limit; i += stepSize) {
            comboBox.getItems().add(i);
        }

        comboBox.getSelectionModel().select(selectIndex);
        return comboBox;
    }

    private int getLobbySize() {
        return lobbyList.getChildren().size();
    }
}