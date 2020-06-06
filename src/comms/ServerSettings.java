package comms;

import java.io.Serializable;

public class ServerSettings implements Serializable {
    private int rounds = 3;
    private int timeInSeconds = 30;
    private String language = "English";
    private int port;

    public ServerSettings() {
        this(10000);
    }

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

    int getPort() {
        return port;
    }
}