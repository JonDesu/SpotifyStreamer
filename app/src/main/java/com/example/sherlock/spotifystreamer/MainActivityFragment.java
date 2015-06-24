package com.example.sherlock.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sherlock.spotifystreamer.DataHelper.ArtistInfo;
import com.example.sherlock.spotifystreamer.DataHelper.ArtistInfoAdapter;

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

    private ArtistInfoAdapter mAdapter;
    private final String LOG_TAG = SpotifySearchTask.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final EditText searchEditText;

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        searchEditText = (EditText) rootView.findViewById(R.id.searchText);

        searchEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            // your action here
                            String searchString = searchEditText.getText().toString();

                            if (searchString.equals("")) return true;

                            Log.i(LOG_TAG, "Searching for artist : " + searchString);
                            SpotifySearchTask spotSearch = new SpotifySearchTask();
                            spotSearch.execute(searchString);

                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(),
                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                            return true;
                        }
                        return false;
                    }
                }
        );

        ArrayList<ArtistInfo> artistInfoList = new ArrayList<>();

        mAdapter = new ArtistInfoAdapter(this.getActivity(),R.layout.list_item_artitsts, artistInfoList);

        //Reference to listview, and attach adapter
        ListView listView = (ListView) rootView.findViewById(R.id.artistResultListView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ArtistInfo selectedArtist = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TopTracksActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, selectedArtist.id);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class SpotifySearchTask extends AsyncTask<String, Void, ArtistsPager> {

        private final String LOG_TAG = SpotifySearchTask.class.getSimpleName();

        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();

        @Override
        protected ArtistsPager doInBackground(String... params) {

            String inputArtist = params[0];
            ArtistsPager resPager = null;

            try{
                 resPager = spotifyService.searchArtists(inputArtist);
            }
            catch(Exception err){
                Log.e(LOG_TAG, "Unknown Error: " + err.getMessage());
            }

            return resPager;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistPagerResults) {

            if (artistPagerResults == null) return;

            if (artistPagerResults.artists.total == 0){
                Toast.makeText(getActivity(), "No Artists found.. Perhaps a different search?", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Artist> artistList = artistPagerResults.artists.items;

            mAdapter.clear();
            for (int i = 0; i < artistList.size(); i++) {

                String artistImageUrl = "http://cdn.embed.ly/providers/logos/spotify.png";
                String artistTextName = null;
                String artistId = null;

                if(artistList.get(i).images.size() == 0) continue;

                try{
                    artistImageUrl = artistList.get(i).images.get(0).url;
                    artistTextName = artistList.get(i).name;
                    artistId = artistList.get(i).id;
                }
                catch(Exception err){
                    Log.e(LOG_TAG, "Error Occured, Skipping artist.. : " + err);
                }

                ArtistInfo currArtistInfo = new ArtistInfo(artistImageUrl,artistTextName,artistId);
                mAdapter.add(currArtistInfo);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
