package com.example.android.colordialog.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.colordialog.R;

public class ColorDialog extends DialogFragment implements DialogInterface.OnClickListener{
    private GridLayout colorGrid;
    private OnColorSelectedListener colorSelectedListener;
    private int numColumns;
    private int[] colorChoices;
    private com.example.android.colordialog.dialog.ColorShape colorShape;
    public static final String COLOR_PREFERENCES = "ColorPreferences";


    //the color to be checked
    private int selectedColorValue;

    private static final String NUM_COLUMNS_KEY = "num_columns";
    private static final String COLOR_SHAPE_KEY = "color_shape";
    private static final String COLOR_CHOICES_KEY = "color_choices";
    private static final String SELECTED_COLOR_KEY = "selected_color";

    public ColorDialog() {
    }

    public static ColorDialog newInstance(int numColumns, com.example.android.colordialog.dialog.ColorShape colorShape, int[] colorChoices, int selectedColorValue) {
        Bundle args = new Bundle();
        args.putInt(NUM_COLUMNS_KEY, numColumns);
        args.putSerializable(COLOR_SHAPE_KEY, colorShape);
        args.putIntArray(COLOR_CHOICES_KEY, colorChoices);
        args.putInt(SELECTED_COLOR_KEY, selectedColorValue);

        ColorDialog dialog = new ColorDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        numColumns = args.getInt(NUM_COLUMNS_KEY);
        colorShape = (com.example.android.colordialog.dialog.ColorShape) args.getSerializable(COLOR_SHAPE_KEY);
        colorChoices = args.getIntArray(COLOR_CHOICES_KEY);
        selectedColorValue = args.getInt(SELECTED_COLOR_KEY);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener colorSelectedListener) {
        this.colorSelectedListener = colorSelectedListener;
        repopulateItems();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnColorSelectedListener) {
            setOnColorSelectedListener((OnColorSelectedListener) context);
        } else {
            repopulateItems();
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View rootView = layoutInflater.inflate(R.layout.dialog_colors, null);

        colorGrid = rootView.findViewById(R.id.color_grid);
        colorGrid.setColumnCount(numColumns);
        repopulateItems();

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("Pick Colour")
                .setPositiveButton("OK", this)
                .setNegativeButton("Cancel", this)
                .create();
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

    private void repopulateItems() {
        if (colorSelectedListener == null || colorGrid == null) {
            return;
        }

        Context context = colorGrid.getContext();
        colorGrid.removeAllViews();

        for (final int color : colorChoices) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.grid_item_color, colorGrid, false);

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(COLOR_PREFERENCES, Context.MODE_PRIVATE);
            int savedColor = sharedPreferences.getInt("selectedColor", 0);
            boolean selected = false;

            if(color == savedColor){
                selected = true;
            }
            else{
                selected = false;
            }

            ColorUtils.setColorViewValue((ImageView) itemView.findViewById(R.id.color_view), color,
                    selected, colorShape, context);


            itemView.setClickable(true);
            itemView.setFocusable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (colorSelectedListener != null) {
                        colorSelectedListener.onColorSelected(color, getTag());
                    }
                    // dismiss();
                }
            });

            colorGrid.addView(itemView);
        }

        sizeDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        sizeDialog();
    }


    private void sizeDialog() {
        if (colorSelectedListener == null || colorGrid == null) {
            return;
        }

        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }

        final Resources res = colorGrid.getContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();

        // Can't use Integer.MAX_VALUE here (weird issue observed otherwise on 4.2)
        colorGrid.measure(
                View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.AT_MOST));
        int width = colorGrid.getMeasuredWidth();
        int height = colorGrid.getMeasuredHeight();

        int extraPadding = res.getDimensionPixelSize(R.dimen.extra_padding);

        width += extraPadding;
        height += 600;

        dialog.getWindow().setLayout(width, height);
    }

    @Override
    @TargetApi(23)
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case -2:
                // Negative
                Toast.makeText(getContext(), "Negative Button Clicked", Toast.LENGTH_SHORT).show();
                break;
            case -1:
                // Positive
                Toast.makeText(getContext(), "Positive Button Clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int newColor, String tag);
    }

    public static class Builder {
        private int numColumns = 5;
        private int[] colorChoices;
        private String title;
        private com.example.android.colordialog.dialog.ColorShape colorShape = com.example.android.colordialog.dialog.ColorShape.CIRCLE;
        private Context context;
        private int selectedColor;
        private String tag;


        public <ColorActivityType extends Activity & OnColorSelectedListener> Builder(@NonNull ColorActivityType context) {
            this.context = context;
            //default colors
            setColorChoices(R.array.color_choices);
        }

        public Builder setNumColumns(int numColumns) {
            this.numColumns = numColumns;
            return this;
        }

        public Builder setColorChoices(@ArrayRes int colorChoicesRes) {
            this.colorChoices = com.example.android.colordialog.dialog.ColorUtils.extractColorArray(colorChoicesRes, context);
            return this;
        }

        public Builder setColorShape(com.example.android.colordialog.dialog.ColorShape colorShape) {
            this.colorShape = colorShape;
            return this;
        }

        public Builder setSelectedColor(@ColorInt int selectedColor) {
            this.selectedColor = selectedColor;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        protected ColorDialog build() {
            ColorDialog dialog = ColorDialog.newInstance(numColumns, colorShape, colorChoices, selectedColor);
            dialog.setOnColorSelectedListener((OnColorSelectedListener) context);
            return dialog;
        }

        public ColorDialog show() {
            ColorDialog dialog = build();
            dialog.show(com.example.android.colordialog.dialog.Utils.resolveContext(context).getFragmentManager(), tag == null ? String.valueOf(System.currentTimeMillis()) : tag);
            return dialog;
        }

    }
}
