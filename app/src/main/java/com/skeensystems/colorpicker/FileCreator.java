package com.skeensystems.colorpicker;

import android.content.Context;

import com.skeensystems.colorpicker.database.SavedColourEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileCreator {

    FileCreator() {
    }

    public void createFiles(Context context) {
        File directory = context.getFilesDir();
        ArrayList<SavedColourEntity> oldSavedColours = new ArrayList<>(Arrays.asList(new SavedColourEntity(0, 99, 25, 8, false),
                new SavedColourEntity(1, 124, 116, 116, false),
                new SavedColourEntity(2, 112, 93, 65, false),
                new SavedColourEntity(3, 131, 73, 12, false),
                new SavedColourEntity(4, 55, 26, 7, false),
                new SavedColourEntity(5, 40, 10, 2, false),
                new SavedColourEntity(6, 91, 51, 18, false),
                new SavedColourEntity(7, 59, 34, 7, false),
                new SavedColourEntity(8, 77, 31, 0, false),
                new SavedColourEntity(9, 244, 226, 84, true),
                new SavedColourEntity(10, 105, 183, 97, false),
                new SavedColourEntity(11, 99, 138, 210, false),
                new SavedColourEntity(12, 108, 132, 213, false),
                new SavedColourEntity(13, 255, 119, 232, false),
                new SavedColourEntity(14, 38, 98, 255, false),
                new SavedColourEntity(15, 25, 94, 255, false),
                new SavedColourEntity(16, 36, 86, 218, false),
                new SavedColourEntity(17, 32, 81, 228, false),
                new SavedColourEntity(18, 24, 36, 101, false),
                new SavedColourEntity(19, 29, 90, 243, false),
                new SavedColourEntity(20, 218, 233, 219, false),
                new SavedColourEntity(21, 158, 180, 175, false),
                new SavedColourEntity(22, 168, 186, 188, true),
                new SavedColourEntity(23, 194, 203, 191, false),
                new SavedColourEntity(24, 91, 87, 64, false),
                new SavedColourEntity(25, 103, 83, 45, false),
                new SavedColourEntity(26, 191, 146, 122, false),
                new SavedColourEntity(27, 30, 154, 54, false),
                new SavedColourEntity(28, 173, 112, 107, true),
                new SavedColourEntity(29, 69, 110, 146, false),
                new SavedColourEntity(30, 70, 129, 149, false),
                new SavedColourEntity(31, 70, 129, 149, false),
                new SavedColourEntity(32, 70, 129, 149, false),
                new SavedColourEntity(33, 65, 51, 58, false),
                new SavedColourEntity(34, 187, 166, 157, true),
                new SavedColourEntity(35, 84, 33, 74, false),
                new SavedColourEntity(36, 68, 44, 86, false),
                new SavedColourEntity(37, 27, 55, 58, false),
                new SavedColourEntity(38, 41, 106, 113, false),
                new SavedColourEntity(39, 0, 87, 112, false),
                new SavedColourEntity(40, 255, 0, 0, false),
                new SavedColourEntity(41, 118, 73, 79, true),
                new SavedColourEntity(42, 63, 87, 108, false),
                new SavedColourEntity(43, 255, 191, 0, false)));


        File file = new File(directory + "pickedColours.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            for (SavedColourEntity pickedColour : oldSavedColours) {
                String colourText = String.format("#%02X%02X%02X", pickedColour.getR(), pickedColour.getG(), pickedColour.getB());
                fos.write(colourText.getBytes());
                fos.write("\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        file = new File(directory + "favoriteColours.txt");
        fos = null;
        try {
            fos = new FileOutputStream(file);
            for (SavedColourEntity pickedColour : oldSavedColours) {
                if (pickedColour.getFavourite()) {
                    fos.write(String.valueOf(pickedColour.getId()).getBytes());
                    fos.write("\n".getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
