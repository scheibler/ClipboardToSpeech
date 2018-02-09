package de.eric_scheibler.clipboardtospeech.utils;

import de.eric_scheibler.clipboardtospeech.BuildConfig;


public class Constants {

    public interface ID {
        public static final int NOTIFICATION_ID = 91223;
        public static final String NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID;
    }

    public interface SETTINGS_KEY {
        public static final String CLIPBOARD_MONITOR_SERVICE_ENABLED = "enableService";
        public static final String CLIPBOARD_ENTRY_LIST = "clipboardEntryList";
    }

    public interface CustomAction {
        public static final String RELOAD_UI = "de.eric_scheibler.clipboardtospeech.customAction.reloadui";
        public static final String UPDATE_SERVICE_NOTIFICATION = "de.eric_scheibler.clipboardtospeech.customAction.updateservicenotification";
    }

}
