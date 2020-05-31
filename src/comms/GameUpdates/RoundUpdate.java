package comms.GameUpdates;

import comms.Round;

public class RoundUpdate extends GameUpdate {
    private Round round;

    public RoundUpdate(Round newRound) {
        this.gameUpdateType = GameUpdateType.ROUND;

        this.round = newRound;
    }

    public Round getRound() {
        return round;
    }
}