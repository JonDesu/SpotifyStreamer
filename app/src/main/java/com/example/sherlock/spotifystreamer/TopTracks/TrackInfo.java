package com.example.sherlock.spotifystreamer.TopTracks;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Jon on 6/25/15.
 */
public class TrackInfo implements Parcelable{
    /**
     * Creates TrackParcelable model from Parcel.
     */
    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public TrackInfo createFromParcel(Parcel source) {
            return new TrackInfo(source);
        }

        @Override
        public TrackInfo[] newArray(int size) {
            return new TrackInfo[size];
        }
    };
    private String mIconUrl;
    private String mTrackTitle;
    private String mAlbumName;
    private String mArtistName;
    private String mId;
    private String mTrackUrl;
    private String mExternalUrl;

    public TrackInfo(Track track) {
        mIconUrl = track.album.images.get(0).url;
        mAlbumName = track.album.name;
        mTrackTitle = track.name;
        mTrackUrl = track.preview_url;
        mArtistName = track.artists.get(0).name;
        mId = track.id;
        if (track.external_urls != null) {
            mExternalUrl = track.external_urls.get("spotify");
        }
    }

    private TrackInfo(Parcel source) {
        mTrackTitle = source.readString();
        mAlbumName = source.readString();
        mArtistName = source.readString();
        mId = source.readString();
        mTrackUrl = source.readString();
        mExternalUrl = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // could alternatively bundle first...
        dest.writeString(mIconUrl);
        dest.writeString(mTrackTitle);
        dest.writeString(mAlbumName);
    }

    public String getTrackTitle(){
        return mTrackTitle;
    }

    public String getAlbumName(){
        return mAlbumName;
    }

    public String getIconUrl(){
        return mIconUrl;
    }

    public String getTrackUrl(){
        return mTrackUrl;
    }

    public String getArtistName(){
        return mArtistName;
    }

    public String getId(){
        return mId;
    }
}