package com.example.android.colordialog.dialog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.nfc.Tag;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.colordialog.DialogClosed;
import com.example.android.colordialog.OnColorDialogListener;
import com.example.android.colordialog.R;

import java.util.Random;


public class ColorPreference extends Preference implements ColorDialog.OnColorSelectedListener, OnColorDialogListener {
    private int[] colorChoices = {};
    private int value = 0;
    private int itemLayoutId = R.layout.pref_color_layout;
    private int itemLayoutLargeId = R.layout.pref_color_layout_large;
    private int numColumns = 5;
    private ColorShape colorShape = ColorShape.CIRCLE;
    private boolean showDialog = true;
    private ColorDialog colorDialog;

    @TargetApi(21)
    public ColorPreference(Context context) {
        super(context);
        initAttrs(null, 0);
        setOnColorDialogListener();

    }

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
        setOnColorDialogListener();

    }

    public ColorPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs, defStyle);
        setOnColorDialogListener();

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

    private void setOnColorDialogListener(){
        ColorDialog.setOnColorDialogListener(this);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageView colorView = view.findViewById(R.id.color_view);
        if (colorView != null) {
            ColorUtils.setColorViewValue(colorView, value, false, colorShape, getContext());
        }
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            this.value = value;
            persistInt(value);
            notifyChanged();
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (showDialog) {
            ColorUtils.showDialog(getContext(), this, getFragmentTag(),
                    numColumns, colorShape, colorChoices, getValue());
            Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
            setOnColorDialogListener();
            colorDialog.repopulateItems();
            Log.v(getFragmentTag(), "Frag tag: " + getFragmentTag());

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

    public int getValue() {
        return value;
    }

    @Override
    public void onColorSelected(int newColor, String tag) {
        setValue(newColor);
    }

    @Override
    public void setColorDialog(ColorDialog dialog) {
        colorDialog = dialog;

    }
}

