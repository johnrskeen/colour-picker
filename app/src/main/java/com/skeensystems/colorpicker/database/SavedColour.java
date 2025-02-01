package com.skeensystems.colorpicker.database;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.skeensystems.colorpicker.Helpers;

import java.util.Locale;

// Saved colour object, provides useful functions for saved colours and stores data about them
@Entity
public class SavedColour implements Colour {
    @PrimaryKey
    private long id;


    // RGB colour values
    @ColumnInfo(name = "r")
    private int r;
    @ColumnInfo(name = "g")
    private int g;
    @ColumnInfo(name = "b")
    private int b;

    // True if colour is favorite, otherwise false

    @ColumnInfo(name = "favorite")
    private boolean favorite;

    // When this colour is the background, should text be white (true) or black (false)
    @Ignore
    private boolean requiresLightText;

    // Stores the closest colour to this in the colour database
    @Ignore
    private DatabaseColour closestMatch;
    @Ignore
    private DatabaseColour firstClosest;
    @Ignore
    private DatabaseColour secondClosest;
    @Ignore
    private DatabaseColour thirdClosest;

    public SavedColour() {
        id = 0;
        r = 0;
        g = 0;
        b = 0;
        favorite = false;
        requiresLightText = false;
    }

    @Ignore
    public SavedColour(long id, int r, int g, int b, boolean favorite) {
        this.id = id;
        this.r = r;
        this.g = g;
        this.b = b;
        this.favorite = favorite;
        updateTextColour();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getColour() {
        return Color.rgb(r, g, b);
    }

    public String getClosestMatchString() {
        if (closestMatch != null) {
            return "\u2248 " + closestMatch;
        }
        else {
            return "";
        }
    }

    public void setR(int r) {
        this.r = r;
        updateTextColour();
    }

    @Override
    public int getR() {
        return r;
    }

    public void setG(int g) {
        this.g = g;
        updateTextColour();
    }

    @Override
    public int getG() {
        return g;
    }

    public void setB(int b) {
        this.b = b;
        updateTextColour();
    }

    @Override
    public int getB() {
        return b;
    }

    private void updateTextColour() {
        requiresLightText = Helpers.backgroundRequiresLightText(r, g, b);
    }

    @Override
    public int getTextColour() {
        if (requiresLightText) return Helpers.LIGHT_TEXT_COLOUR;
        else return Helpers.DARK_TEXT_COLOUR;
    }

    @Override
    public String getHEXString() {
        return Helpers.getHEXString(r, g, b);
    }

    @Override
    public String getRGBString() {
        return Helpers.getRGBString(r, g, b);
    }

    @Override
    public String getHSVString() {
        return Helpers.getHSVString(r, g, b);
    }

    @Override
    public String getHSLString() {
        return Helpers.getHSLString(r, g, b);
    }

    @Override
    public String getCMYKString() {
        return Helpers.getCMYKString(r, g, b);
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean getFavorite() {
        return favorite;
    }



    public void setClosestMatch(DatabaseColour closestMatch) {
        this.closestMatch = closestMatch;
    }

    public DatabaseColour getClosestMatch() {
        return closestMatch;
    }

    @Override
    public String getName() {
        return closestMatch.getName();
    }

    public void setFirstClosest(DatabaseColour firstClosest) {
        this.firstClosest = firstClosest;
    }

    public DatabaseColour getFirstClosest() {
        return firstClosest;
    }

    public void setSecondClosest(DatabaseColour secondClosest) {
        this.secondClosest = secondClosest;
    }

    public DatabaseColour getSecondClosest() {
        return secondClosest;
    }

    public void setThirdClosest(DatabaseColour thirdClosest) {
        this.thirdClosest = thirdClosest;
    }

    public DatabaseColour getThirdClosest() {
        return thirdClosest;
    }


    public String getSortValue() {
        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);

        int newH = (int)hsv[0];
        int newS = Math.round(hsv[1] * 100);
        int newV = Math.round(hsv[2] * 100);

        return String.format(Locale.getDefault(), "%03d", newH)
                + String.format(Locale.getDefault(), "%03d", newS)
                + String.format(Locale.getDefault(), "%03d", newV);
    }


    @NonNull
    @Override
    public String toString() {
        return "Id: " + id + ", Colour: " + r + ", " + g + ", " + b + ", Favorite: " + favorite;
    }
}
