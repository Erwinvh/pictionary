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
    private Stage primaryStage;

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
    private HBox drawingButtonsBox;
    private boolean isDrawing;

    GameWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Pictionary - Game");

        Client.getInstance().setGameUpdateListener(this);

        brushColor = Color.BLACK;

        chatArrayList = new ArrayList<>();

        this.primaryStage.setResizable(false);
        this.primaryStage.setWidth(1000);
        this.primaryStage.setHeight(730);
        this.primaryStage.setScene(new Scene(setupFrame()));
        this.primaryStage.show();
    }

    private VBox setupFrame() {
        VBox base = new VBox();
        HBox head = new HBox();
        head.getChildren().addAll(timeLeftLabel, currentRoundLabel, roleLabel);

        HBox body = new HBox();
        setupCanvas();
        body.getChildren().addAll(getScoreboard(), getDrawingArea(), getInfoVBox());
        base.setSpacing(10);
        base.getChildren().addAll(head, body);
        return base;
    }

    private VBox getScoreboard() {
        VBox scoreBoard = new VBox();
        scoreBoard.getChildren().add(playerScoreMaker(Client.getInstance().getUser()));
        return scoreBoard;
    }

    private HBox playerScoreMaker(User user) {
        HBox playerScore = LobbyWindow.playerMaker(user);
        playerScore.setSpacing(10);

        Label scoreLabel = new Label(user.getScore() + " Points");

        playerScore.getChildren().addAll(scoreLabel);

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
        if (!isDrawing) {
            return;
        }
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
            graphics.setColor(brushColor);
        else {
            graphics.setColor(Color.WHITE);
        }

        Point2D position = new Point2D.Double(mouseEvent.getX(), mouseEvent.getY());

        graphics.fillOval((int) mouseEvent.getX() - radius, (int) mouseEvent.getY() - radius, radius * 2, radius * 2);
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
            case TURN:
                onTurnUpdate((TurnUpdate) gameUpdate);
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
        if (userUpdate.getUser().getId() == Client.getInstance().getUser().getId()) {
            this.isDrawing = userUpdate.getUser().isDrawing();
            if (this.isDrawing) {
                this.currentWordLabel.setText("the word");
                this.drawingButtonsBox.setDisable(false);
                this.roleLabel.setText("Drawing");
                // TODO: 31/05/2020 I am drawing this round! Show the controls and the words to choose from

            }
        }else {
            this.currentWordLabel.setText(" _ ");
            this.drawingButtonsBox.setDisable(true);
            this.roleLabel.setText("Guessing");
        }

        updateScoreboard(userUpdate.getUser());
    }

    private void onTurnUpdate(TurnUpdate turnUpdate) {
        if (turnUpdate.getDrawer().equals(Client.getInstance().getUser())) {
            isDrawing = true;
        } else {
            isDrawing = false;
        }
        if (isDrawing) {
            currentWordLabel.setText(turnUpdate.getWord());
        } else {
            String guessWord = "";
            for (int i = 0; i < turnUpdate.getWord().length(); i++) {
                guessWord = guessWord + "_ ";
            }
            currentWordLabel.setText(guessWord);
        }
    }

    private void updateScoreboard(User user) {

    }

    private VBox getDrawingArea() {
        VBox drawSideSetup = new VBox();

        drawingButtonsBox = new HBox();
        drawingButtonsBox.getChildren().addAll(getColourButtons(), getSizeButtons(), getClearCanvasButton());
        drawingButtonsBox.setSpacing(20);

        drawSideSetup.getChildren().addAll(canvas, drawingButtonsBox);
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
                ChatUpdate newChatUpdate = new ChatUpdate(Client.getInstance().getUser(), messageInput.getText());

                // Make the client send the message to the server
                Client.getInstance().sendObject(newChatUpdate);
                messageInput.clear();
            }
        });

        inputBox.getChildren().addAll(messageInput, sendButton);

        return inputBox;
    }
}