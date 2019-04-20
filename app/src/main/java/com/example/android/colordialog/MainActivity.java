package com.example.android.colordialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.colordialog.dialog.ColorDialog;
import com.example.android.colordialog.dialog.ColorShape;
import com.example.android.colordialog.dialog.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        com.example.android.colordialog.dialog.ColorDialog.OnColorSelectedListener{

    public static final String COLOR_PREFERENCES = "ColorPreferences";

    Button btnOpenDialog;
    ImageView imageView;
    private int newColor;
    private int newColor1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenDialog = findViewById(R.id.btnOpenDialog);
        btnOpenDialog.setOnClickListener(this);

        imageView = findViewById(R.id.image);
        if(imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_check_black).getConstantState())){
            Toast.makeText(this, "The drawable is the same", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "The drawable is different", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOpenDialog:
                openDialog();
                break;
        }
    }

    private void openDialog(){
        ColorDialog dialog = new ColorDialog.Builder(this)
                .setColorChoices(R.array.color_choices)
                .setNumColumns(5)
                .setColorShape(ColorShape.CIRCLE)
                .setTag("MainActivityColor")
                .show();


    }

    @Override
    public void onColorSelected(int newColor, String tag) {
        btnOpenDialog.setTextColor(newColor);
        colorSelected(newColor);
        SharedPreferences sharedPreferences = getSharedPreferences(COLOR_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedColor", newColor);
        editor.commit();


    }

    private void colorSelected(int newColor){

        String hexColor = String.format("#%06X", (0xFFFFFF & newColor));
        Log.v("MainActivity", "Current colour " + hexColor + "   " + String.valueOf(newColor));

        View rootView = getLayoutInflater().inflate(R.layout.dialog_colors, null);
        GridLayout colorGrid = rootView.findViewById(R.id.color_grid);
        colorGrid.setColumnCount(5);

        List colorHexes = Arrays.asList(getResources().getStringArray(R.array.color_choices));

        ArrayList<Integer> colorIntList = new ArrayList<>();

        for (int i = 0; i < colorHexes.size(); i++) {
            String string = String.valueOf(colorHexes.get(i));
            int color = Integer.parseInt(string.replaceFirst("^#",""), 16);
            colorIntList.add(color);
            Log.v(getClass().getSimpleName(), "Color: " + colorHexes.get(i) + "    " + colorIntList.get(i));
        }

        int colorInt = 0;

        for(Object colour : colorHexes) {
            String currentColour = String.valueOf(colour);
            if (currentColour.equalsIgnoreCase(hexColor)) {
                colorInt = colorIntList.get(colorHexes.indexOf(colour));
                int size = colorGrid.getChildCount();
                ImageView view = (ImageView) colorGrid.getChildAt(colorHexes.indexOf(colour));
                Drawable drawable = ColorUtils.makeDrawable(view, colorInt, this, ColorShape.CIRCLE);
                imageView.setImageDrawable(drawable);
                view.setImageDrawable(drawable);

                break;
            }

        }




      }




}

