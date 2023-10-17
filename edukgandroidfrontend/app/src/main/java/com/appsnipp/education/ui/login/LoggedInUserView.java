package com.appsnipp.education.ui.login;
/***
 * This is the login in user view page.
 * @author Shuning Zhang
 * @version 1.0
 */

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}