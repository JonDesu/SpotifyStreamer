package com.example.sherlock.spotifystreamer;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
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



        ArrayList<ArtistInfo> artistInfoList = new ArrayList<ArtistInfo>();

        mAdapter = new ArtistInfoAdapter(this.getActivity(),R.layout.list_item_artitsts, artistInfoList);

        //Reference to listview, and attach adapter
        ListView listView = (ListView) rootView.findViewById(R.id.artistResultListView);
        listView.setAdapter(mAdapter);

        return rootView;
    }

    public class ArtistInfo {
        public String iconUrl;
        public String title;
        public ArtistInfo(){
            super();
        }

        public ArtistInfo(String iconUrl, String title) {
            super();
            this.iconUrl = iconUrl;
            this.title = title;
        }
    }

    public class ArtistInfoAdapter extends ArrayAdapter<ArtistInfo> {

        Context context;
        int layoutResId;
        ArrayList<ArtistInfo> artistList;

        public ArtistInfoAdapter(Context context, int layoutResId,  ArrayList<ArtistInfo> artistList) {
            super(context, layoutResId, artistList);
            this.layoutResId = layoutResId;
            this.context = context;
            this.artistList = artistList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ArtistInfoHolder holder = null;

            if(convertView == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(layoutResId, parent, false);

                holder = new ArtistInfoHolder();
                holder.imageIcon = (ImageView)convertView.findViewById(R.id.list_item_artists_imageview);
                holder.textName = (TextView)convertView.findViewById(R.id.list_item_artists_textview);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ArtistInfoHolder) convertView.getTag();
            }

            ArtistInfo artistInfo = artistList.get(position);
            holder.textName.setText(artistInfo.title);
            Picasso.with(this.context).load(artistInfo.iconUrl).into(holder.imageIcon);


            return convertView;
        }

        public class ArtistInfoHolder
        {
            ImageView imageIcon;
            TextView textName;
        }
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

            List<Artist> artistList = artistPagerResults.artists.items;

            if (artistPagerResults.artists.total > 0) {

                mAdapter.clear();
                for (int i = 0; i < artistList.size(); i++) {

                    String artistImageUrl = "http://cdn.embed.ly/providers/logos/spotify.png";
                    String artistTextName = null;

                    try{
                        artistImageUrl = artistList.get(i).images.get(0).url;
                        artistTextName = artistList.get(i).name;
                    }
                    catch(Exception err){
                        Log.e(LOG_TAG, "Error : " + err);
                        continue;
                    }
                    ArtistInfo currArtistInfo = new ArtistInfo(artistImageUrl,artistTextName);
                    mAdapter.add(currArtistInfo);
                }
                mAdapter.notifyDataSetChanged();
            }

        }
    }
}
