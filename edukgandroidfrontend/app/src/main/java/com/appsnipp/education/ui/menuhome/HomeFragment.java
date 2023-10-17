/*
 * Copyright (c) 2021. rogergcc
 */
 /***
 * This is the home fragment page in the frontend.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.ui.menuhome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.adapter.QuestionRecyclerAdapter;
import com.appsnipp.education.model.Knowledge;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.ui.detail.QuestionDetail;
import com.appsnipp.education.ui.helpers.GridSpacingItemDecoration;
import com.appsnipp.education.ui.listeners.QuestionItemClickListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

import static android.widget.LinearLayout.VERTICAL;


public class HomeFragment extends Fragment
        implements  QuestionItemClickListener {

    private Context mcontext;
    private ArrayList<Question> courseCards;
    private ArrayList<Question> staticCourseCards;
    private QuestionRecyclerAdapter adapter;
    private EditText edtSearch;
    private ArrayList<Question> question = new ArrayList<>();
    private ArrayList<String> historyIdList = new ArrayList<>();
    private ArrayList<Question> questionList = new ArrayList<>();
    private int searched = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        courseCards = new ArrayList<>();
        staticCourseCards = new ArrayList<>();
        ArrayList<String> newString = new ArrayList<>();
        newString.add("math");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initSpinner(view);
        mcontext = this.getContext();
        edtSearch = view.findViewById(R.id.edt_search);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    performSearch();
                    Toast.makeText(mcontext,
                            "Edt Searching Click: " + edtSearch.getText().toString().trim(),
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(
                        2,
                        StaggeredGridLayoutManager.VERTICAL);

        androidx.recyclerview.widget.RecyclerView rvCourses = view.findViewById(R.id.rv_courses);
        rvCourses.setLayoutManager(
                layoutManager
        );
        rvCourses.setClipToPadding(false);
        rvCourses.setHasFixedSize(true);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.horizontal_card);
        rvCourses.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true, 0));

        Activity _that_activity = getActivity();
        question.add(new Question("问题", newString,"1111111","这是一个问题这是问题的题目","这是问题的答案",null));
        ArrayList<String> entities = new ArrayList<>();
        for(Question t: courseCards) {
            staticCourseCards.add(t);
        }
        QuestionItemClickListener _that = this;

        //获取历史记录
        String url = AppSingleton.URL_PREFIX +  "/api/history";
        System.err.println(url);
        SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                getActivity().MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("id", "");
        // build request
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("id",id)
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
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                System.err.println(response.toString());
                if(response.isSuccessful()){
                    String result = response.body().string();
                    System.err.println(result);
                    JsonArray jsonArray = new JsonParser().parse(result).getAsJsonArray();
                    for(JsonElement e: jsonArray) {
                        if(e.getAsJsonObject().get("type").equals("search_question")) {
                            historyIdList.add(e.getAsJsonObject().get("history_id").getAsString());
                            ArrayList<String> nowCourseList = new ArrayList<>();
                            nowCourseList.add(AppSingleton.nowCourse);
                            // text answer feature name qId
                            String[] info = e.getAsJsonObject().get("context").getAsString().split("777");
                            try {
                                questionList.add(new Question(info[3], nowCourseList, info[2], info[0], info[1], null,
                                        questionList.size() + 1,
                                        Integer.valueOf(info[4])));
                            } catch (Exception n) {
                            }
                        }
                    }
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //处理UI需要切换到UI线程处理
                            adapter = new QuestionRecyclerAdapter(mcontext, questionList, _that);
                            rvCourses.setAdapter(adapter);
                        }
                    });
                }
            }
        });

        adapter = new QuestionRecyclerAdapter(mcontext, questionList, this);
        rvCourses.setAdapter(adapter);
        edtSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searched = 1;
                courseCards.clear();
                //策略：先获取一遍实体链接，然后根据实体链接获取问题列表
                OkHttpClient client = new OkHttpClient();
                String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/instanceList";
                System.err.println(url);
                System.err.println(s.toString());
                System.err.println(AppSingleton.nowCourse);
                url += "?";
                SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                        getActivity().MODE_PRIVATE);
                // change in order to prevent from Exception
                String id = preferences.getString("edukg_id", "");
                url += "id=";
                url += id;
                url += "&searchKey=";
                url += s.toString();
                url += "&course=";
                url += AppSingleton.nowCourse;
                okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
                Call call = client.newCall(request);
                call.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.err.println(e);
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        if(response.isSuccessful()){
                            String result = response.body().string();
                            System.err.println(result);
                            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                            int code = jsonObject.get("code").getAsInt();
                            if(code == -1 || code == 9) {

                            } else if(code == 0) {
                                JsonArray answerArray = jsonObject.get("data").getAsJsonArray();

                                ArrayList<String> newCourseList = new ArrayList<>();
                                newCourseList.add(AppSingleton.nowCourse);
                                int cnt = 0;
                                for(JsonElement t: answerArray) {
                                    if(cnt >= 5) {break;}
                                    else {cnt++;}
                                    String entity = t.getAsJsonObject().get("label").getAsString();
                                    String entity_type = t.getAsJsonObject().get("category").getAsString();
                                    // maybe some time in the future uri would be needed
                                    String uri = t.getAsJsonObject().get("uri").getAsString();
                                    entities.add(entity);
                                }
                                (getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getQuestion(entities, rvCourses, _that);
                                    }
                                });
                                //处理UI需要切换到UI线程处理
                            }
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void performSearch() {
        edtSearch.clearFocus();
        InputMethodManager in = (InputMethodManager) mcontext.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
    }

    public void initSpinner(View view) {
        //声明一个下拉列表的数组适配器
        @SuppressLint("ResourceType")
        ArrayAdapter<String> starAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_select, AppSingleton.courseArray);
        //设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框
        Spinner sp = view.findViewById(R.id.spinner_course);
        //设置下拉框的标题，不设置就没有难看的标题了
        sp.setPrompt("请选择科目");
        //设置下拉框的数组适配器
        sp.setAdapter(starAdapter);
        sp.setFadingEdgeLength(3);
        sp.canScrollVertically(VERTICAL);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(sp);
            // 设置下视视图的高度
            popupWindow.setHeight(50);
        }
        catch (Exception e) {
        }

        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    @Override
    public void onDashboardCourseClick(Question question, ImageView imageView) {
        ArrayList<String> english = new ArrayList<String>();
        System.err.println("Clicked");
        english.add(AppSingleton.nowCourse);
        ArrayList<Knowledge> knowledgeBase = new ArrayList<Knowledge>();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new QuestionDetail(question)).commit();
    }

    @Override
    public void onDashboardCourseLongClick(Question question, ImageView imageView) {
        if(searched == 0) {
            for(int i = 0; i < questionList.size(); ++i) {
                if(questionList.get(i).getName().equals(question.getName())) {
                    String historyId = historyIdList.get(i);
                    //获取历史记录
                    String url = AppSingleton.URL_PREFIX +  "/api/del_history";
                    System.err.println(url);
                    SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                            getActivity().MODE_PRIVATE);
                    // change in order to prevent from Exception
                    String id = preferences.getString("id", "");
                    // build request
                    OkHttpClient client = new OkHttpClient();
                    FormBody body = new FormBody.Builder()
                            .add("id",id)
                            .add("history_id", historyId)
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
            }
        }
    }


    class MySelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            AppSingleton.nowCourse = AppSingleton.courseMap.get(AppSingleton.courseArray[i]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    public void getQuestion(ArrayList<String> entities, RecyclerView rvCourses, QuestionItemClickListener _that) {
        ArrayList<String> _entities = new ArrayList<String>();
        for(String entity: entities) {
            _entities.add(entity);
        }
        if(_entities.size() > 0) {
            for(String entity: _entities) {
                String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/questionListByUriName";
                //空格转义
                entity = entity.replaceAll(" ","%20");
                System.err.println(url);
                System.err.println(entity);
                url += "?";
                SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                        getActivity().MODE_PRIVATE);
                // change in order to prevent from Exception
                String id = preferences.getString("edukg_id", "");
                url += "id=";
                url += id;
                url += "&uriName=";
                url += entity;
                OkHttpClient client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
                Call call = client.newCall(request);
                String finalEntity = entity;
                call.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.err.println(e);
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        if(response.isSuccessful()){
                            String result = response.body().string();
                            System.err.println(result);
                            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                            int code = jsonObject.get("code").getAsInt();
                            if(code == -1 || code == 9) {

                            } else if(code == 0) {
                                JsonArray answerArray = jsonObject.get("data").getAsJsonArray();
                                ArrayList<String> newCourseList = new ArrayList<>();
                                newCourseList.add(AppSingleton.nowCourse);
                                for(JsonElement t: answerArray) {
                                    String qBody = t.getAsJsonObject().get("qBody").getAsString();
                                    String qAnswer = t.getAsJsonObject().get("qAnswer").getAsString();
                                    // maybe some time in the future uri would be needed
                                    int id = t.getAsJsonObject().get("id").getAsInt();
                                    courseCards.add(new Question(finalEntity, newCourseList, "", qBody,
                                            qAnswer, null, courseCards.size() + 1,id));
                                }
                                (getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try_add_question(courseCards);
                                        adapter = new QuestionRecyclerAdapter(mcontext, courseCards, _that);
                                        rvCourses.setAdapter(adapter);
                                    }
                                });
                                //处理UI需要切换到UI线程处理
                            }
                        }
                    }
                });
            }
        }
    }
    public void try_add_question(ArrayList<Question> courseCards) {
        try {
            if(courseCards.size() > 2) {
                try_sending(courseCards.get(1), 0);
                try_sending(courseCards.get(0), 0);
            } else if(courseCards.size() > 1) {
                try_sending(courseCards.get(0), 0);
            }
        } catch(Exception e) {

        }
    }
    public void try_sending(Question t, int cnt) {
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
                    .add("type","search_question")
                    .add("id",id)
                    .add("course",AppSingleton.nowCourse)
                    .add("context", t.getText() + "777" + t.getAnswer() + "777" + t.getFeature() + "777" + t.getName() + "777" + t.getqId())
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
                    try_sending(t, cnt + 1);
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
    }
}