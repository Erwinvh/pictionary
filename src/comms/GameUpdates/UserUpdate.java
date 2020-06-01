package comms.GameUpdates;

import comms.User;

public class UserUpdate extends GameUpdate {
    private User user;
    private boolean hasLeft;

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

    @Override
    public String toString() {
        return "UserUpdate{" +
                "user=" + user.toString() +
                ", hasLeft=" + hasLeft +
                '}';
    }
}