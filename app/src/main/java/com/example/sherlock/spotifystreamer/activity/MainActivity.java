package com.example.sherlock.spotifystreamer.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.fragment.MainActivityFragment;
import com.example.sherlock.spotifystreamer.fragment.PlayFragment;
import com.example.sherlock.spotifystreamer.fragment.TopTracksFragment;
import com.example.sherlock.spotifystreamer.model.ArtistInfo;
import com.example.sherlock.spotifystreamer.services.Service;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private static final String TRACK_FRAGMENT_TAG = "TRACK_FRAGMENT_TAG";

    private boolean mTwoPanesActive;
    private MainActivityFragment mainActivityFragment;
    private Service mMusicService;
    private boolean mIsServiceBound = false;
    private Toast mToast;

    private static final String ARTIST_SEARCHED_KEY = "ARTIST_SEARCHED";

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String intentExtra = intent.getStringExtra(SearchManager.QUERY);
            mainActivityFragment.searchForArtists(intentExtra);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Is the app running on a tablet?
        mTwoPanesActive = findViewById(R.id.main_container_large) != null;

        // In two-pane mode, show the detail view in this activity by adding or replacing
        // the detail fragment using a fragment transaction
        if (mTwoPanesActive) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.track_container_large, new TopTracksFragment(), TRACK_FRAGMENT_TAG)
                        .commit();
            }
        }

        // Get the main fragment of this activity
        mainActivityFragment =
                (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

        // Handling search intent
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_now_playing){
            if (mIsServiceBound && !mMusicService.isEmpty()) {
                DialogFragment playFragment = new PlayFragment();
                playFragment.show(getSupportFragmentManager(), "dialog");
            } else {
                Toast.makeText(this, "Please select a track...", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Binding the service
        Intent intent = new Intent(this, Service.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Managing the artist click
     */
    @Override
    public void onArtistItemSelected(ArtistInfo artistInfo) {
        // In single-pane mode, add the trackFragment to the container
        if (mTwoPanesActive) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(TopTracksFragment.TRACK_ARTIST_ITEM_KEY, artistInfo);

            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.track_container_large, fragment, TRACK_FRAGMENT_TAG)
                    .commit();
            // In two-pane mode, start TrackActivity
        } else {
            Intent intent = new Intent(this, TopTracksActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(TopTracksFragment.TRACK_ARTIST_ITEM_KEY, artistInfo);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    /**
     * Setting the MusicService Binding
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Saving an istance of the binded service
            Service.MusicBinder binder = (Service.MusicBinder) service;
            mMusicService = binder.getService();
            mIsServiceBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServiceBound = false;
        }
    };

    public Service getMusicService() {
        return mMusicService;
    }

    public boolean isServiceBound() {
        return mIsServiceBound;
    }
}
