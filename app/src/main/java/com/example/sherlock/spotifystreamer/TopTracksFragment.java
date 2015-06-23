package com.example.sherlock.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    private final String LOG_TAG = TrackSearchTask.class.getSimpleName();
    private String mArtistID;
    private TrackInfoAdapter trackInfoAdapter;

    public TopTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtistID = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        ArrayList<TrackInfo> trackInfoList = new ArrayList<TrackInfo>();

        trackInfoAdapter = new TrackInfoAdapter(this.getActivity(),R.layout.list_item_tracks, trackInfoList);

        //Reference to listview, and attach adapter
        ListView listView = (ListView) rootView.findViewById(R.id.trackResultsListView);
        listView.setAdapter(trackInfoAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        TrackSearchTask trackSearchTask = new TrackSearchTask();
        trackSearchTask.execute(mArtistID);
    }

    public class TrackInfo {
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

    public class TrackInfoAdapter extends ArrayAdapter<TrackInfo> {

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
            TrackInfoHolder holder = null;

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

    public class TrackSearchTask extends AsyncTask<String, Void, Tracks> {

        private final String LOG_TAG = TrackSearchTask.class.getSimpleName();

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();

        @Override
        protected Tracks doInBackground(String... params) {

            String inputArtist = params[0];
            Tracks resPager = null;

            Map<String, Object> map = new HashMap<>();
            map.put("country", Locale.getDefault().getCountry());

            try{
                resPager = spotifyService.getArtistTopTrack(inputArtist,map);
            }
            catch(Exception err){
                Log.e(LOG_TAG, "Unknown Error: " + err.getMessage());
            }

            return resPager;
        }

        @Override
        protected void onPostExecute(Tracks tracksResults) {

            if (tracksResults == null) return;

            List<Track> trackList = tracksResults.tracks;

            trackInfoAdapter.clear();
            for (int i = 0; i < trackList.size(); i++) {

                String albumImageUrl = "http://cdn.embed.ly/providers/logos/spotify.png";
                String trackName = "Unnamed";
                String albumName = "UnknownAlbum";

                albumImageUrl = trackList.get(i).album.images.get(0).url;
                albumName = trackList.get(i).album.name;
                trackName = trackList.get(i).name;

                TrackInfo currArtistInfo = new TrackInfo(albumImageUrl,albumName,trackName);
                trackInfoAdapter.add(currArtistInfo);
            }
            trackInfoAdapter.notifyDataSetChanged();
        }
    }

}
