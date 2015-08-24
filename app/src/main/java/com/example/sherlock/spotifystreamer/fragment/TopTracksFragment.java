package com.example.sherlock.spotifystreamer.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.activity.MainActivity;
import com.example.sherlock.spotifystreamer.activity.TopTracksActivity;
import com.example.sherlock.spotifystreamer.adapter.TrackInfoAdapter;
import com.example.sherlock.spotifystreamer.model.ArtistInfo;
import com.example.sherlock.spotifystreamer.model.TrackInfo;
import com.example.sherlock.spotifystreamer.utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    private final String LOG_TAG = TrackSearchTask.class.getSimpleName();
    private String mArtistID;
    private String mArtistName;
    private TrackInfoAdapter mTrackInfoAdapter;
    private ArrayList<TrackInfo> mTrackInfoList;
    private boolean mSavedInstanceFlag = false;
    private int mPosition;

    public static final String TRACK_ARTIST_ITEM_KEY = "TRACK_ARTIST_ITEM_KEY";
    private static final String TRACK_INFO_LIST_KEY = "TRACK_INFO_LIST_KEY";
    private static final String TRACK_POSITION_KEY = "TRACK_POSITION_KEY";

    @InjectView(R.id.progress_bar_track)ProgressBar progressBarTrack;
    @InjectView(R.id.listview_tracks) ListView listView;

    // If the activity has been re-created get the list back from saveInstanceState
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restoring the ListView (if the activity has been re-created)
        if (savedInstanceState == null || !savedInstanceState.containsKey(TRACK_INFO_LIST_KEY)) {
            mTrackInfoList = new ArrayList<TrackInfo>();
        } else {
            mTrackInfoList = savedInstanceState.getParcelableArrayList(TRACK_INFO_LIST_KEY);
            mSavedInstanceFlag = true;
        }
        // Restoring the selected position (if the activity has been re-created)
        if (savedInstanceState == null || !savedInstanceState.containsKey(TRACK_POSITION_KEY)) {
            mPosition = ListView.INVALID_POSITION;
        } else {
            mPosition = savedInstanceState.getInt(TRACK_POSITION_KEY);
            mSavedInstanceFlag = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!Utilities.NetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        // Getting intent bundle
        Bundle args = getArguments();
        if (args != null) {
            ArtistInfo artistInfo = args.getParcelable(TRACK_ARTIST_ITEM_KEY);
            mArtistID = artistInfo.getId();
            mArtistName = artistInfo.getName();
        }

        // Inflating the layout
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        ButterKnife.inject(this, rootView);

        // Setting the list adapter
        mTrackInfoAdapter = new TrackInfoAdapter(
                getActivity(),
                R.layout.list_item_tracks,
                mTrackInfoList);

        //Reference to listview, and attach adapter
        listView.setAdapter(mTrackInfoAdapter);
        listView.smoothScrollToPosition(mPosition);
        listView.setOnItemClickListener(onTrackItemClickListener);

        return rootView;
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setting the titlebar subtitle with the mArtistName
        // From: http://stackoverflow.com/a/18320838/4836602
        // AppCompatActivity is used instead of ActionBarActivity
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(mArtistName);
        if (mArtistID != null && !mSavedInstanceFlag) {
            TrackSearchTask trackSearchTask = new TrackSearchTask();
            trackSearchTask.execute(mArtistID);
        }
    }

    // Saving the current parcelable item list
    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelableArrayList(TRACK_INFO_LIST_KEY, mTrackInfoList);
        savedState.putInt(TRACK_POSITION_KEY, mPosition);
        super.onSaveInstanceState(savedState);
    }

    private AdapterView.OnItemClickListener onTrackItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            mPosition = position;
            boolean tabletModeActive = getActivity().findViewById(R.id.main_container_large) != null;

            // In two-pane mode, we are in the MainActivity activity
            if (tabletModeActive) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity.isServiceBound()) {
                    mainActivity.getMusicService().setTrackItemList(mTrackInfoList);
                    mainActivity.getMusicService().setTrackPosition(mPosition);
                    mainActivity.getMusicService().playTrack();
                }
                // In single-pane mode, we are in the TrackActivity activity
            } else {
                TopTracksActivity topTracksActivity = (TopTracksActivity) getActivity();
                if (topTracksActivity.isServiceBound()) {
                    topTracksActivity.getMusicService().setTrackItemList(mTrackInfoList);
                    topTracksActivity.getMusicService().setTrackPosition(mPosition);
                    topTracksActivity.getMusicService().playTrack();
                }
            }

            // Show the now-playing fragment
            DialogFragment playFragment = new com.example.sherlock.spotifystreamer.fragment.PlayFragment();
            playFragment.show(getActivity().getSupportFragmentManager(), "dialog");
        }
    };

    public class TrackSearchTask extends AsyncTask<String, Void, Tracks> {

        private final String LOG_TAG = TrackSearchTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            // Clearing the list, showing the progress bar and hiding the msg
            mTrackInfoAdapter.clear();
            progressBarTrack.setVisibility(View.VISIBLE);
        }

        @Override
        protected Tracks doInBackground(String... params) {

            // If no valid input is available...
            if (params.length == 0 || "".equals(params[0])) {
                return null;
            }

            String track = params[0];

            // Starting a spotify web endpoint req
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            try {
                // Setting the country with the user selected country from the settings menu
                Map<String, Object> options = new HashMap<>();
                options.put("country", Utilities.getCountry(getActivity()));
                return spotify.getArtistTopTrack(track, options);
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                Log.e(LOG_TAG, spotifyError.getErrorDetails().message);
                spotifyError.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Tracks tracksResults) {
            // Hiding the progressbar
            progressBarTrack.setVisibility(View.GONE);

            //nothing to do here
            if (tracksResults == null) return;

            if (tracksResults.tracks.size() == 0){
                Toast.makeText(getActivity(), "No tracks listed for this artist...", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            for (Track track : tracksResults.tracks) {
                mTrackInfoList.add(new TrackInfo(track));
            }
            mTrackInfoAdapter.notifyDataSetChanged();
        }
    }

}
