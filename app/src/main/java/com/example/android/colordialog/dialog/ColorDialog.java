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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.colordialog.DialogClosed;
import com.example.android.colordialog.R;

public class ColorDialog extends DialogFragment implements DialogInterface.OnClickListener{
    private GridLayout colorGrid;
    private EditText etName;
    private OnColorSelectedListener colorSelectedListener;
    private int numColumns;
    private int[] colorChoices;
    private ColorShape colorShape;
    public static final String COLOR_PREFERENCES = "ColorPreferences";
    private ImageView imageView;
    static DialogClosed dialogClosedListener;
    public static ColorDialog colorDialog;
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

        colorDialog = new ColorDialog();

        colorDialog.setArguments(args);

        return colorDialog;
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

    public void setOnColorSelectedListener(OnColorSelectedListener colorSelectedListener, ColorDialog fragment) {
        this.colorSelectedListener = colorSelectedListener;
        repopulateItems();
    }

    public static ColorDialog getColorDialog(){
        return colorDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnColorSelectedListener) {
            setOnColorSelectedListener((OnColorSelectedListener) context, new ColorDialog());
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
        etName = rootView.findViewById(R.id.editTextName);
        repopulateItems();

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("Pick Colour")
                .setPositiveButton("OK", this)
                .setNegativeButton("Cancel", this)
                .create();


    }

    public String getName(){

        return etName.getText().toString();
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

    public void repopulateItems() {
        if (colorSelectedListener == null || colorGrid == null) {
            return;
        }

        Context context = colorGrid.getContext();
        colorGrid.removeAllViews();

        int[] colorArray = new int[colorChoices.length];

        for(int i = 0; i < colorChoices.length; i++) {
            String hexColor = String.format("#%06X", (0xFFFFFF & colorChoices[i]));
            hexColor = hexColor.replace("#", "");
            int regularColor = Integer.valueOf(hexColor, 16);
            colorArray[i] = regularColor;
        }

        for(int i = 0; i < colorArray.length; i++){
            Log.v(getClass().getSimpleName(), "Color: "+ colorChoices[i] + " (int): " + colorArray[i]);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(COLOR_PREFERENCES, Context.MODE_PRIVATE);
        int savedColor = sharedPreferences.getInt("selectedColor", 12597547);
        Log.v(getClass().getSimpleName(), "Selected Color: " + String.valueOf(savedColor));



        for (int i = 0; i<colorChoices.length; i++) {
            final int color = colorArray[i]; // Non negative number

            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.grid_item_color, colorGrid, false);
            boolean selected = false;

            if(color == savedColor){
                selected = true;
                Log.v(getClass().getSimpleName(), "Same colour " + String.valueOf(color));
            }
            else{
                Log.v(getClass().getSimpleName(), "Different colour " + String.valueOf(color));
                selected = false;
            }

            final int colorInt = colorChoices[i];
            ColorUtils.setColorViewValue((ImageView) itemView.findViewById(R.id.color_view), colorInt,
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
                dialogClosedListener.onDialogClosed(colorDialog, "homescreen");
                break;
        }
    }

    public static void setOnDialogClosedListener(DialogClosed listener){

        dialogClosedListener = listener;


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
            dialog.setOnColorSelectedListener((OnColorSelectedListener) context, dialog);
            colorDialog.setOnColorSelectedListener((OnColorSelectedListener) context, dialog);
            return dialog;
        }

        public ColorDialog show() {
            ColorDialog dialog = build();
            dialog.show(com.example.android.colordialog.dialog.Utils.resolveContext(context).getFragmentManager(), tag == null ? String.valueOf(System.currentTimeMillis()) : tag);
            return dialog;
        }

    }
}
