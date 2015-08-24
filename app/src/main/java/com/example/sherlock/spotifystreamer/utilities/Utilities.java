package com.example.sherlock.spotifystreamer.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.example.sherlock.spotifystreamer.R;

/**
 * Created by Jon on 6/25/15.
 */
public class Utilities {

    public static boolean NetworkAvailable(Activity context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getCountry(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String countryKey = context.getString(R.string.preferred_country_key);
    String defaultCountry = context.getString(R.string.preferred_country_default);
    return prefs.getString(countryKey, defaultCountry);
    }

    public static void sendBroadcast(Context context, String string) {
        Intent intent = new Intent(string);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
