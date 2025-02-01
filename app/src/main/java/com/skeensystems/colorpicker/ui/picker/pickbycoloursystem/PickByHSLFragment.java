package com.skeensystems.colorpicker.ui.picker.pickbycoloursystem;

import static com.skeensystems.colorpicker.MainActivity.mainActivityViewModel;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.skeensystems.colorpicker.R;
import com.skeensystems.colorpicker.databinding.PickByHslBinding;

public class PickByHSLFragment extends Fragment {

    private PickByHslBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = PickByHslBinding.inflate(inflater, container, false);

        // Update H editText to display latest value from mainActivityViewModel, when not in focus (to prevent infinite loop)
        final Observer<Integer> hObserver = newH -> {
            if (!binding.entry1.hasFocus()) binding.entry1.setText(String.valueOf(newH));
        };
        mainActivityViewModel.getH().observe(getViewLifecycleOwner(), hObserver);

        // Update S editText to display latest value from mainActivityViewModel, when not in focus (to prevent infinite loop)
        final Observer<Integer> sObserver = newS -> {
            if (!binding.entry2.hasFocus()) binding.entry2.setText(String.valueOf(newS));
        };
        mainActivityViewModel.getS().observe(getViewLifecycleOwner(), sObserver);

        // Update V editText to display latest value from mainActivityViewModel, when not in focus (to prevent infinite loop)
        final Observer<Integer> vObserver = newV -> {
            if (!binding.entry3.hasFocus()) binding.entry3.setText(String.valueOf(newV));
        };
        mainActivityViewModel.getV().observe(getViewLifecycleOwner(), vObserver);

        binding.up1.setOnClickListener(view -> mainActivityViewModel.changeH(1));
        binding.up2.setOnClickListener(view -> mainActivityViewModel.changeS(1));
        binding.up3.setOnClickListener(view -> mainActivityViewModel.changeV(1));

        binding.down1.setOnClickListener(view -> mainActivityViewModel.changeH(-1));
        binding.down2.setOnClickListener(view -> mainActivityViewModel.changeS(-1));
        binding.down3.setOnClickListener(view -> mainActivityViewModel.changeV(-1));


        // Make enter button clear focus of current in focus EditText
        View.OnKeyListener textEntryListener = ((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_UP)) {

                String newText = String.valueOf(((EditText)v).getText());
                int newInt = 0;
                try {
                    newInt = Integer.parseInt(newText);
                    if (newInt > 360 && v.getId() == R.id.entry1) newInt = 360;
                    else if (newInt > 100 && !(v.getId() == R.id.entry1)) newInt = 100;
                    else if (newInt < 0) newInt = 0;
                }
                catch (NumberFormatException ignored) {
                }
                if (v.getId() == R.id.entry1) mainActivityViewModel.setH(newInt, true);
                else if (v.getId() == R.id.entry2) mainActivityViewModel.setS(newInt, true);
                else if (v.getId() == R.id.entry3) mainActivityViewModel.setV(newInt, true);

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
                    v.clearFocus();
                    return true;
                }
            }
            return false;
        });

        // Update H editText to display latest value from mainActivityViewModel, when focus is lost
        binding.entry1.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.entry1.setText(String.valueOf(mainActivityViewModel.getH().getValue()));
            }
        });
        // Update S editText to display latest value from mainActivityViewModel, when focus is lost
        binding.entry2.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.entry2.setText(String.valueOf(mainActivityViewModel.getS().getValue()));
            }
        });
        // Update V editText to display latest value from mainActivityViewModel, when focus is lost
        binding.entry3.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.entry3.setText(String.valueOf(mainActivityViewModel.getV().getValue()));
            }
        });

        // Set OnKeyListeners for the EditTexts
        binding.entry1.setOnKeyListener(textEntryListener);
        binding.entry2.setOnKeyListener(textEntryListener);
        binding.entry3.setOnKeyListener(textEntryListener);


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}