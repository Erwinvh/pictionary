package windows;

import comms.Client;
import comms.GameUpdates.*;
import comms.User;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameWindow implements GameUpdateListener {

    //Stage
    private Scene gameWindowScene;
    private Stage PrimaryStage;

    // Chat
    private List<ChatUpdate> chatArrayList;
    private GridPane chatMessagesBox;

    //Game information
    private Label roleLabel = new Label("Guessing");
    private Label currentWordLabel = new Label();
    private Label timeLeftLabel = new Label("180");
    private Label currentRoundLabel = new Label("");

    // Drawing
    private int radius = 30;
    private FXGraphics2D graphics;
    private Color brushColor;
    private Canvas canvas;



    public GameWindow(Stage primaryStage) {
        PrimaryStage = primaryStage;
        PrimaryStage.setTitle("Pictionary - Game");

        Client.getInstance().setGameUpdateListener(this);




        brushColor = Color.BLACK;

        chatArrayList = new ArrayList<>();

        PrimaryStage.setResizable(false);
        PrimaryStage.setWidth(1000);
        PrimaryStage.setHeight(730);
        this.gameWindowScene = new Scene(setupFrame());
        PrimaryStage.setScene(gameWindowScene);
        PrimaryStage.show();
    }

    private VBox setupFrame(){
       VBox base = new VBox();
        HBox head = new HBox();
        head.getChildren().addAll(timeLeftLabel,currentRoundLabel,roleLabel);

        HBox body = new HBox();
        setupCanvas();
        body.getChildren().addAll(getScoreboard(),getDrawingArea(), getInfoVBox());
        base.setSpacing(10);
        base.getChildren().addAll(head,body);
        return base;
    }

    public VBox getScoreboard(){
        VBox ScoreBoard = new VBox();
        ScoreBoard.getChildren().add(playerScoreMaker(Client.getInstance().getUser()));
        return ScoreBoard;
    }

    public HBox playerScoreMaker(User user){
        HBox playerScore = new HBox();
        playerScore.setSpacing(10);

        ImageView imageView = new ImageView();
        File file = new File(user.getProfileImage());
        imageView.setImage(new Image(file.toURI().toString()));
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        Label label = new Label(user.getName());
        Label scoreLabel = new Label(user.getScore() + " Points");

        playerScore.getChildren().addAll(imageView,label,scoreLabel);

        return playerScore;
    }

    private void setupCanvas() {
        this.canvas = new Canvas();
        this.canvas.setWidth(600);
        this.canvas.setHeight(600);

        this.canvas.setOnMouseClicked(this::onMouse);
        this.canvas.setOnMouseDragged(this::onMouse);

        setupGraphics();
    }

    private void setupGraphics() {
        this.graphics = new FXGraphics2D(this.canvas.getGraphicsContext2D());
        this.graphics.setTransform(new AffineTransform());
        this.graphics.setBackground(java.awt.Color.white);
        this.graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
    }

    private void onMouse(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
            graphics.setColor(brushColor);
        else {
            graphics.setColor(Color.WHITE);
        }

        Point2D position = new Point2D.Double(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        graphics.fillOval((int) mouseEvent.getSceneX() - radius, (int) mouseEvent.getSceneY() - radius, radius * 2, radius * 2);
        DrawUpdate drawUpdate = new DrawUpdate(radius, graphics.getColor(), position, false);
        Client.getInstance().sendObject(drawUpdate);
    }

    private void addNewMessage(ChatUpdate newChatUpdate) {
        Label messageLabel = new Label(newChatUpdate.toString());

        if (!chatArrayList.contains(newChatUpdate))
            chatArrayList.add(newChatUpdate);

        int messageRow = chatArrayList.indexOf(newChatUpdate);
        int messageColumn = 1;

        HBox messageBox = new HBox();
        messageBox.getChildren().add(messageLabel);

        Platform.runLater(() -> this.chatMessagesBox.add(messageBox, messageColumn, messageRow));
    }

    @Override
    public void onGameUpdate(GameUpdate gameUpdate) {
        GameUpdate.GameUpdateType gameUpdateType = gameUpdate.getGameUpdateType();
        switch (gameUpdateType) {
            case CHAT:
                onChatUpdate((ChatUpdate) gameUpdate);
                break;

            case DRAW:
                onDrawUpdate((DrawUpdate) gameUpdate);
                break;

            case ROUND:
                onRoundUpdate((RoundUpdate) gameUpdate);
                break;

            case TIMER:
                onTimerUpdate((TimerUpdate) gameUpdate);
                break;

            case USER:
                onUserUpdate((UserUpdate) gameUpdate);
                break;
        }
    }

    private void onDrawUpdate(DrawUpdate drawUpdate) {
        Platform.runLater(() -> {
            if (drawUpdate.isShouldClearCanvas()) {
                graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
            } else {
                int brushSize = drawUpdate.getBrushSize();
                graphics.setColor(drawUpdate.getColor());

                Point2D point = drawUpdate.getPosition();

                graphics.fillOval((int) point.getX() - brushSize, (int) point.getY() - brushSize, brushSize * 2, brushSize * 2);
            }
        });
    }

    private void onChatUpdate(ChatUpdate chatUpdate) {
        addNewMessage(chatUpdate);
    }

    private void onRoundUpdate(RoundUpdate roundUpdate) {
        Platform.runLater(() -> this.currentRoundLabel.setText(String.format("Round %s of %s rounds", roundUpdate.getRoundNum(), roundUpdate.getMaxRounds())));
    }

    private void onTimerUpdate(TimerUpdate timerUpdate) {
        Platform.runLater(() -> this.timeLeftLabel.setText(String.valueOf(timerUpdate.getTimeLeft())));
    }

    private void onUserUpdate(UserUpdate userUpdate) {
        if (userUpdate.hasLeft()) {
            // TODO: 31/05/2020 Remove this user from the display board
            return;
        }

        // If the user update is this user itself
        if (userUpdate.getUser().equals(Client.getInstance().getUser())) {
            if (userUpdate.getUser().isDrawing()) {
                // TODO: 31/05/2020 I am drawing this round! Show the controls and the words to choose from
            }
        }

        updateScoreboard(userUpdate.getUser());
    }

    private void updateScoreboard(User user) {

    }

    private VBox getDrawingArea() {
        VBox drawSideSetup = new VBox();

        HBox buttonsBox = new HBox();
        buttonsBox.getChildren().addAll(getColourButtons(), getSizeButtons(), getClearCanvasButton());
        buttonsBox.setSpacing(20);

        drawSideSetup.getChildren().addAll(canvas, buttonsBox);
        return drawSideSetup;
    }

    private GridPane getColourButtons() {
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

    private Button getClearCanvasButton() {
        Button button = new Button("Clear canvas");
        button.setOnAction(event -> {
            DrawUpdate drawUpdate = new DrawUpdate(0, Color.white, null, true);

            Client.getInstance().sendObject(drawUpdate);
        });

        return button;
    }

    private VBox getInfoVBox() {
        VBox infoVBox = new VBox();

        Label chatLogLabel = new Label("Chat");

        infoVBox.getChildren().addAll(this.roleLabel, this.currentWordLabel, chatLogLabel, getChat(), getInput());

        return infoVBox;
    }

    private ScrollPane getChat() {
        ScrollPane chatScrollPane = new ScrollPane();

        chatMessagesBox = new GridPane();
        chatMessagesBox.setVgap(10);

        for (ChatUpdate chatUpdate : chatArrayList) {
            addNewMessage(chatUpdate);
        }

        chatScrollPane.setContent(chatMessagesBox);

        return chatScrollPane;
    }

    private HBox getInput() {
        HBox inputBox = new HBox();
        TextField messageInput = new TextField();
        Button sendButton = new Button("Send");

        sendButton.setOnAction(event -> {
            if (messageInput.getText() != null) {
                ChatUpdate newChatUpdate = new ChatUpdate(Client.getInstance().getUser().getName(), messageInput.getText());

                // Make the client send the message to the server
                Client.getInstance().sendObject(newChatUpdate);
                messageInput.clear();
            }
        });

        inputBox.getChildren().addAll(messageInput, sendButton);

        return inputBox;
    }

    public Scene getGameWindowScene() {
        return this.gameWindowScene;
    }
}