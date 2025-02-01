package com.skeensystems.colorpicker.ui.picker.pickbycoloursystem;

import static com.skeensystems.colorpicker.MainActivity.mainActivityViewModel;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.skeensystems.colorpicker.databinding.PickByHexBinding;

import java.util.Locale;

public class PickByHEXFragment extends Fragment {

    private PickByHexBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = PickByHexBinding.inflate(inflater, container, false);

        // Update R (HEX) editText to display latest value from mainActivityViewModel, when not in focus (to prevent infinite loop)
        final Observer<Integer> rObserver = newR -> {
            String newString = getHexStringFromInt(newR);
            if (!newString.equals(String.valueOf(binding.entry1.getText())) && !binding.entry1.hasFocus()) {
                binding.entry1.setText(newString);
            }
        };
        mainActivityViewModel.getR().observe(getViewLifecycleOwner(), rObserver);

        // Update G (HEX) editText to display latest value from mainActivityViewModel, when not in focus (to prevent infinite loop)
        final Observer<Integer> gObserver = newG -> {
            String newString = getHexStringFromInt(newG);
            if (!newString.equals(String.valueOf(binding.entry2.getText())) && !binding.entry2.hasFocus()) {
                binding.entry2.setText(newString);
            }
        };
        mainActivityViewModel.getG().observe(getViewLifecycleOwner(), gObserver);

        // Update B (HEX) editText to display latest value from mainActivityViewModel, when not in focus (to prevent infinite loop)
        final Observer<Integer> bObserver = newB -> {
            String newString = getHexStringFromInt(newB);
            if (!newString.equals(String.valueOf(binding.entry3.getText())) && !binding.entry3.hasFocus()) {
                binding.entry3.setText(newString);
            }
        };
        mainActivityViewModel.getB().observe(getViewLifecycleOwner(), bObserver);

        binding.up1.setOnClickListener(view -> mainActivityViewModel.changeR(1));
        binding.up2.setOnClickListener(view -> mainActivityViewModel.changeG(1));
        binding.up3.setOnClickListener(view -> mainActivityViewModel.changeB(1));

        binding.down1.setOnClickListener(view -> mainActivityViewModel.changeR(-1));
        binding.down2.setOnClickListener(view -> mainActivityViewModel.changeG(-1));
        binding.down3.setOnClickListener(view -> mainActivityViewModel.changeB(-1));

        // Only allow valid HEX characters to be inputted
        binding.entry1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called before the text is changed.
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called when the text is being changed.
                // Use charSequence to access the current text in the EditText.
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called after the text has been changed.
                // Use editable to access the updated text in the EditText.
                String newText = String.valueOf(editable);
                int newInt = 0;
                try {
                    newInt = Integer.parseInt(newText, 16);
                    if (newInt > 255) newInt = 255;
                    else if (newInt < 0) newInt = 0;
                }
                catch (NumberFormatException ignored) {
                }
                if (binding.entry1.hasFocus()) mainActivityViewModel.setR(newInt, true);
            }
        });
        // Only allow valid HEX characters to be inputted
        binding.entry2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called before the text is changed.
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called when the text is being changed.
                // Use charSequence to access the current text in the EditText.
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called after the text has been changed.
                // Use editable to access the updated text in the EditText.
                String newText = String.valueOf(editable);
                int newInt = 0;
                try {
                    newInt = Integer.parseInt(newText, 16);
                    if (newInt > 255) newInt = 255;
                    else if (newInt < 0) newInt = 0;
                }
                catch (NumberFormatException ignored) {
                }
                if (binding.entry2.hasFocus()) mainActivityViewModel.setG(newInt, true);
            }
        });
        // Only allow valid HEX characters to be inputted
        binding.entry3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called before the text is changed.
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called when the text is being changed.
                // Use charSequence to access the current text in the EditText.
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called after the text has been changed.
                // Use editable to access the updated text in the EditText.
                String newText = String.valueOf(editable);
                int newInt = 0;
                try {
                    newInt = Integer.parseInt(newText, 16);
                    if (newInt > 255) newInt = 255;
                    else if (newInt < 0) newInt = 0;
                }
                catch (NumberFormatException ignored) {
                }
                if (binding.entry3.hasFocus()) mainActivityViewModel.setB(newInt, true);
            }
        });

        InputFilter[] filters = new InputFilter[] { new HEXInputFilter() };
        binding.entry1.setFilters(filters);
        binding.entry2.setFilters(filters);
        binding.entry3.setFilters(filters);


        // Update R (HEX) editText to display latest value from mainActivityViewModel, when focus is lost
        binding.entry1.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.entry1.setText(getHexStringFromInt(mainActivityViewModel.getR().getValue()));
            }
        });
        // Update G (HEX) editText to display latest value from mainActivityViewModel, when focus is lost
        binding.entry2.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.entry2.setText(getHexStringFromInt(mainActivityViewModel.getG().getValue()));
            }
        });
        // Update B (HEX) editText to display latest value from mainActivityViewModel, when focus is lost
        binding.entry3.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.entry3.setText(getHexStringFromInt(mainActivityViewModel.getB().getValue()));
            }
        });

        // Make enter button clear focus of current in focus EditText
        View.OnKeyListener textEntryListener = ((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
                v.clearFocus();
                return true;
            }
            return false;
        });

        // Set OnKeyListeners for the EditTexts
        binding.entry1.setOnKeyListener(textEntryListener);
        binding.entry2.setOnKeyListener(textEntryListener);
        binding.entry3.setOnKeyListener(textEntryListener);

        return binding.getRoot();
    }

    private String getHexStringFromInt(Integer intToConvert) {
        String hexString = Integer.toHexString(intToConvert).toUpperCase(Locale.getDefault());
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        return hexString;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
