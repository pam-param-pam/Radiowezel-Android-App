package dev.pamparampam.myapplication.radiowezel.dialogie;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.flatdialoglibrary.R;
import com.google.android.material.textfield.TextInputLayout;


public class Dialogie extends Dialog {
    private Context context;

    private TextView title;

    private TextView subtitle;

    private TextView first_button;

    private TextView second_button;

    private TextView third_button;

    private TextInputLayout first_edit_text;

    private EditText second_edit_text;

    private EditText third_edit_text;

    private EditText large_edit_text;

    private ImageView icon;

    private LinearLayout main_frame;

    public Dialogie(Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        title=findViewById(R.id.title);
        subtitle=findViewById(R.id.subtitle);
        first_button=findViewById(R.id.first_button);
        second_button=findViewById(R.id.second_button);
        third_button=findViewById(R.id.third_button);
        first_edit_text=findViewById(R.id.first_edit_text);
        second_edit_text=findViewById(R.id.second_edit_text);
        //third_edit_text=findViewById(R.id.third_edit_text);
        large_edit_text=findViewById(R.id.large_edit_text);
        icon=findViewById(R.id.icon);
        main_frame=findViewById(R.id.main_frame);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        initDefaultCase();
        this.setCancelable(false);



    }

    private void initDefaultCase() {
        setLargeTextFieldBorderColor(Color.parseColor("#FFFFFF"));
        setFirstTextFieldBorderColor(Color.parseColor("#FFFFFF"));
        setSecondTextFieldBorderColor(Color.parseColor("#FFFFFF"));
        setTitleColor(Color.parseColor("#FFFFFF"));
        setSubtitleColor(Color.parseColor("#FFFFFF"));
        setFirstButtonColor(Color.parseColor("#8A56AC"));
        setSecondButtonColor(Color.parseColor("#D47FA6"));
        setThirdButtonColor(Color.parseColor("#998FA2"));
        setBackgroundColor(Color.parseColor("#241332"));
        first_edit_text.setVisibility(View.GONE);
        second_edit_text.setVisibility(View.GONE);
        large_edit_text.setVisibility(View.GONE);
        third_button.setVisibility(View.GONE);
        first_button.setVisibility(View.GONE);
        second_button.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        subtitle.setVisibility(View.GONE);

    }

    public Dialogie isCancelable(boolean cancelable){
        this.setCancelable(cancelable);
        return this;
    }

    public Dialogie setIcon(int image){
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(image);
        return this;
    }

    public Dialogie setTitle(String titleText) {
        title.setVisibility(View.VISIBLE);
        title.setText(titleText);
        return this;
    }
    public Dialogie setTitleColor(int color) {
        title.setVisibility(View.VISIBLE);
        title.setTextColor(color);
        return this;
    }


    public Dialogie setSubtitle(String subtitleText) {
        subtitle.setVisibility(View.VISIBLE);
        subtitle.setText(subtitleText);
        return this;
    }

    public Dialogie setSubtitleColor(int color) {
        subtitle.setVisibility(View.VISIBLE);
        subtitle.setTextColor(color);
        return this;
    }
    public String getFirstTextField() {
        if (first_edit_text.getEditText() == null) {
            return null;
        }
        return first_edit_text.getEditText().toString();
    }

    public Dialogie withFirstTextField(boolean hasEditText){
        if (hasEditText) {
            first_edit_text.setVisibility(View.VISIBLE);
        } else {
            first_edit_text.setVisibility(View.GONE);
        }
        return this;
    }

    public String getLargeTextField() {
        return large_edit_text.getText().toString();
    }

    public Dialogie withLargeText(boolean hasEditText){
        if (hasEditText) {
            large_edit_text.setVisibility(View.VISIBLE);
        } else {
            large_edit_text.setVisibility(View.GONE);
        }
        return this;
    }

    public String getSecondTextField() {
        return second_edit_text.getText().toString();
    }

    public Dialogie withSecondTextField(boolean hasEditText){
        if (hasEditText) {
            second_edit_text.setVisibility(View.VISIBLE);
        } else {
            second_edit_text.setVisibility(View.GONE);
        }
        return this;
    }



    public Dialogie setLargeTextField(String secondText) {
        large_edit_text.setVisibility(View.VISIBLE);
        large_edit_text.setText(secondText);
        return this;
    }

    public Dialogie setFirstTextFieldHint(String firstText) {
        first_edit_text.setVisibility(View.VISIBLE);
        first_edit_text.setHint(firstText);
        return this;
    }

    public Dialogie setSecondTextFieldHint(String secondText) {
        second_edit_text.setVisibility(View.VISIBLE);
        second_edit_text.setHint(secondText);
        return this;
    }

    public Dialogie setLargeTextFieldHint(String secondText) {
        large_edit_text.setVisibility(View.VISIBLE);
        large_edit_text.setHint(secondText);
        return this;
    }


    public Dialogie setFirstTextFieldTextColor(int color) {
        first_edit_text.setVisibility(View.VISIBLE);
        first_edit_text.setPlaceholderTextColor(ColorStateList.valueOf(color));
        return this;
    }

    public Dialogie setSecondTextFieldTextColor(int color) {
        second_edit_text.setVisibility(View.VISIBLE);
        second_edit_text.setTextColor(color);
        return this;
    }

    public Dialogie setLargeTextFieldTextColor(int color) {
        large_edit_text.setVisibility(View.VISIBLE);
        large_edit_text.setTextColor(color);
        return this;
    }
    public Dialogie setFirstTextFieldBorderColor(int color) {
        first_edit_text.setVisibility(View.VISIBLE);
        GradientDrawable drawable = (GradientDrawable)first_edit_text.getBackground();
        drawable.setStroke(5, color);
        return this;
    }

    public Dialogie setSecondTextFieldBorderColor(int color) {
        second_edit_text.setVisibility(View.VISIBLE);
        GradientDrawable drawable = (GradientDrawable)second_edit_text.getBackground();
        drawable.setStroke(5, color);
        return this;
    }

    public Dialogie setLargeTextFieldBorderColor(int color) {
        large_edit_text.setVisibility(View.VISIBLE);
        GradientDrawable drawable = (GradientDrawable)large_edit_text.getBackground();
        drawable.setStroke(5, color);
        return this;
    }

    public Dialogie setFirstTextFieldHintColor(int color) {
        first_edit_text.setVisibility(View.VISIBLE);
        first_edit_text.setHintTextColor(ColorStateList.valueOf(color));
        return this;
    }

    public Dialogie setSecondTextFieldHintColor(int color) {
        second_edit_text.setVisibility(View.VISIBLE);
        second_edit_text.setHintTextColor(color);
        return this;
    }

    public Dialogie setLargeTextFieldHintColor(int color) {
        large_edit_text.setVisibility(View.VISIBLE);
        large_edit_text.setHintTextColor(color);
        return this;
    }



    public Dialogie setSecondTextFieldInputType(int type) {
        second_edit_text.setVisibility(View.VISIBLE);
        second_edit_text.setInputType(type);
        return this;
    }

    public Dialogie setThirdsTextFieldInputType(int type) {
        third_edit_text.setVisibility(View.VISIBLE);
        third_edit_text.setInputType(type);
        return this;
    }

    public Dialogie setLargeTextFieldInputType(int type) {
        second_edit_text.setVisibility(View.VISIBLE);
        second_edit_text.setInputType(type);
        return this;
    }

    public Dialogie setFirstButtonColor(int color) {
        first_button.setVisibility(View.VISIBLE);

        Drawable background = first_button.getBackground();
        changingBackgroundColor(background, color);
        return this;
    }


    public Dialogie setSecondButtonColor(int color) {
        second_button.setVisibility(View.VISIBLE);

        Drawable background = second_button.getBackground();
        changingBackgroundColor(background, color);
        return this;
    }
    public Dialogie setThirdButtonColor(int color) {
        third_button.setVisibility(View.VISIBLE);
        Drawable background = third_button.getBackground();
        changingBackgroundColor(background,color);
        return this;
    }

    public Dialogie setFirstButtonTextColor(int color) {
        first_button.setVisibility(View.VISIBLE);
        first_button.setTextColor(color);
        return this;
    }


    public Dialogie setSecondButtonTextColor(int color) {
        second_button.setVisibility(View.VISIBLE);
        second_button.setTextColor(color);
        return this;
    }
    public Dialogie setThirdButtonTextColor(int color) {
        third_button.setVisibility(View.VISIBLE);
        third_button.setTextColor(color);
        return this;
    }

    public Dialogie setFirstButtonText(String text) {
        first_button.setVisibility(View.VISIBLE);
        first_button.setText(text);
        return this;
    }


    public Dialogie setSecondButtonText(String text) {
        second_button.setVisibility(View.VISIBLE);
        second_button.setText(text);
        return this;
    }
    public Dialogie setThirdButtonText(String text) {
        third_button.setVisibility(View.VISIBLE);
        third_button.setText(text);
        return this;
    }

    public Dialogie setBackgroundColor(int color) {
        Drawable background = main_frame.getBackground();
        changingBackgroundColor(background,color);
        return this;
    }


    public Dialogie withFirstButtonListener(View.OnClickListener confirmListner){
        first_button.setVisibility(View.VISIBLE);
        first_button.setOnClickListener(confirmListner);
        return this;
    }

    public Dialogie withSecondButtonListener(View.OnClickListener cancelListner){
        second_button.setVisibility(View.VISIBLE);
        second_button.setOnClickListener(cancelListner);
        return this;
    }

    public Dialogie withThirdButtonListener(View.OnClickListener cancelListner){
        third_button.setVisibility(View.VISIBLE);
        third_button.setOnClickListener(cancelListner);
        return this;
    }

    public void setFirstTextFieldError(String errorText) {
        first_edit_text.setError(errorText);

    }

    public void setSecondTextFieldError(String errorText) {
        second_edit_text.setError(errorText);

    }

    public void setSecondTextFieldError(String errorText, Drawable icon) {
        second_edit_text.setError(errorText, icon);

    }
    public void setThirdTextFieldError(String errorText) {
        third_edit_text.setError(errorText);

    }

    public void setThirdTextFieldError(String errorText, Drawable icon) {
        third_edit_text.setError(errorText, icon);

    }


    private void changingBackgroundColor(Drawable background, int color) {
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(color);
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(color);
        }
    }

}
