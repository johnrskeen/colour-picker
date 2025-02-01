package com.skeensystems.colorpicker.ui.picker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ManualPickerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ManualPickerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}