package comms.GameUpdates;

public class TimerUpdate extends GameUpdate {
    private int timeLeft;

    public TimerUpdate(int newTimeLeft) {
        super.gameUpdateType = GameUpdateType.TIMER;

        this.timeLeft = newTimeLeft;
    }
}