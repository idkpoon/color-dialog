package com.example.android.colordialog;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.media.Image;
import android.preference.Preference;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.colordialog.dialog.ColorDialog;
import com.example.android.colordialog.dialog.ColorShape;
import com.example.android.colordialog.dialog.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ColorDialog.OnColorSelectedListener, DialogClosed{

    public static final String COLOR_PREFERENCES = "ColorPreferences";

    Button btnOpenDialog, btnSettings;
    public ImageView imageView;
    private int newColor;
    View rootView;
    GridLayout colorView;
    ColorDialog colorDialog;
    TextView textViewName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenDialog = findViewById(R.id.btnOpenDialog);
        btnSettings = findViewById(R.id.btnSettings);
        btnOpenDialog.setOnClickListener(this);
        textViewName = (TextView)findViewById(R.id.tv_name);

        imageView = findViewById(R.id.image);
        if(imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_check_black).getConstantState())){
            Toast.makeText(this, "The drawable is the same", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "The drawable is different", Toast.LENGTH_SHORT).show();
        }
        ColorDialog.setOnDialogClosedListener(this);

        btnSettings.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOpenDialog:
                openDialog();

                break;
            case R.id.btnSettings:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
        }
    }



    public void openDialog(){
        colorDialog = new ColorDialog.Builder(this)
                .setColorChoices(R.array.color_choices)
                .setNumColumns(5)
                .setColorShape(ColorShape.CIRCLE)
                .setTag("MainActivityColor")
                .show();

        colorDialog.repopulateItems();


    }

    @Override
    public void onColorSelected(int newColor, String tag) {
        btnOpenDialog.setTextColor(newColor);

        SharedPreferences sharedPreferences = getSharedPreferences(COLOR_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedColor", newColor);
        editor.commit();

        colorDialog.repopulateItems();


    }




    @Override
    public void onDialogClosed(ColorDialog colorDialog, String TAG) {

        if(TAG.equals("homescreen")) {
            SharedPreferences sharedPreferences = getSharedPreferences(COLOR_PREFERENCES, MODE_PRIVATE);
            int color = sharedPreferences.getInt("selectedColor", 16777215); // 16777215 = white
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String name = colorDialog.getName();

            Log.v(getClass().getSimpleName(), "Name: " + name);

            editor.putString("Name", name);

            ColorUtils.setColorViewValue(imageView, color, false, ColorShape.CIRCLE, this);
            textViewName.setText(name);
        }

    }
}

