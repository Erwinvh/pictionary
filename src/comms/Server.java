package comms;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    String host;
    int port;

    ServerSocket server;
    boolean running;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        this.server = null;
        this.running = false;
    }

    public void start() throws IOException{
        if (this.server != null) {
            System.out.println("Server already created socket, please stop first!");
            return;
        }

        this.server = new ServerSocket(this.port);
        this.running = true;

        while (this.running) {
            System.out.println("Waiting for client to connect...");
            final Socket client = this.server.accept();

            new Thread(() -> handleClientConnectionObject(client)).start();
        }
    }

    private void handleClientConnectionObject(Socket client) {
        System.out.println("Client connected, handling connection.");

        try (ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream())
        ) {
            boolean connected = true;
            //out.writeObject(new Message("I am object server 2.0"));

            while (connected) {
                //Message message = (Message) in.readObject();
                //out.writeObject(message);
            }

            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void handleClientConnectionData(Socket client) {
//        System.out.println("Client connected, handling connection.");
//
//        try (DataInputStream in = new DataInputStream(client.getInputStream());
//             DataOutputStream out = new DataOutputStream(client.getOutputStream())
//        ) {
//            boolean connected = true;
//            out.writeUTF("Hi I am server 1.0");
//
//            while (connected) {
//                String message = in.readUTF();
//                out.writeUTF(message);
//            }
//
//            client.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public boolean getRunning() {
        return running;
    }
}