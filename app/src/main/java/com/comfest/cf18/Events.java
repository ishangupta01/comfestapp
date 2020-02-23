package com.comfest.cf18;

/**
 * Created by ishan on 3/8/18.
 */

public class Events {
    String imageurl;
    String heading;
    String text;

    public Events() {
    }

    public Events(String imageurl, String heading, String text) {
        this.imageurl = imageurl;
        this.heading = heading;
        this.text = text;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
