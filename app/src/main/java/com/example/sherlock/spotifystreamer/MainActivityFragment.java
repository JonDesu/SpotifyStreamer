package com.example.sherlock.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mAdapter;
    private EditText searchText;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        searchText = (EditText) rootView.findViewById(R.id.searchText);
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    SpotifySearchTask spotSearch = new SpotifySearchTask();
                    spotSearch.execute(searchText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        mAdapter = new ArrayAdapter<>(
                            getActivity(),
                            R.layout.list_item_artitsts,
                            R.id.list_item_artists_textview,
                            new ArrayList<String>());

        //Reference to listview, and attach adapter
        ListView listView = (ListView) rootView.findViewById(R.id.artistResultListView);
        listView.setAdapter(mAdapter);

        return rootView;
    }

    public class SpotifySearchTask extends AsyncTask<String, Void, ArtistsPager> {

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();

        @Override
        protected ArtistsPager doInBackground(String... params) {
            String inputArtist = params[0];
            return spotifyService.searchArtists(inputArtist);
        }

        @Override
        protected void onPostExecute(ArtistsPager artistPagerResults) {
            super.onPostExecute(artistPagerResults);

            List<Artist> artistList = artistPagerResults.artists.items;

            if (artistList.size() > 0) {
                mAdapter.clear();
                for (int i = 0; i < artistList.size(); i++) {
                    mAdapter.add(artistList.get(i).name);
                }
                mAdapter.notifyDataSetChanged();
            }

        }
    }
}
