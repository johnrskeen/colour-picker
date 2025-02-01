package com.skeensystems.colorpicker.ui.picker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.skeensystems.colorpicker.ui.picker.pickbycoloursystem.PickByHEXFragment;
import com.skeensystems.colorpicker.ui.picker.pickbycoloursystem.PickByHSLFragment;
import com.skeensystems.colorpicker.ui.picker.pickbycoloursystem.PickByHSVFragment;
import com.skeensystems.colorpicker.ui.picker.pickbycoloursystem.PickByRGBFragment;

class ManualPickerCollectionAdapter extends FragmentStateAdapter {
    public ManualPickerCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a new fragment instance in createFragment(int)
        if (position == 0) return new PickByRGBFragment();
        else if (position == 1) return new PickByHEXFragment();
        else if (position == 2) return new PickByHSVFragment();
        else return new PickByHSLFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}