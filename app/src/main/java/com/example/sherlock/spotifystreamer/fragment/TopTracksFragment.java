package com.example.sherlock.spotifystreamer.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.adapter.TrackInfoAdapter;
import com.example.sherlock.spotifystreamer.model.TrackInfo;
import com.example.sherlock.spotifystreamer.utilities.Utilities;

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

        if (!Utilities.NetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtistID = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        ArrayList<TrackInfo> trackInfoList = new ArrayList<>();

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

            if (tracksResults.tracks.size() == 0){
                Toast.makeText(getActivity(), "No tracks listed for this artist...", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            List<Track> trackList = tracksResults.tracks;

            trackInfoAdapter.clear();
            for (int i = 0; i < trackList.size(); i++) {

                String albumImageUrl = null;
                String trackName = "Unnamed";
                String albumName = "UnknownAlbum";

                try {
                    albumImageUrl = trackList.get(i).album.images.get(0).url;
                    albumName = trackList.get(i).album.name;
                    trackName = trackList.get(i).name;
                }
                catch(Exception err){
                    Log.e(LOG_TAG, "Error occurred while getting track info : " + err.getMessage());
                }

                TrackInfo currArtistInfo = new TrackInfo(albumImageUrl,albumName,trackName);
                trackInfoAdapter.add(currArtistInfo);
            }
            trackInfoAdapter.notifyDataSetChanged();
        }
    }

}
