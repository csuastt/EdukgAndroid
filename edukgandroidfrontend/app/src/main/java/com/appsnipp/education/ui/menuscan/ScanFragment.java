/*
 * Copyright (c) 2021. rogergcc
 */
 /***
 * This is the scan fragment.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.ui.menuscan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.adapter.ScanAdapter;
import com.appsnipp.education.model.CourseCard;
import com.appsnipp.education.model.Knowledge;
import com.appsnipp.education.model.MatchCourse;
import com.appsnipp.education.model.MyMatchesCourses;
import com.appsnipp.education.ui.detail.KnowledgeDetail;
import com.appsnipp.education.ui.helpers.HorizontalMarginItemDecoration;
import com.appsnipp.education.ui.listeners.CoursesItemClickListener;
import com.appsnipp.education.ui.listeners.MatchCourseClickListener;
import com.appsnipp.education.util.MyUtilsApp;
import com.appsnipp.education.util.RecycleDecoration;
import com.appsnipp.education.widget.ACache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static java.lang.Math.abs;


public class ScanFragment extends Fragment
        implements
//        DiscreteScrollView.OnItemChangedListener<MatchesCoursesAdapter.ViewHolder>
        MatchCourseClickListener, View.OnClickListener, CoursesItemClickListener, RecyclerView.OnItemTouchListener {

    private ArrayList<CourseCard> courseCards;
    private ArrayList<CourseCard> staticCourseCards;
    private static final String TAG = "ScanFragment";
    Context mcontext;
    private MyMatchesCourses myMatchesCourses;
    private ImageView searchImage;
    private ViewPager2 viewPager;
    private List<LinearLayout> data;

    public ScanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        RecyclerView recycleKnowledge = view.findViewById(R.id.knowledge_recycle);
        mcontext = this.getContext();
        courseCards = new ArrayList<>();
        staticCourseCards = new ArrayList<>();


        //也许应该有个历史检测之类的

        ScanAdapter knowledgeAdapter = new ScanAdapter(mcontext, courseCards, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mcontext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycleKnowledge.setLayoutManager(layoutManager);
        recycleKnowledge.setAdapter(knowledgeAdapter);
        recycleKnowledge.addItemDecoration(new RecycleDecoration(40));
        recycleKnowledge.addOnItemTouchListener(this);
        recycleKnowledge.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean is_changed = false;
            long time_tag = -501;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                long now_tag = System.currentTimeMillis();
                // 100ms的最小相应间隔，防止抖动
                if (now_tag - time_tag < 100) {
                    return;
                }
                int val = recyclerView.computeVerticalScrollOffset();
                if (val > 0) {
                    if (!is_changed) {
                        TextView view1 = view.findViewById(R.id.tv_find_title);
                        view1.setTextSize(20);
                        EditText view2 = view.findViewById(R.id.et_word);
                        view2.getLayoutParams().height = AppSingleton.dip2px(getContext(), 70);
                        RelativeLayout layout = view.findViewById(R.id.tv_word);
                        layout.setVisibility(View.GONE);
                        is_changed = true;
                        LinearLayout layout1 = view.findViewById(R.id.scan_layout_vertical);
                        layout1.setBackground(getResources().getDrawable(R.drawable.scan_edge));
                        time_tag = now_tag;
                    }
                } else {
                    if (is_changed) {
                        TextView view1 = view.findViewById(R.id.tv_find_title);
                        view1.setTextSize(28);
                        EditText view2 = view.findViewById(R.id.et_word);
                        view2.getLayoutParams().height = AppSingleton.dip2px(getContext(), 150);
                        RelativeLayout layout = view.findViewById(R.id.tv_word);
                        layout.setVisibility(View.VISIBLE);
                        is_changed = false;
                        LinearLayout layout1 = view.findViewById(R.id.scan_layout_vertical);
                        layout1.setBackground(getResources().getDrawable(R.color.white));
                        time_tag = now_tag;
                    }
                }
            }
        });
        TextView mTvWordCount = (TextView) view.findViewById(R.id.tv_word_count);
        EditText mEtWord = (EditText) view.findViewById(R.id.et_word);
        CoursesItemClickListener _that = this;
        final boolean[] backForce = {false};
        mEtWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(backForce[0] == true) {
                    backForce[0] = false;
                } else {
                    if(s.length() > 200) s = s.subSequence(0, 200);
                    mTvWordCount.setText(String.valueOf(s.toString().length()));
                    courseCards.clear();
                    for(int i = 0; i < staticCourseCards.size(); ++i) {
                        String tmpCourseCard = staticCourseCards.get(i).getQuantityCourses() + staticCourseCards.get(i).getCourseTitle();
                        String sPost = s.toString();
                        if(tmpCourseCard.contains(s) || sPost.contains(tmpCourseCard)) {
                            courseCards.add(staticCourseCards.get(i));
                        }
                    }
                    ArrayList<Integer> startList = new ArrayList<>();
                    ArrayList<Integer> endList = new ArrayList<>();
                    String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/linkInstance";
                    System.err.println(url);
                    SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                            getActivity().MODE_PRIVATE);
                    String id = preferences.getString("edukg_id", "");
                    OkHttpClient client = new OkHttpClient();
                    System.err.println(id);
                    System.err.println(s.toString());
                    System.err.println(AppSingleton.nowCourse);
                    FormBody body = new FormBody.Builder()
                            .add("context", s.toString())
                            .add("id", id)
                            .add("course", AppSingleton.nowCourse)
                            .build();
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(url)
                            .post(body)
                            .build();
                    okhttp3.Call call = client.newCall(request);
                    CharSequence finalS = s;
                    call.enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            System.err.println("Failed");
                        }

                        @Override
                        public void onResponse(Call call, okhttp3.Response response) throws IOException {
                            System.err.println(response.toString());
                            if(response.isSuccessful()){
                                try {
                                    String result = response.body().string();
                                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                                    int code = jsonObject.get("code").getAsInt();
                                    if(code == 0) {
                                        JsonArray jsonResult = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
                                        for(JsonElement t: jsonResult) {
                                            String entityType = t.getAsJsonObject().get("entity_type").getAsString();
                                            String entity = t.getAsJsonObject().get("entity").getAsString();
                                            String entityUrl = t.getAsJsonObject().get("entity_url").getAsString();
                                            int startIndex = t.getAsJsonObject().get("start_index").getAsInt();
                                            int endIndex = t.getAsJsonObject().get("end_index").getAsInt();
                                            startList.add(startIndex);
                                            endList.add(endIndex);
                                            courseCards.add(new CourseCard(courseCards.size() + 1, R.drawable.course_design_coding,
                                                    entity, entityType));
                                        }
                                        (getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try_send_scanning(courseCards);
                                                ScanAdapter knowledgeAdapter = new ScanAdapter(mcontext, courseCards, _that);
                                                //文本内容
                                                SpannableString ss = new SpannableString(finalS.toString());
                                                int maxLen = startList.size();
                                                try {
                                                    for (int i = 0; i < maxLen; ++i) {
                                                        //设置字符颜色
                                                        // ss.setSpan(new ForegroundColorSpan(Color.CYAN), startList.get(i), endList.get(i) + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                        //粗体
                                                        ss.setSpan(new StyleSpan(Typeface.BOLD), startList.get(i), endList.get(i) + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    }
                                                } catch ( Exception e ) {

                                                }
                                                mEtWord.setText(ss);
                                                backForce[0] = true;
                                                recycleKnowledge.setAdapter(knowledgeAdapter);
                                            }
                                        });
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    });
                    ScanAdapter knowledgeAdapter = new ScanAdapter(mcontext, courseCards, _that);
                    recycleKnowledge.setAdapter(knowledgeAdapter);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        int currentItem = 1;
        return view;
    }


    private void setupViewpager(ViewPager2 myViewPager, int currentItem, List<MatchCourse> matchCourseList) {
        CourseTopicsViewPager courseTopicsViewPager = new CourseTopicsViewPager(matchCourseList, mcontext, this);
        myViewPager.setAdapter(courseTopicsViewPager);
        // set selected item
        myViewPager.setCurrentItem(currentItem);
        // You need to retain one page on each side so that the next and previous items are visible
        myViewPager.setOffscreenPageLimit(2);
        // Add a PageTransformer that translates the next and previous items horizontally
        // towards the center of the screen, which makes them visible
        int nextItemVisiblePx = (int) getResources().getDimension(R.dimen.viewpager_next_item_visible);
        int currentItemHorizontalMarginPx = (int) getResources().getDimension(R.dimen.viewpager_current_item_horizontal_margin);
        int pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;
        ViewPager2.PageTransformer pageTransformer = (page, position) -> {
            page.setTranslationX(-pageTranslationX * position);
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.setScaleY(1 - (0.15f * abs(position)));
            // If you want a fading effect uncomment the next line:
            // page.alpha = 0.25f + (1 - abs(position))
        };
        myViewPager.setPageTransformer(pageTransformer);
        myViewPager.addItemDecoration(new HorizontalMarginItemDecoration(
                mcontext, R.dimen.viewpager_current_item_horizontal_margin_testing,
                R.dimen.viewpager_next_item_visible_testing)
        );
        myViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                MyUtilsApp.showLog(TAG, String.format(Locale.ENGLISH, "%d/%d", position + 1, matchCourseList.size()));
            }
        });
    }

    @Override
    public void onScrollPagerItemClick(MatchCourse courseCard, ImageView imageView) {
        MyUtilsApp.showLog(TAG, "LogD onScrollPagerItemClick : " + courseCard.toString());
        MyUtilsApp.showToast(mcontext, courseCard.getName());
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v){
        getChildFragmentManager().
                beginTransaction().replace(R.id.scan_fragment_frame,new KnowledgeDetail(),null).
                addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void onDashboardCourseClick(CourseCard courseCard, ImageView imageView) {
        //hard coding
        ArrayList<String> courseLabel = new ArrayList<>();
        courseLabel.add(AppSingleton.nowCourse);
        Knowledge kn = new Knowledge(courseCard.getCourseTitle(),courseCard.getQuantityCourses(),courseCard.getQuantityCourses(), courseLabel, null);
        String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/infoByInstanceName";
        //空格转义
        Map<String, String> mapping = new HashMap<>();
        Map<String, String> out_mapping = new HashMap<>();
        Map<String, String> in_mapping = new HashMap<>();
        String entity = kn.getName().replaceAll(" ","%20");
        url += "?";
        SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                getActivity().MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("edukg_id", "");
        url += "id=";
        url += id;
        url += "&name=";
        url += entity;
        url += "&course=";
        url += AppSingleton.nowCourse;
        OkHttpClient client = new OkHttpClient();
        Request request = new okhttp3.Request.Builder().url(url).build();
        Call call = client.newCall(request);
        String finalEntity = entity;
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                JSONArray jsonArray = AppSingleton.acache.getAsJSONArray(entity + "related");
                System.err.println("loading..." + entity + "related");
                System.err.println(jsonArray.length());
                for(int i=0; i < jsonArray.length(); i++){
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String keyValue = object.toString();
                        System.err.println(keyValue);
                        String[] pre = keyValue.split(":");
                        System.err.println(pre[0]);
                        System.err.println(pre[1]);
                        String key = pre[0].substring(2, pre[0].length() - 1);
                        System.err.println(key);
                        String[] keyLabel = key.split("\\|");
                        String[] valueLabel = pre[1].substring(1, pre[1].length() - 2).split("\\|");
                        System.err.println(keyLabel[1]);
                        System.err.println(valueLabel[1]);
                        if (pre[1].contains("predicateLabel")) {
                            mapping.put(keyLabel[1], valueLabel[1]);
                        } else if (pre[1].contains("predicate_label")) {
                            if (key.contains("object_label")) {
                                in_mapping.put(keyLabel[1], valueLabel[1]);
                            } else if (key.contains("subject_label")) {
                                out_mapping.put(keyLabel[1], valueLabel[1]);
                            }
                        }
                    } catch(Exception n) {
                        System.err.println(n);
                    }
                }

                (getActivity()).runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void run() {
                        System.err.println(mapping.size());
                        System.err.println(in_mapping.size());
                        System.err.println(out_mapping.size());
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.scan_fragment_frame, new KnowledgeDetail(kn, mapping, in_mapping, out_mapping)).commit();
                    }
                });
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if(response.isSuccessful()){
                    String result = response.body().string();
                    System.err.println(result);
                    String finalText = "";
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    int code = jsonObject.get("code").getAsInt();
                    if(code == -1 || code == 9) {

                    } else if(code == 0) {
                        JSONArray savedJsonArray = new JSONArray();
                        System.err.println("Saving..." + entity + "related");
                        boolean flag = false;
                        JsonObject answerObject = jsonObject.get("data").getAsJsonObject();
                        JsonArray property = answerObject.get("property").getAsJsonArray();
                        JsonArray content = answerObject.get("content").getAsJsonArray();
                        for(JsonElement t: property) {
                            try {
                                mapping.put(t.getAsJsonObject().get("object").getAsString().split("#")[0],
                                        t.getAsJsonObject().get("predicateLabel").getAsString());
                                JSONObject savedJsonObject = new JSONObject();
                                savedJsonObject.put("object|" + t.getAsJsonObject().get("object").getAsString().split("#")[0],
                                        "predicateLabel|" + t.getAsJsonObject().get("predicateLabel").getAsString());
                                savedJsonArray.put(savedJsonObject);
                            } catch( Exception e){
                                mapping.put(t.getAsJsonObject().get("object").getAsString(),
                                        t.getAsJsonObject().get("predicateLabel").getAsString());
                                JSONObject savedJsonObject = new JSONObject();
                                try {
                                    savedJsonObject.put("object|" + t.getAsJsonObject().get("object").getAsString(),
                                            "predicateLabel|" + t.getAsJsonObject().get("predicateLabel").getAsString());
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                savedJsonArray.put(savedJsonObject);
                            }
                        }
                        for(JsonElement t: content) {
                            try {
                                in_mapping.put(t.getAsJsonObject().get("object_label").getAsString(),
                                        t.getAsJsonObject().get("predicate_label").getAsString());
                                JSONObject savedJsonObject = new JSONObject();
                                try {
                                    savedJsonObject.put("object_label|" + t.getAsJsonObject().get("object_label").getAsString(),
                                            "predicate_label|" + t.getAsJsonObject().get("predicate_label").getAsString());
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                savedJsonArray.put(savedJsonObject);
                            } catch (Exception e){
                                try {
                                    out_mapping.put(t.getAsJsonObject().get("subject_label").getAsString(),
                                            t.getAsJsonObject().get("predicate_label").getAsString());
                                    JSONObject savedJsonObject = new JSONObject();
                                    try {
                                        savedJsonObject.put("subject_label|" + t.getAsJsonObject().get("subject_label").getAsString(),
                                                "predicate_label|" + t.getAsJsonObject().get("predicate_label").getAsString());
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                    savedJsonArray.put(savedJsonObject);
                                } catch(Exception ee) {

                                }
                            }
                        }
                        AppSingleton.acache.put(entity + "related", savedJsonArray, 2 * ACache.TIME_HOUR);
                        (getActivity()).runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.P)
                            @Override
                            public void run() {
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.scan_fragment_frame, new KnowledgeDetail(kn, mapping, in_mapping, out_mapping)).commit();
                            }
                        });
                        //处理UI需要切换到UI线程处理
                    }
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
    //still there is some problem maybe because the type does not fit well
    public void try_send_scanning(ArrayList<CourseCard> courseCards) {
        try {
            if(courseCards.size() > 2) {
                try_sending(courseCards.get(1), 0);
                try_sending(courseCards.get(0), 0);
            } else if(courseCards.size() > 1){
                try_sending(courseCards.get(0), 0);
            }
        } catch (Exception e) {

        }
    }
    public void try_sending(CourseCard courseCard, int cnt) {
        try {
            if(cnt <= 5) {
                String url = AppSingleton.URL_PREFIX +  "/api/add_history";
                System.err.println(url);
                SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                        getActivity().MODE_PRIVATE);
                // change in order to prevent from Exception
                String id = preferences.getString("id", "");
                // build request
                OkHttpClient client = new OkHttpClient();
                FormBody body = new FormBody.Builder()
                        .add("type","search_entity")
                        .add("id",id)
                        .add("course",AppSingleton.nowCourse)
                        .add("context", courseCard.getCourseTitle())
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                okhttp3.Call call = client.newCall(request);
                call.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.err.println("Failed");
                        try_sending(courseCard, cnt + 1);
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        System.err.println(response.toString());
                        if(response.isSuccessful()){
                            String result = response.body().string();
                            System.err.println(result);
                        }
                    }
                });
            }
        } catch (Exception e){

        }

    }
}