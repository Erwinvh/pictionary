package comms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;
    private boolean connected;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private User user;

    // static inner class - inner classes are not loaded until they are referenced.
    private static class ClientHolder {
        private static Client client = new Client();
    }

    // global access point
    public static Client getInstance() {
        return ClientHolder.client;
    }

    private Client () {
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

            Scanner scanner = new Scanner(System.in);
            while(this.connected){
                System.out.print("Type your message here: ");
//
                String messageText = scanner.nextLine();
//                String messageText = objectInputStream.readUTF();
                Message message = new Message(user.getName(), messageText);
                this.objectOutputStream.writeObject(message);

                Object objectIn = this.objectInputStream.readObject();
                if (objectIn instanceof Message) {
                    Message incomingMessage = (Message) this.objectInputStream.readObject();
                    System.out.println(incomingMessage.toString());
                    // TODO: 23/05/2020 Show this incoming message in GUI
                } // TODO: 23/05/2020 else if (objectIn instanceof Drawing)
            }

//            new Thread(this::handleOutgoingData).start();

            //
//            while (this.connected) {
//                System.out.print("Type your message here: ");
//
//                //String messageText = scanner.nextLine();
//                String messageText = objectInputStream.readUTF();
//                Message message = new Message(user.getName(), messageText);
//                this.objectOutputStream.writeObject(message);
//
//                Object objectIn = this.objectInputStream.readObject();
//                if (objectIn instanceof Message) {
//                    Message incomingMessage = (Message) this.objectInputStream.readObject();
//                    System.out.println(incomingMessage.toString());
//                    // TODO: 23/05/2020 Show this incoming message in GUI
//                } // TODO: 23/05/2020 else if (objectIn instanceof Drawing)
//            }

            System.out.println("Client disconnected");
            clientSocket.close();

        } catch (IOException e) {
            System.out.println("Could not connect to the server due to: " + e.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void handleIncomingData() {
        try {
            Object objectIn = this.objectInputStream.readObject();

            if (objectIn instanceof Message) {
                Message incomingMessage = (Message) this.objectInputStream.readObject();
                System.out.println(incomingMessage.toString());
            } else if(objectIn instanceof DrawUpdate) {
                // Draw update shit
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disconnectFromServer(){
        try {
            this.clientSocket.close();
            this.connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message){
        if (!this.connected)
            throw new IllegalStateException("Client is not connected and thus cannot send a message.");

        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(Message message) {
        if (!this.connected)
            throw new IllegalStateException("Client is not connected and thus cannot send a message.");

        try {
            objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser() {
        if (this.user == null)
            throw new NullPointerException("User has not yet been defined! Remember to call setUser() before trying to connect to a server!");

        return user;
    }
}