package comms.GameUpdates;

import comms.User;

import java.io.Serializable;

public class TurnUpdate extends GameUpdate implements Serializable {
    private String Word;
    private User drawer;

    public TurnUpdate( User drawer, String word) {
        Word = word;
        this.drawer = drawer;
    }

    public String getWord() {
        return Word;
    }

    public User getDrawer() {
        return drawer;
    }
}
