package comms.GameUpdates;

import comms.ServerSettings;

public class SettingsUpdate extends GameUpdate{

    private ServerSettings serverSettings;

    public SettingsUpdate(ServerSettings serverSettings) {
        this.gameUpdateType = GameUpdateType.SETTINGS;
        this.serverSettings = serverSettings;
    }

    public ServerSettings getServerSettings() {
        return serverSettings;
    }
}