package windows;

import comms.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class GameWindow implements DrawUpdateListener, ChatUpdateListener {

    private Scene gameWindowScene;
    private List<Message> chatArrayList = new ArrayList<>();
    private GridPane chatMessagesBox;
    private Canvas canvas = new Canvas();
    private int radius = 30;
    private FXGraphics2D graphics;
    private Color brushColor;

    public GameWindow(Stage primaryStage) {
        primaryStage.setTitle("Pictionary - Game");

        Client.getInstance().setDrawUpdateListener(this);
        Client.getInstance().setChatUpdateListener(this);

        HBox base = new HBox();

        this.gameWindowScene = new Scene(base);

        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        brushColor = Color.BLACK;

        this.chatArrayList.add(new Message("tester1", "test this"));
        this.chatArrayList.add(new Message("tester 2", "test that"));
        this.chatArrayList.add(new Message("tester", "test this"));

        base.getChildren().addAll(fullDrawSetup(), getInfoVBox());
    }

    private VBox fullDrawSetup() {
        VBox drawSideSetup = new VBox();

        HBox ButtonsBox = new HBox();
        ButtonsBox.getChildren().addAll(setupColourButtons(), getSizeButtons());

        setupCanvas();

        drawSideSetup.getChildren().addAll(canvas, ButtonsBox);
        return drawSideSetup;
    }

    private void setupCanvas() {
        canvas.setWidth(600);
        canvas.setHeight(600);

        canvas.setOnMouseClicked(this::onMouse);
        canvas.setOnMouseDragged(this::onMouse);
        draw(new FXGraphics2D(canvas.getGraphicsContext2D()));
    }

    private void onMouse(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
            graphics.setColor(brushColor);
        else {
            graphics.setColor(Color.WHITE);
        }

        Point2D position = new Point2D.Double(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        graphics.fillOval((int) mouseEvent.getSceneX() - radius, (int) mouseEvent.getSceneY() - radius, radius * 2, radius * 2);
        DrawUpdate drawUpdate = new DrawUpdate(radius, graphics.getColor(), position);
        Client.getInstance().sendObject(drawUpdate);
    }

    private GridPane setupColourButtons() {
        GridPane gridpane = new GridPane();

        Button greenButton = new Button("green");
        Button redButton = new Button("red");
        Button blackButton = new Button("black");
        Button blueButton = new Button("blue");
        Button yellowButton = new Button("yellow");
        Button orangeButton = new Button("orange");
        Button purpleButton = new Button("purple");
        Button pinkButton = new Button("pink");

        greenButton.setOnAction(event -> brushColor = Color.green);
        redButton.setOnAction(event -> brushColor = Color.red);
        blackButton.setOnAction(event -> brushColor = Color.black);
        blueButton.setOnAction(event -> brushColor = Color.blue);
        yellowButton.setOnAction(event -> brushColor = Color.yellow);
        orangeButton.setOnAction(event -> brushColor = Color.orange);
        purpleButton.setOnAction(event -> brushColor = Color.magenta);
        pinkButton.setOnAction(event -> brushColor = Color.pink);

        gridpane.add(blackButton, 1, 1);
        gridpane.add(blueButton, 1, 2);
        gridpane.add(greenButton, 2, 1);
        gridpane.add(yellowButton, 4, 2);
        gridpane.add(pinkButton, 2, 2);
        gridpane.add(purpleButton, 3, 2);
        gridpane.add(orangeButton, 4, 1);
        gridpane.add(redButton, 3, 1);

        return gridpane;
    }

    private HBox getSizeButtons() {
        HBox hBox = new HBox();

        Button smallButton = new Button("small");
        smallButton.setOnAction(event -> radius = 10);

        Button mediumButton = new Button("mid");
        mediumButton.setOnAction(event -> radius = 20);

        Button largeButton = new Button("large");
        largeButton.setOnAction(event -> radius = 30);

        hBox.getChildren().addAll(smallButton, mediumButton, largeButton);

        return hBox;
    }

    private void draw(FXGraphics2D graphics) {
        graphics.setTransform(new AffineTransform());
        graphics.setBackground(java.awt.Color.white);
        graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
    }

    private VBox getInfoVBox() {
        VBox infoVBox = new VBox();

        Label roleLabel = new Label("Guessing");
        Label currentWord = new Label("D_N__Y");
        Label chatLogLabel = new Label("Chat");

        infoVBox.getChildren().addAll(roleLabel, currentWord, chatLogLabel, getChat(), getInput());

        return infoVBox;
    }

    private ScrollPane getChat() {
        ScrollPane chatScrollPane = new ScrollPane();
        chatScrollPane.setContent(getChatMessagesBox());

        return chatScrollPane;
    }

    private GridPane getChatMessagesBox() {
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

                // Make the client send the message to the server
                Client.getInstance().sendObject(newMessage);
                //addNewMessage(newMessage);
                messageInput.clear();
            }
        });

        inputBox.getChildren().addAll(messageInput, sendButton);

        return inputBox;
    }

    private void addNewMessage(Message newMessage) {

        Label messageLabel = new Label(newMessage.toString());

        if (!chatArrayList.contains(newMessage))
            chatArrayList.add(newMessage);

        int messageRow = chatArrayList.indexOf(newMessage);
        int messageColumn = 1;

        HBox messageBox = new HBox();
        messageBox.getChildren().add(messageLabel);

        Platform.runLater(() -> this.chatMessagesBox.add(messageBox, messageColumn, messageRow));
    }

    public Scene getGameWindowScene() {
        return this.gameWindowScene;
    }

    @Override
    public void onDrawUpdate(DrawUpdate drawUpdate) {
        Platform.runLater(() -> {

            int brushSize = drawUpdate.getBrushSize();
            graphics.setColor(drawUpdate.getColor());

            Point2D point = drawUpdate.getPosition();

            graphics.fillOval((int) point.getX() - brushSize, (int) point.getY() - brushSize, brushSize * 2, brushSize * 2);
        });
    }

    @Override
    public void onChatUpdate(Message message) {
        addNewMessage(message);
    }
}