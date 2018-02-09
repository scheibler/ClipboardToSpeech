package de.eric_scheibler.clipboardtospeech.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;


public class SettingsManager {

	// static settings
    public static final boolean DEFAULT_CLIPBOARD_MONITOR_SERVICE_ENABLED = true;
    public static final int MAX_NUMBER_OF_SEARCH_TERM_HISTORY_ENTRIES = 30;

	// class variables
	private static SettingsManager settingsManagerInstance;
	private Context context;
    private ApplicationInstance applicationInstance;
	private SharedPreferences settings;

    private ClipboardEntryHistory clipboardEntryHistory;

    public static SettingsManager getInstance(Context context) {
        if (settingsManagerInstance == null) {
            settingsManagerInstance = new SettingsManager(
                    context.getApplicationContext());
        }
        return settingsManagerInstance;
    }

	private SettingsManager(Context context) {
		this.context = context;
        this.applicationInstance = (ApplicationInstance) context.getApplicationContext();
		this.settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

    public String getApplicationVersion() {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    public boolean getClipboardMonitorServiceEnabled() {
        return settings.getBoolean(
                Constants.SETTINGS_KEY.CLIPBOARD_MONITOR_SERVICE_ENABLED,
                DEFAULT_CLIPBOARD_MONITOR_SERVICE_ENABLED);
    }

    public void setClipboardMonitorServiceEnabled(boolean enabled) {
        Editor editor = settings.edit();
        editor.putBoolean(
                Constants.SETTINGS_KEY.CLIPBOARD_MONITOR_SERVICE_ENABLED, enabled);
        editor.apply();
        applicationInstance.updateServiceNotification();
    }


    /**
     * search term history
     */

	public ClipboardEntryHistory getClipboardEntryHistory() {
        if (clipboardEntryHistory == null) {
            JSONArray jsonClipboardEntryList;
            try {
    		    jsonClipboardEntryList = new JSONArray(settings.getString(
                        Constants.SETTINGS_KEY.CLIPBOARD_ENTRY_LIST, "[]"));
    		} catch (JSONException e) {
                jsonClipboardEntryList = new JSONArray();
            }
            clipboardEntryHistory = new ClipboardEntryHistory(jsonClipboardEntryList);
        }
		return clipboardEntryHistory;
	}


    public class ClipboardEntryHistory {

        private ArrayList<String> clipboardEntryList;

        public ClipboardEntryHistory(JSONArray jsonClipboardEntryList) {
            clipboardEntryList = new ArrayList<String>();
            for(int i=0; i<jsonClipboardEntryList.length(); i++){
                try {
                    clipboardEntryList.add(jsonClipboardEntryList.getString(i));
                } catch (JSONException e) {}
            }
        }

        public ArrayList<String> getClipboardEntryList() {
            return this.clipboardEntryList;
        }

        public void clearClipboardEntryList() {
            clipboardEntryList = new ArrayList<String>();
            storeClipboardEntryHistory();
        }

        public void addClipboardEntry(String clipboardEntry) {
            this.removeFromList(clipboardEntry);
            // add at position 0
            this.clipboardEntryList.add(0, clipboardEntry);
            // 
            if (this.clipboardEntryList.size() > MAX_NUMBER_OF_SEARCH_TERM_HISTORY_ENTRIES) {
                clipboardEntryList.remove(clipboardEntryList.size() - 1);
            }
            // store to disk
            storeClipboardEntryHistory();
        }

        public void removeClipboardEntry(String clipboardEntry) {
            this.removeFromList(clipboardEntry);
            storeClipboardEntryHistory();
        }

        private boolean removeFromList(String item) {
            boolean removed = false;
            Iterator<String> it = this.clipboardEntryList.iterator();
            while (it.hasNext()) {
                if (it.next().equals(item)) {
                    it.remove();
                    removed = true;
                }
            }
            return removed;
        }

        private void storeClipboardEntryHistory() {
            JSONArray jsonClipboardEntryList = new JSONArray();
            for (String clipboardEntry : this.clipboardEntryList) {
                jsonClipboardEntryList.put(clipboardEntry);
            }
    		// save settings
	    	Editor editor = settings.edit();
		    editor.putString(
                    Constants.SETTINGS_KEY.CLIPBOARD_ENTRY_LIST,
                    jsonClipboardEntryList.toString());
    		editor.apply();
            // null clipboardEntryHistory object to force reload on next getClipboardEntryHistory()
            clipboardEntryHistory = null;
        }
    }

}
