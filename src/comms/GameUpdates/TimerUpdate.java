package comms.GameUpdates;

public class TimerUpdate extends GameUpdate {
    private int timeLeft;

    public TimerUpdate(int newTimeLeft) {
        super.gameUpdateType = GameUpdateType.TIMER;

        if (newTimeLeft >= 0) {
            this.timeLeft = newTimeLeft;
        } else this.timeLeft = 0;
    }

    public int getTimeLeft() {
        return timeLeft;
    }
}