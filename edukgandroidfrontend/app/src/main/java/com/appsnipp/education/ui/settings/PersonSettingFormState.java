/*
 * Copyright (c) 2021. rogergcc
 */

 /***
 * This is the personal setting state page
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.ui.settings;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
public class PersonSettingFormState {
    @Nullable
    private Integer oldPasswordError;
    @Nullable
    private Integer newPasswordError;
    @Nullable
    private Integer newPassword2Error;
    private boolean isDataValid;

    PersonSettingFormState(@Nullable Integer oldPasswordError,
                           @Nullable Integer newPasswordError,
                           @Nullable Integer newPassword2Error) {
        this.newPasswordError = newPasswordError;
        this.newPassword2Error = newPassword2Error;
        this.oldPasswordError = oldPasswordError;
        this.isDataValid = false;
    }

    PersonSettingFormState(boolean isDataValid) {
        this.newPasswordError = null;
        this.newPassword2Error = null;
        this.oldPasswordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getNewPasswordError() {
        return newPasswordError;
    }

    @Nullable
    public Integer getNewPassword2Error() {
        return newPassword2Error;
    }

    @Nullable
    public Integer getOldPasswordError() {
        return oldPasswordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}

