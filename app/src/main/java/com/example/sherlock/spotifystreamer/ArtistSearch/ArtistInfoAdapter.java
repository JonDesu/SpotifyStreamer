package com.example.sherlock.spotifystreamer.ArtistSearch;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.Utilities.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ArtistInfoAdapter extends ArrayAdapter<ArtistInfo> {

    private final String LOG_TAG = ArtistInfoAdapter.class.getSimpleName();

    private Context mContext;
    private int mLayoutResourceID;
    private List<ArtistInfo> mArtistList;

    public ArtistInfoAdapter(Context context, int LayoutResourceID, List<ArtistInfo> artistList) {
        super(context, LayoutResourceID, artistList);
        mContext = context;
        mLayoutResourceID = LayoutResourceID;
        mArtistList = artistList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceID, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArtistInfo artistInfo = mArtistList.get(position);

        if (artistInfo != null) {
            viewHolder.textView.setText(artistInfo.getName());

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

    static class ViewHolder {

        @InjectView(R.id.list_item_artists_textview)
        TextView textView;
        @InjectView(R.id.list_item_artists_imageview)
        ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
