package com.appsnipp.education.ui.listeners;

/***
 * This is the listener for viewpager selecting event.
 * @author Shuning Zhang
 * @version 1.0
 */

import android.view.View;

public interface OnViewPagerListener {
    /*释放的监听*/
    void onPageRelease(boolean isNext, View view);
    /*选中的监听以及判断是否滑动到底部*/
    void onPageSelected(View view,boolean isBottom);
    /*布局完成的监听*/
    void onLayoutComplete();
}

