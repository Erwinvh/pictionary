package comms.GameUpdates;

import java.io.Serializable;

public abstract class GameUpdate implements Serializable {
    public enum GameUpdateType {
        UNKNOWN,
        CHAT,
        DRAW,
        ROUND,
        TIMER,
        HINT,
        USER,
        TURN,
        SETTINGS,
        STATE
    }

    GameUpdateType gameUpdateType;

    public GameUpdate() {
        this.gameUpdateType = GameUpdateType.UNKNOWN;
    }

    public GameUpdateType getGameUpdateType() {
        if (this.gameUpdateType == null || this.gameUpdateType == GameUpdateType.UNKNOWN) {
            throw new NullPointerException("GameUpdateType was not set! Don't forget to set the type in the constructor");
        }

        return gameUpdateType;
    }
}