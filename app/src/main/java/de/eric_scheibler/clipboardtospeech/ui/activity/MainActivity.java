package de.eric_scheibler.clipboardtospeech.ui.activity;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.support.v7.widget.Toolbar;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import de.eric_scheibler.clipboardtospeech.R;
import de.eric_scheibler.clipboardtospeech.ui.activity.AbstractActivity;
import de.eric_scheibler.clipboardtospeech.utils.ClipboardMonitorService;


public class MainActivity extends AbstractActivity {

    private ClipboardManager clipboardManager;

    private Switch buttonEnableService;
    private ListView listViewClipboardHistory;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // service switch
        buttonEnableService = (Switch) findViewById(R.id.buttonEnableService);
        buttonEnableService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsManagerInstance.setClipboardMonitorServiceEnabled(isChecked);
            }
        });

        // clipboard history
        listViewClipboardHistory = (ListView) findViewById(R.id.listViewClipboardHistory);
        listViewClipboardHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                ClipData clip = ClipData.newPlainText(
                        null, (String) parent.getItemAtPosition(position));
                clipboardManager.setPrimaryClip(clip);
            }
        });
        listViewClipboardHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                settingsManagerInstance.getClipboardEntryHistory().removeClipboardEntry(
                        (String) parent.getItemAtPosition(position));
                updateUI();
                return true;
            }
        });

        Button buttonClearClipboardHistory = (Button) findViewById(R.id.buttonClearClipboardHistory);
        buttonClearClipboardHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                settingsManagerInstance.getClipboardEntryHistory().clearClipboardEntryList();
                updateUI();
            }
        });

        TextView labelListViewClipboardHistoryEmpty    = (TextView) findViewById(R.id.labelListViewClipboardHistoryEmpty);
        labelListViewClipboardHistoryEmpty.setText(getResources().getString(R.string.labelListViewClipboardHistoryEmpty));
        listViewClipboardHistory.setEmptyView(labelListViewClipboardHistoryEmpty);

        // start service
        Intent intent = new Intent(this, ClipboardMonitorService.class);
        startService(intent);
    }

    @Override public void onPause() {
        super.onPause();
    }

    @Override public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        buttonEnableService.setChecked(
                settingsManagerInstance.getClipboardMonitorServiceEnabled());
        listViewClipboardHistory.setAdapter(
                new ArrayAdapter<String>(
                    MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    settingsManagerInstance.getClipboardEntryHistory().getClipboardEntryList())
                );
    }

}
