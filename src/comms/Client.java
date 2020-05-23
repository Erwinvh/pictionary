package comms;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;
    private boolean connected;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private User user;

    public Client (User user) {
        this.clientSocket = null;
        this.connected = false;

        this.objectInputStream = null;
        this.objectOutputStream = null;

        this.user = user;

        // TODO: 23/05/2020 launch GUI
    }

    public boolean connectToServer(String serverAddress, int serverPort) {
        if (this.connected) {
            System.out.println("Client already connected with the server.");
            return true;
        }

        try {
            this.clientSocket = new Socket(serverAddress, serverPort);
            this.connected = true;

            this.objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());

            this.objectOutputStream.writeObject(this.user);

            // FIXME: 23/05/2020 Possible blocking call
            Scanner scanner = new Scanner(System.in);
            while (this.connected) {
                System.out.print("Type here your message: ");

                String messageText = scanner.nextLine();
                Message message = new Message(user.getName(), messageText);
                this.objectOutputStream.writeObject(message);

                Object objectIn = this.objectInputStream.readObject();
                if (objectIn instanceof Message){
                    Message incomingMessage = (Message) this.objectInputStream.readObject();
                    // TODO: 23/05/2020 Show this incoming message in GUI
                } // TODO: 23/05/2020 else if (objectIn instanceof Drawing)
            }

        } catch (IOException e) {
            System.out.println("Could not connect to the server due to: " + e.getMessage());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void disconnectFromServer(){
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}