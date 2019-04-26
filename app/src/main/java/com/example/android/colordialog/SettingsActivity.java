package com.example.android.colordialog;

import android.annotation.TargetApi;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.colordialog.dialog.ColorDialog;
import com.example.android.colordialog.dialog.ColorPreference;
import com.example.android.colordialog.dialog.ColorShape;
import com.example.android.colordialog.dialog.ColorUtils;


public class SettingsActivity extends AppCompatPreferenceActivity implements ColorDialog.OnColorSelectedListener, DialogClosed{

    private static Context context;
    public static final String COLOR_PREFERENCES = "ColorPreferences";
    static PreferenceScreen preferenceScreen;
    private final String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsActivity.context = this;
        ColorDialog.setOnDialogClosedListener(this);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();

    }

    public static Context getMyContext(){
        return context;
    }

    public static class SettingsFragment extends PreferenceFragment{

        ColorPreference color_pref;
        SharedPreferences sharedPreferences;
        private static ColorDialog colorDialog;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferenceScreen = getPreferenceScreen();
            color_pref = (ColorPreference) preferenceScreen.findPreference("color_pref");
            colorDialog = ColorDialog.getColorDialog();

        }

    }


    @TargetApi(23)
    public void onDialogClosed(ColorDialog colorDialog, String TAG) {
        Toast.makeText(this, "Hello idk", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        int color = sharedPreferences.getInt("selectedColor", 0);
        Log.v(TAG, "Color " + String.valueOf(color));
    }

    @Override
    @TargetApi(23)
    public void onColorSelected(int newColor, String tag) {
        SharedPreferences sharedPreferences = getSharedPreferences(COLOR_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String hexColor = String.format("#%06X", (0xFFFFFF & newColor));
        hexColor = hexColor.replace("#", "");
        Log.v(TAG, "Hex Colour: " + hexColor);

        int regularColor = Integer.valueOf(hexColor, 16);

        editor.putInt("selectedColor", regularColor);
        editor.commit();
        Log.v(TAG, "Color Int: " + String.valueOf(regularColor));


        int colour = sharedPreferences.getInt("selectedColor", 8);
        Log.v(TAG, "Color from sharedPreferences: " + colour);

        ColorDialog dialog = ColorDialog.getColorDialog();
        dialog.repopulateItems();


    }




}
