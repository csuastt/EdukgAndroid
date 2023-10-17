package com.appsnipp.education.util;

/***
 * This is a file which is designed to decorate the recycler with spaces (to some extent like the GridSpaceItemDecoration but not the same) 
 * @author Shuning Zhang
 * @version 1.0
 */

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecycleDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public RecycleDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
}
