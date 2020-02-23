package com.comfest.cf18;

/**
 * Created by ishan on 11/3/18.
 */

public class PostImage {


    public String from_image;
    public long timestamp;

    public String getFrom_image() {
        return from_image;
    }

    public void setFrom_image(String from_image) {
        this.from_image = from_image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public PostImage(String from_image, long timestamp) {

        this.from_image = from_image;
        this.timestamp = timestamp;
    }

    public PostImage()
    {

    }

}
