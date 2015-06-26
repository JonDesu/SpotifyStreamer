package com.example.sherlock.spotifystreamer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.fragment.TopTracksFragment;


public class TopTracksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_toptracks, new TopTracksFragment())
                    .commit();
        }
    }

}
