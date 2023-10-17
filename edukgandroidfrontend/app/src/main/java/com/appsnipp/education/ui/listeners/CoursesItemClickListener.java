/*
 * Copyright (c) 2020. rogergcc
 */

/***
 * This is the course item click listener interface used for clicking event of the course item.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.ui.listeners;

import android.widget.ImageView;

import com.appsnipp.education.model.CourseCard;

public interface CoursesItemClickListener {

    void onDashboardCourseClick(CourseCard courseCard, ImageView imageView); // Shoud use imageview to make the shared animation between the two activity

}
