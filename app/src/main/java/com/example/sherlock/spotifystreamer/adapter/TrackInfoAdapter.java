package com.example.sherlock.spotifystreamer.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.model.CircleTransform;
import com.example.sherlock.spotifystreamer.model.TrackInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jon on 6/25/15.
 */
public class TrackInfoAdapter extends ArrayAdapter<TrackInfo> {

    private final String LOG_TAG = ArtistInfoAdapter.class.getSimpleName();

    Context context;
    int layoutResId;
    ArrayList<TrackInfo> trackList;

    public TrackInfoAdapter(Context context, int layoutResId, ArrayList<TrackInfo> trackList) {
        super(context, layoutResId, trackList);
        this.layoutResId = layoutResId;
        this.context = context;
        this.trackList = trackList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrackInfoHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);

            holder = new TrackInfoHolder();
            holder.imageViewIcon = (ImageView) convertView.findViewById(R.id.list_item_artists_imageview);
            holder.textViewAlbum = (TextView) convertView.findViewById(R.id.list_item_album_name_textview);
            holder.textViewTrack = (TextView) convertView.findViewById(R.id.list_item_track_name_textview);

            convertView.setTag(holder);
        } else {
            holder = (TrackInfoHolder) convertView.getTag();
        }

        TrackInfo trackInfo = trackList.get(position);
        holder.textViewTrack.setText(trackInfo.trackTitle);
        holder.textViewAlbum.setText(trackInfo.albumName);


        if (Patterns.WEB_URL.matcher(trackInfo.iconUrl).matches()) {
            Picasso.with(this.context)
                    .load(trackInfo.iconUrl)
                    .placeholder(R.drawable.default_icon_spotify)
                    .transform(new CircleTransform())
                    .into(holder.imageViewIcon);
        } else {
            Picasso.with(this.context)
                    .load(R.drawable.default_icon_spotify)
                    .transform(new CircleTransform())
                    .into(holder.imageViewIcon);
        }

        return convertView;
    }

    public class TrackInfoHolder {
        ImageView imageViewIcon;
        TextView textViewTrack;
        TextView textViewAlbum;
    }
}