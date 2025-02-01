package com.skeensystems.colorpicker.database;

import android.content.Context;

import com.skeensystems.colorpicker.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ColourDatabase {

    //Octree octreeDatabase;

    // ArrayList of named colours
    ArrayList<DatabaseColour> colourDatabase;

    public ColourDatabase(Context context) throws IOException {

        colourDatabase = new ArrayList<>();

        // Read colour database from file
        InputStream inputStream = context.getResources().openRawResource(R.raw.colour_database);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // Get colour name for the first colour
        String colourName = bufferedReader.readLine();
        // Repeat until no more colours
        while (colourName != null) {
            // Get RGB values of each colour
            int r = 0;
            try {
                r = Integer.parseInt(bufferedReader.readLine());
            } catch (NumberFormatException ignored) {
            }
            int g = 0;
            try {
                g = Integer.parseInt(bufferedReader.readLine());
            } catch (NumberFormatException ignored) {
            }
            int b = 0;
            try {
                b = Integer.parseInt(bufferedReader.readLine());
            } catch (NumberFormatException ignored) {
            }

            // Get complementary colour name and RGB values for each colour (this saves calculating complementary colours at runtime)
            String complementaryName = bufferedReader.readLine();
            int complementaryR = 0;
            try {
                complementaryR = Integer.parseInt(bufferedReader.readLine());
            } catch (NumberFormatException ignored) {
            }
            int complementaryG = 0;
            try {
                complementaryG = Integer.parseInt(bufferedReader.readLine());
            } catch (NumberFormatException ignored) {
            }
            int complementaryB = 0;
            try {
                complementaryB = Integer.parseInt(bufferedReader.readLine());
            } catch (NumberFormatException ignored) {
            }

            // Set the data to a DatabaseColour object
            DatabaseColour databaseColour = new DatabaseColour(colourName, r, g, b);
            // Set complementary colour details
            databaseColour.setComplementaryColour(complementaryName, complementaryR, complementaryG, complementaryB);
            // Add colour to the database ArrayList
            colourDatabase.add(databaseColour);

            // Get colour name for the next colour
            colourName = bufferedReader.readLine();
        }


        //octreeDatabase = new Octree(colourDatabase, 128, 128, 128);
    }

    /**
     * Gets up to four colours from the colour database that are closest to the inputted colour
     * @param colour colour to find the closest matches with
     * @return ArrayList<DatabaseColour>(CLOSEST_MATCH, FIRST_CLOSEST, SECOND_CLOSEST, THIRD_CLOSEST) (yes, I know 1st closest is actually 2nd closest etc, but it doesn't really matter)
     */
    public ArrayList<DatabaseColour> getClosestMatches(SavedColour colour) {
        // Set default values to return (obviously these will be filled with closer matches if applicable, as long as there are at least four colours in the database)
        ArrayList<DatabaseColour> closestMatches = new ArrayList<>(Arrays.asList(
                new DatabaseColour("", 0, 0, 0),
                new DatabaseColour("", 0, 0, 0),
                new DatabaseColour("", 0, 0, 0),
                new DatabaseColour("", 0, 0, 0)));

        // Set default values of the distances (these are large enough - largest possible distance is 255^3, but as there are lots of colours, this will not be the closest match)
        ArrayList<Double> distances = new ArrayList<>(Arrays.asList(999999.0, 999999.0, 999999.0, 999999.0));
        // Iterate through all the colours in the database (this is where an Octree might be more efficient, especially if I want to make the database larger in future)
        for (DatabaseColour databaseColour : colourDatabase) {
            // Calculate distance from inputted colour current colour from database
            double tempDistance = Math.sqrt(Math.pow(colour.getR() - databaseColour.getR(), 2)
                    + Math.pow(colour.getG() - databaseColour.getG(), 2)
                    + Math.pow(colour.getB() - databaseColour.getB(), 2));
            // See if it has a place in the closest matches list (comparing distances to those currently in the list)
            for (int i = 0; i < 4; i++) {
                if (tempDistance < distances.get(i)) {
                    closestMatches.add(i, databaseColour);
                    distances.add(i, tempDistance);
                    closestMatches.remove(4);
                    // Remove the last element, as this is no longer needed (only need the four closest matches)
                    distances.remove(4);
                    break;
                }
            }
        }
        // Return ArrayList of the closest matches
        return closestMatches;

        //Octree closestOctree = octreeDatabase.getSubtree(colour);
        //return new NamedColour("Colour", colour.getR(), colour.getG(), colour.getB());
    }
}
