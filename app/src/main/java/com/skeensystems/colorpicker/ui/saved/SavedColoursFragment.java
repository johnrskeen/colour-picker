package com.skeensystems.colorpicker.ui.saved;

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

import com.skeensystems.colorpicker.database.Colour;
import com.skeensystems.colorpicker.database.DatabaseColour;
import com.skeensystems.colorpicker.database.SavedColour;

import java.util.ArrayList;


public class SavedColoursFragment extends Fragment {

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ComposeView composeView = new ComposeView(requireContext());
        SavedColoursScreenKt.setSavedColoursContent(composeView);
        return composeView;
    }
}