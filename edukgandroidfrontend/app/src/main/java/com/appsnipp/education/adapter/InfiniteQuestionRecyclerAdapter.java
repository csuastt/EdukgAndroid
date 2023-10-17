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
import com.appsnipp.education.databinding.ItemCardBinding;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.ui.listeners.QuestionItemClickListener;

import java.util.List;

public class InfiniteQuestionRecyclerAdapter extends RecyclerView.Adapter<InfiniteQuestionRecyclerAdapter._ViewHolder> {

    Context mContext;
    private List<Question> mData;
    private QuestionItemClickListener questionItemClickListener;

    public InfiniteQuestionRecyclerAdapter(Context mContext, List<Question> mData, QuestionItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.questionItemClickListener = listener;
    }

    @NonNull
    @Override
    public InfiniteQuestionRecyclerAdapter._ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater= LayoutInflater.from(viewGroup.getContext());
        ItemCardBinding itemCardBinding = ItemCardBinding.inflate(layoutInflater,viewGroup,false);
        return new _ViewHolder(itemCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final InfiniteQuestionRecyclerAdapter._ViewHolder viewHolder, final int i) {

        final int pos = viewHolder.getAdapterPosition();
        //Set ViewTag
        viewHolder.itemView.setTag(pos);

        viewHolder.setPostImage(mData.get(i));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionItemClickListener.onDashboardCourseClick(mData.get(i), viewHolder.itemCardBinding.cardViewImage);
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
        Question question = mData.get(position);
        return question.getId();
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

        ItemCardBinding itemCardBinding;
        public _ViewHolder(@NonNull ItemCardBinding cardBinding) {
            super(cardBinding.getRoot());
            this.itemCardBinding = cardBinding;
        }

        public void setPostImage(Question question){
            this.itemCardBinding.cardViewImage.setImageResource(R.drawable.arrow_right);
            this.itemCardBinding.stagItemCourse.setText(question.getName());
            this.itemCardBinding.stagItemQuantityCourse.setText(question.getText().substring(0, 15));
        }

    }


}
