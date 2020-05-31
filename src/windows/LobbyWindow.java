package windows;

import comms.Client;
import comms.User;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;

import java.io.File;
import java.util.ArrayList;

public class LobbyWindow {

    private ArrayList<User> lobbyArrayList;
    private Stage PrimaryStage;
    private VBox LobbyList;

    public LobbyWindow(Stage primaryStage) {
        HBox base = new HBox();
        base.setSpacing(40);

        ScrollPane listscroller = new ScrollPane();
        listscroller.setMaxWidth(1000);
        listscroller.setContent(getLobbyListBox());

        base.getChildren().addAll(getGameSettingsBox(), listscroller);
        PrimaryStage = primaryStage;
        PrimaryStage.setScene(new Scene(base));
        PrimaryStage.setTitle("Pictionary - Lobby");
        PrimaryStage.setHeight(600);
        PrimaryStage.setWidth(500);
        PrimaryStage.setResizable(false);
        PrimaryStage.show();
    }

    private VBox getGameSettingsBox() {
        VBox gameSettingsBox = new VBox();
        gameSettingsBox.setAlignment(Pos.CENTER_LEFT);
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

        Label lobbyCodeLabel = new Label("Lobby Code");

        Button startGameButton = new Button("Start game");
        startGameButton.setOnAction(event -> {
            // TODO: 27/05/2020 Launch the GameWindow (if settings are valid)
            if (getLobbySize()<=(Integer) maxAmountPlayersComboBox.getSelectionModel().getSelectedItem()) {
                GameWindow gameWindow = new GameWindow(PrimaryStage);
            }else {
                System.out.println("you have too many players");
            }
        });

        gameSettingsBox.getChildren().addAll(amountOfRoundsLabel, roundsComboBox, languageLabel, languageComboBox, timePerRoundLabel, timePerRoundComboBox, maxAmountPlayersLabel, maxAmountPlayersComboBox, lobbyCodeLabel, startGameButton);
        return gameSettingsBox;
    }

    private VBox getLobbyListBox() {
        LobbyList = new VBox();
        LobbyList.setFillWidth(true);
        LobbyList.setAlignment(Pos.CENTER);

//        for (User user: A list of users on the server){
//            LobbyList.getChildren().add(playermaker(user));
//        }
        LobbyList.getChildren().addAll(playermaker(new User("tester1","resources/pictures/cat.jpg",false)),playermaker(new User("tester1","resources/pictures/cat.jpg",false)), playermaker(Client.getInstance().getUser()));
        return LobbyList;
    }

    private HBox playermaker(User user) {
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

    private ComboBox getComboBox(int min, int limit, int stepSize, int selectIndex) {
        ComboBox<Integer> comboBox = new ComboBox<>();
        for (int i = min; i <= limit; i++) {
            comboBox.getItems().add(i);
        }

        comboBox.getSelectionModel().select(selectIndex);
        return comboBox;
    }

    public int getLobbySize(){
        ObservableList<Node> childrens = LobbyList.getChildren();
        int size = 0;
        for (Node node:childrens
             ) {
            size++;
        }return size;
    }

}