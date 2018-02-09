package de.eric_scheibler.clipboardtospeech.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;

import android.os.IBinder;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import de.eric_scheibler.clipboardtospeech.R;
import de.eric_scheibler.clipboardtospeech.ui.activity.MainActivity;
import de.eric_scheibler.clipboardtospeech.utils.SettingsManager;


public class ClipboardMonitorService extends Service {

    // service vars
    private ClipboardManager clipboardManager;
    private NotificationManager notificationManager;
    private ApplicationInstance applicationInstance;
    private BootCompletedReceiver bootCompletedReceiver;
    private SettingsManager settingsManagerInstance;
    private TTSWrapper ttsWrapperInstance;

    @Override public void onCreate() {
        super.onCreate();
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        applicationInstance = (ApplicationInstance) getApplicationContext();
        settingsManagerInstance = SettingsManager.getInstance(this);
        ttsWrapperInstance = TTSWrapper.getInstance(this);
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void onStart(Intent intent, int startId) {
        if (intent != null) {
            if (Constants.CustomAction.UPDATE_SERVICE_NOTIFICATION.equals(intent.getAction())) {
                startForeground(Constants.ID.NOTIFICATION_ID, getServiceNotification());
                if (! settingsManagerInstance.getClipboardMonitorServiceEnabled()) {
                    stopSelf();
                }
            }
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if (clipboardManager != null) { 
            clipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
        try {
            if (bootCompletedReceiver != null) {
                unregisterReceiver(bootCompletedReceiver);
            }
        } catch (IllegalArgumentException e) {}
        notificationManager.cancel(Constants.ID.NOTIFICATION_ID);
        stopForeground(true);
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override public void onPrimaryClipChanged() {
            if (clipboardManager.hasPrimaryClip()
                    && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                ClipData clip = clipboardManager.getPrimaryClip();
                String clipboardEntry = clip.getItemAt(0).getText().toString().trim();
                if (! clipboardEntry.equals("")) {
                    // speak
                    ttsWrapperInstance.speak(clipboardEntry, false, true);
                    // store to disk
                    settingsManagerInstance.getClipboardEntryHistory().addClipboardEntry(clipboardEntry);
                    // reload ui
                    Intent reloadUIIntent = new Intent(Constants.CustomAction.RELOAD_UI);
                    LocalBroadcastManager.getInstance(ClipboardMonitorService.this).sendBroadcast(reloadUIIntent);
                }
            }
        }
    };


    /**
     * notification
     */

    private Notification getServiceNotification() {
        // launch MainActivity intent
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // return notification
        return new NotificationCompat.Builder(this)
            .setChannelId(Constants.ID.NOTIFICATION_CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
            .setUsesChronometer(false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentText(
                    String.format(
                            getResources().getString(R.string.serviceNotification),
                            enabledOrDisabled(settingsManagerInstance.getClipboardMonitorServiceEnabled())))
            .build();
    }

    private String enabledOrDisabled(boolean enabled) {
        if (enabled) {
            return getResources().getString(R.string.dialogEnabled);
        }
        return getResources().getString(R.string.dialogDisabled);
    }

}
