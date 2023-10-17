/*
 * Copyright (c) 2021. rogergcc
 */
/***
 * This is the practice fragment which contains the tagview and the subpractice fragment.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.ui.menupractice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.adapter.PracticeFragmentAdapter;
import com.appsnipp.education.ui.questionhint.QuestionHintFragment;
import com.appsnipp.education.ui.tagcore.TagCoreFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class PracticeFragment extends Fragment {


    public PracticeFragment() {
        // Required empty public constructor

    }

    private TabLayout tabLayout;
    private List<Fragment> mLayoutFragmentList;
    private Fragment child1Fragment, child2Fragment;

    // ugly code
    private int[] orderStateIdTitle = {0, 1, 2};
    private ImageView searchImageView;
    private ImageView listImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_practice, container, false);
        QuestionHintFragment questionHintFragment = new QuestionHintFragment();
        TagCoreFragment tagCoreFragment = new TagCoreFragment();
        searchImageView = thisView.findViewById(R.id.search_image);
        searchImageView.setOnClickListener(new View.OnClickListener() {      //  点击跳转时
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                //  先是得到Fragment所在的 Activity，然后在得到管理器对象，
                // 获取并开始事务对象，在进行切换，然后在利用addToBackStack()，最终提交事务
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_host_fragment, questionHintFragment).commitAllowingStateLoss();
            }
        });
        listImageView = thisView.findViewById(R.id.list_image);
        listImageView.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_host_fragment, tagCoreFragment).commitAllowingStateLoss();
            }
        });

        tabLayout = thisView.findViewById(R.id.tab_layout);
        for(String course: AppSingleton.selectedCourse) {
            TabLayout.Tab tab1 = tabLayout.newTab().setText(course);
            tabLayout.addTab(tab1);
        }
        ViewPager2 vpOrderContainer = thisView.findViewById(R.id.vp_order_container);
        PracticeFragmentAdapter myAdapter=new PracticeFragmentAdapter(getActivity());
        //给ViewPager2设置适配器
        vpOrderContainer.setAdapter(myAdapter);
        //ViewPager2设置预加载 传入大于1的值来设置预加载数量,默认不预加载
        vpOrderContainer.setOffscreenPageLimit(1);

        new TabLayoutMediator(tabLayout, vpOrderContainer, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                // tab:当前处于选中状态的Tab对象
                // position:当前Tab所处的位置
                int newposition = position;
                if(position > AppSingleton.selectedCourse.size()) {
                    newposition = newposition % AppSingleton.selectedCourse.size();
                }
                tab.setText(AppSingleton.selectedCourse.get(newposition));
                AppSingleton.nowCourse = AppSingleton.selectedCourse.get(position);
            }
        }).attach();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AppSingleton.nowCourse = tab.getText().toString();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                AppSingleton.nowCourse = tab.getText().toString();
            }
        });
        return thisView;
    }
    // Because the implementation of the listener so we must add this part of code

}