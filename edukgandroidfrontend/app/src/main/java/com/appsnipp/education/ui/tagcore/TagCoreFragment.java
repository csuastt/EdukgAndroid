package com.appsnipp.education.ui.tagcore;

/***
 * This is the tag core fragment 
 * @author Shuning Zhang
 * @version 1.0
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.ui.menupractice.PracticeFragment;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagCoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagCoreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<String> tagGroupString = new ArrayList<>();
    private ArrayList<String> noTagGroupString = new ArrayList<>();

    public TagCoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagCoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagCoreFragment newInstance(String param1, String param2) {
        TagCoreFragment fragment = new TagCoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_core, container, false);
        ImageView iconTagToolbar = view.findViewById(R.id.icon_tag_toolbar_back);
        iconTagToolbar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_tag_core, new PracticeFragment()).commit();
            }
        });
        TagContainerLayout tagGroup = view.findViewById(R.id.tag_group);
        TagContainerLayout noTagGroup = view.findViewById(R.id.not_tag_group);

        for(String course: AppSingleton.selectedCourse) {
            tagGroupString.add(course);
        }
        for(String course: AppSingleton.notSelected) {
            noTagGroupString.add(course);
        }
        String[] _tagGroupString = new String[tagGroupString.size()];
        for(int i = 0; i < tagGroupString.size(); ++i) {
            _tagGroupString[i] = tagGroupString.get(i);
        }
        String[] _noTagGroupString = new String[noTagGroupString.size()];
        for(int i = 0; i < noTagGroupString.size(); ++i) {
            _noTagGroupString[i] = noTagGroupString.get(i);
        }
        tagGroup.setTags(_tagGroupString);
        noTagGroup.setTags(_noTagGroupString);

        TagContainerLayout _tagGroup = tagGroup;
        TagContainerLayout _noTagGroup = noTagGroup;

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                // ...
            }

            @Override
            public void onTagLongClick(final int position, String text) {
                for(int i = 0; i < tagGroupString.size(); ++i) {
                    if(text.equals(tagGroupString.get(i))) {
                        tagGroupString.remove(i);
                    }
                }
                _tagGroup.removeAllTags();
                String[] _tagGroupString = new String[tagGroupString.size()];
                for(int i = 0; i < tagGroupString.size(); ++i) {
                    _tagGroupString[i] = tagGroupString.get(i);
                }
                _tagGroup.setTags(_tagGroupString);
                noTagGroupString.add(text);
                noTagGroup.removeAllTags();
                String[] _noTagGroupString = new String[noTagGroupString.size()];
                for(int i = 0; i < noTagGroupString.size(); ++i) {
                    _noTagGroupString[i] = noTagGroupString.get(i);
                }
                noTagGroup.setTags(_noTagGroupString);
                AppSingleton.selectedCourse.clear();
                AppSingleton.notSelected.clear();
                for(String course: tagGroupString) {
                    AppSingleton.selectedCourse.add(course);
                }
                for(String course: noTagGroupString) {
                    AppSingleton.notSelected.add(course);
                }
            }

            @Override
            public void onSelectedTagDrag(int position, String text){
                // ...
            }

            @Override
            public void onTagCrossClick(int position) {
                // ...
            }
        });
        noTagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                // ...
            }

            @Override
            public void onTagLongClick(final int position, String text) {
                for(int i = 0; i < noTagGroupString.size(); ++i) {
                    if(text.equals(noTagGroupString.get(i))) {
                        noTagGroupString.remove(i);
                    }
                }
                _noTagGroup.removeAllTags();
                String[] _noTagGroupString = new String[noTagGroupString.size()];
                for(int i = 0; i < noTagGroupString.size(); ++i) {
                    _noTagGroupString[i] = noTagGroupString.get(i);
                }
                _noTagGroup.setTags(_noTagGroupString);
                tagGroupString.add(text);
                tagGroup.removeAllTags();
                String[] _tagGroupString = new String[tagGroupString.size()];
                for(int i = 0; i < tagGroupString.size(); ++i) {
                    _tagGroupString[i] = tagGroupString.get(i);
                }
                tagGroup.setTags(_tagGroupString);
                AppSingleton.selectedCourse.clear();
                AppSingleton.notSelected.clear();
                for(String course: tagGroupString) {
                    AppSingleton.selectedCourse.add(course);
                }
                for(String course: noTagGroupString) {
                    AppSingleton.notSelected.add(course);
                }

            }

            @Override
            public void onSelectedTagDrag(int position, String text){
                // ...
            }

            @Override
            public void onTagCrossClick(int position) {
                // ...
            }
        });

        return view;
    }
}