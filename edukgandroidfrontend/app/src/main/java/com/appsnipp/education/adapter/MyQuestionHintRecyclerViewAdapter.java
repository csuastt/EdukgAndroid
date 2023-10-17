package com.appsnipp.education.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.R;
import com.appsnipp.education.courseitem.CourseItemContent;
import com.appsnipp.education.model.Question;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CourseItemContent}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyQuestionHintRecyclerViewAdapter extends RecyclerView.Adapter<MyQuestionHintRecyclerViewAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener {
        void OnClicked(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    private List<Question> mValues;
    private final List<Question> staticValues;

    public MyQuestionHintRecyclerViewAdapter(List<Question> items) {
        mValues = items;
        staticValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        final ViewHolder holder = new ViewHolder(view,mOnItemClickListener);
        return holder;
    }

    public void setFilterText(String newText) {
        mValues.clear();
        for(Question t:staticValues) {
            mValues.add(t);
        }
        int mSize = mValues.size();
        int []intArray = new int[mSize];
        for(int index = 0; index < mValues.size(); index++) {
            if(!mValues.get(index).getContent().contains(newText)) {
                mValues.remove(index);
            }
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //random access some keys, not the final version
        holder.mItem = mValues.get(position);
        Integer posi = position;
        String _position = posi.toString();
        holder.mIdView.setText(_position + ".   " + mValues.get(position).getName());
        holder.mContentView.setText(mValues.get(position).getFeature());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mIdView;
        public TextView mContentView;
        public Question mItem;

        public ViewHolder(View view, final OnItemClickListener onClickListener) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        int position = getAdapterPosition();
                        //确保position值有效
                        if (position != RecyclerView.NO_POSITION) {
                            onClickListener.OnClicked(view, position);
                        }
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}