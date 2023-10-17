/*
 * Copyright (c) 2021. rogergcc
 */

 /***
 * This is the personal setting view model page
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.ui.settings;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.appsnipp.education.R;

import java.util.Objects;



public class PersonSettingViewModel extends ViewModel {
    private MutableLiveData<PersonSettingFormState> personSettingFormState
            = new MutableLiveData<>();

    public LiveData<PersonSettingFormState> getPersonSettingFormState() {
        return personSettingFormState;
    }
    public PersonSettingViewModel() { }

    public void settingDataChanged(String oldPassword,
                                   String correctOldPassword,
                                   String newPassword,
                                   String newPassword2) {
        if (!isOldPasswordValid(oldPassword, correctOldPassword)) {
            personSettingFormState.setValue(
                    new PersonSettingFormState(R.string.invalid_oldPassword,
                            null, null));
        } else if (!isPasswordValid(newPassword)) {
            personSettingFormState.setValue(
                    new PersonSettingFormState(null,
                            R.string.invalid_password, null));
        } else if (!isPassword2Valid(newPassword, newPassword2)) {
            personSettingFormState.setValue(
                    new PersonSettingFormState(null,
                            null, R.string.invalid_password2));
        }
        else {
            personSettingFormState.setValue(new PersonSettingFormState(true));
        }
    }

    // if the oldPassword is equal to the correct one
    private boolean isOldPasswordValid(String oldPassword, String correctPassword) {
        return Objects.equals(oldPassword, correctPassword);
    }

    // if the password1 is equal to password2
    private boolean isPassword2Valid(String password, String password2){
        return Objects.equals(password, password2);
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
