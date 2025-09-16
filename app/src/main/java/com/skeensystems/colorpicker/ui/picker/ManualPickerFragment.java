package com.skeensystems.colorpicker.ui.picker;

import static com.skeensystems.colorpicker.MainActivity.colourDAO;
import static com.skeensystems.colorpicker.MainActivity.mainActivityViewModel;
import static com.skeensystems.colorpicker.MainActivity.savedColours;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.skeensystems.colorpicker.HelpersKt;
import com.skeensystems.colorpicker.R;
import com.skeensystems.colorpicker.database.DatabaseColour;
import com.skeensystems.colorpicker.database.SavedColour;
import com.skeensystems.colorpicker.databinding.FragmentManualPickerBinding;

import java.util.ArrayList;
import java.util.Objects;

public class ManualPickerFragment extends Fragment {

    private FragmentManualPickerBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentManualPickerBinding.inflate(inflater, container, false);

        ComposeView composeView = new ComposeView(requireContext());
        PickerScreenKt.setPickerContent(composeView);

        return composeView;
        //return binding.getRoot();
    }

    /**
     * Add current selected colour to savedColours list
     */
    private void saveColour() {
        // Get r value of current selected colour
        int r = Objects.requireNonNull(mainActivityViewModel.getR().getValue());

        // Get g value of current selected colour
        int g = Objects.requireNonNull(mainActivityViewModel.getG().getValue());

        // Get b value of current selected colour
        int b = Objects.requireNonNull(mainActivityViewModel.getB().getValue());

        // Create new colour object, set id to current millis time, favorite defaults to false
        SavedColour savedColour = new SavedColour(System.currentTimeMillis(), r, g, b, false);

        // Calculate closest matches
        ArrayList<DatabaseColour> closestMatches = mainActivityViewModel.getClosestMatches(savedColour);
        if (closestMatches.size() == 4) {
            savedColour.setClosestMatch(closestMatches.get(0));
            savedColour.setFirstClosest(closestMatches.get(1));
            savedColour.setSecondClosest(closestMatches.get(2));
            savedColour.setThirdClosest(closestMatches.get(3));
        }
        else if (!closestMatches.isEmpty()) {
            savedColour.setClosestMatch(closestMatches.get(0));
        }


        // Add this colour to the savedColours list
        savedColours.add(savedColour);
        // Update the database with the new colour
        new Thread(() -> colourDAO.insertAll(savedColour.toSavedColourEntity())).start();

        // Inform user colour was successfully saved
        Snackbar.make(binding.manualPickerMainLayout, "Saved colour", Snackbar.LENGTH_LONG).show();
    }
}