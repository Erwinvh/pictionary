package comms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {

    private ServerSettings serverSettings;

    private ServerSocket serverSocket;
    private boolean running;

    private HashMap<Socket, User> connectedSockets;
    private List<ObjectOutputStream> objectOutputStreams;

    private final String JOIN_MESSAGE = " has joined the room!";
    private final String LEAVE_MESSAGE = " has left the room!";

    public Server(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        this.serverSocket = null;
        this.running = false;

        this.connectedSockets = new HashMap<>();
        this.objectOutputStreams = new ArrayList<>();

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

            sendToAllClients(new Message(user.getName(), JOIN_MESSAGE));

            while (connected) {
                Object objectIn = objectInputStream.readObject();

                if (objectIn instanceof Message || objectIn instanceof DrawUpdate) {
                    // Notify all connected clients a new message or DrawUpdate has been received
                    sendToAllClients(objectIn);
                }
            }

            connectedSockets.remove(socket);
            // TODO: 27/05/2020 Remove objectoutputstream from list
            socket.close();

            sendToAllClients(new Message(user.getName(), LEAVE_MESSAGE));

        } catch (IOException | ClassNotFoundException e) {
            connectedSockets.remove(socket);
        }
    }

    public void nextRound() {

    }

    private void nextDrawer() {

    }

    private void sendToAllClients(Object obj) {
        for (ObjectOutputStream objectOutputStream : objectOutputStreams) {
            try {
                objectOutputStream.writeObject(obj);
            } catch (IOException e) {
                System.out.println("Something went wrong whilst trying to send " + obj.toString() + " to " + objectOutputStream.toString());
                e.printStackTrace();
            }
        }
    }

    public boolean getRunning() {
        return running;
    }
}