package windows;

import comms.Client;
import comms.GameUpdates.*;
import comms.ServerSettings;
import comms.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static comms.GameUpdates.StateUpdate.stateType.LOBBY;

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

    LobbyWindow(Stage primaryStage, List<User> userList) {
        Client.getInstance().setGameUpdateListener(this);

        this.userList = userList;

        BorderPane borderPane = new BorderPane();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMaxWidth(1000);
        scrollPane.setPrefWidth(200);
        scrollPane.setContent(getLobbyListBox());

        borderPane.setLeft(getGameSettingsBox());
        borderPane.setRight(scrollPane);

        this.primaryStage = primaryStage;
        this.primaryStage.setScene(new Scene(borderPane));
        this.primaryStage.setTitle("Pictionary - Lobby - " + Client.getInstance().getUser().getName());
        this.primaryStage.setHeight(600);
        this.primaryStage.setWidth(500);
        this.primaryStage.setResizable(false);
        this.primaryStage.show();

        Client.getInstance().sendObject(new StateUpdate(Client.getInstance().getUser(), LOBBY));
    }

    static HBox playerMaker(User user) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView();
        File file = new File(user.getProfileImage());
        imageView.setImage(new Image(file.toURI().toString()));
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);

        Label label = new Label(user.getName());
        label.setMaxWidth(100);

        hBox.getChildren().addAll(imageView, label);

        return hBox;
    }

    private VBox getGameSettingsBox() {
        VBox gameSettingsBox = new VBox();

        // Do not allow interaction when the user is not the host of this game
        gameSettingsBox.setDisable(!Client.getInstance().getUser().isHost());

        gameSettingsBox.setAlignment(Pos.CENTER_LEFT);
        gameSettingsBox.setPadding(new Insets(0.0, 0.0, 0.0, 20.0));

        gameSettingsBox.setSpacing(5);

        Label amountOfRoundsLabel = new Label("Amount of rounds:");
        roundsComboBox = getComboBox(2, 50, 1, 4);

        Region regionRounds = new Region();
        regionRounds.setPrefHeight(10);

        Label languageLabel = new Label("Language:");
        languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll("English", "Dutch");
        languageComboBox.getSelectionModel().selectFirst();

        Region regionLanguage = new Region();
        regionLanguage.setPrefHeight(10);

        Label timePerRoundLabel = new Label("Time per round in seconds");
        timePerRoundComboBox = getComboBox(10, 180, 10, 5);

        Region regionTime = new Region();
        regionTime.setPrefHeight(20);

        Button startGameButton = new Button("Start game");

        if (Client.getInstance().getUser().isHost()) {
            roundsComboBox.valueProperty().addListener(e -> adjustServerSettings());
            languageComboBox.valueProperty().addListener(e -> adjustServerSettings());
            timePerRoundComboBox.valueProperty().addListener(e -> adjustServerSettings());

            startGameButton.setOnAction(event -> {
                if (userList.size() <= 1) return;

                adjustServerSettings();

                // Send user instance so the server can check whether I am host or not,
                // since only the host can start a game
                Client.getInstance().sendObject(Client.getInstance().getUser());
            });
        }

        gameSettingsBox.getChildren().addAll(amountOfRoundsLabel, roundsComboBox, regionRounds, languageLabel, languageComboBox, regionLanguage, timePerRoundLabel, timePerRoundComboBox, regionTime, startGameButton);

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

    private ComboBox<Integer> getComboBox(int min, int limit, int stepSize, int selectIndex) {
        ComboBox<Integer> comboBox = new ComboBox<>();
        for (int i = min; i <= limit; i += stepSize) {
            comboBox.getItems().add(i);
        }

        comboBox.getSelectionModel().select(selectIndex);
        return comboBox;
    }

    private void adjustServerSettings() {
        ServerSettings adjustedServerSettings = new ServerSettings();
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

            case SETTINGS:
                onSettingsUpdate((SettingsUpdate) gameUpdate);
                break;

            case TURN:
                System.out.println("LobbyWindow received TURN GameUpdateType");
        }
    }

    private void onRoundUpdate(RoundUpdate roundUpdate) {
        if (roundUpdate.getRoundNum() == 1) {
            Platform.runLater(() -> new GameWindow(this.primaryStage, userList, roundsComboBox.getSelectionModel().getSelectedItem()));
        }
    }

    private void onUserUpdate(UserUpdate userUpdate) {
        int matchingIndex = userList.indexOf(userUpdate.getUser());

        Platform.runLater(() -> {
            // If the user has left, try to remove it from the list,
            // if this is successful then also remove it from the scoreboard
            if (userUpdate.hasLeft() && this.userList.remove(userUpdate.getUser())) {
                lobbyList.getChildren().remove(matchingIndex);
            }

            // Otherwise if the user was already added to our list we can find the index and update that user
            else if (this.userList.contains(userUpdate.getUser())) {
                this.lobbyList.getChildren().set(matchingIndex, playerMaker(userUpdate.getUser()));
                this.userList.set(matchingIndex, userUpdate.getUser());
            }

            // Otherwise the user has just joined and we should add it to our list
            else {
                this.lobbyList.getChildren().add(playerMaker(userUpdate.getUser()));
                this.userList.add(userUpdate.getUser());
            }
        });
    }

    private void onSettingsUpdate(SettingsUpdate settingsUpdate) {

        if (Client.getInstance().getUser().isHost()) return;

        ServerSettings newSettings = settingsUpdate.getServerSettings();

        Platform.runLater(() -> {
            roundsComboBox.getSelectionModel().select((Integer) newSettings.getRounds());
            languageComboBox.getSelectionModel().select(newSettings.getLanguage());
            timePerRoundComboBox.getSelectionModel().select((Integer) newSettings.getTimeInSeconds());
        });
    }
}