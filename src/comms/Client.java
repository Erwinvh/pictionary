package comms;

import comms.GameUpdates.GameUpdate;
import comms.GameUpdates.GameUpdateListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private Socket clientSocket;
    private boolean connected;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private User user;

    private GameUpdateListener gameUpdateListener;

    private Thread incomingDataThread;

    // static inner class - inner classes are not loaded until they are referenced.
    private static class ClientHolder {
        private static Client client = new Client();
    }

    // global access point
    public static Client getInstance() {
        return ClientHolder.client;
    }

    private Client() {
        this.clientSocket = null;
        this.connected = false;

        this.objectInputStream = null;
        this.objectOutputStream = null;
    }

    public boolean connectToServer(String serverAddress, int serverPort) {
        if (this.connected) {
            System.out.println("Client already connected with the server.");
            return true;
        }

        if (this.user == null) {
            throw new NullPointerException("User has not yet been defined! Remember to call setUser() before trying to connect to a server!");
        }

        try {
            this.clientSocket = new Socket(serverAddress, serverPort);
            this.connected = true;

            this.objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());

            this.objectOutputStream.writeObject(this.getUser());

            incomingDataThread = new Thread(this::handleIncomingData);
            incomingDataThread.start();

            System.out.println("Client " + user.getName() + " successfully connected!");

            return true;

        } catch (IOException e) {
            System.out.println("Could not connect to the server due to: " + e.toString());
        }

        return false;
    }

    private void handleIncomingData() {
        while (this.connected) {
            try {
                Object objectIn = this.objectInputStream.readObject();

                if (this.gameUpdateListener == null) {
//                    System.out.println("GameUpdateListener was null! Not a big problem, just notifying!");
                    continue;
//                    throw new NullPointerException("GameUpdateListener was null! Fix your shit");
                }

                if (objectIn instanceof GameUpdate) {
                    gameUpdateListener.onGameUpdate((GameUpdate) objectIn);
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Something went wrong whilst handling incoming data!");
                e.printStackTrace();
            }
        }
    }

    public void disconnectFromServer() {
        try {
            this.connected = false;
            sendObject(Boolean.FALSE);
            System.out.println(this.user.getName() + " is willingly disconnecting from server...");

            incomingDataThread.join();

            this.clientSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendObject(Object obj) {
//
//        if (!this.connected)
//            throw new IllegalStateException("Client is not connected and thus cannot send data.");

        try {
            objectOutputStream.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        if (this.user == null)
            throw new NullPointerException("User has not yet been defined! Remember to call setUser() before trying to connect to a server!");

        return user;
    }

    public void setGameUpdateListener(GameUpdateListener gameUpdateListener) {
        this.gameUpdateListener = gameUpdateListener;
    }
}