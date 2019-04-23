package com.example.android.colordialog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.android.colordialog.dialog.ColorDialog;
import com.example.android.colordialog.dialog.ColorPreference;
import com.example.android.colordialog.dialog.ColorShape;

public class SettingsActivity extends AppCompatPreferenceActivity implements ColorDialog.OnColorSelectedListener{

    private static Context context;
    public static final String COLOR_PREFERENCES = "ColorPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsActivity.context = this;

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();

    }

    public static Context getMyContext(){
        return context;
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

    @Override
    public void onColorSelected(int newColor, String tag) {
        SharedPreferences sharedPreferences = getSharedPreferences(COLOR_PREFERENCES,MODE_PRIVATE);
    }

}
