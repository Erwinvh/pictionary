package windows;

import comms.Client;
import comms.GameUpdates.*;
import comms.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static comms.GameUpdates.StateUpdate.stateType.GAME;

public class GameWindow implements GameUpdateListener {

    //Stage
    private Stage primaryStage;

    // Chat
    private List<ChatUpdate> chatArrayList;
    private GridPane chatMessagesBox;

    //Game information
    private Label roleLabel = new Label("Guessing");
    private Label currentWordLabel = new Label();
    private Label timeLeftLabel = new Label();
    private Label currentRoundLabel;
    private Button sendButton;
    private VBox scoreBoard = new VBox();
    private List<User> userList;

    // Drawing
    private int radius = 30;
    private FXGraphics2D graphics;
    private Color brushColor;
    private Canvas canvas;
    private HBox drawingButtonsBox;
    private boolean isDrawing;

    GameWindow(Stage primaryStage, List<User> userList, int maxRounds) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Pictionary - Game - " + Client.getInstance().getUser().getName());

        Client.getInstance().setGameUpdateListener(this);

        this.userList = userList;

        brushColor = Color.BLACK;
        this.currentRoundLabel = new Label(String.format("Round 1 of %s rounds", maxRounds));
        chatArrayList = new ArrayList<>();

        this.primaryStage.setResizable(false);
        this.primaryStage.setWidth(1200);
        this.primaryStage.setHeight(780);
        this.primaryStage.setScene(new Scene(setupFrame()));
        this.primaryStage.show();

        Client.getInstance().sendObject(new StateUpdate(Client.getInstance().getUser(), GAME));
    }

    private BorderPane setupFrame() {
        BorderPane frame = new BorderPane();
        setupCanvas();
        this.currentWordLabel.setFont(new Font("Arial", 30));
        this.timeLeftLabel.setFont(new Font("Arial", 30));
        this.currentRoundLabel.setFont(new Font("Arial", 30));
        HBox head = new HBox();
        head.setPadding(new Insets(5, 5, 5, 5));
        Region emptySpace1 = new Region();
        Region emptySpace2 = new Region();
        head.getChildren().addAll(this.timeLeftLabel, emptySpace1, this.currentWordLabel, emptySpace2, this.currentRoundLabel);
        head.setHgrow(emptySpace1, Priority.ALWAYS);
        head.setHgrow(emptySpace2, Priority.ALWAYS);
//        this.timeLeftLabel.setAlignment(Pos.BASELINE_LEFT);
//        this.currentWordLabel.setAlignment(Pos.CENTER);
//        this.currentRoundLabel.setAlignment(Pos.BASELINE_RIGHT);
        head.setPrefWidth(primaryStage.getWidth());
        frame.setTop(head);
        frame.setAlignment(head, Pos.CENTER);
        frame.setCenter(getDrawingArea());
        frame.setLeft(getScoreboard());
        frame.setRight(getInfoVBox());
        return frame;
    }

    private VBox getScoreboard() {
        this.scoreBoard.setPadding(new Insets(0, 0, 0, 5));
        this.scoreBoard.setMaxWidth(300);
        this.scoreBoard.setMinWidth(300);
        this.scoreBoard.setPrefWidth(300);
//TODO: name too long, pushes chat away and ponts arent visible
        for (User user : this.userList) {
            HBox hbox = playerScoreMaker(user);
            hbox.setPrefWidth(this.scoreBoard.getWidth());
            this.scoreBoard.getChildren().add(hbox);
        }

        return this.scoreBoard;
    }

    private HBox playerScoreMaker(User user) {
        HBox hBox = new HBox();
        ImageView isDrawingImage = new ImageView();
        if (user.isDrawing()) {
            File file = new File("resources/pictures/brush.png");
            isDrawingImage.setImage(new Image(file.toURI().toString()));
        }
        isDrawingImage.setFitHeight(40);
        isDrawingImage.setFitWidth(40);

        HBox playerScore = LobbyWindow.playerMaker(user);
        playerScore.setSpacing(10);
        Region empty = new Region();

        HBox.setHgrow(empty, Priority.ALWAYS);
        Label scoreLabel = new Label(user.getScore() + " ");
        scoreLabel.setMinWidth(50);
        scoreLabel.setMaxWidth(60);
        scoreLabel.setAlignment(Pos.BASELINE_RIGHT);
        hBox.getChildren().addAll(isDrawingImage, playerScore, empty, scoreLabel);
        return hBox;
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
        if (!this.isDrawing) {
            return;
        }

        if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
            graphics.setColor(brushColor);
        else graphics.setColor(Color.WHITE);

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
                this.graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
            } else {
                int brushSize = drawUpdate.getBrushSize();
                this.graphics.setColor(drawUpdate.getColor());

                Point2D point = drawUpdate.getPosition();

                this.graphics.fillOval((int) point.getX() - brushSize, (int) point.getY() - brushSize, brushSize * 2, brushSize * 2);
            }
        });
    }

    private void onChatUpdate(ChatUpdate chatUpdate) {
        addNewMessage(chatUpdate);
    }

    private void onRoundUpdate(RoundUpdate roundUpdate) {
        Platform.runLater(() -> {
            if (roundUpdate.getRoundNum() >= 1 && roundUpdate.getRoundNum() <= roundUpdate.getMaxRounds()) {
                this.currentRoundLabel.setText(String.format("Round %s of %s rounds", roundUpdate.getRoundNum(), roundUpdate.getMaxRounds()));
                return;
            }

            endGame();
        });
    }

    private void onTimerUpdate(TimerUpdate timerUpdate) {
        Platform.runLater(() -> this.timeLeftLabel.setText(timerUpdate.getTimeLeft() + " seconds"));
    }

    private void onUserUpdate(UserUpdate userUpdate) {
        int matchingIndex = userList.indexOf(userUpdate.getUser());

        Platform.runLater(() -> {
            // If the user has left, try to remove it from the list,
            // if this is successful then also remove it from the scoreboard
            if (userUpdate.hasLeft() && this.userList.remove(userUpdate.getUser())) {
                scoreBoard.getChildren().remove(matchingIndex);
            }

            // Otherwise if the user was already added to our list we can find the index and update that user
            else if (this.userList.contains(userUpdate.getUser())) {
                this.scoreBoard.getChildren().set(matchingIndex, playerScoreMaker(userUpdate.getUser()));
                this.userList.set(matchingIndex, userUpdate.getUser());
            }

            // Otherwise the user has just joined and we should add it to our list
            else {
                this.scoreBoard.getChildren().add(playerScoreMaker(userUpdate.getUser()));
                this.userList.add(userUpdate.getUser());
            }
        });
    }

    private void onTurnUpdate(TurnUpdate turnUpdate) {
        graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());

        if (turnUpdate.getWord() == null) endGame();

        this.isDrawing = turnUpdate.getDrawer().equals(Client.getInstance().getUser());
        Platform.runLater(() -> {
            this.drawingButtonsBox.setDisable(!this.isDrawing);

            if (this.isDrawing) {
                this.roleLabel.setText("You are: Drawing");
                this.currentWordLabel.setText(turnUpdate.getWord());
            } else {
                this.roleLabel.setText("You are: Guessing");
                StringBuilder guessWord = new StringBuilder();

                for (int i = 0; i < turnUpdate.getWord().length(); i++) {
                    guessWord.append("_ ");
                }

                this.currentWordLabel.setText(guessWord.toString());
            }
        });
    }

    private VBox getDrawingArea() {
        VBox drawSideSetup = new VBox();

        drawingButtonsBox = new HBox();
        drawingButtonsBox.getChildren().addAll(getColourButtons(), getSizeButtons());
        drawingButtonsBox.setSpacing(20);
        drawingButtonsBox.setPadding(new Insets(5, 0, 0, 0));

        drawSideSetup.getChildren().addAll(canvas, drawingButtonsBox);
        return drawSideSetup;
    }

    private GridPane getColourButtons() {
        GridPane gridpane = new GridPane();

        Button greenButton = new Button();
        greenButton.setStyle("-fx-background-color: #00FF00");

        Button redButton = new Button();
        redButton.setStyle("-fx-background-color: #FF0000");

        Button blackButton = new Button();
        blackButton.setStyle("-fx-background-color: #000000");

        Button blueButton = new Button();
        blueButton.setStyle("-fx-background-color: #0000FF");

        Button yellowButton = new Button();
        yellowButton.setStyle("-fx-background-color: #FFFF00");

        Button orangeButton = new Button();
        orangeButton.setStyle("-fx-background-color: #ff8c00");

        Button purpleButton = new Button();
        purpleButton.setStyle("-fx-background-color: #c74fff");

        Button pinkButton = new Button();
        pinkButton.setStyle("-fx-background-color: #ff659f");

        greenButton.setOnAction(event -> brushColor = Color.green);
        redButton.setOnAction(event -> brushColor = Color.red);
        blackButton.setOnAction(event -> brushColor = Color.black);
        blueButton.setOnAction(event -> brushColor = Color.blue);
        yellowButton.setOnAction(event -> brushColor = Color.yellow);
        orangeButton.setOnAction(event -> brushColor = Color.orange);
        purpleButton.setOnAction(event -> brushColor = Color.magenta);
        pinkButton.setOnAction(event -> brushColor = Color.pink);

        greenButton.setPrefSize(40, 40);
        redButton.setPrefSize(40, 40);
        blackButton.setPrefSize(40, 40);
        blueButton.setPrefSize(40, 40);
        yellowButton.setPrefSize(40, 40);
        orangeButton.setPrefSize(40, 40);
        purpleButton.setPrefSize(40, 40);
        pinkButton.setPrefSize(40, 40);

        gridpane.add(blackButton, 1, 1);
        gridpane.add(blueButton, 1, 2);
        gridpane.add(greenButton, 2, 1);
        gridpane.add(yellowButton, 4, 2);
        gridpane.add(pinkButton, 2, 2);
        gridpane.add(purpleButton, 3, 2);
        gridpane.add(orangeButton, 4, 1);
        gridpane.add(redButton, 3, 1);

        gridpane.setVgap(5);
        gridpane.setHgap(10);


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
        hBox.setPadding(new Insets(1, 0, 0, 0));
        Region emptySpace = new Region();
        emptySpace.setPrefWidth(100);

        hBox.getChildren().addAll(smallButton, mediumButton, largeButton, emptySpace, getClearCanvasButton());

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
        infoVBox.setPadding(new Insets(0, 0, 0, 5));
        infoVBox.setPrefWidth(300);
        infoVBox.setMaxWidth(300);
        infoVBox.setMinWidth(300);
        infoVBox.setSpacing(10);

        Label chatLogLabel = new Label("Chat");

        infoVBox.getChildren().addAll(this.roleLabel, chatLogLabel, getChat(), getInput());

        return infoVBox;
    }

    private ScrollPane getChat() {
        ScrollPane chatScrollPane = new ScrollPane();
        chatScrollPane.setFitToHeight(true);
        chatScrollPane.setPrefHeight(200);
        chatScrollPane.setPrefHeight(500);

        chatMessagesBox = new GridPane();
        chatMessagesBox.setVgap(10);
        chatScrollPane.vvalueProperty().bind(chatMessagesBox.heightProperty());
        for (ChatUpdate chatUpdate : chatArrayList) {
            addNewMessage(chatUpdate);
        }

        chatScrollPane.setContent(chatMessagesBox);

        return chatScrollPane;
    }

    private HBox getInput() {
        HBox inputBox = new HBox();
        TextField messageInput = new TextField();
        sendButton = new Button("Send");

        sendButton.setOnAction(event -> {
            if (!messageInput.getText().trim().isEmpty()) {
                ChatUpdate newChatUpdate = new ChatUpdate(Client.getInstance().getUser(), messageInput.getText().trim());

                // Make the client send the message to the server
                Client.getInstance().sendObject(newChatUpdate);
                messageInput.clear();
            }
        });

        inputBox.getChildren().addAll(messageInput, sendButton);

        return inputBox;
    }

    private void endGame() {
        //Show endscores inpopUP?
        // move back to lobby or home?
        new EndScoreWindow(userList);
        new LobbyWindow(this.primaryStage, this.userList);
    }
}