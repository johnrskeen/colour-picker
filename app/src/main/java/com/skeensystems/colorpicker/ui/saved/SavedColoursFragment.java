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

    /**
     * Saves inputted colour
     */
    private void saveColour(@NonNull Colour colour) {
        // Create new colour object, set id to current millis time, favorite defaults to false
        SavedColour savedColour = new SavedColour(System.currentTimeMillis(), colour.getR(), colour.getG(), colour.getB(), false);

        // Calculate closest matches
        ArrayList<DatabaseColour> closestMatches = mainActivityViewModel.getClosestMatches(savedColour);
        if (closestMatches.size() == 4) {
            savedColour.setClosestMatch(closestMatches.get(0));
            savedColour.setFirstClosest(closestMatches.get(1));
            savedColour.setSecondClosest(closestMatches.get(2));
            savedColour.setThirdClosest(closestMatches.get(3));
        }
        else if (closestMatches.size() >= 1) {
            savedColour.setClosestMatch(closestMatches.get(0));
        }

        // Add this colour to the savedColours list
        savedColours.add(savedColour);
        // Update the database with the new colour
        new Thread(() -> colourDAO.insertAll(savedColour.toSavedColourEntity())).start();
    }
}