import comms.Server;
import comms.ServerSettings;

public class RunServer {
    public static void main(String[] args) {
        ServerSettings serverSettings = new ServerSettings(2, 10,
                10, "English", "localhost", 10000);

        new Server(serverSettings);
    }
}