/*
 * Copyright (c) 2020. rogergcc
 */

package com.appsnipp.education.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.databinding.ItemShopCardBinding;
import com.appsnipp.education.ui.listeners.MatchCourseClickListener;
import com.appsnipp.education.model.MatchCourse;
import com.bumptech.glide.Glide;

import java.util.List;


public class MatchesCoursesAdapter extends RecyclerView.Adapter<MatchesCoursesAdapter.ViewHolder> {

    private List<MatchCourse> mData;
    private MatchCourseClickListener matchCourseClickListener;
    public MatchesCoursesAdapter(List<MatchCourse> mData, MatchCourseClickListener listener) {
        this.mData = mData;
        this.matchCourseClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemShopCardBinding itemCardBinding = ItemShopCardBinding.inflate(inflater,parent,false);
        return new MatchesCoursesAdapter.ViewHolder(itemCardBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.setBind(mData.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchCourseClickListener.onScrollPagerItemClick(mData.get(position), holder.itemCardBinding.image);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemShopCardBinding itemCardBinding;
        public ViewHolder(@NonNull ItemShopCardBinding cardBinding) {
            super(cardBinding.getRoot());
            this.itemCardBinding = cardBinding;

            //this.itemRecyclerMealBinding.
        }

        void setBind(MatchCourse matchCourse){

            itemCardBinding.tvTitulo.setText(matchCourse.getName());
            itemCardBinding.tvCantidadCursos.setText(matchCourse.getNumberOfCourses());

            Glide.with(itemView.getContext())
                    .load(matchCourse.getImageResource())
                    .into(itemCardBinding.image);
        }
    }

}
