package com.skeensystems.colorpicker;

import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skeensystems.colorpicker.database.ColourDatabase;
import com.skeensystems.colorpicker.database.DatabaseColour;
import com.skeensystems.colorpicker.database.SavedColour;

import java.util.ArrayList;
import java.util.Collections;

import kotlin.Triple;

public class MainActivityViewModel extends ViewModel {

    // Instance of ColourDatabase to use when calculating closest matches
    private ColourDatabase colourDatabase;
    // Whether we have set a colour database
    private boolean colourDatabaseActive;

    // LiveData for Manual Picker
    // All of these keep all the other up to date
    // (i.e. changing h value needs to also update rgb values)
    // RGB LiveData
    private final MutableLiveData<Integer> rString;
    private final MutableLiveData<Integer> gString;
    private final MutableLiveData<Integer> bString;
    // HSV LiveData
    private final MutableLiveData<Integer> hString;
    private final MutableLiveData<Integer> sString;
    private final MutableLiveData<Integer> vString;
    // Current colour in Manual Picker
    private final MutableLiveData<Integer> manualPickerCurrentColour;

    // Current central colour in Camera view
    private final MutableLiveData<Integer> cameraCurrentColour;

    // RGB values for Manual Picker
    private int r;
    private int g;
    private int b;

    // HSV values for Manual Picker
    private int h;
    private int s;
    private int v;

    // RGB values for the current central colour from the camera
    private int cameraR;
    private int cameraG;
    private int cameraB;

    // True if currently editing a colour, false otherwise
    private final MutableLiveData<Boolean> editingColour;
    // Colour we are currently editing
    public SavedColour savedColourToEdit;

    public MainActivityViewModel() {
        // Colour database defaults to null (to get any meaningful closest matches, this must be set to an instance of ColourDatabase)
        colourDatabaseActive = false;
        colourDatabase = null;

        // Default colour for manual picker is RED
        rString = new MutableLiveData<>(255);
        gString = new MutableLiveData<>(0);
        bString = new MutableLiveData<>(0);
        r = 255;
        g = 0;
        b = 0;
        // HSV values to display RED
        hString = new MutableLiveData<>(0);
        sString = new MutableLiveData<>(100);
        vString = new MutableLiveData<>(100);
        h = 0;
        s = 100;
        v = 100;

        // Set colour for manual picker current colour (initial = RED)
        manualPickerCurrentColour = new MutableLiveData<>(Color.rgb(255, 0, 0));

        // Set colour for current central camera colour (initial = BLACK)
        cameraCurrentColour = new MutableLiveData<>(Color.rgb(0, 0, 0));
        // Set cameraR, cameraG, cameraB variables initially for BLACK
        cameraR = 0;
        cameraG = 0;
        cameraB = 0;

        // Set editing colour params
        editingColour = new MutableLiveData<>(false);
        savedColourToEdit = null;
    }

    /**
     * Sets the colour database
     * @param colourDatabase instance of ColourDatabase to use when calculating closest matches
     */
    public void setColourDatabase(ColourDatabase colourDatabase) {
        this.colourDatabase = colourDatabase;
        colourDatabaseActive = true;
    }

    /**
     * Gets up to four colours from the colour database that are closest to the inputted colour
     * If the colour database has not yet been set, return the colour BLACK (the colour database will always be set first though)
     * @param colour colour to find the closest matches with
     * @return ArrayList<DatabaseColour>(CLOSEST_MATCH, FIRST_CLOSEST, SECOND_CLOSEST, THIRD_CLOSEST) (yes, I know 1st closest is actually 2nd closest etc, but it doesn't really matter)
     */
    public ArrayList<DatabaseColour> getClosestMatches(SavedColour colour) {
        if (!colourDatabaseActive) return new ArrayList<>(Collections.singleton(new DatabaseColour("", 0, 0, 0)));
        return colourDatabase.getClosestMatches(colour);
    }

    /**
     * Updates current manual picker colour LiveData from current r, g, b values (used by preview button)
     */
    private void updateManualPickerCurrentColour() {
        manualPickerCurrentColour.setValue(Color.rgb(r, g, b));
    }

    /**
     * Gets current manual picker colour LiveData
     * @return the LiveData object storing current manual colour integer
     */
    public LiveData<Integer> getManualPickerCurrentColour() {
        return manualPickerCurrentColour;
    }

    /**
     * Sets r value for manual picker and updates other variables as appropriate
     * @param newR new r value to set
     * @param updateOtherColourSystem stops infinite loop of updating RGB then HSV etc,
     *                                true when called outside this class, ALWAYS false when called internally
     */
    public void setR(Integer newR, boolean updateOtherColourSystem) {
        // Check newR is between 0 and 255, if not do nothing
        if (newR >= 0 && newR <= 255) {
            // Set r to newR value
            r = newR;
            // Update rString with the new value to trigger update of views observing this value
            rString.setValue(newR);
            // Update current manual picker colour
            updateManualPickerCurrentColour();
            // Update HSV variables if required
            if (updateOtherColourSystem) updateHSVfromRGB();
        }
    }

    /**
     * Sets g value for manual picker and updates other variables as appropriate
     * @param newG new g value to set
     * @param updateOtherColourSystem stops infinite loop of updating RGB then HSV etc,
     *                                true when called outside this class, ALWAYS false when called internally
     */
    public void setG(Integer newG, boolean updateOtherColourSystem) {
        // Check newG is between 0 and 255, if not do nothing
        if (newG >= 0 && newG <= 255) {
            // Set g to newG value
            g = newG;
            // Update gString with the new value to trigger update of views observing this value
            gString.setValue(newG);
            // Update current manual picker colour
            updateManualPickerCurrentColour();
            // Update HSV variables if required
            if (updateOtherColourSystem) updateHSVfromRGB();
        }
    }

    /**
     * Sets b value for manual picker and updates other variables as appropriate
     * @param newB new b value to set
     * @param updateOtherColourSystem stops infinite loop of updating RGB then HSV etc,
     *                                true when called outside this class, ALWAYS false when called internally
     */
    public void setB(Integer newB, boolean updateOtherColourSystem) {
        // Check newB is between 0 and 255, if not do nothing
        if (newB >= 0 && newB <= 255) {
            // Set b to newB value
            b = newB;
            // Update bString with the new value to trigger update of views observing this value
            bString.setValue(newB);
            // Update current manual picker colour
            updateManualPickerCurrentColour();
            // Update HSV variables if required
            if (updateOtherColourSystem) updateHSVfromRGB();
        }
    }


    /**
     * Sets h value for manual picker and updates other variables as appropriate
     * @param newH new h value to set
     * @param updateOtherColourSystem stops infinite loop of updating HSV then RGB etc,
     *                                true when called outside this class, ALWAYS false when called internally
     */
    public void setH(Integer newH, boolean updateOtherColourSystem) {
        // Check newH is between 0 and 360, if not do nothing
        if (newH >= 0 && newH <= 360) {
            // Set h to newH value
            h = newH;
            // Update hString with the new value to trigger update of views observing this value
            hString.setValue(newH);
            // No need to call updateManualPickerCurrentColour() here as this will be called when updating RGB values
            // Update RGB variables if required
            if (updateOtherColourSystem) updateRGBfromHSV();
        }
    }

    /**
     * Sets s value for manual picker and updates other variables as appropriate
     * @param newS new s value to set
     * @param updateOtherColourSystem stops infinite loop of updating HSV then RGB etc,
     *                                true when called outside this class, ALWAYS false when called internally
     */
    public void setS(Integer newS, boolean updateOtherColourSystem) {
        // Check newS is between 0 and 100, if not do nothing
        if (newS >= 0 && newS <= 100) {
            // Set s to newS value
            s = newS;
            // Update sString with the new value to trigger update of views observing this value
            sString.setValue(newS);
            // No need to call updateManualPickerCurrentColour() here as this will be called when updating RGB values
            // Update HSV variables if required
            if (updateOtherColourSystem) updateRGBfromHSV();
        }
    }

    /**
     * Sets v value for manual picker and updates other variables as appropriate
     * @param newV new v value to set
     * @param updateOtherColourSystem stops infinite loop of updating HSV then RGB etc,
     *                                true when called outside this class, ALWAYS false when called internally
     */
    public void setV(Integer newV, boolean updateOtherColourSystem) {
        // Check newV is between 0 and 100, if not do nothing
        if (newV >= 0 && newV <= 100) {
            // Set v to newV value
            v = newV;
            // Update vString with the new value to trigger update of views observing this value
            vString.setValue(newV);
            // No need to call updateManualPickerCurrentColour() here as this will be called when updating RGB values
            // Update HSV variables if required
            if (updateOtherColourSystem) updateRGBfromHSV();
        }
    }


    /**
     * Updates HSV values using the current RGB values
     */
    private void updateHSVfromRGB() {
        // Get new HSV values from current RGB values
        Triple<Double, Double, Double> newHSV = HelpersKt.RGBtoHSV(r, g, b);
        // Update HSV variables with the new values
        // no need to update other colour systems as RGB values already convert to HSV values
        setH(Math.toIntExact(Math.round(newHSV.component1())), false);
        setS(Math.toIntExact(Math.round(newHSV.component2())), false);
        setV(Math.toIntExact(Math.round(newHSV.component3())), false);
    }

    /**
     * Updates RGB values using the current HSV values
     */
    private void updateRGBfromHSV() {
        // Get new RGB values from current HSV values
        Triple<Double, Double, Double> newRGB = HelpersKt.HSVtoRGB(h, s / 100.0f, v / 100.0f);
        // Update RGB variables with the new values
        // no need to update other colour systems as HSV values already convert to RGB values
        setR(Math.toIntExact(Math.round(newRGB.component1())), false);
        setG(Math.toIntExact(Math.round(newRGB.component2())), false);
        setB(Math.toIntExact(Math.round(newRGB.component3())), false);
    }

    /**
     * Used by manual picker arrow buttons to +1/-1 from current colour values
     * @param change how much to change r value
     */
    public void changeR(int change) {
        setR(r + change, true);
    }
    /**
     * Used by manual picker arrow buttons to +1/-1 from current colour values
     * @param change how much to change g value
     */
    public void changeG(int change) {
        setG(g + change, true);
    }
    /**
     * Used by manual picker arrow buttons to +1/-1 from current colour values
     * @param change how much to change b value
     */
    public void changeB(int change) {
        setB(b + change, true);
    }

    /**
     * Used by manual picker arrow buttons to +1/-1 from current colour values
     * @param change how much to change h value
     */
    public void changeH(int change) {
        setH(h + change, true);
    }
    /**
     * Used by manual picker arrow buttons to +1/-1 from current colour values
     * @param change how much to change s value
     */
    public void changeS(int change) {
        setS(s + change, true);
    }
    /**
     * Used by manual picker arrow buttons to +1/-1 from current colour values
     * @param change how much to change v value
     */
    public void changeV(int change) {
        setV(v + change, true);
    }


    /**
     * For the observers to attach to and keep views updated with current rString value
     * @return rString
     */
    public LiveData<Integer> getR() {
        return rString;
    }
    /**
     * For the observers to attach to and keep views updated with current gString value
     * @return gString
     */
    public LiveData<Integer> getG() {
        return gString;
    }
    /**
     * For the observers to attach to and keep views updated with current bString value
     * @return bString
     */
    public LiveData<Integer> getB() {
        return bString;
    }

    /**
     * For the observers to attach to and keep views updated with current hString value
     * @return hString
     */
    public LiveData<Integer> getH() {
        return hString;
    }
    /**
     * For the observers to attach to and keep views updated with current sString value
     * @return sString
     */
    public LiveData<Integer> getS() {
        return sString;
    }
    /**
     * For the observers to attach to and keep views updated with current vString value
     * @return vString
     */
    public LiveData<Integer> getV() {
        return vString;
    }

    /**
     * Updates current camera colour to be the current values of cameraR, cameraG, cameraB
     */
    private void updateCurrentCameraColour() {
        cameraCurrentColour.setValue(Color.rgb(cameraR, cameraG, cameraB));
    }

    /**
     * For the observers to attach to and keep colour previews updated with current camera colour
     * @return cameraCurrentColour
     */
    public LiveData<Integer> getCameraCurrentColour() {
        return cameraCurrentColour;
    }

    /**
     * Sets r value of the current camera colour and updates colour LiveData string
     * @param r - r value of colour
     */
    public void setCameraR(int r) {
        // Set cameraR to new value of r
        cameraR = r;
        // Updates currentCameraColour using this new r value
        updateCurrentCameraColour();
    }
    /**
     * Sets g value of the current camera colour and updates colour LiveData string
     * @param g - g value of colour
     */
    public void setCameraG(int g) {
        // Set cameraG to new value of g
        cameraG = g;
        // Updates currentCameraColour using this new r value
        updateCurrentCameraColour();
    }
    /**
     * Sets b value of the current camera colour and updates colour LiveData string
     * @param b - b value of colour
     */
    public void setCameraB(int b) {
        // Set cameraB to new value of b
        cameraB = b;
        // Updates currentCameraColour using this new r value
        updateCurrentCameraColour();
    }

    /**
     * Gets red value of current camera colour
     * @return cameraR
     */
    public int getCameraR() {
        return cameraR;
    }

    /**
     * Gets green value of current camera colour
     * @return cameraG
     */
    public int getCameraG() {
        return cameraG;
    }

    /**
     * Gets blue value of current camera colour
     * @return cameraB
     */
    public int getCameraB() {
        return cameraB;
    }

    /**
     * Controls whether ManualPicker is in editing mode or not
     */
    public void setEditingColour(boolean editingColour) {
        this.editingColour.setValue(editingColour);
    }

    /**
     * For the observers to attach to and keep ManualPicker editing mode status in sync
     * @return editingColour
     */
    public LiveData<Boolean> getEditingColour() {
        return editingColour;
    }
}