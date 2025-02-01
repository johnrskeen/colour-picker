package com.skeensystems.colorpicker.database;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skeensystems.colorpicker.HelpersKt;

public class DatabaseColour implements Colour {

    // Name of colour
    private final String name;

    // RGB colour values
    private final int r;
    private final int g;
    private final int b;

    private DatabaseColour complementaryColour;

    // When this colour is the background, should text be white (true) or black (false)
    private boolean requiresLightText;

    public DatabaseColour(String name, int r, int g, int b) {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
        updateTextColour();
    }

    @Override
    public int getColour() {
        return Color.rgb(r, g, b);
    }

    public void setComplementaryColour(String complementaryName, int complementaryR, int complementaryG, int complementaryB) {
        complementaryColour = new DatabaseColour(complementaryName, complementaryR, complementaryG, complementaryB);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getR() {
        return r;
    }

    @Override
    public int getG() {
        return g;
    }

    @Override
    public int getB() {
        return b;
    }
    public DatabaseColour getComplementaryColour() {
        return complementaryColour;
    }

    private void updateTextColour() {
        requiresLightText = HelpersKt.backgroundRequiresLightText(r, g, b);
    }

    @Override
    public int getTextColour() {
        if (requiresLightText) return HelpersKt.LIGHT_TEXT_COLOUR;
        else return HelpersKt.DARK_TEXT_COLOUR;
    }

    @Override
    public String getHEXString() {
        return HelpersKt.getHEXString(r, g, b);
    }

    @Override
    public String getRGBString() {
        return HelpersKt.getRGBString(r, g, b);
    }

    @Override
    public String getHSVString() {
        return HelpersKt.getHSVString(r, g, b);
    }

    @Override
    public String getHSLString() {
        return HelpersKt.getHSLString(r, g, b);
    }

    @Override
    public String getCMYKString() {
        return HelpersKt.getCMYKString(r, g, b);
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (!obj.getClass().equals(this.getClass())) return false;
        return r == ((DatabaseColour) obj).r && g == ((DatabaseColour) obj).g && b == ((DatabaseColour) obj).b;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
