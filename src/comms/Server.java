package comms;

import comms.GameUpdates.ChatUpdate;
import comms.GameUpdates.GameUpdate;
import comms.GameUpdates.RoundUpdate;
import comms.GameUpdates.UserUpdate;

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
    private static final String wordFileName = "words.json";
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

        // TODO: 23/05/2020 Add client that is hosting the serverSocket to the list as well?

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
            objectOutputStreams.add(objectOutputStream);

            sendToAllClients(new ChatUpdate(user.getName(), JOIN_MESSAGE));

            while (connected) {
                Object objectIn = objectInputStream.readObject();

                if (objectIn instanceof Boolean) {
                    connected = (boolean) objectIn;
                }

                if (objectIn instanceof GameUpdate) {
                    // Notify all connected clients a new GameUpdate has been received
                    sendToAllClients(objectIn);
                }
            }

            connectedSockets.remove(socket);
            objectOutputStreams.remove(objectOutputStream);
            socket.close();

            sendToAllClients(new ChatUpdate(user.getName(), LEAVE_MESSAGE));

            if (connectedSockets.size() == 0) {
                stop();
            }

        } catch (IOException | ClassNotFoundException e) {
            connectedSockets.remove(socket);
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

            System.out.println(getClass().getResource(wordFileName).getFile());
            File file = new File(getClass().getResource(wordFileName).getFile());
//            File file = new File(getClass().getResource(wordFileName).getFile());
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

    private void nextRound() {
        currentDrawerIndex = 0;
        nextDrawer(true);

        currentRoundIndex++;
        if (serverSettings.getRounds() == currentRoundIndex) {
            // TODO: 31/05/2020 End game
            return;
        }

        sendToAllClients(new RoundUpdate(currentRoundIndex, this.serverSettings.getRounds()));
    }

    private void nextDrawer(boolean isFirst) {
        User[] users = (User[]) connectedSockets.values().toArray();

        if (isFirst) {
            users[currentDrawerIndex].setDrawing(true);
            sendToAllClients(new UserUpdate(users[currentDrawerIndex], false));
            return;
        }

        users[currentDrawerIndex].setDrawing(false);
        sendToAllClients(new UserUpdate(users[currentDrawerIndex], false));

        if (currentDrawerIndex == users.length) {
            nextRound();
            return;
        }

        // Increase index of current drawer and then set the corresponding user to allow interaction with the canvas
        currentDrawerIndex++;
        users[currentDrawerIndex].setDrawing(true);
        sendToAllClients(new UserUpdate(users[currentDrawerIndex], false));
    }

    public boolean getRunning() {
        return running;
    }
}