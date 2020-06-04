package comms.GameUpdates;

import comms.User;

public class StateUpdate extends GameUpdate {
    private User user;
    private stateType state;

    public StateUpdate(User user, stateType state){
        this.user = user;
        this.state = state;
        super.gameUpdateType = GameUpdateType.STATE;
    }
    public enum stateType{
        LOBBY,
        GAME
    }

    public stateType getState() {
        return state;
    }

    public User getUser() {
        return user;
    }
}
