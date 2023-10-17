package com.appsnipp.education.ui.login;

import android.os.Bundle;

import com.appsnipp.education.activities.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginActivityTest {
    private LoginActivity myLoginActivity;
    Bundle mySavedInstanceState;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onCreate() {
        myLoginActivity.onCreate(mySavedInstanceState);

    }
}