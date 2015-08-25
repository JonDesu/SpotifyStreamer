package com.example.sherlock.spotifystreamer.ArtistSearch;

import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.Utilities.Utilities;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class MainActivityFragment extends Fragment {

    private static final String ARTIST_POSITION_KEY = "ARTIST_POSITION_KEY";
    private static final String ARTIST_INFO_LIST_KEY = "ARTIST_INFO_LIST_KEY";
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    @InjectView(R.id.listview_artists) ListView listView;
    @InjectView(R.id.progress_bar_artists) ProgressBar progressBarArtist;
    @InjectView(R.id.search_text_artists) EditText searchEditText;
    private ArrayList<ArtistInfo> mArtistInfoList = new ArrayList<>();
    private ArtistInfoAdapter mAdapter;
    private int mPosition = ListView.INVALID_POSITION;
    private AdapterView.OnItemClickListener onArtistItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            ArtistInfo artistInfo = mAdapter.getItem(position);
            ((Callback) getActivity()).onArtistItemSelected(artistInfo);
            mPosition = position;
        }
    };

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(ARTIST_INFO_LIST_KEY) &&
                savedInstanceState.containsKey(ARTIST_POSITION_KEY)) {
            mArtistInfoList = savedInstanceState.getParcelableArrayList(ARTIST_INFO_LIST_KEY);
            mPosition = savedInstanceState.getInt(ARTIST_POSITION_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);

                searchEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                            String searchString = searchEditText.getText().toString();

                            if (searchString.equals("")) return true;

                            Log.i(LOG_TAG, "Searching for artist : " + searchString);
                            searchForArtists(searchString);

                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(),
                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                            return true;
                        }
                        return false;
                    }
                }
        );

        mAdapter = new ArtistInfoAdapter(this.getActivity(),R.layout.list_item_artists, mArtistInfoList);

        listView.setAdapter(mAdapter);
        listView.smoothScrollToPosition(mPosition);
        listView.setOnItemClickListener(onArtistItemClickListener);

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelableArrayList(ARTIST_INFO_LIST_KEY, mArtistInfoList);
        savedState.putInt(ARTIST_POSITION_KEY, mPosition);
    }

    public void searchForArtists(String artist) {
        mAdapter.clear();
        if (Utilities.NetworkAvailable(getActivity())) {
            ArtistSearchTask artistSearchTask = new ArtistSearchTask();
            artistSearchTask.execute(artist);
        } else {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    public interface Callback {
        void onArtistItemSelected(ArtistInfo artistInfo);
    }

    public class ArtistSearchTask extends AsyncTask<String, Void, ArtistsPager> {

        private final String LOG_TAG = ArtistSearchTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressBarArtist.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArtistsPager doInBackground(String... params) {

            if (params.length == 0 || "".equals(params[0])) return null;

            String inputArtist = params[0];

            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();

            try{
                 return spotifyService.searchArtists(inputArtist);
            }
            catch(Exception err){
                Log.e(LOG_TAG, "Unknown Error: " + err.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistPagerResults) {
            progressBarArtist.setVisibility(View.GONE);
            if (artistPagerResults == null) return;

            if (artistPagerResults.artists.total < 1){
                Toast.makeText(getActivity(), "No Artists found.. Perhaps a different search?", Toast.LENGTH_SHORT).show();
                return;
            }

            for(Artist artist : artistPagerResults.artists.items) {
                mArtistInfoList.add(new ArtistInfo(artist));
            }

            mAdapter.notifyDataSetChanged();
        }
    }

}
