package comms.GameUpdates;

import comms.User;

import java.io.Serializable;

public class ChatUpdate extends GameUpdate implements Serializable {

    private User user;
    private String message;
    private boolean isSystemMessage;

    public ChatUpdate(User user, String message) {
        this(user, message, false);
    }

    public ChatUpdate(User user, String message, boolean isSystemMessage) {
        super.gameUpdateType = GameUpdateType.CHAT;

        this.user = user;
        this.message = message;
        this.isSystemMessage = isSystemMessage;
    }

    public User getUser() {
        if (isSystemMessage) {
            return null;
        }

        return user;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (isSystemMessage) {
            return "[System]: " + getMessage();
        }

        return getUser().getName() + ": " + getMessage();
    }
}