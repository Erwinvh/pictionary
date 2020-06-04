package comms;

import comms.GameUpdates.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    // Game
    private static final String wordFileName = "/words.json";
    private final String JOIN_MESSAGE = "has joined the room!";
    private final String LEAVE_MESSAGE = "has left the room!";
    // Network
    private ServerSettings serverSettings;
    private ServerSocket serverSocket;
    private boolean running;
    private HashMap<User, Socket> connectedUsers;
    private List<ObjectOutputStream> objectOutputStreams;
    private Queue<String> englishWordList = new LinkedList<>();

    private int currentDrawerIndex = 0;
    private int currentRoundIndex = 0;
    private String currentWord;
    private ArrayList<User> correctlyGuesses;
    private int recordTime = 0;
    private int roundTime;

    public Server(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        this.serverSocket = null;
        this.running = false;

        this.connectedUsers = new LinkedHashMap<>();
        this.objectOutputStreams = new ArrayList<>();
        this.correctlyGuesses = new ArrayList<>();
        setupWordList();

        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException {
        if (this.serverSocket != null) {
            System.out.println("Server already created socket, please stop first!");
            return;
        }

        this.serverSocket = new ServerSocket(this.serverSettings.getPort());
        this.running = true;

        while (this.running) {
            System.out.println("Waiting for client to connect...");
            final Socket client = this.serverSocket.accept();

            new Thread(() -> handleClientConnectionObject(client)).start();
        }

        System.out.println("Server stopped");
    }

    private void stop() throws IOException {
        System.out.println("Stopping server...");
        this.running = false;
        serverSocket.close();
    }

    private void handleClientConnectionObject(Socket socket) {
        System.out.println("A new client has connected (" + socket.toString() + "), handling connection.");
        User user = null;

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            boolean connected = true;

            // The client will send itself when connected
            user = (User) objectInputStream.readObject();
            connectedUsers.put(user, socket);
            sendToAllClients(new UserUpdate(user, false));
            objectOutputStreams.add(objectOutputStream);

            connectedUsers.keySet().forEach(userInstance -> {
                try {
                    objectOutputStream.writeObject(new UserUpdate(userInstance, false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            sendToAllClients(new ChatUpdate(user, JOIN_MESSAGE));

            while (connected) {
                Object objectIn = objectInputStream.readObject();

                if (objectIn instanceof Boolean) {
                    connected = (boolean) objectIn;
                } else if (objectIn instanceof GameUpdate) {
                    if (((GameUpdate) objectIn).getGameUpdateType().equals(GameUpdate.GameUpdateType.CHAT)) {
                        if (checkWord((ChatUpdate) objectIn)) continue;
                    } else if (((GameUpdate) objectIn).getGameUpdateType().equals(GameUpdate.GameUpdateType.SETTINGS)) {
                        adjustServerSettings((SettingsUpdate) objectIn);
                    }

                    // Notify all connected clients a new GameUpdate has been received
                    sendToAllClients(objectIn);

                } else if (objectIn instanceof User) {
                    if (((User) objectIn).isHost()) {
                        startGame();
                    }
                }
            }

            this.connectedUsers.remove(user);
            this.objectOutputStreams.remove(objectOutputStream);
            socket.close();

            sendToAllClients(new ChatUpdate(user, LEAVE_MESSAGE));
            sendToAllClients(new UserUpdate(user, true));

            // Stop the entire server when all clients have left
            if (this.connectedUsers.size() == 0 || user.isHost()) {
                stop();
            }
        } catch (IOException | ClassNotFoundException e) {
            connectedUsers.remove(user);
        }
    }

    private void adjustServerSettings(SettingsUpdate settingsUpdate) {
        ServerSettings adjustedSettings = settingsUpdate.getServerSettings();
        this.serverSettings.setTimeInSeconds(adjustedSettings.getTimeInSeconds());
        this.serverSettings.setRounds(adjustedSettings.getRounds());
        this.serverSettings.setLanguage(adjustedSettings.getLanguage());
    }

    private boolean checkWord(ChatUpdate chatUpdate) {
        String message = chatUpdate.getMessage().trim().toLowerCase();
        String currentWord = this.currentWord.trim().toLowerCase();

        if (message.equalsIgnoreCase(currentWord) && !chatUpdate.getUser().isDrawing()) {
            sendToAllClients(new ChatUpdate(null, chatUpdate.getUser().getName() + " has guessed the word!", true));

            if (this.correctlyGuesses.isEmpty()) {
                chatUpdate.getUser().addScore(300);
                this.correctlyGuesses.add(chatUpdate.getUser());
                this.recordTime = serverSettings.getTimeInSeconds() - this.roundTime;

            } else {
                int points = 300 - (25 / connectedUsers.size()) * correctlyGuesses.size();
                correctlyGuesses.add(chatUpdate.getUser());
                chatUpdate.getUser().addScore(points);
            }

            return true;
        }

        int matchedCharacters = 0;
        for (int i = 0; i < message.length(); i++) {
            if (i > this.currentWord.length())
                break;

            if (message.charAt(i) == currentWord.charAt(i)) {
                matchedCharacters++;
            }
        }

        if (matchedCharacters >= currentWord.length() - 2) {
            // ALMOST CORRECT!
            sendToSpecificClient(new ChatUpdate(null, "You are very close!", true), chatUpdate.getUser());
        }

        return false;
    }

    private void sendToAllClients(Object obj) {
        if (!(obj instanceof TimerUpdate))
            System.out.println("Sending \"" + obj.toString() + "\" to " + connectedUsers.size() + " clients...");

        for (ObjectOutputStream objectOutputStream : objectOutputStreams) {
            try {
                objectOutputStream.writeObject(obj);
                objectOutputStream.reset();
            } catch (IOException e) {
                System.out.println("Something went wrong whilst trying to send " + obj.toString() + " to " + objectOutputStream.toString());
                e.printStackTrace();
            }
        }
    }

    private void setupWordList() {
        try {
            File file = new File(getClass().getResource(wordFileName).getFile());
            if (!file.exists()) {
                throw new FileNotFoundException("The " + wordFileName + "was not found");
            } else {
                try (Reader reader = new FileReader(file)) {
                    JsonReader jsonReader = Json.createReader(reader);
                    JsonArray wordsJsonArray = jsonReader.readArray();

                    for (int i = 0; i < wordsJsonArray.size(); i++) {
                        JsonObject wordObject = wordsJsonArray.getJsonObject(i);
                        String word = wordObject.getString("english");
                        englishWordList.add(word);
                    }

                    jsonReader.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        nextRound(true);
    }

    private void nextRound(boolean isFirst) {
        currentDrawerIndex = 0;

        if (!isFirst)
            currentRoundIndex++;

        if (serverSettings.getRounds() == currentRoundIndex) {
            sendToAllClients(new RoundUpdate(this.serverSettings.getRounds()+1,this.serverSettings.getRounds()));
            currentRoundIndex=0;
            return;
        }

        sendToAllClients(new RoundUpdate(currentRoundIndex, this.serverSettings.getRounds()));

        if (isFirst) {
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    nextDrawer(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        } else nextDrawer(false);

        startTimer();
    }

    private void startTimer() {
        new Thread(() -> {
            this.roundTime = serverSettings.getTimeInSeconds();
            while (this.roundTime > 0) {
                try {
                    Thread.sleep(1000);
                    this.roundTime--;
                    sendToAllClients(new TimerUpdate(this.roundTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            nextRound(false);
        }).start();
    }

    private void nextDrawer(boolean isFirst) {
        List<User> users = new ArrayList<>(connectedUsers.keySet());
        User currentDrawer = users.get(this.currentDrawerIndex);

        pickNextWord(0);

        addPointsToDrawer(currentDrawer);

        if (isFirst) {
            currentDrawer.setDrawing(true);
            sendToAllClients(new TurnUpdate(currentDrawer, currentWord));
            return;
        }

        // Check if the current drawer is the last drawer of this round
        if (currentDrawerIndex == users.size()) {
            nextRound(false);
            return;
        }

        currentDrawer.setDrawing(false);

        // Increase index of current drawer and then set the corresponding user to allow interaction with the canvas
        currentDrawerIndex++;

        applyAllPoints();

        users.get(currentDrawerIndex).setDrawing(true);
        sendToAllClients(new TurnUpdate(users.get(currentDrawerIndex), currentWord));
    }

    private void applyAllPoints() {
        for (User user : connectedUsers.keySet()) {
            sendToAllClients(new UserUpdate(user, false));
        }
    }

    private void addPointsToDrawer(User currentDrawer) {
        if (currentRoundIndex != 0 && currentDrawerIndex != 0 && correctlyGuesses.size() > 0) {
            int pointsToAdd = (correctlyGuesses.size() / (connectedUsers.size() - 1) / recordTime) * 500;
            currentDrawer.addScore(pointsToAdd);
            correctlyGuesses.clear();
        }
    }

    private void pickNextWord(int i) {
        if (i > 100) return;

        this.currentWord = this.englishWordList.poll();
        if (this.currentWord == null) {
            // REACHED END OF THE LIST
            setupWordList();
            pickNextWord(++i);
        }
    }

    public boolean getRunning() {
        return running;
    }

    private void sendToSpecificClient(Object object, User user) {
        List<User> users = new ArrayList<>(connectedUsers.keySet());
        int index = users.indexOf(user);
        try {
            objectOutputStreams.get(index).writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSettings getServerSettings() {
        return serverSettings;
    }
}