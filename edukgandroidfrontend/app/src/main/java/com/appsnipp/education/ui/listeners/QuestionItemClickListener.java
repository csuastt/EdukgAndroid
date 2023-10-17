/*
 * Copyright (c) 2020. rogergcc
 */

/***
 * This is the question item click listener interface used for clicking event of the question item.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.ui.listeners;

import android.widget.ImageView;

import com.appsnipp.education.model.Question;

public interface QuestionItemClickListener {
    void onDashboardCourseClick(Question question, ImageView imageView); // Should use imageview to make the shared animation between the two activity
    void onDashboardCourseLongClick(Question question, ImageView imageView);
}