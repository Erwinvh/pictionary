package comms.GameUpdates;

public abstract class GameUpdate {
    public enum GameUpdateType {
        CHAT,
        DRAW,
        ROUND,
        TIMER
    }

    GameUpdateType gameUpdateType;

    public GameUpdateType getGameUpdateType() {
        return gameUpdateType;
    }
}