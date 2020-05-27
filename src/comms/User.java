package comms;

import javafx.scene.image.Image;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
//    private Image profileImage;
    private int score;
    private boolean isHost;
    private boolean isDrawing;

    public User(String name, /*Image profileImage, */boolean isHost) {
        this.name = name;
//        this.profileImage = profileImage;
        this.score = 0;
        this.isHost = isHost;
        isDrawing = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
//
//    public Image getProfileImage() {
//        return profileImage;
//    }
//
//    public void setProfileImage(Image profileImage) {
//        this.profileImage = profileImage;
//    }

    public boolean isDrawing(){
        return isDrawing;
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

    public boolean isHost() {
        return isHost;
    }
}