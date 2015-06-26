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
import com.example.sherlock.spotifystreamer.model.ArtistInfo;
import com.example.sherlock.spotifystreamer.model.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jon on 6/25/15.
 */
public class ArtistInfoAdapter extends ArrayAdapter<ArtistInfo> {

    private final String LOG_TAG = ArtistInfoAdapter.class.getSimpleName();

    Context context;
    int layoutResId;
    ArrayList<ArtistInfo> artistList;

    public ArtistInfoAdapter(Context context, int layoutResId, ArrayList<ArtistInfo> artistList) {
        super(context, layoutResId, artistList);
        this.layoutResId = layoutResId;
        this.context = context;
        this.artistList = artistList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArtistInfoHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);

            holder = new ArtistInfoHolder();
            holder.imageIcon = (ImageView) convertView.findViewById(R.id.list_item_artists_imageview);
            holder.textName = (TextView) convertView.findViewById(R.id.list_item_artists_textview);
            convertView.setTag(holder);
        } else {
            holder = (ArtistInfoHolder) convertView.getTag();
        }

        ArtistInfo artistInfo = artistList.get(position);
        holder.textName.setText(artistInfo.title);

        if (Patterns.WEB_URL.matcher(artistInfo.iconUrl).matches()) {
            Picasso.with(this.context)
                    .load(artistInfo.iconUrl)
                    .transform(new CircleTransform())
                    .into(holder.imageIcon);
        } else {
            Picasso.with(this.context)
                    .load(R.drawable.default_icon_spotify)
                    .transform(new CircleTransform())
                    .into(holder.imageIcon);
        }

        return convertView;
    }

    public class ArtistInfoHolder {
        ImageView imageIcon;
        TextView textName;
    }
}
