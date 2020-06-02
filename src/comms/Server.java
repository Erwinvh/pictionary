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

    private HashMap<Socket, User> connectedSockets;
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

        this.connectedSockets = new LinkedHashMap<>();
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

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            boolean connected = true;

            // The client will send itself when connected
            User user = (User) objectInputStream.readObject();
            connectedSockets.put(socket, user);
            sendToAllClients(new UserUpdate(user, false));
            objectOutputStreams.add(objectOutputStream);

            connectedSockets.values().forEach(userInstance -> {
                try {
                    objectOutputStream.writeObject(new UserUpdate(userInstance, false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            sendToAllClients(new ChatUpdate(user.getName(), JOIN_MESSAGE));

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

            this.connectedSockets.remove(socket);
            this.objectOutputStreams.remove(objectOutputStream);
            socket.close();

            sendToAllClients(new ChatUpdate(user.getName(), LEAVE_MESSAGE));
            sendToAllClients(new UserUpdate(user, true));

            // Stop the entire server when all clients have left
            if (this.connectedSockets.size() == 0 || user.isHost()) {
                stop();
            }

        } catch (IOException | ClassNotFoundException e) {
            connectedSockets.remove(socket);
        }
    }

    private void checkWord(ChatUpdate chatUpdate) {
        String message = chatUpdate.getMessage();
        if (message.trim().equalsIgnoreCase(currentWord)) {
            //GUESSED CORRECTLY!
            return;
        }

        int matchedCharacters = 0;
        for (int i = 0; i < message.length(); i++) {
            if (i > this.currentWord.length())
                return;

            if (message.charAt(i) == this.currentWord.charAt(i)) {
                matchedCharacters++;
            }
        }

        if (matchedCharacters >= this.currentWord.length() - 1) {
            // ALMOST CORRECT!
//             sendToUser(chatUpdate.getUser());
        }
    }

    private void sendToAllClients(Object obj) {
        System.out.println("Sending \"" + obj.toString() + "\" to " + connectedSockets.size() + " clients...");

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
        nextDrawer(true);

        if (!isFirst)
            currentRoundIndex++;

        if (serverSettings.getRounds() == currentRoundIndex) {
            // TODO: 31/05/2020 End game
            return;
        }

        sendToAllClients(new RoundUpdate(currentRoundIndex, this.serverSettings.getRounds()));
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
        }).start();
    }

    private void nextDrawer(boolean isFirst) {
        List<User> users = new ArrayList<>(connectedSockets.values());
        User currentDrawer = users.get(currentDrawerIndex);
        pickNextWord();

        if (isFirst) {
            currentDrawer.setDrawing(true);
            sendToAllClients(new UserUpdate(currentDrawer, false));

            return;
        }

        // Disable drawing for current drawer
        currentDrawer.setDrawing(false);
        sendToAllClients(new UserUpdate(currentDrawer, false));

        // Check if the current drawer is the last drawer of this round
        if (currentDrawerIndex == users.size()) {
            nextRound(false);
            return;
        }

        // Increase index of current drawer and then set the corresponding user to allow interaction with the canvas
        currentDrawerIndex++;
        users.get(currentDrawerIndex).setDrawing(true);
        sendToAllClients(new TurnUpdate(users.get(currentDrawerIndex), currentWord));
        sendToAllClients(new UserUpdate(users.get(currentDrawerIndex), false));
    }

    private void pickNextWord(){
        currentWord = englishWordList.poll();
    }

    public boolean getRunning() {
        return running;
    }

    public void sendToSpecificClient(Object object, User user){
        for (Map.Entry entry: connectedSockets.entrySet()) {
            if (user.equals(entry.getValue())){
                List keys = new ArrayList(connectedSockets.keySet());
                for (int i = 0; i < keys.size(); i++) {
                    if (keys.get(i).equals(entry.getKey())){
                        try {
                            ObjectOutputStream oos = objectOutputStreams.get(i);
                            oos.writeObject(object);
                        }catch (Exception e){
                            System.out.println("something went wrong while sending to specific person");
                            e.printStackTrace();
                        }
                        return;

                    }
                }

            }
        }

    }

}