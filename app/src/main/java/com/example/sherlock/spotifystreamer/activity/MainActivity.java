package com.example.sherlock.spotifystreamer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.fragment.MainActivityFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_main, new MainActivityFragment())
                    .commit();
        }
    }
}
