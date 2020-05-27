package comms;

public class ServerSettings {
    private int rounds;
    private int timeInSeconds;
    private int maxPlayers;
    private String language;
    private String serverAddress;
    private int port;

    public ServerSettings(int rounds, int timeInSeconds, int maxPlayers, String language, String serverAddress, int port) {
        this.rounds = rounds;
        this.timeInSeconds = timeInSeconds;
        this.maxPlayers = maxPlayers;
        this.language = language;
        this.serverAddress = serverAddress;
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

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getLanguage() {
        return language;
    }


    public String getServerAddress() {
        return serverAddress;
    }

    public int getPort() {
        return port;
    }

}