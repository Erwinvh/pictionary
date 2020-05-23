package comms;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    
    private String host;
    private int port;
    private Socket socket;
    private boolean connected;

//    private DataInputStream in;
//    private DataOutputStream out;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public Client (String host, int port) {
        this.host = host;
        this.port = port;

        this.socket = null;
        this.connected = false;
//        this.in = null;
//        this.out = null;
        this.objectInputStream = null;
        this.objectOutputStream = null;
    }

//    public boolean connectData ( ) {
//        if (this.connected) {
//            System.out.println("Client already connected with the server.");
//            return true;
//        }
//
//        try {
//            this.socket = new Socket(this.host, this.port);
//            this.connected = true;
//
//            this.in = new DataInputStream(this.socket.getInputStream());
//            this.out = new DataOutputStream(this.socket.getOutputStream());
//
//            String serverId = this.in.readUTF();
//            System.out.println("Connected with server: " + serverId);
//
//            Scanner scanner = new Scanner(System.in);
//            while (this.connected) {
//                System.out.print("Type here your message: ");
//                String message = scanner.nextLine();
//                this.out.writeUTF(message);
//
//                String response = this.in.readUTF();
//                System.out.println("Got server response: " + response);
//            }
//
//        } catch (IOException e) {
//            System.out.println("Could not connect to the server: " + e.getMessage());
//        }
//
//        return false;
//    }

    public boolean connectObject ( ) {
        if (this.connected) {
            System.out.println("Client already connected with the server.");
            return true;
        }

        try {
            this.socket = new Socket(this.host, this.port);
            this.connected = true;

            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());

//            Message serverId = (Message) this.objectInputStream.readObject(); // instanceof
//            System.out.println("Connected with server: " + serverId.getMessage());

            Scanner scanner = new Scanner(System.in);
            while (this.connected) {
                System.out.print("Type here your message: ");
                String message = scanner.nextLine();
//                this.objectOutputStream.writeObject(new Message(message));

//                Message response = (Message) this.objectInputStream.readObject();
//                System.out.println("Got server response: " + response.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Could not connect to the server: " + e.getMessage());

        } /*catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/

        return false;
    }
}