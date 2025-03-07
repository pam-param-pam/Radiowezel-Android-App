package dev.pamparampam.myapplication.radiowezel.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textview.MaterialTextView;

import dev.pamparampam.myapplication.R;


public class ProgressBarDialog extends DialogFragment {
    private Bundle bundle;

    public ProgressBarDialog() {}

    public static ProgressBarDialog newInstance(String title) {
        ProgressBarDialog myFragment = new ProgressBarDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.dialog_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);

        String pTitle = bundle.getString("title", "Loading...");

        MaterialTextView title = view.findViewById(R.id.progress_title);
        title.setText(pTitle);
    }
}
