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

    /**
     * add inputted colour to savedColours list
     * @param r - r value 0 <= r <= 255
     * @param g - g value 0 <= g <= 255
     * @param b - b value 0 <= b <= 255
     */
    private void pickColour(int r, int g, int b) {
        // Create new colour object, set id to current millis time, favorite defaults to false
        SavedColour savedColour = new SavedColour(System.currentTimeMillis(), r, g, b, false);

        // Calculate closest match colours for this new colour from the colours database
        ArrayList<DatabaseColour> closestMatches = mainActivityViewModel.getClosestMatches(savedColour);
        // Set the closest matches to the SavedColour object
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
        new Thread(() -> colourDAO.insertAll(savedColour)).start();

        // Inform user colour was successfully saved
        // Snackbar.make(binding.previewPickedColour, "Saved colour " + savedColour.getHEXString() + " (\u2248" + savedColour.getName() + ")", Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        // Enable updating of colour preview
        MainActivity.onCamera = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Enable updating of colour preview
        MainActivity.onCamera = false;
    }
}