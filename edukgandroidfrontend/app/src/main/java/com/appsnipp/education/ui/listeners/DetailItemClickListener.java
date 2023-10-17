/*
 * Copyright (c) 2020. rogergcc
 */

/***
 * This is the detail item click listener interface used for clicking event of the detail item.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.ui.listeners;

import android.widget.ImageView;

import com.appsnipp.education.model.Question;

public interface DetailItemClickListener {
    void onDashboardDetailClick(Question question, ImageView imageView); // Should use imageview to make the shared animation between the two activity
    void onDashboardDetailLongClick(Question question, ImageView imageView);
}