package com.skeensystems.colorpicker;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.AspectRatioStrategy;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.common.util.concurrent.ListenableFuture;
import com.skeensystems.colorpicker.database.AppDatabase;
import com.skeensystems.colorpicker.database.ColourDAO;
import com.skeensystems.colorpicker.database.ColourDatabase;
import com.skeensystems.colorpicker.database.DatabaseColour;
import com.skeensystems.colorpicker.database.SavedColour;
import com.skeensystems.colorpicker.databinding.ActivityMainTabsBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import kotlin.Triple;

public class MainActivity extends AppCompatActivity {

    // True disables ads
    // False MUST be used for production
    private final boolean adsDisabled = false;
    // True disables camera
    // False MUST be used for production
    private final boolean cameraDisabled = false;

    // Database object for reading/writing to the app database
    public static ColourDAO colourDAO;

    // List containing all colours that have been picked
    public static ArrayList<SavedColour> savedColours;

    private ActivityMainTabsBinding binding;

    // Width and height of the screen
    public static int screenWidth = 0;
    public static int screenHeight = 0;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    // Set to true when on the camera page - when false, less work needs to be done
    public static boolean onCamera = false;

    // Set to true when device is portrait, false when landscape
    public static boolean portrait = true;

    public static MainActivityViewModel mainActivityViewModel;

    // Records timestamp of last frame, so we can limit to 10fps
    private long lastFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Main shade value for the status and navigation bars (black = dark theme, white = light theme
        int mainColour = getColourFromTheme(R.attr.mainColour);
        // Set status bar and navigation bar colours
        accentPickedColour(mainColour);
        Window window = getWindow();
        window.setNavigationBarColor(mainColour);

        int orientation = this.getResources().getConfiguration().orientation;
        portrait = orientation == Configuration.ORIENTATION_PORTRAIT;

        super.onCreate(savedInstanceState);

        Bitmap bitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(mainColour);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        getWindow().setBackgroundDrawable(bitmapDrawable);

        // View model for storing data (especially for values used in manual picker)
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Get previous HSV values used in ManualPicker
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        // Set these HSV values, so ManualPicker displays last colour when opened
        mainActivityViewModel.setH(sharedPref.getInt(getString(R.string.previousManualPickerH), 0), true);
        mainActivityViewModel.setS(sharedPref.getInt(getString(R.string.previousManualPickerS), 100), true);
        mainActivityViewModel.setV(sharedPref.getInt(getString(R.string.previousManualPickerV), 100), true);

        // Check for camera permission (and request if necessary)
        String[] Permissions = {Manifest.permission.CAMERA};
        if (!hasPermissions(this, Permissions)) {
            ActivityCompat.requestPermissions(this, Permissions, 1);
        }

        // Do database stuff on background thread
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "colour_picker_database").build();
            colourDAO = db.colourDAO();

            // Initialise the colour database
            try {
                mainActivityViewModel.setColourDatabase(new ColourDatabase(this));
            } catch (IOException ignored) {
            }

            // Maybe need to close the database here to stop leaks?
            //db.close();

            // If this is the first time the user has opened v2+, this converts previous files into new database data
            migrateToDatabase();

            // Load saved colours from the database
            savedColours = (ArrayList<SavedColour>) colourDAO.getAll();

            // Calculate closest matches for all colours in saved colour list and store these values in each SavedColour object
            // For more efficiency these could be stored on the database, although this way allows the colour database to be changed whenever without need for migration
            for (int i = 0; i < savedColours.size(); i++) {
                // Get closest matches for each colour
                ArrayList<DatabaseColour> closestMatches = mainActivityViewModel.getClosestMatches(savedColours.get(i));
                // If size equals four then we have a closest match and next three closest, otherwise just set the closest colour if there are 1, 2, or 3 close match colours returned
                // So far, I have not seen less than four colours be returned here
                if (closestMatches.size() == 4) {
                    savedColours.get(i).setClosestMatch(closestMatches.get(0));
                    savedColours.get(i).setFirstClosest(closestMatches.get(1));
                    savedColours.get(i).setSecondClosest(closestMatches.get(2));
                    savedColours.get(i).setThirdClosest(closestMatches.get(3));
                }
                else if (!closestMatches.isEmpty()) {
                    savedColours.get(i).setClosestMatch(closestMatches.get(0));
                }
            }

            // Initialise app interface after database has been loaded
            //TODO loading screen for before this
            runOnUiThread(() -> {
                binding = ActivityMainTabsBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());

                // Set up bottom navigation view pager
                MainActivityCollectionAdapter collectionAdapter = new MainActivityCollectionAdapter(this);
                binding.pager.setAdapter(collectionAdapter);
                // Keep all three pages active (I don't think this is really necessary, but it might stop recalculation of SavedColours layout, which is quite expensive)
                binding.pager.setOffscreenPageLimit(2);

                binding.pager.post(() -> {
                    // Set view pager to display CameraFragment
                    binding.pager.setCurrentItem(1, false);
                    // Make view pager and bottom tabs visible
                    binding.pager.setVisibility(View.VISIBLE);
                    if (portrait) Objects.requireNonNull(binding.tabLayout).setVisibility(View.VISIBLE);
                    else Objects.requireNonNull(binding.navigationRail).setVisibility(View.VISIBLE);
                    // Load ad
                    if (!adsDisabled) loadAd();
                });

                // Observer for if colour is being edited in the ManualPicker (from a saved colour)
                // This is how the app knows to change from SavedColoursFragment to ManualPickerFragment
                final Observer<Boolean> editingColourObserver = editColour -> {
                    if (editColour) {
                        // Smooth scroll to ManualPickerFragment
                        binding.pager.setCurrentItem(2, true);
                    }
                };
                mainActivityViewModel.getEditingColour().observe(this, editingColourObserver);

                if (portrait) {
                    // Portrait layout
                    setUpPortraitLayout();
                }
                else {
                    // Landscape layout
                    setUpLandscapeLayout();
                }

                // Get screen width and height and set to variables to be used when drawing some layouts
                getScreenDims();

                // Start camera
                if (!cameraDisabled) startCamera();
            });
        }).start();
    }


    private void setUpPortraitLayout() {
        assert binding.tabLayout != null;
        // Set up tab layout
        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            if (position == 0)
                tab.setText(getResources().getString(R.string.title_saved_colours));
            else if (position == 1)
                tab.setText(getResources().getString(R.string.title_camera));
            else tab.setText(getResources().getString(R.string.title_manual_picker));
        }).attach();

        // Set icons of tabs
        Objects.requireNonNull(binding.tabLayout.getTabAt(0)).setIcon(R.drawable.ic_navigation_saved_colours_outline);
        Objects.requireNonNull(binding.tabLayout.getTabAt(1)).setIcon(R.drawable.ic_navigation_camera_filled);
        Objects.requireNonNull(binding.tabLayout.getTabAt(2)).setIcon(R.drawable.ic_navigation_manual_picker_outline);

        // Set colour of tabs when selected
        binding.tabLayout.setSelectedTabIndicatorColor(getColourFromTheme(R.attr.reversedColour));

        // Get colour state list for tab colours
        ColorStateList colourStateList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colourStateList = getResources().getColorStateList(R.color.tab_colours, getTheme());
        } else {
            colourStateList = AppCompatResources.getColorStateList(this, R.color.tab_colours);
        }
        // Set icon and text colours of tabs
        binding.tabLayout.setTabIconTint(colourStateList);
        binding.tabLayout.setTabTextColors(colourStateList);

        // Detect when page has changed, so tab icons can be updated (filled = on page, outline = not on page)
        // Also exit editing colour mode when scrolling off ManualPickerFragment (if editing colour mode is enabled)
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // On SavedColours page
                if (position == 0) {
                    mainActivityViewModel.setEditingColour(false);
                    Objects.requireNonNull(binding.tabLayout.getTabAt(0)).setIcon(R.drawable.ic_navigation_saved_colours_filled);
                    Objects.requireNonNull(binding.tabLayout.getTabAt(1)).setIcon(R.drawable.ic_navigation_camera_outline);
                    Objects.requireNonNull(binding.tabLayout.getTabAt(2)).setIcon(R.drawable.ic_navigation_manual_picker_outline);
                }
                // On Camera page
                else if (position == 1) {
                    mainActivityViewModel.setEditingColour(false);
                    Objects.requireNonNull(binding.tabLayout.getTabAt(0)).setIcon(R.drawable.ic_navigation_saved_colours_outline);
                    Objects.requireNonNull(binding.tabLayout.getTabAt(1)).setIcon(R.drawable.ic_navigation_camera_filled);
                    Objects.requireNonNull(binding.tabLayout.getTabAt(2)).setIcon(R.drawable.ic_navigation_manual_picker_outline);
                }
                // On ManualPicker page
                else if (position == 2) {
                    Objects.requireNonNull(binding.tabLayout.getTabAt(0)).setIcon(R.drawable.ic_navigation_saved_colours_outline);
                    Objects.requireNonNull(binding.tabLayout.getTabAt(1)).setIcon(R.drawable.ic_navigation_camera_outline);
                    Objects.requireNonNull(binding.tabLayout.getTabAt(2)).setIcon(R.drawable.ic_navigation_manual_picker_filled);
                }
                super.onPageSelected(position);
            }
        });
    }


    private void setUpLandscapeLayout() {
        assert binding.navigationRail != null;
        binding.navigationRail.setOnItemSelectedListener(item -> {
            String title = String.valueOf(item.getTitle());
            if (title.equals(getString(R.string.title_saved_colours_landscape)))
                binding.pager.setCurrentItem(0, true);
            else if (title.equals(getString(R.string.title_camera)))
                binding.pager.setCurrentItem(1, true);
            else if (title.equals(getString(R.string.title_manual_picker)))
                binding.pager.setCurrentItem(2, true);
            return true;
        });


        // Detect when page has changed, so tab icons can be updated (filled = on page, outline = not on page)
        // Also exit editing colour mode when scrolling off ManualPickerFragment (if editing colour mode is enabled)
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // On SavedColours page
                if (position == 0) {
                    mainActivityViewModel.setEditingColour(false);
                    binding.navigationRail.getMenu().getItem(0).setChecked(true);
                    binding.navigationRail.getMenu().getItem(0).setIcon(R.drawable.ic_navigation_saved_colours_filled);
                    binding.navigationRail.getMenu().getItem(1).setIcon(R.drawable.ic_navigation_camera_outline);
                    binding.navigationRail.getMenu().getItem(2).setIcon(R.drawable.ic_navigation_manual_picker_outline);
                }
                // On Camera page
                else if (position == 1) {
                    mainActivityViewModel.setEditingColour(false);
                    binding.navigationRail.getMenu().getItem(1).setChecked(true);
                    binding.navigationRail.getMenu().getItem(0).setIcon(R.drawable.ic_navigation_saved_colours_outline);
                    binding.navigationRail.getMenu().getItem(1).setIcon(R.drawable.ic_navigation_camera_filled);
                    binding.navigationRail.getMenu().getItem(2).setIcon(R.drawable.ic_navigation_manual_picker_outline);
                }
                // On ManualPicker page
                else if (position == 2) {
                    binding.navigationRail.getMenu().getItem(2).setChecked(true);
                    binding.navigationRail.getMenu().getItem(0).setIcon(R.drawable.ic_navigation_saved_colours_outline);
                    binding.navigationRail.getMenu().getItem(1).setIcon(R.drawable.ic_navigation_camera_outline);
                    binding.navigationRail.getMenu().getItem(2).setIcon(R.drawable.ic_navigation_manual_picker_filled);
                }
                super.onPageSelected(position);
            }
        });
    }


    /**
     * Loads ad, creates and displays AdView to the user
     */
    private void loadAd() {
        // Initialise ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        // Create AdView
        AdView adView = new AdView(this);
        // Set AdUnitId of AdView to correct id (from ad in AdMob)
        adView.setAdUnitId(BuildConfig.adUnitID);

        // Create request for the ad
        AdRequest adRequest = new AdRequest.Builder().build();

        // Set the adaptive ad size on the AdView
        adView.setAdSize(getAdSize());

        // Load ad
        adView.loadAd(adRequest);

        // Add AdView to the layout
        binding.adViewContainer.addView(adView);

        // Set listener on AdView, so we know when ad is loaded/opened/impression occurs/error loading etc
        // Send data to google sheet when these actions occur
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                collectAdsData("l");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                collectAdsData(loadAdError.toString());
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                collectAdsData("c");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                collectAdsData("i");
            }
        });
    }

    /**
     * Calculates size of ad based off screen dimensions
     * @return AdSize to set to the AdView
     */
    private AdSize getAdSize() {
        // Determine the screen width (minus decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Get adaptive ad size and return for setting on the ad view
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    /**
     * Sends data to google sheet to collect information on ad stats
     * @param outcome l = ad loaded, i = ad impression, c = ad clicked, error message = ad failed to load
     */
    private void collectAdsData(String outcome) {
        new Thread(() -> {
            // URL of the google form to submit the data on
            String fullUrl = BuildConfig.adsDataURL;
            // Create the data
            String data = null;
            try {
                // Version code
                data = "entry.1054911767=" + URLEncoder.encode(String.valueOf(BuildConfig.VERSION_CODE), "utf-8") + "&" +
                        // Outcome
                        "entry.1945605800=" + URLEncoder.encode(outcome, "utf-8") + "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                // Send the request
                URL url = new URL(fullUrl);
                URLConnection urlConnection = url.openConnection();
                // Set timeouts of request
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setDoOutput(true);
                // Write the data to the output stream
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(data);
                outputStreamWriter.flush();
                // Needed to fully complete the POST request
                new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    /**
     * Reads pre-v2 data and writes it to the app database.
     * Colours are assigned ids from 0 upwards (new colours are assigned System.currentTimeMillis() as their id)
     */
    private void migrateToDatabase() {
        // ArrayList to store the colours read from the file
        ArrayList<SavedColour> oldSavedColours = new ArrayList<>();
        File directory = getFilesDir();
        BufferedReader reader;

        // Id counter
        int count = 0;
        try {
            reader = new BufferedReader(new FileReader(directory + "pickedColours.txt"));
            String nextLine = reader.readLine();
            while (nextLine != null) {
                // Create the colour object
                SavedColour savedColour = new SavedColour();
                // Set id
                savedColour.setId(count);
                // Increment id counter by 1 for next iteration
                count++;
                // Set r, g and b values using #RRGGBB from file
                savedColour.setR(Integer.parseInt(nextLine.substring(1, 3), 16));
                savedColour.setG(Integer.parseInt(nextLine.substring(3, 5), 16));
                savedColour.setB(Integer.parseInt(nextLine.substring(5), 16));
                // Add SavedColour object to the ArrayList
                oldSavedColours.add(savedColour);
                nextLine = reader.readLine();
            }
        } catch (IOException ignored) {
            return;
        }


        // Assigns favorite colour to true for desired colours
        try {
            reader = new BufferedReader(new FileReader(directory + "favoriteColours.txt"));
            String nextLine = reader.readLine();
            while (nextLine != null) {
                oldSavedColours.get(Integer.parseInt(nextLine)).setFavorite(true);
                nextLine = reader.readLine();

            }
            reader.close();
        } catch (IOException ignored) {
        }


        System.out.println(new File(getFilesDir() + "pickedColours.txt").delete());
        System.out.println(new File(getFilesDir() + "favoriteColours.txt").delete());

        // TESTING
        /*oldSavedColours = new ArrayList<>(Arrays.asList(new SavedColour(0, 99, 25, 8, false),
                new SavedColour(1, 124, 116, 116, false),
                new SavedColour(2, 112, 93, 65, false),
                new SavedColour(3, 131, 73, 12, false),
                new SavedColour(4, 55, 26, 7, false),
                new SavedColour(5, 40, 10, 2, false),
                new SavedColour(6, 91, 51, 18, false),
                new SavedColour(7, 59, 34, 7, false),
                new SavedColour(8, 77, 31, 0, false),
                new SavedColour(9, 244, 226, 84, true),
                new SavedColour(10, 105, 183, 97, false),
                new SavedColour(11, 99, 138, 210, false),
                new SavedColour(12, 108, 132, 213, false),
                new SavedColour(13, 255, 119, 232, false),
                new SavedColour(14, 38, 98, 255, false),
                new SavedColour(15, 25, 94, 255, false),
                new SavedColour(16, 36, 86, 218, false),
                new SavedColour(17, 32, 81, 228, false),
                new SavedColour(18, 24, 36, 101, false),
                new SavedColour(19, 29, 90, 243, false),
                new SavedColour(20, 218, 233, 219, false),
                new SavedColour(21, 158, 180, 175, false),
                new SavedColour(22, 168, 186, 188, false),
                new SavedColour(23, 194, 203, 191, false),
                new SavedColour(24, 91, 87, 64, false),
                new SavedColour(25, 103, 83, 45, false),
                new SavedColour(26, 191, 146, 122, false),
                new SavedColour(27, 30, 154, 54, false),
                new SavedColour(28, 173, 112, 107, false),
                new SavedColour(29, 69, 110, 146, false),
                new SavedColour(30, 70, 129, 149, false),
                new SavedColour(31, 70, 129, 149, false),
                new SavedColour(32, 70, 129, 149, false),
                new SavedColour(33, 65, 51, 58, false),
                new SavedColour(34, 187, 166, 157, false),
                new SavedColour(35, 84, 33, 74, false),
                new SavedColour(36, 68, 44, 86, false),
                new SavedColour(37, 27, 55, 58, false),
                new SavedColour(38, 41, 106, 113, false),
                new SavedColour(39, 0, 87, 112, false),
                new SavedColour(40, 255, 0, 0, false),
                new SavedColour(41, 118, 73, 79, false),
                new SavedColour(42, 63, 87, 108, false),
                new SavedColour(43, 255, 191, 0, false)));*/

        // Write all colours in the ArrayList to the database
        for (SavedColour savedColour : oldSavedColours) {
            colourDAO.insertAll(savedColour);
        }
    }


    /**
     * Start the image analysis and set camera to send output to "cameraView"
     */
    private void startCamera() {
        // Run this on a background thread, to avoid blocking the main thread (on slower devices, starting the camera can take a few seconds)
        new Thread(() -> {
            cameraProviderFuture = ProcessCameraProvider.getInstance(MainActivity.this);
            // Add listener to cameraProviderFuture, so we know when it is ready to bind image analysis
            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(MainActivity.this));
        }).start();
    }

    /**
     * Set up image analyser, binding to the camera, allowing us to do stuff on each frame
     */
    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        // Set time to "lastFrame"
        lastFrame = System.currentTimeMillis();

        // Create ImageAnalysis object, setting it to only keep the latest frame (to avoid a queue of frames backing up)
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setResolutionSelector(new ResolutionSelector.Builder().setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY).build())
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
            // Limit updates of central pixel colour to 10fps
            boolean includeFrame = true;
            // If current time is within 100ms of last frame time, skip this frame
            if (System.currentTimeMillis() < lastFrame + 100) {
                includeFrame = false;
            }
            // Otherwise set last frame time to the current time
            else {
                lastFrame = System.currentTimeMillis();
            }

            // Skip analysis if not on camera page
            if (onCamera && includeFrame) {
                // Get colour of pixel at centre of current frame
                Triple<Double, Double, Double> colour = GetColour.getColour(imageProxy);

                // Round RGB values to integers
                int r = Math.toIntExact(Math.round(colour.component1()));
                int g = Math.toIntExact(Math.round(colour.component2()));
                int b = Math.toIntExact(Math.round(colour.component3()));

                // Set RGB values in mainActivityViewModel, so we can access the colour in CameraFragment
                mainActivityViewModel.setCameraR(r);
                mainActivityViewModel.setCameraG(g);
                mainActivityViewModel.setCameraB(b);

                // Set colour of status and navigation bars to the current colour (CURRENTLY DISABLED)
                // accentPickedColour(r, g, b);
            }
            // If not on camera page, set colour of status and navigation bars to black/white depending on night theme (NOT CURRENTLY NEEDED, AS ABOVE LINE IS NOT USED)
            //else {
            //    accentPickedColour(getColourFromTheme(R.attr.mainColour));
            //}

            // Finish analysis and allow next frame to be received
            imageProxy.close();
        });

        // Build the preview and select camera to use (back)
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(binding.cameraView.getSurfaceProvider());

        cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, imageAnalysis, preview);
    }

    /**
     * Returns a colour from the theme using it's id
     *
     * @param id R.attr.COLOUR_NAME
     * @return TypedValue containing the colour (.data returns the colour)
     */
    private int getColourFromTheme(int id) {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(id, value, true);
        return value.data;
    }

    /**
     * Sets colour of status and navigation bars to inputted value
     * @param r red part of new colour
     * @param g green part of new colour
     * @param b blue part of new colour
     */
    private void accentPickedColour(int r, int g, int b) {
        // Format the colour as a colour object
        int newColour = Color.rgb(r, g, b);
        Window window = getWindow();
        // Set background of status bar to the new colour
        window.setStatusBarColor(newColour);
        // Temporarily don't set this colour
        //window.setNavigationBarColor(newColour);

        // Needs min API 23 (current min is 21)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Set status bar text colour to white
            if (HelpersKt.backgroundRequiresLightText(r, g, b)) {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            // Set status bar text colour to black
            else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    private void accentPickedColour(int colour) {
        accentPickedColour(Color.red(colour), Color.green(colour), Color.blue(colour));
    }


    /**
     * Get screen width and height and set the dimension variables
     */
    private void getScreenDims() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }


    /**
     * Check to see if we have camera permission
     * @return true if we have camera permission, false if we don't
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Request for camera permission
     * @param requestCode  The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *                     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera Permission required for Colour Picker to function", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Sets back action to send to CameraFragment if not already on it, otherwise back will close app
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (binding.pager.getCurrentItem() != 1) {
                binding.pager.setCurrentItem(1, true);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}