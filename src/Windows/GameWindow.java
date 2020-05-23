package Windows;

import comms.Message;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
    private GridPane chatbox = new GridPane();

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
                new Message(, messageInput.getText());
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
            int row = chatArrayList.indexOf(message);
            int column = 1;
            if (message.getUsername().equals("tester")) {
column=2;
            }
            this.chatbox.add(label,column,row);

        }
    }

//    public void chatLogUpdate(Message newMessage){
//        HBox MessageBox = new HBox();
//        if (newMessage.getUsername().equals("tester")){
//        }
//        MessageBox.getChildren().add(new Label(newMessage.toString()));
//        this.chatbox.getChildren().add(MessageBox);
//    }

}
