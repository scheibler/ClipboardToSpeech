ClipboardToSpeech
=================

This Android application:

- Monitors the clipboard and speaks new content immediately
- Uses the default tts engine for speech output
- Contains a clipboard history with the last 30 entries
- Autostarts at boot time
- Creates a low-priority notification when the background service is running
- Requires at least Android version 4.1 and is compatible to 8.0 and 8.1

Install pre-compiled version with:

```
adb install app/src/main/misc/releases/ClipboardToSpeech_vx.x.x.apk
```

Or build manually:

```
./gradlew build
```

