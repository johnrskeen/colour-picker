package com.skeensystems.colorpicker.database;

public interface Colour {

    int getColour();

    int getR();

    int getG();

    int getB();

    int getTextColour();

    String getHEXString();

    String getRGBString();

    String getHSVString();

    String getHSLString();

    String getCMYKString();

    String getName();

}
