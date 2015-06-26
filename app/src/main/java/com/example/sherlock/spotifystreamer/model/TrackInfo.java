package com.example.sherlock.spotifystreamer.model;

/**
 * Created by Jon on 6/25/15.
 */
public class TrackInfo {
    public String iconUrl;
    public String trackTitle;
    public String albumName;

    public TrackInfo(String iconUrl, String trackTitle, String albumName) {
        super();
        this.iconUrl = iconUrl;
        this.trackTitle = trackTitle;
        this.albumName = albumName;
    }
}
