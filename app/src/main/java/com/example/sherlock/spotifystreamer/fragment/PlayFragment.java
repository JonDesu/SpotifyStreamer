package com.example.sherlock.spotifystreamer.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.services.Service;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * PlayFragment
 * Shows and manages the playback of the tracks.
 */
public class PlayFragment extends DialogFragment {

    private Service mService;
    private Handler mSeekbarHandler = null;
    private boolean mIsServiceBound = false;

    @InjectView(R.id.play_image_view) ImageView imageView;
    @InjectView(R.id.play_tview_message) TextView tviewArtist;
    @InjectView(R.id.play_tview_album) TextView tviewAlbum;
    @InjectView(R.id.play_tview_track) TextView tviewTrack;
    @InjectView(R.id.play_tview_current_time) TextView tviewCurrentTime;
    @InjectView(R.id.textview_time_max) TextView tviewMaxTime;
    @InjectView(R.id.play_button) Button playButton;
    @InjectView(R.id.progressbar) ProgressBar progressBar;
    @InjectView(R.id.play_seekbar) SeekBar seekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating the layout and initializing ButterKnife
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    // Removing the fragmentDialog title
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @OnClick(R.id.next_button)
    public void buttonNextClicked() {
        sendActionToService(Service.INTENT_ACTION_NEXT);
    }

    @OnClick(R.id.prev_button)
    public void buttonPrevClicked() {
        sendActionToService(Service.INTENT_ACTION_PREV);
    }

    @OnClick(R.id.play_button)
    public void buttonPlayClicked() {
        if (mService.isPlaying()) {
            sendActionToService(Service.INTENT_ACTION_PAUSE);
        } else {
            sendActionToService(Service.INTENT_ACTION_PLAY);
        }
    }

    @OnClick(R.id.stop_button)
    public void buttonStopClicked() {
        if (mIsServiceBound && mService.isPrepared()) {
            sendActionToService(Service.INTENT_ACTION_STOP);
            Dialog dialog = getDialog();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public void sendActionToService (String action) {
        Intent intent = new Intent(getActivity(), Service.class);
        intent.setAction(action);
        getActivity().startService(intent);
    }

    public void updateView () {
        // Setting the track info
        tviewArtist.setText(mService.getCurrentTrack().getArtistName());
        tviewAlbum.setText(mService.getCurrentTrack().getAlbumName());
        tviewTrack.setText(mService.getCurrentTrack().getTrackTitle());
        if (imageView != null) {
            Picasso.with(getActivity())
                    .load(mService.getCurrentTrack().getIconUrl())
                    .fit().centerCrop()
                    .into(imageView);
        }
        // Setting the play/pause button
        if (mIsServiceBound && mService.isPlaying()) {
            playButton.setBackgroundResource(R.drawable.ic_action_playback_play);
        } else {
            playButton.setBackgroundResource(R.drawable.ic_action_playback_pause);
        }
        // Setting the seekBar and progressBar
        if (mIsServiceBound && mService.isPrepared()) {
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            if (mSeekbarHandler == null) {
                setSeekBar();
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            if (mSeekbarHandler != null) {
                mSeekbarHandler.removeCallbacks(seekBarRunnable);
            }
            mSeekbarHandler = null;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Binding the service
        Intent intent = new Intent(getActivity(), Service.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        // Setting the width of the dialog programmatically
        boolean hasTwoPanes = getActivity().findViewById(R.id.main_container_large) != null;
        if (!hasTwoPanes) {
            Dialog dialog = getDialog();
            if (dialog != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(mBroadcastReceiver,
                        new IntentFilter(Service.BROADCAST_PLAYBACK_STATE_CHANGED));
    }

    @Override
    public void onPause(){
        super.onPause();
        // Unregistering the broadcast receiver
        // Using a try & catch, hint by:
        // http://stackoverflow.com/questions/6165070/receiver-not-registered-exception-error
        try {
            getActivity().unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        } catch (Exception e) {
            Log.e("unregisterReceiver", "Receiver not registered");
        }
        // Removing the runnable callback from the seekbar handler
        if (mSeekbarHandler != null) {
            mSeekbarHandler.removeCallbacks(seekBarRunnable);
        }
        mSeekbarHandler = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbinding the service
        getActivity().unbindService(mServiceConnection);
    }

    // Fix for a bug in onDestroyView of a FragmenDialog
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    /**
     * Setting the Service Binding
     */
    private ServiceConnection mServiceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Saving an istance of the binded service
            Service.MusicBinder binder = (Service.MusicBinder) service;
            mService = binder.getService();
            mIsServiceBound = true;
            updateView();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServiceBound = false;
            getActivity().unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
            Dialog dialog = getDialog();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    };

    /**
     * Setting the broadcast receiver to intercept broadcast msg from Service
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            updateView();
        }
    };

    /**
     * Management of the seekbar
     */
    private void setSeekBar() {
        seekBar.setMax(30);
        tviewMaxTime.setText("00:30");

        mSeekbarHandler = new Handler();
        if (getActivity() != null) {
            getActivity().runOnUiThread(seekBarRunnable);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mService != null && fromUser && mIsServiceBound) {
                    mService.seekTo(progress * 1000);
                }
            }
        });
    }

    Runnable seekBarRunnable = new Runnable() {

        @Override
        public void run() {
            if (mIsServiceBound && mService.isPlaying() &&
                    mService.getCurrentPosition() < mService.getDuration()) {
                int time = mService.getCurrentPosition() / 1000;
                seekBar.setProgress(time);
                tviewCurrentTime.setText("00:" + String.format("%02d", time));
            }
            mSeekbarHandler.postDelayed(this, 1000);
        }
    };

}
