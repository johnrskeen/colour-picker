package com.skeensystems.colorpicker.ui.picker.pickbycoloursystem;

import android.text.InputFilter;
import android.text.Spanned;

class HEXInputFilter implements InputFilter {

    /**
     * Filters user input to accept only valid HEX characters
     * @return inputted string but with any non-HEX characters removed
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder filtered = new StringBuilder();
        for (int i = start; i < end; i++) {
            char currentChar = source.charAt(i);
            if (Character.isDigit(currentChar)
                    || currentChar == 'A' || currentChar == 'B' || currentChar == 'C'
                    || currentChar == 'D' || currentChar == 'E' || currentChar == 'F') {
                filtered.append(currentChar);
            }
        }
        return filtered.toString();
    }
}
