package windows;

import comms.Client;
import comms.Message;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GameWindow {

    private Scene gameWindowScene;

    private ArrayList<Message> chatArrayList = new ArrayList<>();
    private GridPane chatMessagesBox;

    public GameWindow(Stage primaryStage) {
        HBox base = new HBox();

        //TODO canvashere
        // base.getChildren().add(canvasshit);
        //test
        this.chatArrayList.add(new Message("tester1", "test this"));
        this.chatArrayList.add(new Message("tester 2", "test that"));
        this.chatArrayList.add(new Message("tester", "test this"));

        base.getChildren().add(getInfoVBox());

        this.gameWindowScene = new Scene(base);
    }

    private VBox getInfoVBox() {
        VBox infoVBox = new VBox();

        Label roleLabel = new Label("Guessing");
        Label currentWord = new Label("D_N__Y");
        Label chatLogLabel = new Label("Chat");

        infoVBox.getChildren().addAll(roleLabel, currentWord, chatLogLabel, getChat(), getInput());

        return infoVBox;
    }

    private ScrollPane getChat(){
        ScrollPane chatScrollPane = new ScrollPane();
        chatScrollPane.setContent(getChatMessagesBox());

        return chatScrollPane;
    }

    private GridPane getChatMessagesBox(){
        chatMessagesBox = new GridPane();

        chatMessagesBox.setVgap(10);

        for (Message message : chatArrayList) {
                addNewMessage(message);
        }

        return chatMessagesBox;
    }

    private HBox getInput() {
        HBox inputBox = new HBox();
        TextField messageInput = new TextField();
        Button sendButton = new Button("Send");

        sendButton.setOnAction(event -> {
            if (messageInput.getText() != null) {
                Message newMessage = new Message(Client.getInstance().getUser().getName(), messageInput.getText());
                addNewMessage(newMessage);
                messageInput.clear();
            }
        });

        inputBox.getChildren().addAll(messageInput, sendButton);

        return inputBox;
    }

    private void addNewMessage(Message newMessage){

        Label messageLabel = new Label(newMessage.toString());

        if (!chatArrayList.contains(newMessage))
            chatArrayList.add(newMessage);

        int messageRow = chatArrayList.indexOf(newMessage);
        int messageColumn = 1;

        HBox messageBox = new HBox();
        messageBox.getChildren().add(messageLabel);

        if (newMessage.getUsername().equals(Client.getInstance().getUser().getName())) {
            messageColumn = 2;
        }

        chatMessagesBox.add(messageBox, messageColumn, messageRow);
    }

    public Scene getGameWindowScene() {
        return this.gameWindowScene;
    }
}