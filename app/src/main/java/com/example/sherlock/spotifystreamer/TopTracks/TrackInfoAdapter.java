package com.example.sherlock.spotifystreamer.TopTracks;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sherlock.spotifystreamer.ArtistSearch.ArtistInfoAdapter;
import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.Utilities.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TrackInfoAdapter extends ArrayAdapter<TrackInfo> {

    private final String LOG_TAG = ArtistInfoAdapter.class.getSimpleName();

    Context context;
    int layoutResourceId;
    List<TrackInfo> objects;

    public TrackInfoAdapter(Context context, int layoutResourceId, List<TrackInfo> objects) {
        super(context, layoutResourceId, objects);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            // Inflating the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            // Setting viewholder
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            // Getting back info from the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TrackInfo trackItem = objects.get(position);

        if (trackItem != null) {
            viewHolder.textViewTrack.setText(trackItem.getTrackTitle());
            viewHolder.textViewAlbum.setText(trackItem.getAlbumName());

            if (viewHolder.imageView != null) {
                Picasso.with(getContext())
                        .load(trackItem.getIconUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.default_icon_spotify)
                        .error(R.drawable.default_icon_spotify)
                        .into(viewHolder.imageView);
            }
        }

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.list_item_track_name_textview)
        TextView textViewTrack;
        @InjectView(R.id.list_item_album_name_textview)
        TextView textViewAlbum;
        @InjectView(R.id.list_item_track_imageview)
        ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}