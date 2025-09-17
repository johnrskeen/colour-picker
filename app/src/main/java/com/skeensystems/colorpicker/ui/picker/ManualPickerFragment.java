package com.skeensystems.colorpicker.ui.picker;

import static com.skeensystems.colorpicker.MainActivity.colourDAO;
import static com.skeensystems.colorpicker.MainActivity.mainActivityViewModel;
import static com.skeensystems.colorpicker.MainActivity.savedColours;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.skeensystems.colorpicker.HelpersKt;
import com.skeensystems.colorpicker.R;
import com.skeensystems.colorpicker.database.DatabaseColour;
import com.skeensystems.colorpicker.database.SavedColour;
import com.skeensystems.colorpicker.databinding.FragmentManualPickerBinding;

import java.util.ArrayList;
import java.util.Objects;

public class ManualPickerFragment extends Fragment {

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ComposeView composeView = new ComposeView(requireContext());
        PickerScreenKt.setPickerContent(composeView);
        return composeView;
    }
}