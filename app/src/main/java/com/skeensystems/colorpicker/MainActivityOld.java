package com.skeensystems.colorpicker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.skeensystems.colorpicker.database.SavedColourEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivityOld extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            //colourDAO.insertAll(savedColour);
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