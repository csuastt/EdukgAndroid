package com.appsnipp.education.adapter;

/*
 * Copyright (c) 2021. rogergcc
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.databinding.ItemCardBigProgramBinding;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.ui.listeners.QuestionItemClickListener;

import java.util.List;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter._ViewHolder> {

    Context mContext;
    private List<Question> mData;
    private QuestionItemClickListener questionItemClickListener;
    private ProgressBar progress;

    public ProgramAdapter(Context mContext, ProgressBar progressExercise, List<Question> mData, QuestionItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.progress = progressExercise;
        this.questionItemClickListener = listener;
    }

    @NonNull
    @Override
    public ProgramAdapter._ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater= LayoutInflater.from(viewGroup.getContext());
        ItemCardBigProgramBinding itemCardBinding = ItemCardBigProgramBinding.inflate(layoutInflater,viewGroup,false);
        return new ProgramAdapter._ViewHolder(itemCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProgramAdapter._ViewHolder viewHolder, final int i) {

        final int pos = viewHolder.getAdapterPosition();
        //Set ViewTag
        viewHolder.itemView.setTag(pos);
        if(progress != null) {
            progress.setProgress(i);
        }
        viewHolder.setPostImage(mData.get(i), i, mData.size());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //questionItemClickListener.onDashboardCourseClick(mData.get(i), viewHolder.itemCardBinding.cardViewImage);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //questionItemClickListener.onDashboardCourseLongClick(mData.get(i), viewHolder.itemCardBinding.cardViewImage);
                return false;
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

        ItemCardBigProgramBinding itemCardBinding;
        final long startTime;
        public _ViewHolder(@NonNull ItemCardBigProgramBinding cardBinding) {
            super(cardBinding.getRoot());
            this.itemCardBinding = cardBinding;
            this.startTime = System.currentTimeMillis();
        }

        public void setPostImage(Question question, int now, int total){
            System.err.println(question.getName());
            System.err.println(now);
            System.err.println(total);

            // TODO implement the real knowledge card
        }

    }
}

