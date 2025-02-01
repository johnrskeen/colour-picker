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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

    // View pager for RGB/HEX/HSV layouts
    ViewPager2 viewPager;

    // Height of button that shows the scroll of the view pager
    float scrollBarHeight = 0;

    // ?mainColourAccent
    int mainColour;
    // ?reversedColourAccent
    int accentColour;

    // Dimensions of buttons at top of page used to select colour
    private int selectorButtonWidth;
    private int selectorButtonHeight;
    // X and Y coordinates of these buttons
    private int selectorButtonX;
    private int selectorButtonY;

    // Width/height of target button, so we can ensure the button is positioned centrally (centre of target is over correct colour)
    private int targetButtonOffset;

    private SharedPreferences sharedPref;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ManualPickerViewModel manualPickerViewModel = new ViewModelProvider(this).get(ManualPickerViewModel.class);


        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);


        binding = FragmentManualPickerBinding.inflate(inflater, container, false);

        // Target button has width/height 20dp, calculate this in pixels
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        targetButtonOffset = Math.round(20 * metrics.density);


        // Get main and accent colour
        mainColour = getColourFromTheme(R.attr.mainColourAccent);
        accentColour = getColourFromTheme(R.attr.reversedColourAccent);

        // View tree observer to get height of view pager layouts, so we know what to set height of scroll button as
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove layout listener from setModeHEX
                binding.setModeHEX.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Get Y of HEX layout (this is the second layout in the view pager)
                scrollBarHeight = binding.setModeHEX.getY();

                // Set params of tabScrollBar (button indicating scroll of view pager)
                binding.tabScrollBar.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) scrollBarHeight));
            }
        };
        // Set layout listener to setModeHEX
        binding.setModeHEX.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

        // Get reference to RGB/HEX/HSV view pager
        viewPager = binding.pager;
        // Make collectionAdapter class
        ManualPickerCollectionAdapter collectionAdapter = new ManualPickerCollectionAdapter(this);
        // Set adapter to the view pager
        viewPager.setAdapter(collectionAdapter);

        // Offscreen page limit needs to be 2, otherwise the weird problem with edit texts losing focus happens
        // This keeps all pages loaded
        // (without this, when first clicking on an edit text, it would immediately lose focus)
        viewPager.setOffscreenPageLimit(2);


        // Listener to know when page has been changed, so we can set the height of the button scroll indicator (tabScrollBar)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Set Y position of tabScrollBar
                binding.tabScrollBar.setY((scrollBarHeight) * (position + positionOffset));
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                // Set colours of set mode to RGB/HEX/HSV buttons depending on what mode we are currently in
                // (mainColour for unselected, accentColour for selected)
                if (position == 0) {
                    binding.setModeRGB.setBackgroundColor(accentColour);
                    binding.setModeHEX.setBackgroundColor(mainColour);
                    binding.setModeHSV.setBackgroundColor(mainColour);
                }
                else if (position == 1) {
                    binding.setModeRGB.setBackgroundColor(mainColour);
                    binding.setModeHEX.setBackgroundColor(accentColour);
                    binding.setModeHSV.setBackgroundColor(mainColour);
                }
                else {
                    binding.setModeRGB.setBackgroundColor(mainColour);
                    binding.setModeHEX.setBackgroundColor(mainColour);
                    binding.setModeHSV.setBackgroundColor(accentColour);
                }
                super.onPageSelected(position);
            }
        });

        // Set onClickListeners for change mode buttons
        binding.setModeRGB.setOnClickListener(v -> viewPager.setCurrentItem(0));
        binding.setModeHEX.setOnClickListener(v -> viewPager.setCurrentItem(1));
        binding.setModeHSV.setOnClickListener(v -> viewPager.setCurrentItem(2));



        // Measure dimensions and coordinates of manualPickerSelector view and update coordinates of outline and inside buttons
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener2 = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove layout listener from manualPickerSelector
                binding.manualPickerSelector.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Set selectorButton dimensions/coordinates variables
                selectorButtonWidth = binding.manualPickerSelector.getWidth();
                selectorButtonHeight = binding.manualPickerSelector.getHeight();
                selectorButtonX = Math.round(binding.manualPickerSelector.getX());
                selectorButtonY = Math.round(binding.manualPickerSelector.getY());

                // Set x of target button from current S value (% of way across screen)
                int x = Math.round((Objects.requireNonNull(mainActivityViewModel.getS().getValue()) * selectorButtonWidth) / 100.0f);
                // Shift target button by x coordinate of selectorButton (takes into account margins of selectorButton)
                x += selectorButtonX;
                // Shift target button back by the offset to make it central
                x -= targetButtonOffset;
                // Set x of picker outline and inside
                binding.pickerOutline.setX(x);
                binding.pickerInside.setX(x);

                // Set y of target button from current V value (% of way across screen)
                int y = Math.round(selectorButtonHeight - (Objects.requireNonNull(mainActivityViewModel.getV().getValue()) * selectorButtonHeight) / 100.0f);
                // Shift target button by y coordinate of selectorButton (takes into account margins of selectorButton)
                y += selectorButtonY;
                // Shift target button back by the offset to make it central
                y -= targetButtonOffset;
                // Set y of picker outline and inside
                binding.pickerOutline.setY(y);
                binding.pickerInside.setY(y);
            }
        };
        // Set layout listener to manualPickerSelector
        binding.manualPickerSelector.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener2);


        // Set onTouchListener of manualPickerSelector, so we can set/update the currently selected colour correctly
        binding.manualPickerSelector.setOnTouchListener((v, event) -> {
            // Stop main view pager interpreting these events as a swipe
            binding.manualPickerSelector.getParent().requestDisallowInterceptTouchEvent(true);
            // Set x and y of touch position
            int x = Math.round(event.getX());
            int y = Math.round(event.getY());
            // Cap x and y at 0 and by the width and height of the selector button
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x > selectorButtonWidth) x = selectorButtonWidth;
            if (y > selectorButtonHeight) y = selectorButtonHeight;
            // Set S and V values as the % of x and y across the screen (top left = (0,0), bottom right = (100,100))
            mainActivityViewModel.setS(100 * x / selectorButtonWidth, true);
            mainActivityViewModel.setV(100 - (100 * y / selectorButtonHeight), true);
            return false;
        });

        // Bind observers to HSV LiveData
        bindObservers();

        // Detect when slider is changed, so we can update H of current colour
        binding.manualPickerSlider.addOnChangeListener((slider, value, fromUser) -> {
            // Stop main view pager interpreting these events as a swipe
            binding.manualPickerSlider.getParent().requestDisallowInterceptTouchEvent(true);

            // If update didn't come from user, don't update H, as this means an update of H has triggered this slider value change
            // (avoids infinite loop)
            if (fromUser) {
                // Set H value of current colour as the slider value and update other colour systems
                mainActivityViewModel.setH(Math.round(value), true);
            }
        });
        // Set slider params to match parent (parent height is hard-coded, currently at 30dp)
        RelativeLayout.LayoutParams paramsSlider = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // Centre slider in layout
        paramsSlider.addRule(RelativeLayout.CENTER_IN_PARENT);

        // Measure slider layout height and set thumb radius of slider to half this height
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener3 = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove layout listener from sliderLayout
                binding.sliderLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Set radius of slider thumb to half slider height
                binding.manualPickerSlider.setThumbRadius(binding.sliderLayout.getHeight() / 2);
            }
        };
        // Add layout listener to sliderLayout
        binding.sliderLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener3);


        // Set onClickListeners for action buttons
        binding.saveManualPickedColour.setOnClickListener(v -> saveColour());
        binding.saveCopyOfEditingColour.setOnClickListener(v -> saveCopyOfColour());
        binding.overwriteEditingColour.setOnClickListener(v -> overwriteColour());

        return binding.getRoot();
    }


    /**
     * Bind observers to LiveData objects in mainActivityViewModel
     */
    private void bindObservers() {
        // Observer for when H changes, so we can set position of slider
        final Observer<Integer> hObserver = newH -> {
            // Set colour of slider thumb and selector button (colour of top right corner)
            int colour = Color.HSVToColor(new float[]{newH, 1, 1});
            ColorStateList mainColourStateList = new ColorStateList(new int[][]{{}}, new int[]{colour});
            binding.manualPickerMainColour.setBackgroundTintList(mainColourStateList);
            binding.manualPickerSlider.setThumbTintList(mainColourStateList);
            // Set position of slider to value of newH
            binding.manualPickerSlider.setValue(newH);

            // Store current H value, so we can remember this when the app is next opened
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.previousManualPickerH), newH);
            editor.apply();
        };
        mainActivityViewModel.getH().observe(getViewLifecycleOwner(), hObserver);

        // Observer for when S changes, so we can set x coordinate of target buttons
        final Observer<Integer> sObserver = newS -> {
            int x = Math.round((newS * selectorButtonWidth) / 100.0f);
            // Shift target button by x coordinate of selectorButton (takes into account margins of selectorButton)
            x += selectorButtonX;
            // Shift target button back by the offset to make it central
            x -= targetButtonOffset;
            // Set x of picker outline and inside
            binding.pickerOutline.setX(x);
            binding.pickerInside.setX(x);

            // Store current S value, so we can remember this when the app is next opened
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.previousManualPickerS), newS);
            editor.apply();
        };
        mainActivityViewModel.getS().observe(getViewLifecycleOwner(), sObserver);

        // Observer for when V changes, so we can set y coordinate of target buttons
        final Observer<Integer> vObserver = newV -> {
            int y = Math.round((100 - newV) * selectorButtonHeight / 100.0f);
            // Shift target button by y coordinate of selectorButton (takes into account margins of selectorButton)
            y += selectorButtonY;
            // Shift target button back by the offset to make it central
            y -= targetButtonOffset;
            // Set y of picker outline and inside
            binding.pickerOutline.setY(y);
            binding.pickerInside.setY(y);

            // Store current V value, so we can remember this when the app is next opened
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.previousManualPickerV), newV);
            editor.apply();
        };
        mainActivityViewModel.getV().observe(getViewLifecycleOwner(), vObserver);


        // Observer for when current colour changes, so we can set the colour of preview button
        final Observer<Integer> currentColourObserver = newColour -> {
            // Create colour state list using new colour
            ColorStateList previewColourStateList = ColorStateList.valueOf(newColour);
            // Set colour of inside ring of target to this new colour
            binding.pickerInside.setBackgroundTintList(previewColourStateList);
            // If editing colour, set colour of previewColour to the colour we are currently editing
            // If not editing colour, set colour of previewColour to the current colour
            boolean editingColour = Boolean.TRUE.equals(mainActivityViewModel.getEditingColour().getValue());
            if (editingColour) {
                int editColour = Color.BLACK;
                try {
                    editColour = mainActivityViewModel.savedColourToEdit.getColour();
                }
                catch (NullPointerException ignored) {}
                binding.previewColour.setBackgroundColor(editColour);
            }
            else {
                binding.previewColour.setBackgroundColor(newColour);
            }
            // Set colour of editPreviewColour to the current colour
            binding.editPreviewColour.setBackgroundColor(newColour);

            // Set colour of outline of target to white/black, depending on currently selected colour
            // (uses same algorithm that determines text colour to decide this)
            int pickerOutlineColour = Color.BLACK;
            if (HelpersKt.backgroundRequiresLightText(Color.red(newColour), Color.green(newColour), Color.blue(newColour))) pickerOutlineColour = Color.WHITE;
            ColorStateList outlineColourStateList = ColorStateList.valueOf(pickerOutlineColour);
            binding.pickerOutline.setBackgroundTintList(outlineColourStateList);
        };
        mainActivityViewModel.getManualPickerCurrentColour().observe(getViewLifecycleOwner(), currentColourObserver);
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
        new Thread(() -> colourDAO.insertAll(savedColour)).start();

        // Inform user colour was successfully saved
        Snackbar.make(binding.manualPickerMainLayout, "Saved colour", Snackbar.LENGTH_LONG).show();
    }


    /**
     * Save copy of current selected colour when editing (old colour is unchanged)
     */
    private void saveCopyOfColour() {
        // Save the new colour
        saveColour();
        // Exit editing mode
        mainActivityViewModel.setEditingColour(false);
        int colour = Color.BLACK;
        try {
            colour = mainActivityViewModel.getManualPickerCurrentColour().getValue();
        }
        catch (NullPointerException ignored) {}
        binding.previewColour.setBackgroundColor(colour);
        // Make editing FABs invisible
        binding.editingColourSaveLayout.setVisibility(View.INVISIBLE);
        // Make save colour FAB visible
        binding.saveManualPickedColour.setVisibility(View.VISIBLE);
    }


    /**
     * Overwrites editing colour with current selected colour
     */
    private void overwriteColour() {
        // Get r value of current selected colour
        int r = Objects.requireNonNull(mainActivityViewModel.getR().getValue());;
        mainActivityViewModel.savedColourToEdit.setR(r);

        // Get g value of current selected colour
        int g = Objects.requireNonNull(mainActivityViewModel.getG().getValue());
        mainActivityViewModel.savedColourToEdit.setG(g);

        // Get b value of current selected colour
        int b = Objects.requireNonNull(mainActivityViewModel.getB().getValue());
        mainActivityViewModel.savedColourToEdit.setB(b);

        // Updated closest matches
        ArrayList<DatabaseColour> closestMatches = mainActivityViewModel.getClosestMatches(mainActivityViewModel.savedColourToEdit);
        if (closestMatches.size() == 4) {
            mainActivityViewModel.savedColourToEdit.setClosestMatch(closestMatches.get(0));
            mainActivityViewModel.savedColourToEdit.setFirstClosest(closestMatches.get(1));
            mainActivityViewModel.savedColourToEdit.setSecondClosest(closestMatches.get(2));
            mainActivityViewModel.savedColourToEdit.setThirdClosest(closestMatches.get(3));
        }
        else if (!closestMatches.isEmpty()) {
            mainActivityViewModel.savedColourToEdit.setClosestMatch(closestMatches.get(0));
        }

        // Update colour on database
        new Thread(() -> colourDAO.updateColour(mainActivityViewModel.savedColourToEdit.getId(), r, g, b)).start();
        // Inform user colour was successfully overwritten
        Snackbar.make(binding.manualPickerMainLayout, "Successfully edited colour", Snackbar.LENGTH_LONG).show();

        // Exit editing mode
        mainActivityViewModel.setEditingColour(false);
        binding.previewColour.setBackgroundColor(mainActivityViewModel.savedColourToEdit.getColour());
        // Make editing FABs invisible
        binding.editingColourSaveLayout.setVisibility(View.INVISIBLE);
        // Make save colour FAB visible
        binding.saveManualPickedColour.setVisibility(View.VISIBLE);
    }


    /**
     * Returns a colour from the theme using it's id
     * @param id - R.attr.COLOUR_NAME
     * @return TypedValue containing the colour (.data returns the colour)
     */
    private int getColourFromTheme(int id) {
        TypedValue value = new TypedValue();
        requireContext().getTheme().resolveAttribute(id, value, true);
        return value.data;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Remeasure height of manualPickerSelector view, this decreases when an ad is loaded (also then update y coordinate of outline and inside buttons
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener2 = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.manualPickerSelector.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                selectorButtonWidth = binding.manualPickerSelector.getWidth();
                selectorButtonHeight = binding.manualPickerSelector.getHeight();
                selectorButtonX = Math.round(binding.manualPickerSelector.getX());
                selectorButtonY = Math.round(binding.manualPickerSelector.getY());

                int x = Math.round((Objects.requireNonNull(mainActivityViewModel.getS().getValue()) * selectorButtonWidth) / 100.0f);
                x += selectorButtonX;
                x -= targetButtonOffset;
                binding.pickerOutline.setX(x);
                binding.pickerInside.setX(x);

                int y = Math.round(selectorButtonHeight - (Objects.requireNonNull(mainActivityViewModel.getV().getValue()) * selectorButtonHeight) / 100.0f);
                y += selectorButtonY;
                y -= targetButtonOffset;
                binding.pickerOutline.setY(y);
                binding.pickerInside.setY(y);
            }
        };
        binding.manualPickerSelector.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener2);

        boolean editingColour = Boolean.TRUE.equals(mainActivityViewModel.getEditingColour().getValue());
        if (editingColour) {
            binding.editingColourSaveLayout.setVisibility(View.VISIBLE);
            binding.saveManualPickedColour.setVisibility(View.INVISIBLE);
        } else {
            binding.editingColourSaveLayout.setVisibility(View.INVISIBLE);
            binding.saveManualPickedColour.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        int colour = Color.BLACK;
        try {
            colour = mainActivityViewModel.getManualPickerCurrentColour().getValue();
        }
        catch (NullPointerException ignored) {}
        binding.previewColour.setBackgroundColor(colour);
        binding.editPreviewColour.setBackgroundColor(colour);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}