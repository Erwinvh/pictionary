package windows;

import comms.Client;
import comms.GameUpdates.*;
import comms.ServerSettings;
import comms.User;
import javafx.application.Platform;
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
import java.util.List;

public class LobbyWindow implements GameUpdateListener {

    private Stage primaryStage;

    private List<User> userList;
    private VBox lobbyList = new VBox();
    private ComboBox<Integer> roundsComboBox;
    private ComboBox<String> languageComboBox;
    private ComboBox<Integer> timePerRoundComboBox;

    LobbyWindow(Stage primaryStage) {
        this(primaryStage, new ArrayList<>());
    }

    LobbyWindow(Stage primaryStage, List<User> userList){
        Client.getInstance().setGameUpdateListener(this);

        HBox base = new HBox();
        base.setSpacing(40);

        this.userList = userList;

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
        gameSettingsBox.setDisable(!Client.getInstance().getUser().isHost());
        gameSettingsBox.setAlignment(Pos.CENTER_LEFT);
        gameSettingsBox.setSpacing(10);

        Label amountOfRoundsLabel = new Label("Amount of rounds:");
        roundsComboBox = getComboBox(1, 50, 1, 4);

        Label languageLabel = new Label("Language:");
        languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll("English", "Dutch");
        languageComboBox.getSelectionModel().selectFirst();

        Label timePerRoundLabel = new Label("Time per Round in seconds");
        timePerRoundComboBox = getComboBox(10, 120, 10, 5);

        Label lobbyCodeLabel = new Label("Lobby Code");

        Button startGameButton = new Button("Start game");
        startGameButton.setOnAction(event -> {
                // Send user instance so the server can check whether I am host or not,
                // since only the host can start a game
                adjustServerSettings();
                Client.getInstance().sendObject(Client.getInstance().getUser());
        });

        gameSettingsBox.getChildren().addAll(amountOfRoundsLabel, roundsComboBox, languageLabel, languageComboBox, timePerRoundLabel, timePerRoundComboBox, lobbyCodeLabel, startGameButton);
        return gameSettingsBox;
    }

    private VBox getLobbyListBox() {
        lobbyList.setFillWidth(true);
        lobbyList.setAlignment(Pos.CENTER);

        for (User user : this.userList) {
            lobbyList.getChildren().add(playerMaker(user));
        }

        return lobbyList;
    }

    static HBox playerMaker(User user) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView();
        File file = new File(user.getProfileImage());
        imageView.setImage(new Image(file.toURI().toString()));
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);

        Label label = new Label(user.getName());

        hBox.getChildren().addAll(imageView, label);

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

    private void adjustServerSettings(){
        ServerSettings adjustedServerSettings = new ServerSettings(0);
        adjustedServerSettings.setTimeInSeconds(timePerRoundComboBox.getSelectionModel().getSelectedItem());
        adjustedServerSettings.setRounds(roundsComboBox.getSelectionModel().getSelectedItem());
        adjustedServerSettings.setLanguage(languageComboBox.getSelectionModel().getSelectedItem());
        Client.getInstance().sendObject(new SettingsUpdate(adjustedServerSettings));
    }

    @Override
    public void onGameUpdate(GameUpdate gameUpdate) {
        GameUpdate.GameUpdateType gameUpdateType = gameUpdate.getGameUpdateType();
        switch (gameUpdateType) {
            case ROUND:
                onRoundUpdate((RoundUpdate) gameUpdate);
                break;

            case USER:
                onUserUpdate((UserUpdate) gameUpdate);
                break;

            case TURN:
                System.out.println("LobbyWindow received TURN gameUpdateType");
        }
    }

    private void onRoundUpdate(RoundUpdate roundUpdate) {
        if (roundUpdate.getRoundNum() == 0) {
            Platform.runLater(() -> new GameWindow(this.primaryStage, userList));
        }
    }

    private void onUserUpdate(UserUpdate userUpdate) {
        Platform.runLater(() -> {
            if (userUpdate.hasLeft()) {
                int indexToRemove = userList.indexOf(userUpdate.getUser());
                lobbyList.getChildren().remove(indexToRemove);
                userList.remove(userUpdate.getUser());
            } else {
                userList.add(userUpdate.getUser());
                lobbyList.getChildren().add(playerMaker(userUpdate.getUser()));
            }
        });
    }
}