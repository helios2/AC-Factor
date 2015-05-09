package it.adepti.ac_factor;

public class VideoItem {

    private String videoName;
    private String likeLink;

    public VideoItem(String videoName, String likeLink){
        this.likeLink = likeLink;
        this.videoName = videoName;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getLikeLink() {
        return likeLink;
    }

    public void setLikeLink(String likeLink) {
        this.likeLink = likeLink;
    }
}
