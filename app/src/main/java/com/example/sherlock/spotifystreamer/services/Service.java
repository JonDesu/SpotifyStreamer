package com.example.sherlock.spotifystreamer.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.sherlock.spotifystreamer.R;
import com.example.sherlock.spotifystreamer.activity.MainActivity;
import com.example.sherlock.spotifystreamer.model.TrackInfo;
import com.example.sherlock.spotifystreamer.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Service extends android.app.Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;
    private ArrayList<TrackInfo> mTrackItemList = null;
    private int mTrackPosition;
    private final IBinder mMusicBinder = new MusicBinder();
    private boolean mIsPrepared = false;

    private static final int NOTIFICATION_ID = 1;
    public static final String BROADCAST_PLAYBACK_STATE_CHANGED = "PLAYBACK_STATE_CHANGED";
    public static final String INTENT_ACTION_PLAY = "ACTION_PLAY";
    public static final String INTENT_ACTION_PAUSE = "ACTION_PAUSE";
    public static final String INTENT_ACTION_NEXT = "ACTION_NEXT";
    public static final String INTENT_ACTION_PREV = "ACTION_PREV";
    public static final String INTENT_ACTION_STOP = "ACTION_STOP";

    @Override
    public void onCreate(){
        super.onCreate();
        mTrackPosition = 0;
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            handleIntent(intent);
        }
        return START_STICKY;
    }

    private void handleIntent(Intent intent) {
        switch (intent.getAction()) {
            case INTENT_ACTION_PLAY:
                if (mIsPrepared) {
                    start();
                    buildNotification();
                }
                break;
            case INTENT_ACTION_PAUSE:
                if (mIsPrepared) {
                    pause();
                    buildNotification();
                }
                break;
            case INTENT_ACTION_PREV:
                playPrev();
                buildNotification();
                break;
            case INTENT_ACTION_NEXT:
                playNext();
                buildNotification();
                break;
            case INTENT_ACTION_STOP:
                stop();
                break;
        }
    }

    private void buildNotification() {

        // Building the notification settings
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.default_icon_spotify);
        builder.setContentTitle(getCurrentTrack().getTrackTitle());
        builder.setContentText(getCurrentTrack().getArtistName());
        builder.setLargeIcon(getNotificationIcon());
        builder.setStyle(new NotificationCompat.MediaStyle());

        // Setting the notification default intent (starting MainActivity)
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        // Adding the notification controls actions
        builder.addAction(createAction
                (R.drawable.ic_action_playback_prev, "Previous", INTENT_ACTION_PREV));
        if (isPlaying()) {
            builder.addAction(createAction
                    (R.drawable.ic_action_playback_pause, "Pause", INTENT_ACTION_PAUSE));
        } else {
            builder.addAction(createAction
                    (R.drawable.ic_action_playback_play, "Play", INTENT_ACTION_PLAY));
        }
        builder.addAction(createAction
                (R.drawable.ic_action_playback_next, "Next", INTENT_ACTION_NEXT));

        // Setting the notification delete intent
        Intent stopIntent = new Intent(getApplicationContext(), Service.class);
        stopIntent.setAction(INTENT_ACTION_STOP);
        PendingIntent deleteIntent = PendingIntent.
                getService(getApplicationContext(), 1, stopIntent, 0);
        builder.setDeleteIntent(deleteIntent);

        // Setting the notification manager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private Bitmap getNotificationIcon() {
        Bitmap bitmap = null;
        try {
            bitmap = new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        return Picasso.with(getApplicationContext())
                                .load(getCurrentTrack().getIconUrl())
                                .resize(200, 200)
                                .placeholder(R.drawable.default_icon_spotify)
                                .get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(bitmap == null) return BitmapFactory.decodeResource(getResources(), R.drawable.default_icon_spotify);

        return bitmap;
    }

    private android.support.v4.app.NotificationCompat.Action createAction (int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), Service.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent =
                PendingIntent.getService(getApplicationContext(), NOTIFICATION_ID, intent, 0);
        return new android.support.v4.app.NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    public void initMusicPlayer(){
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    /*
     * Binding settings
     */
    public class MusicBinder extends Binder {
        public Service getService() {
            return Service.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        return true;
    }

    public void playTrack(){
        mIsPrepared = false;
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(mTrackItemList.get(mTrackPosition).getTrackUrl());
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }

    /*
     * MediaPlayer implementation
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mIsPrepared = false;
        mp.reset();
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mIsPrepared = false;
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsPrepared = true;
        mp.start();
        buildNotification();
        Utilities.sendBroadcast(this, BROADCAST_PLAYBACK_STATE_CHANGED);
    }

    /**
     * Getters and setters.
     */
    public void setTrackItemList(ArrayList<TrackInfo> trackItemList) {
        mTrackItemList = trackItemList;
    }

    public void setTrackPosition(int trackPosition) {
        mTrackPosition = trackPosition;
    }

    public boolean isPrepared() {
        return mIsPrepared;
    }

    public boolean isEmpty() {
        return mTrackItemList == null;
    }

    public TrackInfo getCurrentTrack() {
        return mTrackItemList.get(mTrackPosition);
    }

    /**
     * Playback control methods.
     */
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void pause() {
        mMediaPlayer.pause();
        Utilities.sendBroadcast(this, BROADCAST_PLAYBACK_STATE_CHANGED);
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
        Utilities.sendBroadcast(this, BROADCAST_PLAYBACK_STATE_CHANGED);
    }

    public void start() {
        mMediaPlayer.start();
        Utilities.sendBroadcast(this, BROADCAST_PLAYBACK_STATE_CHANGED);
    }

    public void playPrev() {
        mTrackPosition--;
        if(mTrackPosition < 0) {
            mTrackPosition = mTrackItemList.size() - 1;
        }
        playTrack();
        Utilities.sendBroadcast(this, BROADCAST_PLAYBACK_STATE_CHANGED);
    }

    public void playNext() {
        mTrackPosition ++;
        if (mTrackPosition >= mTrackItemList.size()) {
            mTrackPosition = 0;
        }
        playTrack();
        Utilities.sendBroadcast(this, BROADCAST_PLAYBACK_STATE_CHANGED);
    }

    public void stop() {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        mMediaPlayer.pause();
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        super.onDestroy();
    }
}

