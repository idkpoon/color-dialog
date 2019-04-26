package com.example.android.colordialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static class SettingsFragment extends PreferenceFragment {

        static ColorPreference color_pref;
        SharedPreferences sharedPreferences;
        private static ColorDialog colorDialog;



        @Override
        @TargetApi(23)
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferenceScreen = getPreferenceScreen();
            color_pref = (ColorPreference) preferenceScreen.findPreference("color_pref");
            colorDialog = ColorDialog.getColorDialog();
            sharedPreferences = getContext().getSharedPreferences(COLOR_PREFERENCES, MODE_PRIVATE);
            String value = sharedPreferences.getString("categoryName", "");
            color_pref.setSummary(value);



        }

        public static ColorPreference getColorPreference(){
            return color_pref;
        }

    }

    @TargetApi(23)
    public void onDialogClosed(ColorDialog colorDialog, String TAG) {

        String value = colorDialog.getName();

        SharedPreferences.Editor editor = getMySharedPreferences().edit();
        editor.putString("categoryName", value);
        editor.commit();

        SettingsFragment.getColorPreference().setSummary(value);



        SharedPreferences sharedPreferences = getSharedPreferences(COLOR_PREFERENCES, MODE_PRIVATE);
        int color = sharedPreferences.getInt("selectedColor", 0);

        View rootView = getLayoutInflater().inflate(R.layout.pref_color_layout, null, true);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.color_view);

        ImageView colorView = ColorPreference.getImageView();
        ColorPreference.setColorViewValue(colorView, color, false, ColorShape.CIRCLE, this);

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

    public SharedPreferences getMySharedPreferences(){
        return getSharedPreferences(COLOR_PREFERENCES, MODE_PRIVATE);
    }

    public static int getSelectedColor(){
        int color = getMyContext().getSharedPreferences(COLOR_PREFERENCES, MODE_PRIVATE).getInt("selectedColor", 0);
        return color;
    }



}
