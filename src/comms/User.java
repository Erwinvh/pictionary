package comms;

import javafx.scene.image.Image;

public class User {

    private String name;
    private Image profileImage;
    private int score;

    public User(String name, Image profileImage) {
        this.name = name;
        this.profileImage = profileImage;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Image profileImage) {
        this.profileImage = profileImage;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addscore(int addedPoints){
        this.score+=addedPoints;
    }
}
