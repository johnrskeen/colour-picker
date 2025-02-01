package com.skeensystems.colorpicker;

import android.graphics.Color;

import kotlin.Triple;

// Class of useful functions
public class Helpers {

    // Dark and light text colours (to be used on light and dark backgrounds respectively)
    public static int DARK_TEXT_COLOUR = Color.BLACK;
    public static int LIGHT_TEXT_COLOUR =  0xffdddddd;

    /**
     * Calculates whether text should be light or dark when inputted colour is the background
     * @param r red value, 0 <= r <= 255
     * @param g green value, 0 <= g <= 255
     * @param b blue value, 0 <= b <= 255
     * @return true if inputted colour requires light text,
     *         false if inputted colour requires dark text
     */
    public static boolean backgroundRequiresLightText(int r, int g, int b) {
        // Convert r from [0, 255] to [0, 1]
        double newR = r / 255.0;
        // Run the sums to do the magic
        if (newR <= 0.04045) {
            newR = newR / 12.92;
        }
        else {
            newR = Math.pow(((newR + 0.055) / 1.055), 2.4);
        }
        // Convert g from [0, 255] to [0, 1]
        double newG = g / 255.0;
        // Run the sums to do the magic
        if (newG <= 0.04045) {
            newG = newG / 12.92;
        }
        else {
            newG = Math.pow(((newG + 0.055) / 1.055), 2.4);
        }
        // Convert b from [0, 255] to [0, 1]
        double newB = b / 255.0;
        // Run the sums to do the magic
        if (newB <= 0.04045) {
            newB = newB / 12.92;
        }
        else {
            newB = Math.pow(((newB + 0.055) / 1.055), 2.4);
        }
        // Final calculations
        double l = 0.2126 * newR + 0.7152 * newG + 0.0722 * newB;
        // Return answer to the question
        return l <= 0.179;

        // As you probably guessed, I did not write this function, but so far it has given acceptable results
    }

    /**
     * Converts RGB colour to HSV
     * This function already exists, Color.RGBtoHSV(r, g, b, hsv) (hsv is float[3])
     * @param r red value, 0 <= r <= 255
     * @param g green value, 0 <= g <= 255
     * @param b blue value, 0 <= b <= 255
     * @return Triple(h, s, v)
     */
    public static Triple<Double, Double, Double> RGBtoHSV(int r, int g, int b) {
        // Just a formula to calculate HSV values from RGB values
        float rScaled = (float) r / 255;
        float gScaled = (float) g / 255;
        float bScaled = (float) b / 255;

        float comparison = Math.max(rScaled, gScaled);
        float cMax = Math.max(comparison, bScaled);

        comparison = Math.min(rScaled, gScaled);
        float cMin = Math.min(comparison, bScaled);

        float delta = cMax - cMin;

        double h;

        if (delta == 0) {
            h = 0;
        } else if (cMax == rScaled) {
            h = 60 * (((gScaled - bScaled) / delta) % 6);
        } else if (cMax == gScaled) {
            h = 60 * (((bScaled - rScaled) / delta) + 2);
        } else {
            h = 60 * (((rScaled - gScaled) / delta) + 4);
        }

        if (h < 0) {
            h += 360;
        }

        double s;

        if (cMax == 0) {
            s = 0;
        } else {
            s = 100 * delta / cMax;
        }

        double v = cMax * 100;

        return new Triple<>(h, s, v);
    }

    /**
     * Converts HSV colour to RGB
     * @param h 0 <= h <= 360
     * @param s 0 <= s <= 1
     * @param v 0 <= v <= 1
     * @return Triple(r, g, b)
     */
    public static Triple<Double, Double, Double> HSVtoRGB(float h, float s, float v) {
        // Just a formula to calculate RGB values from HSV values
        assert h >= 0;
        if (h == 360) h = 0;
        double c = s * v;
        double x = c * (1 - Math.abs((h / 60) % 2 - 1));
        double m = v - c;
        Triple<Double, Double, Double> rgbTemp = new Triple<>(0.0, 0.0, 0.0);
        if (h < 60) {
            rgbTemp = new Triple<>(c, x, 0.0);
        }
        else if (h < 120) {
            rgbTemp = new Triple<>(x, c, 0.0);
        }
        else if (h < 180) {
            rgbTemp = new Triple<>(0.0, c, x);
        }
        else if (h < 240) {
            rgbTemp = new Triple<>(0.0, x, c);
        }
        else if (h < 300) {
            rgbTemp = new Triple<>(x, 0.0, c);
        }
        else if (h < 360) {
            rgbTemp = new Triple<>(c, 0.0, x);
        }
        else {
            assert false;
        }

        return new Triple<>((rgbTemp.component1() + m) * 255,
                (rgbTemp.component2() + m) * 255,
                (rgbTemp.component3() + m) * 255);
    }


    /**
     * Returns HEX string from inputted RGB values
     * @param r red value, 0 <= r <= 255
     * @param g green value, 0 <= g <= 255
     * @param b blue value, 0 <= b <= 255
     * @return HEX string of inputted RGB colour
     */
    public static String getHEXString(int r, int g, int b) {
        return String.format("#%06X", (0xFFFFFF & Color.rgb(r, g, b)));
    }

    /**
     * Returns RGB string from inputted RGB values
     * @param r red value, 0 <= r <= 255
     * @param g green value, 0 <= g <= 255
     * @param b blue value, 0 <= b <= 255
     * @return RGB string of inputted RGB colour
     */
    public static String getRGBString(int r, int g, int b) {
        return r + ", " + g + ", " + b;
    }

    /**
     * Returns HSV string from inputted RGB values
     * @param r red value, 0 <= r <= 255
     * @param g green value, 0 <= g <= 255
     * @param b blue value, 0 <= b <= 255
     * @return HSV string of inputted RGB colour
     */
    public static String getHSVString(int r, int g, int b) {
        // Convert RGB values to HSV values
        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);
        // Construct HSV string
        return (int)hsv[0] + "\u00B0, " + Math.round(hsv[1] * 100) + "%, " + Math.round(hsv[2] * 100) + "%";
    }

    /**
     * Returns HSL string from inputted RGB values
     * @param r red value, 0 <= r <= 255
     * @param g green value, 0 <= g <= 255
     * @param b blue value, 0 <= b <= 255
     * @return HSL string of inputted RGB colour
     */
    public static String getHSLString(int r, int g, int b) {
        // Convert RGB values to HSV values
        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);
        // Convert HSV values to HSL values
        int h = (int)hsv[0];
        double l = hsv[2] * (1 - 0.5 * hsv[1]);
        double s = 0;
        if (!(l == 0 || l == 100)) {
            s = (hsv[2] - l) / Math.min(l, 1 - l);
        }
        // Construct HSL string
        return h + "\u00B0, " + Math.round(s * 100) + "%, " + Math.round(l * 100) + "%";
    }

    /**
     * Returns CMYK string from inputted RGB values
     * @param r red value, 0 <= r <= 255
     * @param g green value, 0 <= g <= 255
     * @param b blue value, 0 <= b <= 255
     * @return CMYK string of inputted RGB colour
     */
    public static String getCMYKString(int r, int g, int b) {
        // Convert RGB values to CMYK values
        double rP = r / 255.0f;
        double gP = g / 255.0f;
        double bP = b / 255.0f;
        double k = 1 - Math.max(rP, Math.max(gP, bP));
        double c = (1 - rP - k) / (1 - k);
        double m = (1 - gP - k) / (1 - k);
        double y = (1 - bP - k) / (1 - k);
        // Convert CMYK string
        return Math.round(c * 100) + "%, " + Math.round(m * 100) + "%, " + Math.round(y * 100) + "%, " + Math.round(k * 100) + "%";
    }
}
