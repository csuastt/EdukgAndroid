package com.appsnipp.education.adapter;

/*
 * Copyright (c) 2021. rogergcc
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.R;
import com.appsnipp.education.databinding.ItemCardBigBinding;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.ui.listeners.QuestionItemClickListener;
import com.appsnipp.education.widget.Counter;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter._ViewHolder> {

    Context mContext;
    private List<Question> mData;
    private QuestionItemClickListener questionItemClickListener;
    private ProgressBar progress;
    private Counter totalCnt;

    public QuestionAdapter(Context mContext, ProgressBar progressExercise, Counter mTotalCnt,  List<Question> mData, QuestionItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.progress = progressExercise;
        this.questionItemClickListener = listener;
        this.totalCnt = mTotalCnt;
    }

    @NonNull
    @Override
    public QuestionAdapter._ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater= LayoutInflater.from(viewGroup.getContext());
        ItemCardBigBinding itemCardBinding = ItemCardBigBinding.inflate(layoutInflater,viewGroup,false);
        return new QuestionAdapter._ViewHolder(itemCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuestionAdapter._ViewHolder viewHolder, final int i) {

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

    public class _ViewHolder extends RecyclerView.ViewHolder{

        ItemCardBigBinding itemCardBinding;
        final long startTime;
        public _ViewHolder(@NonNull ItemCardBigBinding cardBinding) {
            super(cardBinding.getRoot());
            this.itemCardBinding = cardBinding;
            this.startTime = System.currentTimeMillis();
        }

        public void setPostImage(Question question, int now, int total){
            this.itemCardBinding.exerciseTitle.setText("问题：" + question.getName());
            this.itemCardBinding.exerciseFinishText.setText("完成数：" +
                    ((Integer)(now)).toString() + "/" + ((Integer)(total)).toString());
            this.itemCardBinding.exerciseRightText.setText("正确数：" +
                    ((Integer)(totalCnt.get())).toString());
            String rawText = question.getText();
            String [] A = rawText.split("A");
            this.itemCardBinding.exerciseText.setText(A[0]);
            String [] B = A[1].split("B");
            this.itemCardBinding.exerciseA.setText(B[0]);
            String [] C = B[1].split("C");
            this.itemCardBinding.exerciseB.setText(C[0]);
            String [] D = C[1].split("D");
            this.itemCardBinding.exerciseC.setText(D[0]);
            this.itemCardBinding.exerciseD.setText(D[1]);
            _ViewHolder _that = this;
            this.itemCardBinding.exerciseAans.setText("滑动查看答案");
            this.itemCardBinding.switchAnswer.setChecked(false);
            this.itemCardBinding.switchAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked == true) {
                        _that.itemCardBinding.exerciseAans.setText(question.getAnswer());
                        if((question.getAnswer().contains("A") && _that.itemCardBinding.exerciseAnswer.getCheckedRadioButtonId() == R.id.exerciseA) ||
                                (question.getAnswer().contains("B") && _that.itemCardBinding.exerciseAnswer.getCheckedRadioButtonId() == R.id.exerciseB) ||
                                (question.getAnswer().contains("C") && _that.itemCardBinding.exerciseAnswer.getCheckedRadioButtonId() == R.id.exerciseC) ||
                                (question.getAnswer().contains("D") && _that.itemCardBinding.exerciseAnswer.getCheckedRadioButtonId() == R.id.exerciseD)
                        ) {
                            totalCnt.increase();
                       }
                    } else {
                        _that.itemCardBinding.exerciseAans.setText("滑动查看答案");
                    }
                }
            });
        }

    }


}

