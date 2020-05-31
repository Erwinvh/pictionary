package comms;

import javafx.scene.image.Image;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String profileImage;
    private int score;
    private boolean isHost;
    private boolean isDrawing;

    public User(String name, String imageLocation,boolean isHost) {
        this.name = name;
this.profileImage = imageLocation;
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

    public void setDrawing(boolean drawing) {
        isDrawing = drawing;
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

    public String getProfileImage() {
        return profileImage;
    }
}