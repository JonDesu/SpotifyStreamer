package com.example.sherlock.spotifystreamer.adapter;

import android.app.Activity;
import android.content.Context;
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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jon on 6/25/15.
 */
public class ArtistInfoAdapter extends ArrayAdapter<ArtistInfo> {

    private final String LOG_TAG = ArtistInfoAdapter.class.getSimpleName();

    private Context context;
    private int layoutResourceId;
    private List<ArtistInfo> objects;

    public ArtistInfoAdapter(Context context, int layoutResourceId, List <ArtistInfo> objects) {
        super(context, layoutResourceId, objects);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.objects = objects;
    }

    static class ViewHolder {

        @InjectView(R.id.list_item_artists_textview) TextView textView;
        @InjectView(R.id.list_item_artists_imageview) ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
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

        ArtistInfo artistInfo = objects.get(position);

        if (artistInfo != null) {
            viewHolder.textView.setText(artistInfo.getName());
            // Don't load the image again if it was already fetched
            if (viewHolder.imageView != null) {
                Picasso.with(getContext())
                        .load(artistInfo.getIconUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.default_icon_spotify)
                        .error(R.drawable.default_icon_spotify)
                        .into(viewHolder.imageView);
            }
        }

        return convertView;
    }
}
