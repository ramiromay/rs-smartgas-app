package com.realssoft.smartgas.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.realssoft.smartgas.R;

public class RequestLocationFragment extends DialogFragment {

    private Toolbar toolbar;

    public static final String TAG = "example_dialog";

    public RequestLocationFragment() {
        // Required empty public constructor
    }
    public static void display(FragmentManager fragmentManager) {
        RequestLocationFragment exampleDialog = new RequestLocationFragment();
        exampleDialog.show(fragmentManager, TAG);
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_request_location, container, false);

        toolbar = view.findViewById(R.id.idTBDialogRequestLocation);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_ios_new_24);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Some Title");
        toolbar.setOnMenuItemClickListener(item -> {
            requireActivity().finishAffinity();
            System.exit(0);
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroy() {
        requireActivity().finishAffinity();
        System.exit(0);
        super.onDestroy();

    }
}