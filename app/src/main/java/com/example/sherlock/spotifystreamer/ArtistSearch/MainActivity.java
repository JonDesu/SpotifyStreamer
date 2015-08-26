package com.example.sherlock.spotifystreamer.ArtistSearch;

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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.sherlock.spotifystreamer.Player.PlayFragment;
import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.Settings.SettingsActivity;
import com.example.sherlock.spotifystreamer.TopTracks.TopTracksActivity;
import com.example.sherlock.spotifystreamer.TopTracks.TopTracksFragment;



public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private static final String TRACK_FRAGMENT_TAG = "TRACK_FRAGMENT_TAG";

    private boolean mTabletModeActive;
    private boolean mIsServiceBound = false;
    private MainActivityFragment mainActivityFragment;
    private com.example.sherlock.spotifystreamer.Services.MusicService mMusicService;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            com.example.sherlock.spotifystreamer.Services.MusicService.MusicBinder binder = (com.example.sherlock.spotifystreamer.Services.MusicService.MusicBinder) service;
            mMusicService = binder.getService();
            mIsServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServiceBound = false;
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        receiveIntent(intent);
    }

    private void receiveIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String intentExtra = intent.getStringExtra(SearchManager.QUERY);
            mainActivityFragment.searchForArtists(intentExtra);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabletModeActive = findViewById(R.id.main_container_large) != null;

        // In two-pane mode, show the detail view in this activity by adding or replacing
        // the detail fragment using a fragment transaction
        if (mTabletModeActive) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.track_container_large, new TopTracksFragment(), TRACK_FRAGMENT_TAG)
                        .commit();
            }
        }

        mainActivityFragment =
                (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

        receiveIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_now_playing) {
            if (mIsServiceBound && !mMusicService.isEmpty()) {

                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                DialogFragment playFragment = new PlayFragment();
                playFragment.show(getSupportFragmentManager(), "dialog");
            } else {
                Toast.makeText(this, "Please select a track...", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, com.example.sherlock.spotifystreamer.Services.MusicService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbinding the service
        unbindService(mServiceConnection);
    }

    @Override
    public void onArtistItemSelected(ArtistInfo artistInfo) {

        if (mTabletModeActive) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(TopTracksFragment.TRACK_ARTIST_ITEM_KEY, artistInfo);

            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.track_container_large, fragment, TRACK_FRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, TopTracksActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(TopTracksFragment.TRACK_ARTIST_ITEM_KEY, artistInfo);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public com.example.sherlock.spotifystreamer.Services.MusicService getMusicService() {
        return mMusicService;
    }

    public boolean isServiceBound() {
        return mIsServiceBound;
    }
}
