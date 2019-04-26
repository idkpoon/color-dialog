package com.example.android.colordialog.dialog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.preference.Preference;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.colordialog.R;
import com.example.android.colordialog.SettingsActivity;

public class ColorPreference extends Preference implements ColorDialog.OnColorSelectedListener {
    private int[] colorChoices = {};
    private int value = 0;
    private int itemLayoutId = R.layout.pref_color_layout;
    private int itemLayoutLargeId = R.layout.pref_color_layout_large;
    private int numColumns = 5;
    private ColorShape colorShape = ColorShape.CIRCLE;
    private boolean showDialog = true;
    private static View preferenceView;
    private SharedPreferences sharedPreferences;
    ColorDialog colorDialog;
    private static ImageView colorView;

    public ColorPreference(Context context) {
        super(context);
        initAttrs(null, 0);
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);

    }

    public ColorPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs, defStyle);

    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorPreference, defStyle, defStyle);

        PreviewSize previewSize;
        try {
            numColumns = a.getInteger(R.styleable.ColorPreference_numColumns, numColumns);
            colorShape = ColorShape.getShape(a.getInteger(R.styleable.ColorPreference_colorShape, 1));
            previewSize = PreviewSize.getSize(a.getInteger(R.styleable.ColorPreference_viewSize, 1));
            showDialog = a.getBoolean(R.styleable.ColorPreference_showDialog, true);
            int choicesResId = a.getResourceId(R.styleable.ColorPreference_colorChoices,
                    R.array.default_color_choice_values);
            colorChoices = ColorUtils.extractColorArray(choicesResId, getContext());

        } finally {
            a.recycle();
        }

        setWidgetLayoutResource(previewSize == PreviewSize.NORMAL ? itemLayoutId : itemLayoutLargeId);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        colorView = view.findViewById(R.id.color_view);
        SharedPreferences sharedPreferences = getSharedPreferences();
//        int color = sharedPreferences.getInt("selectedColor", 0);
        int color = SettingsActivity.getSelectedColor();

        if (colorView != null) {
            setColorViewValue(colorView, color, false, ColorShape.CIRCLE, getContext());

        } else {
            Log.v(getClass().getSimpleName(), "Color view is null");
        }
    }

    public static ImageView getImageView(){
        return colorView;
    }

    public static void setColorViewValue(ImageView imageView, int color, boolean selected, ColorShape shape, Context context) {
        Resources res = imageView.getContext().getResources();
        Log.v("ColorPreference", "Colour from colourviewvalue: " + String.valueOf(color));
//        color = -13304063;
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

        String hexColor = String.format("#%06X", (0xFFFFFF & color));

        colorChoiceDrawable.setColor(Color.parseColor(hexColor));
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

        imageView.setImageDrawable(colorChoiceDrawable);
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


    @Override
    protected void onClick() {
        super.onClick();
        if (showDialog) {
            ColorUtils.showDialog(getContext(), this, getFragmentTag(),
                    numColumns, colorShape, colorChoices, getValue());
             colorDialog = ColorUtils.getDialog();



        }
    }



    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        //helps during activity re-creation
        if (showDialog) {
            ColorUtils.attach(getContext(), this, getFragmentTag());
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public String getFragmentTag() {
        return "color_" + getKey();
    }


    public void setValue(int value) {
        if (callChangeListener(value)) {
            this.value = value;
            persistInt(value);
            notifyChanged();

        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public void onColorSelected(int newColor, String tag) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        int color = sharedPreferences.getInt("selectedColor", 0);
        setValue(color);

    }
}