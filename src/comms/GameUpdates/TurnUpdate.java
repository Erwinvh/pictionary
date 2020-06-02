package comms.GameUpdates;

import comms.User;

public class TurnUpdate extends GameUpdate {
    private String word;
    private User drawer;

    public TurnUpdate(User drawer, String word) {
        super.gameUpdateType = GameUpdateType.TURN;

        this.word = word;
        this.drawer = drawer;
    }

    public String getWord() {
        return word;
    }

    public User getDrawer() {
        return drawer;
    }
}