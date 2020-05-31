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
        if (gameUpdateType == null){
            throw new NullPointerException("GameUpdateType was null! Don't forget to set the type in the constructor");
        }

        return gameUpdateType;
    }
}