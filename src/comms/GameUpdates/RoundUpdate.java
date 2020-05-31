package comms.GameUpdates;

public class RoundUpdate extends GameUpdate {
    private int roundNum;
    private int maxRounds;

    public RoundUpdate(int roundNum, int maxRounds) {
        this.gameUpdateType = GameUpdateType.ROUND;

        this.roundNum = roundNum;
        this.maxRounds = maxRounds;
    }

    public int getRoundNum() {
        return roundNum;
    }

    public int getMaxRounds() {
        return maxRounds;
    }
}