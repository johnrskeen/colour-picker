package com.skeensystems.colorpicker.ui.camera;

import static com.skeensystems.colorpicker.MainActivity.colourDAO;
import static com.skeensystems.colorpicker.MainActivity.mainActivityViewModel;
import static com.skeensystems.colorpicker.MainActivity.savedColours;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;

import com.skeensystems.colorpicker.MainActivity;
import com.skeensystems.colorpicker.database.DatabaseColour;
import com.skeensystems.colorpicker.database.SavedColour;

import java.util.ArrayList;

public class CameraFragment extends Fragment {

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ComposeView composeView = new ComposeView(requireContext());
        CameraScreenKt.setCameraContent(composeView);
        return composeView;
    }
}