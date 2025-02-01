package com.skeensystems.colorpicker.ui.saved;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.skeensystems.colorpicker.MainActivity.colourDAO;
import static com.skeensystems.colorpicker.MainActivity.mainActivityViewModel;
import static com.skeensystems.colorpicker.MainActivity.portrait;
import static com.skeensystems.colorpicker.MainActivity.savedColours;
import static com.skeensystems.colorpicker.MainActivity.screenWidth;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.skeensystems.colorpicker.R;
import com.skeensystems.colorpicker.database.Colour;
import com.skeensystems.colorpicker.database.DatabaseColour;
import com.skeensystems.colorpicker.database.SavedColour;
import com.skeensystems.colorpicker.databinding.FragmentSavedColoursBinding;
import com.skeensystems.colorpicker.ui.DropDownButton;
import com.skeensystems.colorpicker.ui.SelectableButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class SavedColoursFragment extends Fragment {

    private FragmentSavedColoursBinding binding;

    // Margin for each saved colour button
    int margin = 5;

    // Corner radius for saved colours when not selected
    float cornerRadius = 0;

    // Number of columns to display the saved colours in
    int numberOfCols = 4;

    // Stores width and height of each button (minus margins on each side)
    private int buttonDims;

    // The system "short" animation time duration in milliseconds
    // This duration is ideal for subtle animations or animations that occur frequently.
    private int shortAnimationDuration;
    // The system "medium" animation time duration in milliseconds
    private int mediumAnimationDuration;

    // True if colour inspection view is active, false otherwise
    private boolean inspectingColour;
    // True if confirm delete layout is active, disables bottom action buttons
    private boolean consideringDelete;

    // Height of bottom actions layout (used by the animation to know how tall to end up)
    private int inspectColourDetailsLayoutHeight = 0;

    // Width of saved colours layout (used to calculate size of saved colour buttons)
    private int savedColoursContainerWidth = 0;

    // Coordinates of view that is currently expanded (so we can shrink preview)
    private int inspectingX = 0;
    private int inspectingY = 0;

    // 0 = newest to oldest
    // 1 = oldest to newest
    // 2 = by colour
    private int displayOrder = 0;

    // 0 = no filter
    // 1 = favorite colours
    private int filterValue = 0;

    // Current colour the user is inspecting details of
    private SavedColour currentInspectingSavedColour;

    // True if layout needs to be redrawn when hiding colour preview
    private boolean needLayoutRefresh;

    // This must be Long, not SavedColour, to avoid duplicate colours overwriting
    private HashMap<Long, SelectableButton> savedColourButtons;

    // Selecting mode values
    private final int NOT_SELECTING_MODE = 0;
    private final int SELECTING_MODE_WAITING_FOR_FIRST_RELEASE = 1;
    private final int SELECTING_MODE = 2;

    // Initial selecting mode value
    private int selectingMode = NOT_SELECTING_MODE;

    // Contains all buttons that are currently selected and their corresponding SavedColour
    private HashMap<SelectableButton, SavedColour> selectedColours;


    // Copy type values
    private final int ALL = -1;
    private final int NAME = 0;
    private final int HEX = 1;
    private final int RGB = 2;
    private final int HSV = 3;
    private final int HSL = 4;
    private final int CMYK = 5;


    // Sort values
    private final int NEWEST_TO_OLDEST = 0;
    private final int OLDEST_TO_NEWEST = 1;
    private final int BY_COLOUR = 2;

    //TODO filter values


    // Padding for saved colours layout so all colours can be seen, even when select actions container is visible
    private Button selectActionsPadding;


    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inspectingColour = false;
        consideringDelete = false;
        selectedColours = new HashMap<>();
        savedColourButtons = new HashMap<>();

        //TODO decide on this
        // if (!portrait) numberOfCols = 6;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        // Set default corner radius to 10dp
        cornerRadius = 10 * metrics.density;

        SavedColoursViewModel savedColoursViewModel = new ViewModelProvider(this).get(SavedColoursViewModel.class);

        binding = FragmentSavedColoursBinding.inflate(inflater, container, false);

        //TODO settings
        binding.openSettingsButton.setOnClickListener(v -> {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(R.layout.fragment_manual_picker);
            bottomSheetDialog.show();
        });


        // Set up drop down views (to display information about similar and complementary colours to the inspecting colour on click)
        binding.similar1.setDropDownButton((Button) inflater.inflate(R.layout.drop_down_button, binding.similar1, false));
        binding.similar1.setDropDownView(inflater.inflate(R.layout.drop_down_layout, binding.similar1, false));
        binding.similar2.setDropDownButton((Button) inflater.inflate(R.layout.drop_down_button, binding.similar2, false));
        binding.similar2.setDropDownView(inflater.inflate(R.layout.drop_down_layout, binding.similar2, false));
        binding.similar3.setDropDownButton((Button) inflater.inflate(R.layout.drop_down_button, binding.similar3, false));
        binding.similar3.setDropDownView(inflater.inflate(R.layout.drop_down_layout, binding.similar3, false));

        binding.similarComplementary1.setDropDownButton((Button) inflater.inflate(R.layout.drop_down_button, binding.similarComplementary1, false));
        binding.similarComplementary1.setDropDownView(inflater.inflate(R.layout.drop_down_layout, binding.similarComplementary1, false));
        binding.similarComplementary2.setDropDownButton((Button) inflater.inflate(R.layout.drop_down_button, binding.similarComplementary2, false));
        binding.similarComplementary2.setDropDownView(inflater.inflate(R.layout.drop_down_layout, binding.similarComplementary2, false));
        binding.similarComplementary3.setDropDownButton((Button) inflater.inflate(R.layout.drop_down_button, binding.similarComplementary3, false));
        binding.similarComplementary3.setDropDownView(inflater.inflate(R.layout.drop_down_layout, binding.similarComplementary3, false));

        // For some reason this is needed, as otherwise all of these layouts have maxWidth set to 712?
        binding.similar1.setMaxWidth(screenWidth);
        binding.similar2.setMaxWidth(screenWidth);
        binding.similar3.setMaxWidth(screenWidth);
        binding.similarComplementary1.setMaxWidth(screenWidth);
        binding.similarComplementary2.setMaxWidth(screenWidth);
        binding.similarComplementary3.setMaxWidth(screenWidth);

        // Auto animate updates to these views
        setAutoAnimate(binding.complementaryColoursTitle);
        setAutoAnimate(binding.savedColoursContainer);
        setAutoAnimate(binding.multiSelectParentForAnimation);
        setAutoAnimate(binding.scrollView);

        // Hide inspected colour when title or spinners clicked
        binding.touchOutside.setOnClickListener(v -> hideInspectedColour());

        // Calculate Y position of binding.similarColoursLayout when it has been drawn (so we know offset of DropDownButtons when they are used)
        ViewTreeObserver.OnGlobalLayoutListener similarColoursLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.similarColoursLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                binding.similar1.setScrollParams(binding.inspectColourScrollView, binding.similarColoursLayout.getY());
                binding.similar2.setScrollParams(binding.inspectColourScrollView, binding.similarColoursLayout.getY());
                binding.similar3.setScrollParams(binding.inspectColourScrollView, binding.similarColoursLayout.getY());

                binding.similarComplementary1.setScrollParams(binding.inspectColourScrollView, binding.similarColoursLayout.getY());
                binding.similarComplementary2.setScrollParams(binding.inspectColourScrollView, binding.similarColoursLayout.getY());
                binding.similarComplementary3.setScrollParams(binding.inspectColourScrollView, binding.similarColoursLayout.getY());
            }
        };
        binding.similarColoursLayout.getViewTreeObserver().addOnGlobalLayoutListener(similarColoursLayoutListener);


        // Enable copying of colour when detail buttons clicked
        binding.hexTitle.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, HEX));
        binding.hexDetails.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, HEX));
        binding.rgbTitle.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, RGB));
        binding.rgbDetails.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, RGB));
        binding.hsvTitle.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, HSV));
        binding.hsvDetails.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, HSV));
        binding.hslTitle.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, HSL));
        binding.hslDetails.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, HSL));
        binding.cmykTitle.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, CMYK));
        binding.cmykDetails.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, CMYK));

        binding.copyInspectedColourButton.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, ALL));
        binding.inspectColourApproxName.setOnClickListener(view -> copySingleColour(currentInspectingSavedColour, NAME));


        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        // Load displayOrder and filterValue from previous sessions of the app (if they exist), otherwise use 0 as the default value
        displayOrder = sharedPref.getInt(getString(R.string.settingsSortChoice), 0);
        filterValue = sharedPref.getInt(getString(R.string.settingsFilterChoice), 0);

        // Setup sort drop down menu
        List<String> sortItems = Arrays.asList(
                getResources().getString(R.string.newestFirst),
                getResources().getString(R.string.oldestFirst),
                getResources().getString(R.string.byColour));

        ArrayAdapter<String> adapterSort = new ArrayAdapter<>(requireContext(), R.layout.list_item, sortItems);
        AutoCompleteTextView sortView = (AutoCompleteTextView) binding.sort.getEditText();
        if (sortView != null) {
            // Set initial sort order text value
            if (displayOrder == NEWEST_TO_OLDEST) sortView.setText(getResources().getString(R.string.newestFirst));
            else if (displayOrder == OLDEST_TO_NEWEST) sortView.setText(getResources().getString(R.string.oldestFirst));
            else sortView.setText(getResources().getString(R.string.byColour));

            // Set adapter to sortView
            sortView.setAdapter(adapterSort);

            // Set listener to detect when an item is selected by the user
            sortView.setOnItemClickListener((adapterView, view, position, id) -> {
                displayOrder = position;

                // Store current sort value, so we can remember this when the app is next opened
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.settingsSortChoice), position);
                editor.apply();

                // Hide inspected colour when sort order is changed
                if (inspectingColour) hideInspectedColour();
                // Exit selecting mode when sort order is changed
                exitSelectingMode();
                // Recalculate positions of saved colours when sort order is changed
                drawSavedColoursGrid();
            });
        }

        // Setup filter drop down menu
        List<String> filterItems = Arrays.asList(
                getResources().getString(R.string.noFilter),
                getResources().getString(R.string.favorites));

        ArrayAdapter<String> adapterFilter = new ArrayAdapter<>(requireContext(), R.layout.list_item, filterItems);
        AutoCompleteTextView filterView = (AutoCompleteTextView) binding.filter.getEditText();
        if (filterView != null) {
            // Set initial filter text value
            if (filterValue == 0) filterView.setText(getResources().getString(R.string.noFilter));
            else filterView.setText(getResources().getString(R.string.favorite));

            // Set adapter to filterView
            filterView.setAdapter(adapterFilter);

            // Set listener to detect when an item is selected by the user
            filterView.setOnItemClickListener((adapterView, view, position, id) -> {
                filterValue = position;

                // Store current filter value, so we can remember this when the app is next opened
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.settingsFilterChoice), position);
                editor.apply();

                // Hide inspected colour when filter is changed
                if (inspectingColour) hideInspectedColour();
                // Exit selecting mode when filter is changed
                exitSelectingMode();
                // Recalculate positions of saved colours when filter is changed
                drawSavedColoursGrid();
            });
        }


        // Calculate width of binding.savedColoursContainer when it has been drawn (so we can calculate size of saved colour buttons)
        ViewTreeObserver.OnGlobalLayoutListener savedColoursLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.savedColoursContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                savedColoursContainerWidth = binding.savedColoursContainer.getWidth();

                // Calculate the column width
                int columnWidth = savedColoursContainerWidth / numberOfCols;
                // Calculate width and height of each button (minus margins on each side)
                buttonDims = columnWidth - 2 * margin;

                if (portrait) {
                    // Make and set the params for the animation view
                    RelativeLayout.LayoutParams inspectColourParams = new RelativeLayout.LayoutParams(buttonDims, buttonDims);
                    binding.inspectColourAnimation.setLayoutParams(inspectColourParams);
                }

                drawSavedColoursGrid();
            }
        };
        binding.savedColoursContainer.getViewTreeObserver().addOnGlobalLayoutListener(savedColoursLayoutListener);


        // Calculate height of binding.inspectColourDetailsLayout when it has been drawn
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.inspectColourDetailsLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                inspectColourDetailsLayoutHeight = binding.inspectColourDetailsLayout.getHeight();
            }
        };
        binding.inspectColourDetailsLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

        // Set onClickListeners for inspect colour actions
        binding.hideInspectedColourButton.setOnClickListener(view -> hideInspectedColour());
        binding.favoriteInspectedColourButton.setOnClickListener(view -> favoriteInspectedColour());
        binding.editInspectedColourButton.setOnClickListener(view -> editInspectedColour());
        binding.deleteInspectedColourButton.setOnClickListener(view -> confirmDeleteInspectedColour());

        // Set onClickListeners for delete inspected colour (single delete) layout buttons
        binding.cancelDelete.setOnClickListener(view -> hideConfirmDeleteInspectedColour());
        binding.confirmDelete.setOnClickListener(view -> deleteInspectedColour());


        // Set drawableTint and textColour of select action buttons
        TypedValue value = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.defaultTextColour, value, true);
        setDrawableTintAndTextColour(binding.unselectSelectedColours, value.data);
        setDrawableTintAndTextColour(binding.deleteSelectedColours, value.data);
        setDrawableTintAndTextColour(binding.favoriteSelectedColours, value.data);
        setDrawableTintAndTextColour(binding.unFavoriteSelectedColours, value.data);
        setDrawableTintAndTextColour(binding.copySelectedColours, value.data);

        // Set onClickListeners for select action buttons
        binding.unselectSelectedColours.setOnClickListener(view -> {
            if (!consideringDelete) exitSelectingMode();
        });
        binding.deleteSelectedColours.setOnClickListener(view -> {
            if (!consideringDelete) confirmDeleteSelectedColours();
        });
        binding.favoriteSelectedColours.setOnClickListener(view -> {
            if (!consideringDelete) updateFavorites(true);
        });
        binding.unFavoriteSelectedColours.setOnClickListener(view -> {
            if (!consideringDelete) updateFavorites(false);
        });
        binding.copySelectedColours.setOnClickListener(view -> {
            if (!consideringDelete) {
                copyMultipleColours(selectedColours.values());
                exitSelectingMode();
            }
        });

        // Set onClickListeners for delete all layout buttons
        binding.cancelDeleteAll.setOnClickListener(view -> hideConfirmDeleteSelectedColours());
        binding.confirmDeleteAll.setOnClickListener(view -> deleteAllSelectedColours());


        // Set onTouchListener of manualPickerSelector, so we can set/update the currently selected colour correctly
        binding.multiSelectScrollView.setOnTouchListener((v, event) -> {
            // Stop main view pager interpreting these events as a swipe
            binding.multiSelectScrollView.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        // Make bottom padding for when select actions layout is visible
        selectActionsPadding = new Button(requireContext());
        // Set colour to invisible
        selectActionsPadding.setBackgroundColor(0x00000000);
        // Set height of selectActionsPadding to be height of binding.multiSelectActionsContainer (when it has been drawn)
        ViewTreeObserver.OnGlobalLayoutListener selectActionLayoutPaddingListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                selectActionsPadding.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                selectActionsPadding.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, binding.multiSelectActionsContainer.getHeight()));
            }
        };
        selectActionsPadding.getViewTreeObserver().addOnGlobalLayoutListener(selectActionLayoutPaddingListener);


        // Get the default short and medium animation duration to be used for various animations
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        return binding.getRoot();
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
        new Thread(() -> colourDAO.insertAll(savedColour)).start();

        // Refresh saved colours layout when inspected colour is hidden
        needLayoutRefresh = true;

        // Inform user colour was successfully saved
        Snackbar.make(binding.inspectColourDetailsLayout, "Saved colour", Snackbar.LENGTH_LONG).show();
    }

    /**
     * Copy current inspecting colour details to clipboard
     * @param detailsToCopy -1 = all colour details, 0 = colour name, 1 = hex, 2 = rgb, 3 = hsv, 4 = hsl, 5 = cmyk
     */
    private void copySingleColour(@NonNull Colour colour, int detailsToCopy) {
        // Gets a handle to the clipboard service
        ClipboardManager clipboard = (ClipboardManager)requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // Create a new text clip to put on the clipboard
        String text = "";
        ClipData clip = ClipData.newPlainText("", "");
        // Create text with desired data from the colour to copy
        if (detailsToCopy == ALL) {
            text = colour.getName() +
                    "\nHEX " + colour.getHEXString() +
                    "\nRGB " + colour.getRGBString() +
                    "\nHSV " + colour.getHSVString() +
                    "\nHSL " + colour.getHSLString() +
                    "\nCMYK " + colour.getCMYKString();
            clip = ClipData.newPlainText("Colour", text);
            text = "Copied!";
        }
        else if (detailsToCopy == NAME) {
            text = colour.getName();
            clip = ClipData.newPlainText("Colour name", text);
        }
        else if (detailsToCopy == HEX) {
            text = "HEX " + colour.getHEXString();
            clip = ClipData.newPlainText("HEX", text);
            text = "Copied: \"" + text + "\"";
        }
        else if (detailsToCopy == RGB) {
            text = "RGB " + colour.getRGBString();
            clip = ClipData.newPlainText("RGB", text);
            text = "Copied: \"" + text + "\"";
        }
        else if (detailsToCopy == HSV) {
            text = "HSV " + colour.getHSVString();
            clip = ClipData.newPlainText("HSV", text);
            text = "Copied: \"" + text + "\"";
        }
        else if (detailsToCopy == HSL) {
            text = "HSL " + colour.getHSLString();
            clip = ClipData.newPlainText("HSL", text);
            text = "Copied: \"" + text + "\"";
        }
        else if (detailsToCopy == CMYK) {
            text = "CMYK " + colour.getCMYKString();
            clip = ClipData.newPlainText("CMYK", text);
            text = "Copied: \"" + text + "\"";
        }
        // Set the clipboard's primary clip
        clipboard.setPrimaryClip(clip);
        // Only show a toast for Android 12 and lower
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            Snackbar.make(binding.inspectColourDetailsLayout, text, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Copy list of colour details to clipboard
     */
    private void copyMultipleColours(@NonNull Collection<SavedColour> colours) {
        // Gets a handle to the clipboard service
        ClipboardManager clipboard = (ClipboardManager)requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // Create a new text clip to put on the clipboard
        StringBuilder text = new StringBuilder();
        for (Colour colour : colours) {
            text.append(colour.getName());
            text.append("\nHEX ");
            text.append(colour.getHEXString());
            text.append("\nRGB ");
            text.append(colour.getRGBString());
            text.append("\nHSV ");
            text.append(colour.getHSVString());
            text.append("\nHSL ");
            text.append(colour.getHSLString());
            text.append("\nCMYK ");
            text.append(colour.getCMYKString());
            text.append("\n\n");
        }
        ClipData clip = ClipData.newPlainText("Colours", text);
        // Set the clipboard's primary clip
        clipboard.setPrimaryClip(clip);
        // Only show a toast for Android 12 and lower
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            String infoText = "Copied " + colours.size() + " colours";
            if (colours.size() == 1) infoText = "Copied 1 colour";
            Snackbar.make(binding.inspectColourDetailsLayout, infoText, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Calculates index of the savedColour based off position in the grid
     * @param index i * numberOfCols + j
     * @param numberOfSavedColours total saved colours (to allow traversal of savedColours in reverse order)
     * @return index of colour in savedColours to use
     */
    private int calculateIndex(int index, int numberOfSavedColours) {
        // Newest to oldest
        if (displayOrder == NEWEST_TO_OLDEST) return numberOfSavedColours - 1 - index;
        // Oldest to newest
        else if (displayOrder == OLDEST_TO_NEWEST) return index;
        // By colour
        else if (displayOrder == BY_COLOUR) return index;
        // Default = oldest to newest (this shouldn't ever be called)
        else return index;
    }

    /**
     * Filters savedColours based on the current filters applied
     * @return filtered ArrayList
     */
    private ArrayList<SavedColour> filterColours() {
        // No filter
        if (filterValue == 0) return new ArrayList<>(savedColours);
        // Favorite colours
        else if (filterValue == 1) {
            ArrayList<SavedColour> favoriteSavedColours = new ArrayList<>();
            for (SavedColour savedColour : savedColours) {
                if (savedColour.getFavorite()) favoriteSavedColours.add(savedColour);
            }
            return favoriteSavedColours;
        }
        // Default = no filter
        else return new ArrayList<>(savedColours);
    }


    /**
     * Creates a SelectableButton for the inputted savedColour
     * @param savedColour saved colour to apply to the button
     * @param buttonParams params to apply to the button
     * @param i x pos in grid
     * @param j y pos in grid
     */
    private void createSavedColourButton(SavedColour savedColour, RelativeLayout.LayoutParams buttonParams, int i, int j) {
        // Make a button for each saved colour and give it the desired attributes
        SelectableButton selectableButton = new SelectableButton(requireContext());
        selectableButton.setLayoutParams(buttonParams);

        // Set background of selectableButton and set it's colour to be the current savedColour
        selectableButton.colour.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.saved_colour_background, requireActivity().getTheme()));
        ColorStateList backgroundColour = new ColorStateList(new int[][]{{}}, new int[]{savedColour.getColour()});
        selectableButton.colour.setBackgroundTintList(backgroundColour);
        // Set elevation to 1000, so this is above all other buttons (most importantly invisible buttons)
        selectableButton.setElevation(1000);

        // Set tick and favoriteIcon backgrounds, and set their colours to dark/light, so they can be seen against the background
        selectableButton.setTickBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_tick, requireActivity().getTheme()));
        selectableButton.setFavoriteIndicatorBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_filled, requireActivity().getTheme()));
        selectableButton.setBackgroundTint(savedColour.getTextColour());

        // Set default (unselected) corner radius of selectableButton
        selectableButton.setUnselectedCornerRadius(cornerRadius);
        // Set whether selectableButton should have a favoriteIcon
        selectableButton.updateFavorite(savedColour.getFavorite());
        // Set duration of all animations of selectableButton
        selectableButton.setAnimationDuration(shortAnimationDuration);

        // Set coordinates of selectableButton
        selectableButton.setX((2 * margin + buttonDims) * j);
        selectableButton.setY((2 * margin + buttonDims) * i);

        selectableButton.colour.setOnClickListener(view -> {
            // Do nothing if button has no background (this is because it is invisible i.e. not included in an applied filter)
            if (selectableButton.colour.getBackground() != null) {
                // Select the button if in selecting mode
                if (selectingMode == SELECTING_MODE)
                    selectColour(selectableButton, savedColour);
                // If this button has just been long pressed, then don't try and select it again
                else if (selectingMode == SELECTING_MODE_WAITING_FOR_FIRST_RELEASE)
                    selectingMode = SELECTING_MODE;
                // Otherwise, make button expand into detailed view when clicked
                else inspectColour(selectableButton, savedColour);
            }
        });

        // Set onLongClickListener to select the button
        selectableButton.colour.setOnLongClickListener(view -> {
            if (selectingMode == NOT_SELECTING_MODE && !inspectingColour && selectableButton.colour.getBackground() != null) {
                enterSelectingMode();
                selectableButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                selectColour(selectableButton, savedColour);
            }
            return false;
        });

        // Add SelectableButton to the savedColourButtons, so we can reposition it later on
        savedColourButtons.put(savedColour.getId(), selectableButton);

        // Add SelectableButton to the RelativeLayout
        binding.savedColoursContainer.addView(selectableButton);
    }

    /**
     * Draws all the saved colour buttons in the correct positions (taking into account sort order and any filters)
     */
    private void drawSavedColoursGrid() {
        // Filter saved colours
        ArrayList<SavedColour> currentColoursToDisplay = filterColours();

        // Sort the list by colour if required
        if (displayOrder == BY_COLOUR) {
            Collections.sort(currentColoursToDisplay, new Comparator<SavedColour>() {
                @Override
                public int compare(SavedColour o1, SavedColour o2) {
                    return o1.getSortValue().compareTo(o2.getSortValue());
                }
            });
        }

        // Get size of the saved colour list (the number of saved colours)
        int numberOfSavedColours = currentColoursToDisplay.size();
        // Calculate number of rows needed to display all the colours
        int rows = numberOfSavedColours / numberOfCols;
        // If colours don't fit exactly into rows, add extra row at the bottom
        if (rows * numberOfCols != numberOfSavedColours) rows += 1;
        // Make the params to be used for each of the saved colour buttons
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(buttonDims, buttonDims);
        buttonParams.setMargins(margin, margin, margin, margin);
        // Each iteration makes a new LinearLayout for the row
        for (int i = 0; i < rows; i++) {
            // Add each button
            for (int j = 0; j < numberOfCols; j++) {
                // Skip the iteration of the loop if no colour exists (happens at the end if colours don't fit exactly into the rows)
                if (i * numberOfCols + j >= numberOfSavedColours) continue;
                // Get the colour for this button (using the correct index, calculated in "calculateIndex", using "displayOrder")
                SavedColour savedColour = currentColoursToDisplay.get(calculateIndex(i * numberOfCols + j, numberOfSavedColours));

                SelectableButton selectableButton = savedColourButtons.get(savedColour.getId());
                // If a SelectableButton has not yet been created for the savedColour, create it
                if (selectableButton == null) {
                    createSavedColourButton(savedColour, buttonParams, i, j);
                }
                // Otherwise, just reposition it
                else {
                    // Animate button change of X and Y to new coordinates
                    selectableButton.animate().xBy(margin + (2 * margin + buttonDims) * j - selectableButton.getX());
                    selectableButton.animate().yBy(margin + (2 * margin + buttonDims) * i - selectableButton.getY());
                    // Set background of saved colour button (as it might have been removed in a filter application)
                    selectableButton.colour.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.saved_colour_background, requireActivity().getTheme()));
                    ColorStateList backgroundColour = new ColorStateList(new int[][]{{}}, new int[]{savedColour.getColour()});
                    selectableButton.colour.setBackgroundTintList(backgroundColour);
                    // Set elevation to 1000, so this is above all other buttons (most importantly invisible buttons)
                    selectableButton.setElevation(1000);
                }

                // Set height of savedColourContainer, so we can see all the saved colour buttons
                ViewGroup.LayoutParams params = binding.savedColoursContainer.getLayoutParams();
                params.height = (2 * margin + buttonDims) * (i + 1);
                binding.savedColoursContainer.setLayoutParams(params);
            }
        }

        // Hide all buttons that are not included in the filter
        for (SavedColour colour : savedColours) {
            if (!currentColoursToDisplay.contains(colour)) {
                SelectableButton selectableButton = savedColourButtons.get(colour.getId());
                // THIS MUST BE ALWAYS VISIBLE, OTHERWISE FILTERING BREAKS BUTTONS THAT HAVE BEEN SELECTED
                // TODO actually figure out why selected buttons broke when filtered
                if (selectableButton != null) {
                    selectableButton.colour.setBackground(null);
                    selectableButton.setElevation(0);
                }
            }
        }
    }

    /**
     * Toggles selected status of inputted SelectableButton
     * @param button SelectableButton to select
     * @param savedColour colour of the SelectableButton being selected (so we can carry out the actions on this colour)
     */
    private void selectColour(SelectableButton button, SavedColour savedColour) {
        // Button is already selected, so deselect it
        if (selectedColours.get(button) != null) {
            selectedColours.remove(button);
            button.deselect();
            // If there are now no more selected buttons, exit selecting mode
            if (selectedColours.size() == 0) {
                exitSelectingMode();
            }
        }
        // Button is not selected, so select it
        else {
            selectedColours.put(button, savedColour);
            button.select();
        }
    }

    /**
     * Enter selecting mode, so now onClick of saved colour buttons will select them
     */
    private void enterSelectingMode() {
        selectedColours = new HashMap<>();
        // Set selecting mode to SELECTING_MODE_WAITING_FOR_FIRST_RELEASE
        // This stops the first button being selected twice
        selectingMode = SELECTING_MODE_WAITING_FOR_FIRST_RELEASE;

        // Set scroll position of select actions container to start of the layout
        binding.multiSelectScrollView.setScrollX(0);
        // Make select actions container visible
        binding.multiSelectActionsContainer.setVisibility(VISIBLE);
        // Add bottom padding (so all colours can be seen, even when actions container is visible)
        //TODO can this instead toggle visibility from VISIBLE to GONE
        binding.selectActionsPaddingContainer.addView(selectActionsPadding);
    }

    /**
     * Deselect all currently selected buttons and stop selecting of new buttons by click (need a long click to re-enter selecting mode)
     */
    private void exitSelectingMode() {
        // Deselect all currently selected buttons
        for (SelectableButton button : selectedColours.keySet()) {
            button.deselect();
        }
        // Empty selectedColours HashMap
        selectedColours = new HashMap<>();
        // Set selecting mode to NOT_SELECTING_MODE
        selectingMode = NOT_SELECTING_MODE;

        // Make select actions container invisible
        binding.multiSelectActionsContainer.setVisibility(GONE);
        // Remove bottom padding (so all colours can be seen, even when actions container is visible)
        //TODO can this instead toggle visibility from VISIBLE to GONE
        binding.selectActionsPaddingContainer.removeView(selectActionsPadding);
    }

    /**
     * Updates all selected buttons with favorite value
     * @param favorite true = mark all as favorite, false = mark all as not favorite
     */
    private void updateFavorites(boolean favorite) {
        for (SavedColour savedColour : selectedColours.values()) {
            // Update SavedColour object with new favorite status
            savedColour.setFavorite(favorite);

            // Update relevant button to display favorite status
            SelectableButton button = savedColourButtons.get(savedColour.getId());
            if (button != null) button.updateFavorite(favorite);

            // Update new favorite status on database
            new Thread(() -> colourDAO.updateFavoriteColour(savedColour.getId(), savedColour.getFavorite())).start();
        }
        // Exit selecting mode
        exitSelectingMode();
        // If filtering favorite colours, recalculate position of saved colour buttons
        if (filterValue == 1) drawSavedColoursGrid();
    }


    /**
     * Assigns inputted colour's properties to the inputted DropDownButton
     */
    private void displaySimilarColour(@NonNull DatabaseColour colour, @NonNull DropDownButton colourLayout) {
        // Set background of DropDownButton to be the inputted colour
        ColorStateList backgroundColour = new ColorStateList(new int[][]{{}}, new int[]{colour.getColour()});
        colourLayout.setBackgroundTintList(backgroundColour);
        // Set text of title button to be the name of the colour
        colourLayout.dropDownButton.setText(colour.toString());

        // Set text colour of title button to be dark/light, so it can be seen against the background
        ColorStateList colourStateList = new ColorStateList(new int[][]{{}}, new int[]{colour.getTextColour()});
        colourLayout.dropDownButton.setTextColor(colour.getTextColour());

        // Set colour of arrow to be dark/light, so it can be seen against the background
        colourLayout.arrow.setImageTintList(colourStateList);
        // Set background of the arrow (it's an avd)
        colourLayout.arrow.setImageResource(R.drawable.anim_arrow_to_up);

        // Make the layout visible
        colourLayout.setVisibility(VISIBLE);
        // Make the drop down part of the layout invisible
        colourLayout.dropDownView.setVisibility(GONE);

        // Set background tint and text colour of action buttons so that they can be seen against the current colour (as it is the background)
        setDrawableTintAndTextColour(colourLayout.findViewById(R.id.saveSimilarColourButton), colour.getTextColour());
        setDrawableTintAndTextColour(colourLayout.findViewById(R.id.copySimilarColourButton), colour.getTextColour());
        //((ExtendedFloatingActionButton)colourLayout.findViewById(R.id.saveSimilarColourButton)).setSupportCompoundDrawablesTintList(colourStateList);
        //((ExtendedFloatingActionButton)colourLayout.findViewById(R.id.copySimilarColourButton)).setSupportCompoundDrawablesTintList(colourStateList);
        colourLayout.findViewById(R.id.saveSimilarColourButton).setOnClickListener(view -> saveColour(colour));
        colourLayout.findViewById(R.id.copySimilarColourButton).setOnClickListener(view -> copySingleColour(colour, ALL));

        // Enable copying of colour when detail buttons clicked
        colourLayout.findViewById(R.id.hexTitle).setOnClickListener(view -> copySingleColour(colour, HEX));
        colourLayout.findViewById(R.id.hexDetails).setOnClickListener(view -> copySingleColour(colour, HEX));
        colourLayout.findViewById(R.id.rgbTitle).setOnClickListener(view -> copySingleColour(colour, RGB));
        colourLayout.findViewById(R.id.rgbDetails).setOnClickListener(view -> copySingleColour(colour, RGB));
        colourLayout.findViewById(R.id.hsvTitle).setOnClickListener(view -> copySingleColour(colour, HSV));
        colourLayout.findViewById(R.id.hsvDetails).setOnClickListener(view -> copySingleColour(colour, HSV));
        colourLayout.findViewById(R.id.hslTitle).setOnClickListener(view -> copySingleColour(colour, HSL));
        colourLayout.findViewById(R.id.hslDetails).setOnClickListener(view -> copySingleColour(colour, HSL));
        colourLayout.findViewById(R.id.cmykTitle).setOnClickListener(view -> copySingleColour(colour, CMYK));
        colourLayout.findViewById(R.id.cmykDetails).setOnClickListener(view -> copySingleColour(colour, CMYK));

        setColourDetails(colour, colourLayout);
    }

    /**
     * Sets the colour details to the inputted layout (layout must have all the ids defined)
     * @param colour details of this colour will be set to the layout
     * @param layout layout to set the details of the colour to
     */
    private void setColourDetails(Colour colour, View layout) {
        // Set text colour of HEX, RGB, HSV, HSL and CMYK information
        ((Button)layout.findViewById(R.id.hexTitle)).setTextColor(colour.getTextColour());
        ((Button)layout.findViewById(R.id.hexDetails)).setTextColor(colour.getTextColour());
        ((Button)layout.findViewById(R.id.rgbTitle)).setTextColor(colour.getTextColour());
        ((Button)layout.findViewById(R.id.rgbDetails)).setTextColor(colour.getTextColour());
        ((Button)layout.findViewById(R.id.hsvTitle)).setTextColor(colour.getTextColour());
        ((Button)layout.findViewById(R.id.hsvDetails)).setTextColor(colour.getTextColour());
        ((Button)layout.findViewById(R.id.hslTitle)).setTextColor(colour.getTextColour());
        ((Button)layout.findViewById(R.id.hslDetails)).setTextColor(colour.getTextColour());
        ((Button)layout.findViewById(R.id.cmykTitle)).setTextColor(colour.getTextColour());
        ((Button)layout.findViewById(R.id.cmykDetails)).setTextColor(colour.getTextColour());

        // Set HEX, RGB, HSV, HSL and CMYK information
        ((Button)layout.findViewById(R.id.hexDetails)).setText(colour.getHEXString());
        ((Button)layout.findViewById(R.id.rgbDetails)).setText(colour.getRGBString());
        ((Button)layout.findViewById(R.id.hsvDetails)).setText(colour.getHSVString());
        ((Button)layout.findViewById(R.id.hslDetails)).setText(colour.getHSLString());
        ((Button)layout.findViewById(R.id.cmykDetails)).setText(colour.getCMYKString());
    }


    /**
     * Sets the layout to auto animate layout changes
     * @param layout layout to set the auto animate property to
     */
    private void setAutoAnimate(ViewGroup layout) {
        layout.setLayoutTransition(new LayoutTransition());
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    /**
     * Sets drawableTint and textColour of the provided ExtendedFloatingActionButton
     * @param button Extended-FAB to set colour to
     * @param colour colour to set drawableTint and textColour as
     */
    private void setDrawableTintAndTextColour(ExtendedFloatingActionButton button, int colour) {
        Drawable[] drawables = button.getCompoundDrawablesRelative();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.setColorFilter(colour, PorterDuff.Mode.SRC_ATOP);
            }
        }

        button.setTextColor(colour);
    }

    /**
     * Creates and shows the inspectColourDetailsLayout to the user when a saved colour is clicked
     * @param selectableButton button that was clicked
     * @param savedColour colour to inspect details of
     */
    private void inspectColour(SelectableButton selectableButton, SavedColour savedColour) {
        // Calculate x and y coordinates of the inspected button, so we know where to grow the view from
        int x = (int) selectableButton.getX();
        int y = (int) selectableButton.getY();

        // Don't allow two colours to be inspected at once
        if (inspectingColour) return;
        // Inspecting a colour, this blocks the user from doing certain actions until they stop inspecting the colour
        inspectingColour = true;

        // Allow inspected colour to be dismissed when title or spinners clicked
        binding.touchOutside.setVisibility(VISIBLE);

        // Adjust for current scroll position and subtract this from received y position
        y -= binding.scrollView.getScrollY();

        // Set coordinates of origin, so we can shrink view back to this place (taking into account scrollY)
        inspectingX = x;
        inspectingY = y;

        // Set current inspecting colour, so we can change its properties later on if desired
        currentInspectingSavedColour = savedColour;


        // Set favorite colour button background to reflect whether current colour is favorite or not
        Drawable star_icon;
        if (savedColour.getFavorite()) {
            star_icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_filled, requireActivity().getTheme());
        }
        else {
            star_icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_outline, requireActivity().getTheme());
        }
        binding.favoriteInspectedColourButton.setCompoundDrawablesWithIntrinsicBounds(null, star_icon , null, null);

        // Set inspect details scroll view scrollY to 0
        binding.inspectColourScrollView.setScrollY(0);

        if (portrait) {
            // Set X and Y of animation button to match the colour button clicked
            binding.inspectColourAnimation.setX(x);
            binding.inspectColourAnimation.setY(y);
        }
        // Set background colour of animation button to match colour of button clicked
        binding.inspectColourAnimation.setBackgroundColor(savedColour.getColour());
        // Make the animation button visible
        binding.inspectColourAnimation.setVisibility(VISIBLE);
        // Set layout to be invisible
        binding.inspectColourDetailsLayout.setAlpha(0);

        // Set text colour of closest match colour name button so that text can be seen against the current colour (as it is the background)
        binding.inspectColourApproxName.setTextColor(savedColour.getTextColour());
        // Set closest match colour name to title button
        binding.inspectColourApproxName.setText(savedColour.getClosestMatchString());

        // Set background tint and text colour of action buttons so that they can be seen against the current colour (as it is the background)
        setDrawableTintAndTextColour(binding.hideInspectedColourButton, savedColour.getTextColour());
        setDrawableTintAndTextColour(binding.favoriteInspectedColourButton, savedColour.getTextColour());
        setDrawableTintAndTextColour(binding.deleteInspectedColourButton, savedColour.getTextColour());

        // Set text colour and details of HEX, RGB, HSV, HSL and CMYK buttons
        setColourDetails(savedColour, binding.inspectColourScrollView);
        System.out.println(binding.inspectColourDetailsLayout.getMaxHeight());

        // Set background and text of similar colour buttons
        DatabaseColour closestMatch = savedColour.getClosestMatch();
        DatabaseColour firstClosest = savedColour.getFirstClosest();
        DatabaseColour secondClosest = savedColour.getSecondClosest();
        DatabaseColour thirdClosest = savedColour.getThirdClosest();

        // If no close colours exist, then hide the similarColoursLayout (I have never seen this happen)
        if (firstClosest == null && secondClosest == null && thirdClosest == null) {
            binding.similarColoursLayout.setVisibility(GONE);
        }
        // Otherwise set the similarColoursLayout to be visible
        else {
            binding.similarColoursLayout.setVisibility(VISIBLE);
        }

        // Set details of firstClosest colour and complementary colour
        if (firstClosest != null) {
            displaySimilarColour(firstClosest, binding.similar1);
            displaySimilarColour(firstClosest.getComplementaryColour(), binding.similarComplementary1);
        }
        else {
            binding.similar1.setVisibility(GONE);
            binding.similarComplementary1.setVisibility(GONE);
        }

        // Set details of secondClosest colour and complementary colour (if this is different from the previous complementary colour)
        if (secondClosest != null) {
            displaySimilarColour(secondClosest, binding.similar2);

            // Don't display the complementary colour if it is the same as the previous complementary colour
            // TODO calculate complementary colours better so that there are always three for each colour
            if (!(firstClosest != null ? firstClosest.getComplementaryColour() : 0).equals(secondClosest.getComplementaryColour()))
                displaySimilarColour(secondClosest.getComplementaryColour(), binding.similarComplementary2);
            else binding.similarComplementary2.setVisibility(GONE);
        }
        else {
            binding.similar2.setVisibility(GONE);
            binding.similarComplementary2.setVisibility(GONE);
        }

        // Set details of thirdClosest colour and complementary colour (if this is different from the previous two complementary colours)
        if (thirdClosest != null) {
            displaySimilarColour(thirdClosest, binding.similar3);

            // Don't display the complementary colour if it is the same as either of the two previous complementary colours
            if (!thirdClosest.getComplementaryColour().equals((secondClosest != null ? secondClosest.getComplementaryColour() : 0)) &&
            !thirdClosest.getComplementaryColour().equals((firstClosest != null ? firstClosest.getComplementaryColour() : 0)))
                displaySimilarColour(thirdClosest.getComplementaryColour(), binding.similarComplementary3);
            else binding.similarComplementary3.setVisibility(GONE);
        }
        else {
            binding.similar3.setVisibility(GONE);
            binding.similarComplementary3.setVisibility(GONE);
        }

        // Try adding in the closest match complementary colour if one of the three close match complementary colours are the same
        // If the second complementary colour was the same as the first, use this layout
        if (binding.similarComplementary2.getVisibility() == GONE) {
            if (!closestMatch.getComplementaryColour().equals((firstClosest != null ? firstClosest.getComplementaryColour() : 0)) &&
                    !closestMatch.getComplementaryColour().equals((secondClosest != null ? secondClosest.getComplementaryColour() : 0)) &&
                    !closestMatch.getComplementaryColour().equals((thirdClosest != null ? thirdClosest.getComplementaryColour() : 0))) {
                displaySimilarColour(closestMatch.getComplementaryColour(), binding.similarComplementary2);
            }
        }
        // Otherwise try and use the final complementary colour layout
        else if (binding.similarComplementary3.getVisibility() == GONE) {
            if (!closestMatch.getComplementaryColour().equals((firstClosest != null ? firstClosest.getComplementaryColour() : 0)) &&
                    !closestMatch.getComplementaryColour().equals((secondClosest != null ? secondClosest.getComplementaryColour() : 0)) &&
                    !closestMatch.getComplementaryColour().equals((thirdClosest != null ? thirdClosest.getComplementaryColour() : 0))) {
                displaySimilarColour(closestMatch.getComplementaryColour(), binding.similarComplementary3);
            }
        }
        // NOTE: using this method, there will always be at least one complementary colour displayed for every colour

        // Set background colour of actions layout and actions layout animation, ready for later
        binding.inspectColourDetailsLayout.setBackgroundColor(savedColour.getColour());

        // Scale animation for expanding inspect colour layout
        ScaleAnimation scaleAnimation;
        if (portrait) {
            // Calculate horizontal and vertical scale factors
            float scaleX = savedColoursContainerWidth / (float)buttonDims;
            float scaleY = inspectColourDetailsLayoutHeight / (float)buttonDims;

            // View expands from button clicked to fill screen
            scaleAnimation = new ScaleAnimation(0.0f, scaleX, 0.0f, scaleY,
                    Animation.ABSOLUTE, x * scaleX / (scaleX - 1.0f),
                    Animation.ABSOLUTE, y * scaleY / (scaleY - 1.0f));
        }
        else {
            // View expands from centre of right pane to fill area
            scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
        }

        scaleAnimation.setDuration(shortAnimationDuration);
        scaleAnimation.setFillAfter(true);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.inspectColourDetailsLayout.setVisibility(VISIBLE);
                binding.inspectColourDetailsLayout.animate().setDuration(shortAnimationDuration).alpha(1).setListener(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        // Start the expanding animation
        binding.inspectColourAnimation.startAnimation(scaleAnimation);
    }

    /**
     * Hides current colour on display, zooms back to where it was from
     */
    private void hideInspectedColour() {
        if (consideringDelete) return;

        // Hide outside touch fade out button
        binding.touchOutside.setVisibility(GONE);

        // Fade out text
        binding.inspectColourDetailsLayout.animate().setDuration(shortAnimationDuration).alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                // Make bottom actions layout invisible
                binding.inspectColourDetailsLayout.setVisibility(View.INVISIBLE);

                // Scale animation for hiding inspect colour layout
                ScaleAnimation scaleAnimation;
                if (portrait) {
                    // Calculate horizontal and vertical scale factors
                    float scaleX = savedColoursContainerWidth / (float)buttonDims;
                    float scaleY = inspectColourDetailsLayoutHeight / (float)buttonDims;

                    // View shrinks back to original position
                    scaleAnimation = new ScaleAnimation(scaleX, 0.0f, scaleY, 0.0f,
                            Animation.ABSOLUTE, inspectingX * scaleX / (scaleX - 1.0f),
                            Animation.ABSOLUTE, inspectingY * scaleY / (scaleY - 1.0f));
                }
                else {
                    // View shrinks to centre of right pane
                    scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                }

                scaleAnimation.setDuration(shortAnimationDuration);
                scaleAnimation.setFillAfter(true);

                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Without clearing animations on the view, it will be set to visible immediately after setVisibility(View.INVISIBLE)
                        binding.inspectColourAnimation.clearAnimation();
                        // Set animation view to be invisible
                        binding.inspectColourAnimation.setVisibility(GONE);
                        // Make animation view have transparent background (needed in landscape mode, as view normal size is right pane)
                        binding.inspectColourAnimation.setBackgroundColor(0x00000000);

                        // Colour is now hidden, user can now inspect another colour
                        inspectingColour = false;

                        // When unfavoriting a colour from favorites filter, need to refresh layout
                        if (needLayoutRefresh) drawSavedColoursGrid();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                // Start the hiding animation after the details text has faded out
                binding.inspectColourAnimation.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
            }
        });
    }

    /**
     * Toggles favorite colour status of the currentInspectingColour
     */
    private void favoriteInspectedColour() {
        if (consideringDelete) return;
        // Update SavedColour object with new favorite status
        currentInspectingSavedColour.setFavorite(!currentInspectingSavedColour.getFavorite());

        // Update relevant button to display favorite status
        SelectableButton button = savedColourButtons.get(currentInspectingSavedColour.getId());
        if (button != null) button.updateFavorite(currentInspectingSavedColour.getFavorite());

        // Update star background to reflect current favorite status
        Drawable star_icon;
        if (currentInspectingSavedColour.getFavorite()) {
            star_icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_filled, requireActivity().getTheme());
        }
        else {
            star_icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_outline, requireActivity().getTheme());
            if (filterValue == 1) needLayoutRefresh = true;
        }
        // Set star background as drawableTop
        binding.favoriteInspectedColourButton.setCompoundDrawablesWithIntrinsicBounds(null, star_icon , null, null);
        // Set drawable tint and text colour as dark/light, so it can be seen against the background
        setDrawableTintAndTextColour(binding.favoriteInspectedColourButton, currentInspectingSavedColour.getTextColour());

        // Update new favorite status on database
        new Thread(() -> colourDAO.updateFavoriteColour(currentInspectingSavedColour.getId(), currentInspectingSavedColour.getFavorite())).start();
    }

    /**
     * Switches to manual picked page in edit mode
     */
    private void editInspectedColour() {
        // Set params of editing colour in mainActivityViewModel, so we can access the colour in ManualPickerFragment
        mainActivityViewModel.savedColourToEdit = currentInspectingSavedColour;
        mainActivityViewModel.setR(currentInspectingSavedColour.getR(), true);
        mainActivityViewModel.setG(currentInspectingSavedColour.getG(), true);
        mainActivityViewModel.setB(currentInspectingSavedColour.getB(), true);
        mainActivityViewModel.setEditingColour(true);
    }

    /**
     * Confirms with the user whether to delete the selected colour
     */
    private void confirmDeleteInspectedColour() {
        if (consideringDelete) return;
        // Considering delete, block certain other actions from happening until user decides whether to delete the selected colours
        consideringDelete = true;
        binding.confirmDeleteColourLayout.setVisibility(VISIBLE);
        binding.confirmDeleteColourLayout.animate().setDuration(shortAnimationDuration).alpha(1).setListener(null);
    }

    /**
     * Hides the confirmDeleteColourLayout by fading it out
     */
    private void hideConfirmDeleteInspectedColour() {
        // Animate confirmDeleteColourLayout alpha to 0
        binding.confirmDeleteColourLayout.animate().setDuration(shortAnimationDuration).alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                // Set confirmDeleteColourLayout visibility to GONE
                binding.confirmDeleteColourLayout.setVisibility(GONE);
                // No longer considering delete
                consideringDelete = false;
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
            }
        });
    }

    /**
     * Deletes the currentInspectingColour from the database
     */
    private void deleteInspectedColour() {
        // Remove inspected colour from savedColours
        savedColours.remove(currentInspectingSavedColour);

        // Remove inspected colour's SelectableButton from the savedColoursContainer
        SelectableButton button = savedColourButtons.get(currentInspectingSavedColour.getId());
        if (button != null) {
            binding.savedColoursContainer.removeView(button);
        }
        // Remove inspected colour's SelectableButton from the list of savedColourButtons
        savedColourButtons.remove(currentInspectingSavedColour.getId());

        // Delete inspected colour from the database
        new Thread(() -> colourDAO.delete(currentInspectingSavedColour)).start();
        // Fade out inspected colour
        consideringDelete = false;
        hideConfirmDeleteInspectedColour();
        needLayoutRefresh = true;
        hideInspectedColour();
    }


    /**
     * Confirms with the user whether to delete the currently selected colours
     */
    private void confirmDeleteSelectedColours() {
        if (consideringDelete) return;
        // Considering delete, block certain other actions from happening until user decides whether to delete the selected colours
        consideringDelete = true;
        binding.confirmDeleteSelectedColoursLayout.setVisibility(VISIBLE);
        binding.confirmDeleteSelectedColoursLayout.animate().setDuration(shortAnimationDuration).alpha(1).setListener(null);
    }

    /**
     * Hides the confirmDeleteSelectedColoursLayout by fading it out
     */
    private void hideConfirmDeleteSelectedColours() {
        // Set confirmDeleteSelectedColoursLayout visibility to GONE
        binding.confirmDeleteSelectedColoursLayout.setVisibility(GONE);
        // No longer considering delete
        consideringDelete = false;
    }

    /**
     * Deletes all colours currently selected from the database
     */
    private void deleteAllSelectedColours() {
        // Go through all colours in selectedColours and delete them
        for (SavedColour colour : selectedColours.values()) {
            // Remove current colour from savedColours
            savedColours.remove(colour);

            // Remove current colour's SelectableButton from the savedColoursContainer
            SelectableButton button = savedColourButtons.get(colour.getId());
            if (button != null) {
                binding.savedColoursContainer.removeView(button);
            }
            // Remove current colour's SelectableButton from the list of savedColourButtons
            savedColourButtons.remove(colour.getId());

            // Delete current colour from the database
            new Thread(() -> colourDAO.delete(colour)).start();
        }
        // Hide confirmDeleteSelectedColoursLayout
        hideConfirmDeleteSelectedColours();
        // Exit selecting mode (as all selected colours have now been deleted)
        exitSelectingMode();
        // Reposition saved colours
        drawSavedColoursGrid();
    }


    @Override
    public void onResume() {
        super.onResume();
        // Remeasure height of inspectColourDetailsLayout, this decreases when an ad is loaded, and set params of views relying on this
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.inspectColourDetailsLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                inspectColourDetailsLayoutHeight = binding.inspectColourDetailsLayout.getHeight();

            }
        };
        binding.inspectColourDetailsLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

        // Reposition saved colours after resuming (in case new colours have been saved)
        if (savedColoursContainerWidth != 0) drawSavedColoursGrid();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Need to set consideringDelete to false before hideInspectedColour() is called
        consideringDelete = false;
        // Hide inspecting colour,
        hideInspectedColour();

        // Hide deleting colour layouts
        binding.confirmDeleteColourLayout.setVisibility(GONE);
        binding.confirmDeleteColourLayout.setAlpha(0);
        binding.confirmDeleteSelectedColoursLayout.setVisibility(GONE);
        binding.confirmDeleteSelectedColoursLayout.setAlpha(0);

        // Exit selecting mode
        if (selectingMode != NOT_SELECTING_MODE) exitSelectingMode();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //TODO check this is okay (needed for orientation changes)
        //binding = null;
    }
}