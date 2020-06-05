package windows;

import comms.User;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.List;

public class EndScoreWindow {

    private List<User> userList;

    public EndScoreWindow(List<User> userList) {
        this.userList = userList;
        setUp();
    }

    public void setUp(){
        Stage scoreStage = new Stage();
        Scene scoreScene = new Scene(ScoreList());
        scoreStage.setScene(scoreScene);
        scoreStage.show();
    }

    private VBox ScoreList(){
        Label scoreLabel = new Label("Score list:");
        VBox scoreList = new VBox();
        scoreList.getChildren().add(scoreLabel);
        ArrayList<User> contained = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User highestUser = null;
            int highest = -1;
            for (User user: userList) {

                if (user.getScore()>highest&&!contained.contains(user)){
                    highest = user.getScore();
                    highestUser = user;
                }
            }
            if (highestUser!=null)contained.add(highestUser);
        }
        for (User user:contained) {
            scoreList.getChildren().add(playermaker(user));
        }
        return scoreList;
    }

    private HBox playermaker(User user){
        HBox playerScore = LobbyWindow.playerMaker(user);

        playerScore.setSpacing(10);

        javafx.scene.control.Label scoreLabel = new javafx.scene.control.Label(user.getScore() + " Points");

        playerScore.getChildren().addAll(scoreLabel);
        return playerScore;
    }



}
