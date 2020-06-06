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

    static final String JOIN_MESSAGE = "has joined the room!";
    static final String LEAVE_MESSAGE = "has left the room!";
    // Game
    private static final String wordFileName = "/words.json";
    // Network
    private ServerSettings serverSettings;
    private ServerSocket serverSocket;
    private boolean running;
    private Queue<String> wordList = new LinkedList<>();

    private int currentDrawerIndex = 0;
    private int currentRoundIndex = 1;
    private String currentWord;
    private ArrayList<User> correctlyGuesses;
    private int recordTime = 0;

    private Clients clients = new Clients();

    private TimerThread timerThread = new TimerThread();

    public Server(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        this.serverSocket = null;
        this.running = false;
        this.correctlyGuesses = new ArrayList<>();

        setupWordList();

        try {
            start();
        } catch (IOException e) {
            stop();
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

    private void stop() {
        try {
            System.out.println("Stopping server...");
            this.running = false;
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Something went wrong whilst trying to stop the server");
        }
    }

    private void handleClientConnectionObject(Socket socket) {
        System.out.println("A new client has connected (" + socket.toString() + "), handling connection.");
        User user = null;

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            boolean connected = true;

            // The client will send itself when connected
            user = (User) objectInputStream.readObject();

            Thread.currentThread().setName(user.getName());

            this.clients.addClient(user, objectOutputStream);

            while (connected) {
                Object objectIn = objectInputStream.readObject();

                if (objectIn instanceof Boolean) {
                    connected = (boolean) objectIn;
                } else if (objectIn instanceof GameUpdate) {
                    if (((GameUpdate) objectIn).getGameUpdateType().equals(GameUpdate.GameUpdateType.CHAT)) {
                        if (this.checkWord((ChatUpdate) objectIn)) continue;
                    } else if (((GameUpdate) objectIn).getGameUpdateType().equals(GameUpdate.GameUpdateType.SETTINGS)) {
                        this.adjustServerSettings((SettingsUpdate) objectIn);
                    } else if (((GameUpdate) objectIn).getGameUpdateType().equals(GameUpdate.GameUpdateType.STATE)) {
                        this.clients.adjustState((StateUpdate) objectIn);
                        continue;
                    }

                    // Notify all connected clients a new GameUpdate has been received
                    this.clients.sendToAllClients(objectIn);

                } else if (objectIn instanceof User) {
                    if (((User) objectIn).isHost()) {
                        this.clients.getStateMap().replace(user, StateUpdate.stateType.GAME);
                        startGame();
                    }
                }
            }

            this.clients.removeClient(user);
            socket.close();

            // Stop the entire server when the host has left
            if (user.isHost()) {
                stop();
            }

        } catch (IOException | ClassNotFoundException e) {
            this.clients.removeClient(user);
        }
    }

    private void adjustServerSettings(SettingsUpdate settingsUpdate) {
        ServerSettings adjustedSettings = settingsUpdate.getServerSettings();
        this.serverSettings.setTimeInSeconds(adjustedSettings.getTimeInSeconds());
        this.serverSettings.setRounds(adjustedSettings.getRounds());
        this.serverSettings.setLanguage(adjustedSettings.getLanguage());

        this.setupWordList();
    }

    private boolean checkWord(ChatUpdate chatUpdate) {
        String message = chatUpdate.getMessage().trim().toLowerCase();
        String currentWord = this.currentWord.trim().toLowerCase();

        if (chatUpdate.getUser().isDrawing() || correctlyGuesses.contains(chatUpdate.getUser())) return false;

        if (message.equalsIgnoreCase(currentWord)) {
            this.clients.sendToAllClients(new ChatUpdate(null, chatUpdate.getUser().getName() + " has guessed the word!", true));

            if (this.correctlyGuesses.isEmpty()) {
                chatUpdate.getUser().addScore(300);
                this.correctlyGuesses.add(chatUpdate.getUser());
                this.recordTime = serverSettings.getTimeInSeconds() - this.timerThread.roundTime;

            } else {
                int points = 300 - (25 / this.clients.getConnectedUsers().size()) * correctlyGuesses.size();
                chatUpdate.getUser().addScore(points);
                correctlyGuesses.add(chatUpdate.getUser());
            }

            return true;
        }

        int matchedCharacters = 0;
        for (int i = 0; i < message.length(); i++) {
            if (i >= this.currentWord.length())
                break;

            if (message.charAt(i) == currentWord.charAt(i)) {
                matchedCharacters++;
            }
        }

        if (matchedCharacters >= currentWord.length() - 2) {
            // ALMOST CORRECT!
            this.clients.sendToSpecificClient(new ChatUpdate(null, "You are very close!", true), chatUpdate.getUser());
        }

        return false;
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

                    List<String> wordList = new ArrayList<>();
                    for (int i = 0; i < wordsJsonArray.size(); i++) {
                        JsonObject wordObject = wordsJsonArray.getJsonObject(i);
                        String word = wordObject.getString(this.serverSettings.getLanguage().toLowerCase());
                        wordList.add(word);
                    }

                    Collections.shuffle(wordList);
                    this.wordList = new LinkedList<>(wordList);

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
            endGame();
            return;
        }

        this.clients.sendToAllClients(new RoundUpdate(currentRoundIndex, this.serverSettings.getRounds()));

        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!attendanceGame());

        nextTurn(true);
    }

    private void endGame() {
        this.clients.sendToAllClients(new RoundUpdate(-1, this.serverSettings.getRounds()));
        this.currentRoundIndex = 1;
    }

    private boolean attendanceGame() {
        for (StateUpdate.stateType stateType : this.clients.getStateMap().values()) {
            if (stateType == StateUpdate.stateType.LOBBY || stateType == null) return false;
        }

        return true;
    }

    private void startTimer() {
        this.timerThread = new TimerThread();
        this.timerThread.start();
    }

    private void nextTurn(boolean isFirst) {
        List<User> users = new ArrayList<>(this.clients.getConnectedUsers().keySet());
        User currentDrawer = users.get(this.currentDrawerIndex);

        if (!isFirst) {
            addPointsToUser(currentDrawer);
            applyAllPoints();

            currentDrawer.setDrawing(false);
            currentDrawerIndex++;

            if (this.currentDrawerIndex > users.size() - 1) {
                nextRound(false);
                return;
            }

            currentDrawer = users.get(currentDrawerIndex);
        }

        currentDrawer.setDrawing(true);
        pickNextWord(0);
        startTimer();
        this.clients.sendToAllClients(new TurnUpdate(users.get(currentDrawerIndex), currentWord));
    }

    private void applyAllPoints() {
        for (User user : this.correctlyGuesses) {
            this.clients.sendToAllClients(new UserUpdate(user, false));
        }

        correctlyGuesses.clear();
    }

    private void addPointsToUser(User user) {
        if (!correctlyGuesses.isEmpty()) {
            int pointsToAdd = (int) ((correctlyGuesses.size() / (this.clients.getConnectedUsers().size() - 1) / (double) recordTime) * 500);
            user.addScore(pointsToAdd);
            correctlyGuesses.add(user);
        }
    }

    private void pickNextWord(int i) {
        if (i > 100) return;

        this.currentWord = this.wordList.poll();
        if (this.currentWord == null) {
            // REACHED END OF THE LIST
            setupWordList();
            pickNextWord(++i);
        }
    }

    private class TimerThread extends Thread {

        int roundTime = 0;

        @Override
        public void run() {
            this.roundTime = serverSettings.getTimeInSeconds();
            clients.sendToAllClients(new TimerUpdate(this.roundTime));

            while (this.roundTime > 0) {
                try {
                    Thread.sleep(1000);
                    this.roundTime--;
                    clients.sendToAllClients(new TimerUpdate(this.roundTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            nextTurn(false);
        }
    }
}