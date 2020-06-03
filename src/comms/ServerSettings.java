package comms;

import java.io.Serializable;

public class ServerSettings implements Serializable {
    private int rounds = 3;
    private int timeInSeconds = 30;
    private String language = "English";
    private String serverAddress = "localhost";
    private int port;

    public ServerSettings(int port) {
        this.port = port;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public void setTimeInSeconds(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getPort() {
        return port;
    }
}