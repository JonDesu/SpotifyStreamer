package com.example.sherlock.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataHelper {

    public static class ArtistInfo {
        public String iconUrl;
        public String title;
        public String id;
        public ArtistInfo(){
            super();
        }

        public ArtistInfo(String iconUrl, String title, String id) {
            super();
            this.iconUrl = iconUrl;
            this.title = title;
            this.id = id;
        }
    }

    public static class ArtistInfoAdapter extends ArrayAdapter<ArtistInfo> {

        Context context;
        int layoutResId;
        ArrayList<ArtistInfo> artistList;

        public ArtistInfoAdapter(Context context, int layoutResId,  ArrayList<ArtistInfo> artistList) {
            super(context, layoutResId, artistList);
            this.layoutResId = layoutResId;
            this.context = context;
            this.artistList = artistList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ArtistInfoHolder holder;

            if(convertView == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(layoutResId, parent, false);

                holder = new ArtistInfoHolder();
                holder.imageIcon = (ImageView)convertView.findViewById(R.id.list_item_artists_imageview);
                holder.textName = (TextView)convertView.findViewById(R.id.list_item_artists_textview);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ArtistInfoHolder) convertView.getTag();
            }

            ArtistInfo artistInfo = artistList.get(position);
            holder.textName.setText(artistInfo.title);
            Picasso.with(this.context).load(artistInfo.iconUrl).transform(new CircleTransform()).into(holder.imageIcon);


            return convertView;
        }

        public class ArtistInfoHolder
        {
            ImageView imageIcon;
            TextView textName;
        }
    }

    public static class TrackInfo {
        public String iconUrl;
        public String trackTitle;
        public String albumName;

        public TrackInfo(){
            super();
        }

        public TrackInfo(String iconUrl, String trackTitle, String albumName) {
            super();
            this.iconUrl = iconUrl;
            this.trackTitle = trackTitle;
            this.albumName = albumName;
        }
    }

    public static class TrackInfoAdapter extends ArrayAdapter<TrackInfo> {

        Context context;
        int layoutResId;
        ArrayList<TrackInfo> trackList;

        public TrackInfoAdapter(Context context, int layoutResId,  ArrayList<TrackInfo> trackList) {
            super(context, layoutResId, trackList);
            this.layoutResId = layoutResId;
            this.context = context;
            this.trackList = trackList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TrackInfoHolder holder;

            if(convertView == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(layoutResId, parent, false);

                holder = new TrackInfoHolder();
                holder.imageViewIcon = (ImageView)convertView.findViewById(R.id.list_item_artists_imageview);
                holder.textViewAlbum = (TextView)convertView.findViewById(R.id.list_item_album_name_textview);
                holder.textViewTrack = (TextView)convertView.findViewById(R.id.list_item_track_name_textview);

                convertView.setTag(holder);
            }
            else
            {
                holder = (TrackInfoHolder) convertView.getTag();
            }

            TrackInfo trackInfo = trackList.get(position);
            holder.textViewTrack.setText(trackInfo.trackTitle);
            holder.textViewAlbum.setText(trackInfo.albumName);
            Picasso.with(this.context).load(trackInfo.iconUrl).transform(new CircleTransform()).into(holder.imageViewIcon);


            return convertView;
        }

        public class TrackInfoHolder
        {
            ImageView imageViewIcon;
            TextView textViewTrack;
            TextView textViewAlbum;
        }
    }

}
