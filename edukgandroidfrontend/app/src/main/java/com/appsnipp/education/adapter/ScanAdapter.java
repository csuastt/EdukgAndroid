package com.appsnipp.education.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.R;
import com.appsnipp.education.databinding.ItemCard3Binding;
import com.appsnipp.education.ui.listeners.CoursesItemClickListener;
import com.appsnipp.education.model.CourseCard;

import java.util.List;

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter._ViewHolder> {

    Context mContext;
    private List<CourseCard> mData;
    private CoursesItemClickListener coursesItemClickListener;

    public ScanAdapter(Context mContext, List<CourseCard> mData, CoursesItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.coursesItemClickListener = listener;
    }

    @NonNull
    @Override
    public ScanAdapter._ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater= LayoutInflater.from(viewGroup.getContext());
        ItemCard3Binding itemCard3Binding = ItemCard3Binding.inflate(layoutInflater,viewGroup,false);
        return new ScanAdapter._ViewHolder(itemCard3Binding);
    }

    public void onBindViewHolder(@NonNull final ScanAdapter._ViewHolder viewHolder, final int i) {

        final int pos = viewHolder.getAdapterPosition();
        //Set ViewTag
        viewHolder.itemView.setTag(pos);

        viewHolder.setPostImage(mData.get(i));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coursesItemClickListener.onDashboardCourseClick(mData.get(i), viewHolder.itemCard3Binding.cardViewImage);
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

        ItemCard3Binding itemCard3Binding;
        public _ViewHolder(@NonNull ItemCard3Binding cardBinding) {
            super(cardBinding.getRoot());
            this.itemCard3Binding = cardBinding;
        }

        void setPostImage(CourseCard courseCard){
            this.itemCard3Binding.cardViewImage.setImageResource(R.drawable.no_picture);
            this.itemCard3Binding.stagItemCourse.setText(courseCard.getCourseTitle());
            this.itemCard3Binding.stagItemQuantityCourse.setText(courseCard.getQuantityCourses());
        }

    }
}
