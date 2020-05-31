package comms.GameUpdates;

import java.io.Serializable;

public class ChatUpdate extends GameUpdate implements Serializable {
    private String username;
    private String message;

    public ChatUpdate(String username, String message) {
        super.gameUpdateType = GameUpdateType.CHAT;

        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getUsername() + ": " + getMessage();
    }
}