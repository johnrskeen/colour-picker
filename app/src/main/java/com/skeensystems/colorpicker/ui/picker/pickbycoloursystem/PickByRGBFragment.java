package com.skeensystems.colorpicker.ui.picker.pickbycoloursystem;

import static com.skeensystems.colorpicker.MainActivity.mainActivityViewModel;

import android.annotation.SuppressLint;
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
import com.skeensystems.colorpicker.databinding.PickByRgbBinding;

public class PickByRGBFragment extends Fragment {

    private PickByRgbBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = PickByRgbBinding.inflate(inflater, container, false);

        // Update R editText to display latest value from mainActivityViewModel, when not in focus (to prevent infinite loop)
        final Observer<Integer> rObserver = newR -> {
            if (!binding.entry1.hasFocus()) binding.entry1.setText(String.valueOf(newR));
        };
        mainActivityViewModel.getR().observe(getViewLifecycleOwner(), rObserver);

        // Update G editText to display latest value from mainActivityViewModel, when not in focus (to prevent infinite loop)
        final Observer<Integer> gObserver = newG -> {
            if (!binding.entry2.hasFocus()) binding.entry2.setText(String.valueOf(newG));
        };
        mainActivityViewModel.getG().observe(getViewLifecycleOwner(), gObserver);

        // Update B editText to display latest value from mainActivityViewModel, when not in focus (to prevent infinite loop)
        final Observer<Integer> bObserver = newB -> {
            if (!binding.entry3.hasFocus()) binding.entry3.setText(String.valueOf(newB));
        };
        mainActivityViewModel.getB().observe(getViewLifecycleOwner(), bObserver);

        binding.up1.setOnClickListener(view -> mainActivityViewModel.changeR(1));
        binding.up2.setOnClickListener(view -> mainActivityViewModel.changeG(1));
        binding.up3.setOnClickListener(view -> mainActivityViewModel.changeB(1));

        binding.down1.setOnClickListener(view -> mainActivityViewModel.changeR(-1));
        binding.down2.setOnClickListener(view -> mainActivityViewModel.changeG(-1));
        binding.down3.setOnClickListener(view -> mainActivityViewModel.changeB(-1));


        // Make enter button clear focus of current in focus EditText
        View.OnKeyListener textEntryListener = ((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_UP)) {

                String newText = String.valueOf(((EditText)v).getText());
                int newInt = 0;
                try {
                    newInt = Integer.parseInt(newText);
                    if (newInt > 255) newInt = 255;
                    else if (newInt < 0) newInt = 0;
                }
                catch (NumberFormatException ignored) {
                }
                if (v.getId() == R.id.entry1) mainActivityViewModel.setR(newInt, true);
                else if (v.getId() == R.id.entry2) mainActivityViewModel.setG(newInt, true);
                else if (v.getId() == R.id.entry3) mainActivityViewModel.setB(newInt, true);

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
                    v.clearFocus();
                    return true;
                }
            }
            return false;
        });

        // Update R editText to display latest value from mainActivityViewModel, when focus is lost
        binding.entry1.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.entry1.setText(String.valueOf(mainActivityViewModel.getR().getValue()));
            }
        });
        // Update G editText to display latest value from mainActivityViewModel, when focus is lost
        binding.entry2.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.entry2.setText(String.valueOf(mainActivityViewModel.getG().getValue()));
            }
        });
        // Update B editText to display latest value from mainActivityViewModel, when focus is lost
        binding.entry3.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.entry3.setText(String.valueOf(mainActivityViewModel.getB().getValue()));
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