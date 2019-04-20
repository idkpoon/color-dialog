package com.example.android.colordialog.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import com.example.android.colordialog.R;
import com.example.android.colordialog.dialog.ColorDialog;
import com.example.android.colordialog.dialog.ColorShape;
import java.util.Vector;

public class ColorUtils {


    public static void setColorViewValue(ImageView imageView, int color, boolean selected, ColorShape shape, Context context) {
        Resources res = imageView.getContext().getResources();

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
        if (selected) {
            VectorDrawable vectorCheck = (VectorDrawable) res.getDrawable(isColorDark(color)
                    ? R.drawable.ic_check_white
                    : R.drawable.ic_check_black);
            Bitmap checkmark = getBitmap(vectorCheck);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), checkmark);

            bitmapDrawable.setGravity(Gravity.CENTER);
            drawable = new LayerDrawable(new Drawable[]{
                    colorChoiceDrawable,
                    bitmapDrawable});
        }

        imageView.setImageDrawable(drawable);
    }

    public static Drawable makeDrawable(ImageView imageView, int color, Context context, ColorShape shape){
        Resources res = context.getResources();



        GradientDrawable colorChoiceDrawable = new GradientDrawable();

        Drawable currentDrawable = imageView.getDrawable();
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

        return colorChoiceDrawable;
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }




    private static final int BRIGHTNESS_THRESHOLD = 150;

    private static boolean isColorDark(int color) {
        return ((30 * Color.red(color) +
                59 * Color.green(color) +
                11 * Color.blue(color)) / 100) <= BRIGHTNESS_THRESHOLD;
    }

    public static int[] extractColorArray(@ArrayRes int arrayId, Context context) {
        String[] choicesString = context.getResources().getStringArray(arrayId);
        int[] choicesInt = context.getResources().getIntArray(arrayId);

        // If user uses color reference(i.e. @color/color_choice) in the array,
        // the choicesString contains null values. We use the choicesInt in such case.
        boolean isStringArray = choicesString[0] != null;
        int length = isStringArray ? choicesString.length : choicesInt.length;

        int[] colorChoices = new int[length];
        for (int i = 0; i < length; i++) {
            colorChoices[i] = isStringArray ? Color.parseColor(choicesString[i]) : choicesInt[i];
        }

        return colorChoices;
    }

    public static void showDialog(Context context, ColorDialog.OnColorSelectedListener listener, String tag,
                                  int numColumns, ColorShape colorShape, int[] colorChoices, int selectedColorValue) {
        ColorDialog fragment = ColorDialog.newInstance(numColumns, colorShape, colorChoices, selectedColorValue);
        fragment.setOnColorSelectedListener(listener);

        Activity activity = Utils.resolveContext(context);
        if (activity != null) {
            activity.getFragmentManager()
                    .beginTransaction()
                    .add(fragment, tag)
                    .commit();
        }
    }

    public static void attach(Context context, ColorDialog.OnColorSelectedListener listener, String tag) {
        Activity activity = Utils.resolveContext(context);
        if (activity != null) {
            ColorDialog fragment = (ColorDialog) activity.getFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                // re-bind preference to fragment
                fragment.setOnColorSelectedListener(listener);
            }
        }
    }


}
