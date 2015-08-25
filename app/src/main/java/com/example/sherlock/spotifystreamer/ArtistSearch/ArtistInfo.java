package com.example.sherlock.spotifystreamer.ArtistSearch;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistInfo implements Parcelable {

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
    private String mIconUrl;
    private String mId;
    private String mArtistName;

    public ArtistInfo(Artist artist) {
        mIconUrl = null;
        mArtistName = artist.name;
        mId = artist.id;

        if (!artist.images.isEmpty()) mIconUrl = artist.images.get(0).url;
    }

    private ArtistInfo(Parcel parcel) {
        mArtistName = parcel.readString();
        mIconUrl = parcel.readString();
        mId = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mArtistName);
        parcel.writeString(mIconUrl);
        parcel.writeString(mId);
    }

    public String getIconUrl(){return mIconUrl;}
    public String getName(){return mArtistName;}
    public String getId(){return mId;}
}
