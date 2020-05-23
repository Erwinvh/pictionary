package comms;

public class ServerSettings {
    private int rounds;
    private int timeInSeconds;
    private int maxPlayers;
    private String Language;
    private String serverAddress;
    private String port;

    public ServerSettings(int rounds, int timeInSeconds, int maxPlayers, String language, String serverAddress, String port) {
        this.rounds = rounds;
        this.timeInSeconds = timeInSeconds;
        this.maxPlayers = maxPlayers;
        Language = language;
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
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
