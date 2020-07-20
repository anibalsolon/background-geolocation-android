/*
According to apache license

This is fork of christocracy cordova-plugin-background-geolocation plugin
https://github.com/christocracy/cordova-plugin-background-geolocation

This is a new class
*/

package com.marianhello.bgloc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.marianhello.bgloc.data.ConfigurationDAO;
import com.marianhello.bgloc.data.DAOFactory;
import com.marianhello.bgloc.service.LocationServiceImpl;
import com.marianhello.bgloc.service.LocationServiceIntentBuilder;

import org.json.JSONException;

/**
 * BootCompletedReceiver class
 */


public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getName();
    private static final String KEY_COMMAND = "cmd";

    @Override
     public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received boot completed");
        ConfigurationDAO dao = DAOFactory.createConfigurationDAO(context);
        Config config = null;

        try {
            config = dao.retrieveConfiguration();
        } catch (JSONException e) {
            //noop
        }

        if (config == null) { return; }

        Log.d(TAG, "Boot completed " + config.toString());


        if (config.getStartOnBoot()) {
            Log.i(TAG, "Starting service after boot");
            Intent locationServiceIntent = new Intent(context, LocationServiceImpl.class);
            locationServiceIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
            LocationServiceIntentBuilder.Command cmd = new LocationServiceIntentBuilder.Command(0);
            locationServiceIntent.putExtra(KEY_COMMAND, cmd.toBundle());
            locationServiceIntent.putExtra("config", config);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(locationServiceIntent);
            } else {
                Toast.makeText(context, "Covid-Safe-Paths boot completed broadcast receiver ", Toast.LENGTH_LONG).show();
                context.startService(locationServiceIntent);
            }
        }
     }
}
