package windows;

import comms.User;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;

import java.util.ArrayList;

public class LobbyWindow {

    private ArrayList<User> lobbyArrayList;
    private Scene lobbyWindowScene;

    public LobbyWindow() {
        HBox base = new HBox();
        base.setSpacing(40);

        ScrollPane listscroller = new ScrollPane();
        listscroller.setContent(getLobbyListBox());

        base.getChildren().addAll(getGameSettingsBox(), listscroller);
        lobbyWindowScene = new Scene(base);
    }

    private VBox getGameSettingsBox(){
        VBox gameSettingsBox = new VBox();
        gameSettingsBox.setSpacing(10);

        Label amountOfRoundsLabel = new Label("Amount of rounds:");
        ComboBox roundsComboBox = getComboBox(1, 50, 1, 4);

        Label languageLabel = new Label("Language:");
        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll("English", "Dutch");
        languageComboBox.getSelectionModel().selectFirst();

        Label timePerRoundLabel = new Label("Time per Round in seconds");
        ComboBox timePerRoundComboBox = getComboBox(10, 120, 10, 5);

        Label maxAmountPlayersLabel = new Label("Max. amount of players:");
        ComboBox maxAmountPlayersComboBox = getComboBox(2, 20, 1, 0);

        Button startGameButton = new Button("Start game");
        startGameButton.setOnAction(event -> {
            // TODO: 27/05/2020 Launch the GameWindow (if settings are valid)
        });

        gameSettingsBox.getChildren().addAll(amountOfRoundsLabel,roundsComboBox,languageLabel,languageComboBox,timePerRoundLabel,timePerRoundComboBox,maxAmountPlayersLabel,maxAmountPlayersComboBox, startGameButton);
        return gameSettingsBox;
    }

    private VBox getLobbyListBox(){
        return new VBox();
    }

    public Scene getLobbyWindowScene() {
        return lobbyWindowScene;
    }

    private ComboBox getComboBox(int min, int limit, int stepSize, int selectIndex){
        ComboBox<Integer> comboBox = new ComboBox<>();
        for (int i = min; i <= limit; i++) {
            comboBox.getItems().add(i);
        }

        comboBox.getSelectionModel().select(selectIndex);
        return comboBox;
    }
}