package com.appsnipp.education.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.appsnipp.education.ui.menupractice.SubPracticeFragment;

//自定义适配器  ViewPager2的适配器继承FragmentStateAdapter
public class PracticeFragmentAdapter extends FragmentStateAdapter {

    //存放Fragment
    private Fragment[] fragments;
    private static String[] title = {"语文", "数学", "英语"};
    private int[] orderStateIdTitle = {0, 1, 2};
    //构造器
    public PracticeFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments=new Fragment[title.length];
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //Fragment懒加载
        if (fragments[position]==null){
            //传递参数到OderListFragment
            Bundle bundle=new Bundle();//相当于多个键值对集合
            bundle.putInt("orderStateId",orderStateIdTitle[position]);

            SubPracticeFragment oderListFragment=new SubPracticeFragment();
            oderListFragment.setArguments(bundle);
            fragments[position]=oderListFragment;
        }
        //返回需要显示的Fragment
        return fragments[position];
    }

    //返回有多少个页面
    @Override
    public int getItemCount() {
        return fragments.length;
    }
}

