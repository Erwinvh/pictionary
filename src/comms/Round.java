package comms;

import java.io.Serializable;

public class Round implements Serializable {
    private int roundNum;
//    private String[] possibleWords; Don't want to send this to all users

    public Round(int roundNumber) {
        this.roundNum = roundNumber;
    }

    public int getRoundNum() {
        return roundNum;
    }
}