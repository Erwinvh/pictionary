package comms.GameUpdates;

import java.io.Serializable;

public abstract class GameUpdate implements Serializable {
    public enum GameUpdateType {
        CHAT,
        DRAW,
        ROUND,
        TIMER,
        HINT,
        USER
    }

    GameUpdateType gameUpdateType;

    public GameUpdateType getGameUpdateType() {
        if (gameUpdateType == null) {
            throw new NullPointerException("GameUpdateType was null! Don't forget to set the type in the constructor");
        }

        return gameUpdateType;
    }
}