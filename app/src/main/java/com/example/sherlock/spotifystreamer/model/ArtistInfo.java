package com.example.sherlock.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Jon on 6/25/15.
 */
public class ArtistInfo implements Parcelable {
    private String mIconUrl;
    private String mId;
    private String mArtistName;

    public ArtistInfo(Artist artist) {
        mIconUrl = null;
        mArtistName = artist.name;
        mId = artist.id;

        if (!artist.images.isEmpty()) mIconUrl = artist.images.get(0).url;
    }

    private ArtistInfo(Parcel source) {
        mArtistName = source.readString();
        mIconUrl = source.readString();
        mId = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // could alternatively bundle first...
        dest.writeString(mIconUrl);
        dest.writeString(mArtistName);
        dest.writeString(mId);
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public ArtistInfo createFromParcel(Parcel source) {
            return new ArtistInfo(source);
        }

        @Override
        public ArtistInfo[] newArray(int size) {
            return new ArtistInfo[size];
        }
    };

    public String getIconUrl(){return mIconUrl;}
    public String getName(){return mArtistName;}
    public String getId(){return mId;}
}
