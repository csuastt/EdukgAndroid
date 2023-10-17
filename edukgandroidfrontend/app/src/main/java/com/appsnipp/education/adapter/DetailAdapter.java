/*
 * Copyright (c) 2021. rogergcc
 */
/***
 * This detail adapter class is an adapter suitable for the detail page recycler
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.databinding.ItemCardBinding;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.ui.listeners.DetailItemClickListener;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter._ViewHolder> {

    Context mContext;
    private List<Question> mData;
    private DetailItemClickListener questionItemClickListener;

    public DetailAdapter(Context mContext, List<Question> mData, DetailItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.questionItemClickListener = listener;
    }

    @NonNull
    @Override
    public DetailAdapter._ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater= LayoutInflater.from(viewGroup.getContext());
        ItemCardBinding itemCardBinding = ItemCardBinding.inflate(layoutInflater,viewGroup,false);
        return new _ViewHolder(itemCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final DetailAdapter._ViewHolder viewHolder, final int i) {

        final int pos = viewHolder.getAdapterPosition();
        //Set ViewTag
        viewHolder.itemView.setTag(pos);

        viewHolder.setPostImage(mData.get(i));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

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

        ItemCardBinding itemCardBinding;
        public _ViewHolder(@NonNull ItemCardBinding cardBinding) {
            super(cardBinding.getRoot());
            this.itemCardBinding = cardBinding;
        }

        public void setPostImage(Question question){
            this.itemCardBinding.stagItemCourse.setText(question.getName());
            try {
                this.itemCardBinding.stagItemQuantityCourse.setText(question.getText().substring(0, 15));
            } catch (Exception n){
                this.itemCardBinding.stagItemQuantityCourse.setText(question.getText());
            }
        }

    }


}
