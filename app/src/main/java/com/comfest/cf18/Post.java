package com.comfest.cf18;

/**
 * Created by ishan on 11/3/18.
 */

public class Post {

    public String link;
    public String from_name;
    public long timestamp;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String from;
    public String caption;

    public String getLink() {
        return link;
    }


    public void setLink(String link) {
        this.link = link;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Post(String link, String from_name, long timestamp, String from) {

        this.link = link;
        this.from_name = from_name;
        this.timestamp = timestamp;
        this.from = from;
        this.caption = caption;
    }

    public Post()
    {

    }
}
