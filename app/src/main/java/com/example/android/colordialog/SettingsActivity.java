package com.example.android.colordialog;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android.colordialog.dialog.ColorDialog;
import com.example.android.colordialog.dialog.ColorPreference;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
     }



    public static class SettingsFragment extends PreferenceFragment {

        Preference color_pref;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            color_pref = preferenceScreen.findPreference("color_pref");

        }
    }
}
