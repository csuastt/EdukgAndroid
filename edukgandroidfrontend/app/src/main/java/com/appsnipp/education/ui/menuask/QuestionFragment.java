package com.appsnipp.education.ui.menuask;

/***
 * This is the question page fragment page.
 * @author Shuning Zhang
 * @version 1.0
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.appsnipp.education.R;
import com.appsnipp.education.activities.ChatActivity;
import com.appsnipp.education.model.CourseCard;
import com.appsnipp.education.ui.listeners.CoursesItemClickListener;

public class QuestionFragment extends Fragment
        implements CoursesItemClickListener {

    @Override
    public void onDashboardCourseClick(CourseCard courseCard, ImageView imageView) {

    }

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.question_and_answer, container, false);
        androidx.constraintlayout.widget.ConstraintLayout constraintLayout = thisView.findViewById(R.id.touch_and_change);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        startActivity(intent);
        return thisView;
    }
}
