package comms;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {

    private String name;
    private String profileImage;
    private int score;
    private boolean isHost;
    private boolean isDrawing;

    private UUID id;

    public User(String name, String imageLocation, boolean isHost) {
        this.name = name;
        this.profileImage = imageLocation;
        this.score = 0;
        this.isHost = isHost;
        this.isDrawing = false;
        this.id = UUID.randomUUID();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDrawing() {
        return isDrawing;
    }

    public void setDrawing(boolean drawing) {
        isDrawing = drawing;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addscore(int addedPoints) {
        this.score += addedPoints;
    }

    public boolean isHost() {
        return isHost;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return id.equals(((User) obj).getId());
        }

        return false;
    }

    public UUID getId() {
        return id;
    }

    public String getProfileImage() {
        return profileImage;
    }
}