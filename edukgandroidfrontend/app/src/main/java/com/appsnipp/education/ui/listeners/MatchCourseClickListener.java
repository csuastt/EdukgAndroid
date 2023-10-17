/*
 * Copyright (c) 2020. rogergcc
 */

/***
 * This is the match course listener interface used for matching course adapter and corresponding card clicking event.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.ui.listeners;

import android.widget.ImageView;

import com.appsnipp.education.model.MatchCourse;

public interface MatchCourseClickListener {

    void onScrollPagerItemClick(MatchCourse courseCard, ImageView imageView); // Shoud use imageview to make the shared animation between the two activity

}
