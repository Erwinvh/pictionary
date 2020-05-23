package Windows;

import com.sun.xml.internal.bind.v2.TODO;
import comms.Message;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;


public class GameWindow {

    private Scene GameWindowScene;
    private Label roleLabel = new Label();
    private ArrayList<Message> chatArrayList= new ArrayList<>();
    private ScrollPane chatLog;
    private VBox chatbox = new VBox();

    public GameWindow(Stage primaryStage) {

        HBox base = new HBox();

        //TODO canvashere


        VBox information = new VBox();
        this.roleLabel.setText("tester");
        information.getChildren().add(this.roleLabel);
        information.getChildren().add(new Label("guessing word"));
        this.chatLog = new ScrollPane();
        this.chatLog.setContent(chatbox);
        information.getChildren().add(new Label("chatlog"));
        information.getChildren().add(this.chatLog);
        
        HBox input = new HBox();
        TextField messageInput = new TextField();
        Button sendButton = new Button("send");
        sendButton.setOnAction(event -> {
            if (messageInput.getText() != null) {
                new Message("tester", messageInput.getText());
            }
        });
        input.getChildren().add(messageInput);
        input.getChildren().add(sendButton);
        information.getChildren().add(input);
        base.getChildren().add(information);

        //test
        this.chatArrayList.add(new Message("tester1", "test this"));
        this.chatArrayList.add(new Message("tester 2", "test that"));
        this.chatArrayList.add(new Message("tester", "test this"));

        //
chatlogsetup();


        this.GameWindowScene = new Scene(base);
    }


    public Scene getGameWindowScene() {
        return this.GameWindowScene;
    }

    public void chatlogsetup(){
        for (Message message: chatArrayList) {
            Label label = new Label(message.toString());
            HBox hBox=new HBox();
            hBox.getChildren().add(label);
            if (message.getUsername().equals("tester")) {
                hBox.setAlignment(Pos.BASELINE_RIGHT);
            }
            this.chatbox.setSpacing(10);
            this.chatbox.getChildren().add(hBox);

        }
    }

    public void chatLogUpdate(Message newMessage){
        HBox MessageBox = new HBox();
        if (newMessage.getUsername().equals("tester")){
        }
        MessageBox.getChildren().add(new Label(newMessage.toString()));
        this.chatbox.getChildren().add(MessageBox);
    }

}
