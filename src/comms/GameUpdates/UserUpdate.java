package comms.GameUpdates;

import comms.User;

public class UserUpdate extends GameUpdate {
    private User user;
    private boolean hasLeft;
    // TODO: 31/05/2020 How are we going to handle a user who has left? Maybe send over a boolean, or send the entire list of users?

    public UserUpdate(User user, boolean hasLeft) {
        super.gameUpdateType = GameUpdateType.USER;
        this.user = user;
        this.hasLeft = hasLeft;
    }

    public User getUser() {
        return user;
    }

    public boolean hasLeft() {
        return hasLeft;
    }
}