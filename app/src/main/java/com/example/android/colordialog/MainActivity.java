package com.example.android.colordialog;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
        ColorDialog.OnColorSelectedListener, DialogClosed{

    public static final String COLOR_PREFERENCES = "ColorPreferences";

    Button btnOpenDialog;
    public ImageView imageView;
    private int newColor;
    View rootView;
    GridLayout colorView;
    ColorDialog colorDialog;


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
        ColorDialog.setOnDialogClosedListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOpenDialog:
                openDialog();

                break;
        }
    }



    public void openDialog(){
        colorDialog = new ColorDialog.Builder(this)
                .setColorChoices(R.array.color_choices)
                .setNumColumns(5)
                .setColorShape(ColorShape.CIRCLE)
                .setTag("MainActivityColor")
                .show();

        rootView = getLayoutInflater().inflate(R.layout.dialog_colors,null);
        colorView = rootView.findViewById(R.id.color_grid);
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


    public void makeNewDrawable(int color, ColorShape shape){
        Resources res = getResources();

        Drawable currentDrawable = imageView.getDrawable();

        GradientDrawable colorChoiceDrawable;
        if (currentDrawable instanceof GradientDrawable) {
            // Reuse drawable
            colorChoiceDrawable = (GradientDrawable) currentDrawable;
        } else {
            colorChoiceDrawable = new GradientDrawable();
            colorChoiceDrawable.setShape(shape == ColorShape.SQUARE ? GradientDrawable.RECTANGLE : GradientDrawable.OVAL);
        }

        // Set stroke to dark version of color
        int darkenedColor = Color.rgb(
                Color.red(color) * 192 / 256,
                Color.green(color) * 192 / 256,
                Color.blue(color) * 192 / 256);

        colorChoiceDrawable.setColor(color);
        colorChoiceDrawable.setStroke((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, res.getDisplayMetrics()), darkenedColor);

        Drawable drawable = colorChoiceDrawable;

        imageView.setImageDrawable(drawable);
    }


    @Override
    public void onDialogClosed(ColorDialog colorDialog, String TAG) {
        SharedPreferences sharedPreferences = getSharedPreferences(COLOR_PREFERENCES, MODE_PRIVATE);
        int color = sharedPreferences.getInt("selectedColor", 16777215); // 16777215 = white

        ColorUtils.setColorViewValue(imageView, color, false, ColorShape.CIRCLE, this);

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }
}

