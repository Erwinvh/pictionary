package comms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private ServerSettings serverSettings;

    private ServerSocket serverSocket;
    private boolean running;

//    private List<Client> connectedClients;
//    private List<User> connectedUsers;

    private HashMap<Socket, User> connectedSockets;

    private final String JOIN_MESSAGE = " has joined the room!";
    private final String LEAVE_MESSAGE = " has left the room!";

    public Server(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        this.serverSocket = null;
        this.running = false;

        this.connectedSockets = new HashMap<>();

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
    }

    private void stop() throws IOException {
        serverSocket.close();
    }

    private void handleClientConnectionObject(Socket socket) {
        System.out.println("Client connected, handling connection.");

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())
        ) {
            boolean connected = true;

            User user = (User) objectInputStream.readObject();
            connectedSockets.put(socket, user);

            objectOutputStream.writeObject(new Message(user.getName(), JOIN_MESSAGE));

            while (connected) {
                Object objectIn = objectInputStream.readObject();

                if (objectIn instanceof Message){
                    objectOutputStream.writeObject(objectIn);


                } // TODO: 23/05/2020 else if (objectIn instanceof DrawingCanvas)

                //Message message = (Message) in.readObject();
                //out.writeObject(message);
            }

            objectOutputStream.writeObject(new Message(user.getName(), LEAVE_MESSAGE));
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            connectedSockets.remove(socket);
        }
    }

    public void nextRound(){

    }

    private void nextDrawer(){

    }

    public boolean getRunning() {
        return running;
    }
}