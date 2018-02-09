package de.eric_scheibler.clipboardtospeech.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.support.v4.content.ContextCompat;


public class BootCompletedReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                && SettingsManager.getInstance(context).getClipboardMonitorServiceEnabled()) {
            Intent updateServiceNotificationIntent = new Intent(context, ClipboardMonitorService.class);
            updateServiceNotificationIntent.setAction(Constants.CustomAction.UPDATE_SERVICE_NOTIFICATION);
            ContextCompat.startForegroundService(context, updateServiceNotificationIntent);
        }
    }

}
