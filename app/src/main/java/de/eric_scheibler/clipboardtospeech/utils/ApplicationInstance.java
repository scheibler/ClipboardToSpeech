package de.eric_scheibler.clipboardtospeech.utils;

import android.annotation.TargetApi;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.content.Intent;

import android.os.Build;

import android.support.v4.content.ContextCompat;

import de.eric_scheibler.clipboardtospeech.R;


public class ApplicationInstance extends Application {

    @Override public void onCreate() {
        super.onCreate();
        // set notification channel for android 8
        createNotificationChannel();
        // settings manager instance
        SettingsManager settingsManagerInstance = SettingsManager.getInstance(this);
        // update notification
        if (settingsManagerInstance.getClipboardMonitorServiceEnabled()) {
            updateServiceNotification();
        }
    }


    /**
     * notification
     */

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    Constants.ID.NOTIFICATION_CHANNEL_ID,
                    getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setShowBadge(true);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(notificationChannel);
        }
    }

    public void updateServiceNotification() {
        Intent updateServiceNotificationIntent = new Intent(this, ClipboardMonitorService.class);
        updateServiceNotificationIntent.setAction(Constants.CustomAction.UPDATE_SERVICE_NOTIFICATION);
        ContextCompat.startForegroundService(this, updateServiceNotificationIntent);
    }

}
