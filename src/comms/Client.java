package comms;

import comms.GameUpdates.ChatUpdateListener;
import comms.GameUpdates.GameUpdate;
import comms.GameUpdates.GameUpdateListener;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket clientSocket;
    private boolean connected;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private User user;

    private GameUpdateListener gameUpdateListener;
    private ChatUpdateListener chatUpdateListener;

    private Thread incomingObjectThread;
    private Thread incomingDataThread;

    public void setChatUpdateListener(ChatUpdateListener chatUpdateListener) {
        this.chatUpdateListener = chatUpdateListener;
    }

    // Static inner class - inner classes are not loaded until they are referenced.
    private static class ClientHolder {
        private static Client client = new Client();
    }

    // Global access point
    public static Client getInstance() {
        return ClientHolder.client;
    }

    private Client() {
        this.clientSocket = null;
        this.connected = false;

        this.user = null;

        this.objectInputStream = null;
        this.objectOutputStream = null;
        this.dataInputStream = null;
    }

    public synchronized boolean connectToServer(String serverAddress, int serverPort) {
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

            this.dataOutputStream = new DataOutputStream(this.clientSocket.getOutputStream());

            this.objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.objectOutputStream.writeObject(this.getUser());

            this.objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());

            this.dataInputStream = new DataInputStream(this.clientSocket.getInputStream());

//            incomingObjectThread = new Thread(this::handleIncomingData);
//            incomingObjectThread.start();

            incomingDataThread = new Thread(this::handleIncomingObjects);
            incomingDataThread.start();

            System.out.println("Client " + user.getName() + " successfully connected!");

            return true;

        } catch (IOException e) {
            System.out.println("Could not connect to the server due to: " + e.toString());
        }

        return false;
    }

    private void handleIncomingChat() {
        int errorCounter = 0;
        while (this.connected){
            this.connected = clientSocket.isConnected();
            if (!this.connected) return;
            if (errorCounter >= 15) {
                System.out.println("Something went wrong whilst handling incoming chatMessage!");
                disconnectFromServer();
            }
            if (this.chatUpdateListener != null) {
                synchronized (this.dataInputStream) {
                    try {
                        String message = this.dataInputStream.readUTF();
                        if (!message.isEmpty()) {
                            chatUpdateListener.onChatUpdate(message);
                        }

                        errorCounter--;

                    } catch (IOException e) {
                        errorCounter++;
                        System.out.println("Something went wrong whilst receiving via dataStreams");
                    }
                }
            }
        }
    }

    private void handleIncomingObjects() {
        int errorCounter = 0;

        while (this.connected) {

            this.connected = clientSocket.isConnected();
            if (!this.connected) return;

            if (errorCounter >= 50) {
                errorCounter = 0;
                System.out.println("Something went wrong whilst handling incoming data!");
                disconnectFromServer();
            }

            synchronized (this.objectInputStream) {
                try {
                    Object objectIn = this.objectInputStream.readObject();

                    if (this.gameUpdateListener == null) {
                        System.out.println("GameUpdateListener was null!");
                        continue;
                    }

                    if (objectIn instanceof User) {
                        if (objectIn.equals(this.getUser())) {
                            System.out.println("Updated user");
                            this.user = (User) objectIn;
                        }
                    } else if (objectIn instanceof GameUpdate) {
                        gameUpdateListener.onGameUpdate((GameUpdate) objectIn);
                    }

                    errorCounter = 0;

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    errorCounter++;

                } catch (NullPointerException e) {
                    System.out.println("Received a null object!");
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void disconnectFromServer() {
        try {
            System.out.println(this.user.getName() + " is willingly disconnecting from server...");
            sendObject(Boolean.FALSE);
            this.connected = false;

            incomingObjectThread.join();
            incomingDataThread.join();

            this.clientSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendObject(Object obj) {
        if (!this.connected)
//            throw new IllegalStateException("Client is not connected and thus cannot send data.");
            System.out.println("Client is not connected and thus cannot send data.");

        try {
            this.objectOutputStream.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendData(String message){
        if (!this.connected)
            System.out.println("Client is not connected and thus cannot send data.");

        try {
            this.dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        if (this.user == null)
            throw new NullPointerException("User has not yet been defined! Remember to call setUser() before trying to connect to a server!");

        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGameUpdateListener(GameUpdateListener gameUpdateListener) {
        this.gameUpdateListener = gameUpdateListener;
    }

    public boolean isConnected() {
        return connected;
    }
}