package com.skeensystems.colorpicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.skeensystems.colorpicker.ui.camera.CameraFragment;
import com.skeensystems.colorpicker.ui.picker.ManualPickerFragment;
import com.skeensystems.colorpicker.ui.saved.SavedColoursFragment;

class MainActivityCollectionAdapter extends FragmentStateAdapter {

    public MainActivityCollectionAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a new fragment instance in createFragment(int)
        if (position == 0) {
            return new SavedColoursFragment();
        }
        else if (position == 1) {
            return new CameraFragment();
        }
        else {
            return new ManualPickerFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}