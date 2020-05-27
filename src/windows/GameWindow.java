package windows;

import comms.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
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
    private WritableImage imageToDraw;
    private List<Message> chatArrayList = new ArrayList<>();
    private GridPane chatMessagesBox;
    private Canvas canvas = new Canvas();
    private int radius = 30;
    private FXGraphics2D graphics;
    private Color brushColor;

    private List<Point2D> positions;

    private Canvas canvas2;

    public GameWindow(Stage primaryStage) {
        primaryStage.setTitle("Pictionary - Game");

        Client.getInstance().setDrawUpdateListener(this);
        Client.getInstance().setChatUpdateListener(this);

        HBox base = new HBox();

        canvas2 = new Canvas();
        canvas2.setWidth(600);
        canvas2.setHeight(600);

        this.gameWindowScene = new Scene(base);

        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        positions = new ArrayList<>();

        this.chatArrayList.add(new Message("tester1", "test this"));
        this.chatArrayList.add(new Message("tester 2", "test that"));
        this.chatArrayList.add(new Message("tester", "test this"));

        base.getChildren().addAll(fullDrawSetup(), canvas2, getInfoVBox());
    }

    private VBox fullDrawSetup() {
        VBox drawSideSetup = new VBox();

        HBox ButtonsBox = new HBox();
        ButtonsBox.getChildren().addAll(setupColourButtons(), sizeButtons());

        setupCanvas();

        drawSideSetup.getChildren().addAll(canvas, ButtonsBox);
        return drawSideSetup;
    }

    private void setupCanvas() {
        canvas.setWidth(600);
        canvas.setHeight(600);

        canvas.setOnMouseClicked(this::onMouseClicked);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnMouseDragReleased(this::onMouseDragReleased);

        draw(new FXGraphics2D(canvas.getGraphicsContext2D()));
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        this.positions.clear();
        this.positions.add(new Point2D.Double(mouseEvent.getX(), mouseEvent.getY()));

        DrawUpdate drawUpdate = new DrawUpdate(radius, graphics.getColor(), this.positions);
        Client.getInstance().sendObject(drawUpdate);
    }

    private void onMouseDragReleased(MouseEvent mouseEvent) {
        DrawUpdate drawUpdate = new DrawUpdate(radius, graphics.getColor(), this.positions);
        Client.getInstance().sendObject(drawUpdate);
        positions.clear();
    }

    private void onMouseDragged(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            graphics.fillOval((int) mouseEvent.getSceneX() - radius, (int) mouseEvent.getSceneY() - radius, radius * 2, radius * 2);
            positions.add(new Point2D.Double(mouseEvent.getSceneX(), mouseEvent.getSceneY()));

        } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
//            final WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
//            imageToDraw = canvas.snapshot(new SnapshotParameters(), writableImage);
//            canvas2.getGraphicsContext2D().drawImage(imageToDraw, 0, 0);
//
//            BufferedImage bi = new BufferedImage((int) canvas.getWidth(), (int) canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2 = bi.createGraphics();

            graphics.setColor(Color.white);
            graphics.fillOval((int) mouseEvent.getSceneX() - radius, (int) mouseEvent.getSceneY() - radius, radius * 2, radius * 2);

            positions.add(new Point2D.Double(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
        }
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

        greenButton.setOnAction(event -> {
            brushColor = Color.green;

        });
        redButton.setOnAction(event -> {
            brushColor = Color.red;
        });
        blackButton.setOnAction(event -> {
            graphics.setColor(Color.black);
        });
        blueButton.setOnAction(event -> {
            graphics.setColor(Color.blue);
        });
        yellowButton.setOnAction(event -> {
            graphics.setColor(Color.yellow);
        });
        orangeButton.setOnAction(event -> {
            graphics.setColor(Color.orange);
        });
        purpleButton.setOnAction(event -> {
            graphics.setColor(Color.magenta);
        });
        pinkButton.setOnAction(event -> {
            graphics.setColor(Color.pink);
        });

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

    public HBox sizeButtons() {
        HBox hBox = new HBox();

        Button smallButton = new Button("small");
        smallButton.setOnAction(event -> {
            radius = 10;
        });
        Button mediumButton = new Button("mid");
        mediumButton.setOnAction(event -> {
            radius = 20;
        });
        Button largeButton = new Button("large");
        largeButton.setOnAction(event -> {
            radius = 30;
        });
        hBox.getChildren().addAll(smallButton, mediumButton, largeButton);

        return hBox;
    }


    public void draw(FXGraphics2D graphics) {
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
                addNewMessage(newMessage);
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

//        if (newMessage.getUsername().equals(Client.getInstance().getUser().getName())) {
//            messageColumn = 2;
//        }

        chatMessagesBox.add(messageBox, messageColumn, messageRow);
    }

    public Scene getGameWindowScene() {
        return this.gameWindowScene;
    }

    @Override
    public void onDrawUpdate(DrawUpdate drawUpdate) {
        // TODO: 27/05/2020 Update canvas using the DrawUpdate
        System.out.println("drawupdate");
//        if (Client.getInstance().getUser().isDrawing()) {
//            return;
//        }
        int brushsize = drawUpdate.getBrushSize();
        graphics.setColor(drawUpdate.getColor());
        for (Point2D point : drawUpdate.getPositions()) {
            graphics.fillOval((int) point.getX() - brushsize, (int) point.getY() - brushsize, brushsize * 2, brushsize * 2);
        }
    }

    @Override
    public void onChatUpdate(Message message) {
        addNewMessage(message);
    }
}