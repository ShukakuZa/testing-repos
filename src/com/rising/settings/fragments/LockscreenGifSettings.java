package com.rising.settings.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class LockscreenGifSettings extends PreferenceFragmentCompat {
    private static final int PICK_GIF_REQUEST = 1;
    private SwitchPreference enableGif;
    private Preference selectGif;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.lockscreen_gif_settings);
        setupPreferences();
    }

    private void setupPreferences() {
        enableGif = findPreference("enable_gif");
        selectGif = findPreference("select_gif");

        enableGif.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean enabled = (Boolean) newValue;
            Settings.System.putInt(getContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_GIF_ENABLED, enabled ? 1 : 0);
            return true;
        });

        selectGif.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/gif");
            startActivityForResult(intent, PICK_GIF_REQUEST);
            return true;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_GIF_REQUEST && resultCode == Activity.RESULT_OK) {
            Settings.System.putString(getContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_GIF_PATH, data.getDataString());
        }
    }
}
