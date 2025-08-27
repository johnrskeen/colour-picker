package com.skeensystems.colorpicker.ui.camera;

import static com.skeensystems.colorpicker.MainActivity.colourDAO;
import static com.skeensystems.colorpicker.MainActivity.mainActivityViewModel;
import static com.skeensystems.colorpicker.MainActivity.savedColours;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.skeensystems.colorpicker.HelpersKt;
import com.skeensystems.colorpicker.MainActivity;
import com.skeensystems.colorpicker.database.DatabaseColour;
import com.skeensystems.colorpicker.database.SavedColour;
import com.skeensystems.colorpicker.databinding.FragmentCameraBinding;

import java.util.ArrayList;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;


    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CameraViewModel cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // When pickColour button is clicked, add the current central colour to saved colours
        binding.pickColour.setOnClickListener(v -> pickColour(mainActivityViewModel.getCameraR(), mainActivityViewModel.getCameraG(), mainActivityViewModel.getCameraB()));

        // Observer for central colour
        final Observer<Integer> colourObserver = newColour -> {
            // Colour state list for new central colour
            ColorStateList colorStateList = new ColorStateList(new int[][]{{}}, new int[]{newColour});
            // Set tint of central preview view to the current centre colour
            binding.pickerPreview.setBackgroundTintList(colorStateList);
            // Update cross colour
            int crossColour = Color.BLACK;
            // If central colour is dark enough, change cross colour to white
            if (HelpersKt.backgroundRequiresLightText(Color.red(newColour), Color.green(newColour),Color.blue(newColour))) {
                crossColour = Color.WHITE;
            }
            // Set colour of cross (made up of these 4 views)
            binding.crossLeft.setBackgroundColor(crossColour);
            binding.crossRight.setBackgroundColor(crossColour);
            binding.crossTop.setBackgroundColor(crossColour);
            binding.crossBottom.setBackgroundColor(crossColour);


            // Calculate closest match of the current colour (this is where an Octree might be needed, as this is calculated on every update of the central colour)
            // Make dummy SavedColour object
            SavedColour savedColour = new SavedColour(-1, Color.red(newColour), Color.green(newColour),Color.blue(newColour), false);
            ArrayList<DatabaseColour> closestMatches = mainActivityViewModel.getClosestMatches(savedColour);
            if (!closestMatches.isEmpty()) {
                savedColour.setClosestMatch(closestMatches.get(0));
            }
            else {
                // If for some reason (I can't think of any) no close colours are returned, use a blank colour instead
                savedColour.setClosestMatch(new DatabaseColour("", 0, 0, 0));
            }

            // Update preview picked colour button to inform user of current colour they are looking at
            binding.previewPickedColour.setBackgroundTintList(new ColorStateList(new int[][]{{}}, new int[]{savedColour.getColour()}));
            binding.previewPickedColour.setTextColor(savedColour.getTextColour());
            // Text: " ~ COLOUR_NAME \n COLOUR_HEX_VALUE"
            String text = "\u2248 ";
            text += savedColour.getClosestMatch().getName();
            text += "\n";
            text += savedColour.getHEXString();
            binding.previewPickedColour.setText(text);
        };
        // Bind observer to getCurrentCameraColour in mainActivityViewModel
        mainActivityViewModel.getCameraCurrentColour().observe(getViewLifecycleOwner(), colourObserver);

        ComposeView composeView = new ComposeView(requireContext());
        CameraScreenKt.setCameraContent(composeView);

        return composeView;
        //return root;
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
        Snackbar.make(binding.previewPickedColour, "Saved colour " + savedColour.getHEXString() + " (\u2248" + savedColour.getName() + ")", Snackbar.LENGTH_LONG).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}