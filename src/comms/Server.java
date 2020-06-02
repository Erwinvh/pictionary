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

    // Network
    private ServerSettings serverSettings;

    private ServerSocket serverSocket;
    private boolean running;

    private HashMap<User, Socket> connectedUsers;
    private List<ObjectOutputStream> objectOutputStreams;

    private final String JOIN_MESSAGE = "has joined the room!";
    private final String LEAVE_MESSAGE = "has left the room!";

    // Game
    private static final String wordFileName = "/words.json";
    private Queue<String> englishWordList = new LinkedList<>();

    private int currentDrawerIndex = 0;
    private int currentRoundIndex = 0;
    private String currentWord;

    public Server(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        this.serverSocket = null;
        this.running = false;

        this.connectedUsers = new LinkedHashMap<>();
        this.objectOutputStreams = new ArrayList<>();

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
                    // Notify all connected clients a new GameUpdate has been received
                    sendToAllClients(objectIn);

                    if (((GameUpdate) objectIn).getGameUpdateType().equals(GameUpdate.GameUpdateType.CHAT)) {
                        checkWord((ChatUpdate) objectIn);
                    }

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

    private void checkWord(ChatUpdate chatUpdate) {
        String message = chatUpdate.getMessage().trim().toLowerCase();
        String currentWord = this.currentWord.trim().toLowerCase();

        if (message.equalsIgnoreCase(currentWord)) {
            sendToAllClients(new ChatUpdate(null, chatUpdate.getUser() + " has guessed the word!", true));
            // TODO: 02/06/2020 Add points
            return;
        }

        int matchedCharacters = 0;
        for (int i = 0; i < message.length(); i++) {
            if (i > this.currentWord.length())
                return;

            if (message.charAt(i) == currentWord.charAt(i)) {
                matchedCharacters++;
            }
        }

        if (matchedCharacters >= currentWord.length() - 2) {
            // ALMOST CORRECT!
            sendToSpecificClient(new ChatUpdate(null, "You are very close!", true), chatUpdate.getUser());
        }
    }

    private void sendToAllClients(Object obj) {
        if (!(obj instanceof TimerUpdate))
        System.out.println("Sending \"" + obj.toString() + "\" to " + connectedUsers.size() + " clients...");

        for (ObjectOutputStream objectOutputStream : objectOutputStreams) {
            try {
                objectOutputStream.writeObject(obj);
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

        // startTimer();
        // Perhaps nextRound(isFirst = true)? or just sendToAllClients(RoundUpdate of first round)? :D
        // TODO: 01/06/2020 other setup for the beginning of the game

    }

    private void nextRound(boolean isFirst) {
        currentDrawerIndex = 0;

        if (!isFirst)
            currentRoundIndex++;

        if (serverSettings.getRounds() == currentRoundIndex) {
            // TODO: 31/05/2020 End game
            return;
        }

        sendToAllClients(new RoundUpdate(currentRoundIndex, this.serverSettings.getRounds()));
        nextDrawer(true);
        // TODO: 01/06/2020 maybe first wait to let the drawer select the word before starting the timer
        startTimer();
    }

    private void startTimer() {
        new Thread(() -> {
            int roundTime = serverSettings.getTimeInSeconds();
            while (roundTime >= 0) {
                try {
                    Thread.sleep(1000);
                    roundTime--;
                    sendToAllClients(new TimerUpdate(roundTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // TODO: 02/06/2020 Next round since the timer has ended!
        }).start();
    }

    private void nextDrawer(boolean isFirst) {
        List<User> users = new ArrayList<>(connectedUsers.keySet());
        User currentDrawer = users.get(currentDrawerIndex);
        pickNextWord();

        if (isFirst) {
            sendToAllClients(new TurnUpdate(currentDrawer, currentWord));
            return;
        }

        // Check if the current drawer is the last drawer of this round
        if (currentDrawerIndex == users.size()) {
            nextRound(false);
            return;
        }

        // Increase index of current drawer and then set the corresponding user to allow interaction with the canvas
        currentDrawerIndex++;
        sendToAllClients(new TurnUpdate(users.get(currentDrawerIndex), currentWord));
    }

    private void pickNextWord() {
        this.currentWord = this.englishWordList.poll();
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
}