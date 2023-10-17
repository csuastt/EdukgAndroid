/*
 * Copyright (c) 2020. rogergcc
 */
/***
 * This knowledge class is the debug class showing the courses matched which is somehow a generaion of debugging info
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.appsnipp.education.App;
import com.appsnipp.education.R;

import java.util.Arrays;
import java.util.List;


public class MyMatchesCourses {

    private static final String STORAGE = "shop";
    private SharedPreferences storage;

    private MyMatchesCourses() {
        storage = App.getInstance().getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
    }

    public static MyMatchesCourses get() {
        return new MyMatchesCourses();
    }

    public List<MatchCourse> getData() {
        return Arrays.asList(
                new MatchCourse(1, "Mathematics", "12 courses available", R.drawable.education_2),
                new MatchCourse(2, "Chinese", "50 courses available", R.drawable.education_3),
                new MatchCourse(3, "English", "265 courses available", R.drawable.education_4),
                new MatchCourse(4, "Physics", "18 courses available", R.drawable.education_1),
                new MatchCourse(5, "Chemistry", "36 courses available", R.drawable.education_5),
                new MatchCourse(6, "Biology", "145 courses available", R.drawable.education_6)
        );
    }

    public boolean isRated(int itemId) {
        return storage.getBoolean(String.valueOf(itemId), false);
    }

    public void setRated(int itemId, boolean isRated) {
        storage.edit().putBoolean(String.valueOf(itemId), isRated).apply();
    }
}
