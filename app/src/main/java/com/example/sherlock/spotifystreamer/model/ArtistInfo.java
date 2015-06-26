package com.example.sherlock.spotifystreamer.model;

/**
 * Created by Jon on 6/25/15.
 */
public class ArtistInfo {
    public String iconUrl;
    public String title;
    public String id;

    public ArtistInfo(String iconUrl, String title, String id) {
        super();
        this.iconUrl = iconUrl;
        this.title = title;
        this.id = id;
    }
}
