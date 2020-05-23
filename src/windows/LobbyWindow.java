package windows;

import comms.User;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;


public class LobbyWindow {

    private ArrayList<User> lobbyArrayList;
    private Scene lobbyWindowScene;

    public LobbyWindow() {
        HBox base = new HBox();
        base.getChildren().add(getGameSettingsBox());
        ScrollPane listscroller = new ScrollPane();
        listscroller.setContent(getLobbyListBox());
        base.getChildren().add(listscroller);
        lobbyWindowScene = new Scene(base);
    }

    public VBox getGameSettingsBox(){
        VBox gameSettingsBox = new VBox();

        Label amountOfRoundsLabel = new Label("Amount of rounds:");
        ComboBox roundsComboBox = getComboBox(50);

        Label languageLabel = new Label("Language:");
        ComboBox languageComboBox = new ComboBox();
        languageComboBox.getItems().add("English");
        languageComboBox.getItems().add("Dutch");
        languageComboBox.getSelectionModel().selectFirst();

        Label timePerRoundLabel = new Label("Time per Round in seconds");
        ComboBox timePerRoundComboBox = getTimeComboBox(120);

        Label maxAmountPlayersLabel = new Label("Max. amount of players:");
        ComboBox maxAmountPlayersComboBox = getComboBox(20);

        Label privateGameCode = new Label();

        gameSettingsBox.getChildren().addAll(amountOfRoundsLabel,roundsComboBox,languageLabel,languageComboBox,timePerRoundLabel,timePerRoundComboBox,maxAmountPlayersLabel,maxAmountPlayersComboBox, privateGameCode);
        return gameSettingsBox;
    }

    public VBox getLobbyListBox(){
        VBox lobbyListBox = new VBox();

        return lobbyListBox;
    }

    public ComboBox getTimeComboBox(int limit){
        ComboBox comboBox = new ComboBox();
        for (int i = 10; i <= limit; i+=10) {
            comboBox.getItems().add(i);
        }
        comboBox.getSelectionModel().select(2);
        return comboBox;
    }

    public Scene getLobbyWindowScene() {
        return lobbyWindowScene;
    }

    public ComboBox getComboBox (int limit){
        ComboBox comboBox = new ComboBox();
        for (int i = 2; i <= limit; i++) {
            comboBox.getItems().add(i);
        }
        comboBox.getSelectionModel().selectFirst();
        return comboBox;
    }


}
