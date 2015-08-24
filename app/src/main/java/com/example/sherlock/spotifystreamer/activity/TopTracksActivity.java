package com.example.sherlock.spotifystreamer.activity;

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
import com.example.sherlock.spotifystreamer.fragment.PlayFragment;
import com.example.sherlock.spotifystreamer.fragment.TopTracksFragment;
import com.example.sherlock.spotifystreamer.services.Service;


public class TopTracksActivity extends AppCompatActivity {

    private Service mMusicService;
    private boolean mIsServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable(TopTracksFragment.TRACK_ARTIST_ITEM_KEY,
                    getIntent().getParcelableExtra(TopTracksFragment.TRACK_ARTIST_ITEM_KEY));

            TopTracksFragment trackFragment = new TopTracksFragment();
            trackFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.track_container_large, trackFragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Binding the service
        Intent intent = new Intent(this, Service.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbinding the service
        unbindService(mServiceConnection);
    }

    /**
     * Setting the Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
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

    /**
     * Setting the MusicService Binding
     */
    private ServiceConnection mServiceConnection = new ServiceConnection(){
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
