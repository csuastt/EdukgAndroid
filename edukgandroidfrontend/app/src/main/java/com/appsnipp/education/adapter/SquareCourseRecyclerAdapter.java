/*
 * Copyright (c) 2021. rogergcc
 */

package com.appsnipp.education.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.R;
import com.appsnipp.education.databinding.ItemCard2Binding;
import com.appsnipp.education.model.CourseCard;
import com.appsnipp.education.ui.listeners.CoursesItemClickListener;

import java.util.List;

public class SquareCourseRecyclerAdapter extends RecyclerView.Adapter<SquareCourseRecyclerAdapter._ViewHolder> {

    Context mContext;
    private List<CourseCard> mData;
    private CoursesItemClickListener coursesItemClickListener;

    public SquareCourseRecyclerAdapter(Context mContext, List<CourseCard> mData, CoursesItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.coursesItemClickListener = listener;
    }

    @NonNull
    @Override
    public SquareCourseRecyclerAdapter._ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater= LayoutInflater.from(viewGroup.getContext());
        ItemCard2Binding itemCard2Binding = ItemCard2Binding.inflate(layoutInflater,viewGroup,false);
        return new _ViewHolder(itemCard2Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SquareCourseRecyclerAdapter._ViewHolder viewHolder, final int i) {

        final int pos = viewHolder.getAdapterPosition();
        //Set ViewTag
        viewHolder.itemView.setTag(pos);

        viewHolder.setPostImage(mData.get(i));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coursesItemClickListener.onDashboardCourseClick(mData.get(i), viewHolder.itemCard2Binding.cardViewImage);
            }
        });
    }

    public int getDimensionValuePixels(int dimension)
    {
        return (int) mContext.getResources().getDimension(dimension);
    }


    public int dpToPx(int dp)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    @Override
    public long getItemId(int position) {
        CourseCard courseCard = mData.get(position);
        return courseCard.getId();
    }
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class _ViewHolder extends RecyclerView.ViewHolder{

        ItemCard2Binding itemCard2Binding;
        public _ViewHolder(@NonNull ItemCard2Binding cardBinding) {
            super(cardBinding.getRoot());
            this.itemCard2Binding = cardBinding;
        }

        public void setPostImage(CourseCard courseCard){
            this.itemCard2Binding.cardViewImage.setImageResource(R.drawable.no_picture);
            this.itemCard2Binding.stagItemCourse.setText(courseCard.getCourseTitle());
            this.itemCard2Binding.stagItemQuantityCourse.setText(courseCard.getQuantityCourses());
        }

    }


}
