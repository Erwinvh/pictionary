package comms;

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

    private DrawUpdateListener drawUpdateListener;
    private ChatUpdateListener chatUpdateListener;

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

            new Thread(this::handleIncomingData).start();

            System.out.println("Client " + user.getName() + " successfully connected!");

            return true;

        } catch (IOException e) {
            System.out.println("Could not connect to the server due to: " + e.toString());
        }

        return false;
    }

    private void handleIncomingData() {
        while (true) {
            try {
                Object objectIn = this.objectInputStream.readObject();

                if (this.chatUpdateListener == null)
                    throw new NullPointerException("ChatUpdateListener was null! Fix your shit");

                if (this.drawUpdateListener == null)
                    throw new NullPointerException("DrawUpdateListener was null! Fix your shit");

                if (objectIn instanceof Message) {
                    chatUpdateListener.onChatUpdate((Message) objectIn);
                } else if (objectIn instanceof DrawUpdate) {
                    drawUpdateListener.onDrawUpdate((DrawUpdate) objectIn);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnectFromServer() {
        try {
            System.out.println(this.user.getName() + " is willingly disconnecting from server...");
            this.clientSocket.close();
            this.connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendObject(Object obj) {

        if (!this.connected)
            throw new IllegalStateException("Client is not connected and thus cannot send data.");

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

    public void setDrawUpdateListener(DrawUpdateListener drawUpdateListener) {
        this.drawUpdateListener = drawUpdateListener;
    }

    public void setChatUpdateListener(ChatUpdateListener chatUpdateListener) {
        this.chatUpdateListener = chatUpdateListener;
    }
}