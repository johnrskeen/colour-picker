package com.skeensystems.colorpicker;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.skeensystems.colorpicker.database.AppDatabase;
import com.skeensystems.colorpicker.database.ColourDAO;
import com.skeensystems.colorpicker.database.DatabaseColour;
import com.skeensystems.colorpicker.database.SavedColour;
import com.skeensystems.colorpicker.database.SavedColourEntity;
import com.skeensystems.colorpicker.databinding.ActivityMainTabsBinding;
import com.skeensystems.colorpicker.ui.saved.SavedColoursViewModel;

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
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // True disables ads
    // False MUST be used for production
    // TODO compose camera doesn't work if this is set to true
    private final boolean adsDisabled = false;

    // Database object for reading/writing to the app database
    public static ColourDAO colourDAO;

    // List containing all colours that have been picked
    public static ArrayList<SavedColour> savedColours;

    public static MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        // If this is the first time the user has opened v2+, this converts previous files into new database data
        new Thread(this::migrateToDatabase).start();
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
        //binding.adViewContainer.addView(adView);

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
        ArrayList<SavedColourEntity> oldSavedColours = new ArrayList<>();
        File directory = getFilesDir();
        BufferedReader reader;

        // Id counter
        int count = 0;
        try {
            reader = new BufferedReader(new FileReader(directory + "pickedColours.txt"));
            String nextLine = reader.readLine();
            while (nextLine != null) {
                // Set r, g and b values using #RRGGBB from file
                int r =  Integer.parseInt(nextLine.substring(1, 3), 16);
                int g = Integer.parseInt(nextLine.substring(3, 5), 16);
                int b = Integer.parseInt(nextLine.substring(5), 16);
                // Create the colour object
                SavedColourEntity savedColour = new SavedColourEntity(count, r, g, b, false);
                // Add SavedColour object to the ArrayList
                oldSavedColours.add(savedColour);
                // Increment id counter by 1 for next iteration
                count++;
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
                int index = Integer.parseInt(nextLine);
                SavedColourEntity oldSavedColour = oldSavedColours.get(index);
                oldSavedColours.set(index, new SavedColourEntity(oldSavedColour.getId(), oldSavedColour.getR(), oldSavedColour.getG(), oldSavedColour.getB(), true));
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
        for (SavedColourEntity savedColour : oldSavedColours) {
            colourDAO.insertAll(savedColour);
        }
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
}