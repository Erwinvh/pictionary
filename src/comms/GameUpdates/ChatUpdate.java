package comms.GameUpdates;

import comms.User;

import java.io.Serializable;

public class ChatUpdate extends GameUpdate implements Serializable {
    private User user;
    private String message;

    public ChatUpdate(User user, String message) {
        super.gameUpdateType = GameUpdateType.CHAT;

        this.user = user;
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getUser().getName() + ": " + getMessage();
    }
}