/*
 * Copyright (C) 2023-2024 the MistOS Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rising.settings.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class LockscreenGifSettings extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener {
    
    private static final String KEY_ENABLE_GIF = "lockscreen_gif_enable";
    private static final String KEY_SELECT_GIF = "lockscreen_gif_select";
    private static final int REQUEST_SELECT_GIF = 1001;
    
    private SwitchPreference mEnableGif;
    private Preference mSelectGif;
    
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.lockscreen_gif_settings);
        
        mEnableGif = findPreference(KEY_ENABLE_GIF);
        mSelectGif = findPreference(KEY_SELECT_GIF);
        
        mEnableGif.setOnPreferenceChangeListener(this);
        mSelectGif.setOnPreferenceClickListener(preference -> {
            launchGifPicker();
            return true;
        });
        
        updatePreferences();
    }
    
    private void launchGifPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/gif");
        startActivityForResult(intent, REQUEST_SELECT_GIF);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_GIF && resultCode == Activity.RESULT_OK) {
            Uri selectedGif = data.getData();
            Settings.Secure.putString(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_GIF_PATH,
                    selectedGif.toString());
            updatePreferences();
        }
    }
    
    private void updatePreferences() {
        boolean enabled = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.LOCKSCREEN_GIF_ENABLED, 0) == 1;
        String gifPath = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCKSCREEN_GIF_PATH);
                
        mEnableGif.setChecked(enabled);
        mSelectGif.setEnabled(enabled);
        
        if (gifPath != null) {
            mSelectGif.setSummary(R.string.lockscreen_gif_selected);
        } else {
            mSelectGif.setSummary(R.string.lockscreen_gif_select_summary);
        }
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableGif) {
            boolean enabled = (Boolean) newValue;
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.LOCKSCREEN_GIF_ENABLED,
                    enabled ? 1 : 0);
            updatePreferences();
            return true;
        }
        return false;
    }
}
