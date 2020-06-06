package windows;

import comms.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class EndScoreWindow {

    private List<User> userList;

    EndScoreWindow(List<User> userList) {
        this.userList = userList;
        setup();
    }

    private void setup() {
        Stage scoreStage = new Stage();
        scoreStage.setTitle("The game has ended!");

        scoreStage.setResizable(false);

        scoreStage.setWidth(500);
        scoreStage.setHeight(this.userList.size() * 100 + 50);

        Scene scoreScene = new Scene(getScoreList());

        scoreStage.setScene(scoreScene);
        scoreStage.show();
    }

    private VBox getScoreList() {
        VBox scoreList = new VBox();

        scoreList.setAlignment(Pos.TOP_CENTER);
        scoreList.setSpacing(10.0);
        scoreList.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));

        Label scoreLabel = new Label("Rankings:");
        scoreLabel.setFont(new Font("Arial", 30));
        scoreList.getChildren().add(scoreLabel);

        userList.sort(Comparator.comparingInt(User::getScore));

        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            scoreList.getChildren().add(playerMaker(user, getPlace(i + 1)));
        }

        return scoreList;
    }

    private HBox playerMaker(User user, String place) {
        HBox playerScore = LobbyWindow.playerMaker(user);
        playerScore.setAlignment(Pos.CENTER);
        playerScore.setSpacing(10);

        Label scoreLabel = new Label(place + " place with " + user.getScore() + " points:");

        playerScore.getChildren().add(0, scoreLabel);
        return playerScore;
    }

    private String getPlace(int place) {
        if (place >= 11 && place <= 13) {
            return "th";
        }

        switch (place % 10) {
            case 1:
                return place + "st";

            case 2:
                return place + "nd";

            case 3:
                return place + "rd";

            default:
                return place + "th";
        }
    }
}