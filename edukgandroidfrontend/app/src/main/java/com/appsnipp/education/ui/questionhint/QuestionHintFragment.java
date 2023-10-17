package com.appsnipp.education.ui.questionhint;

/***
 * This is the question hint fragment contained in the practice page
 * @author Shuning Zhang
 * @version 1.0
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.adapter.MyQuestionHintRecyclerViewAdapter;
import com.appsnipp.education.model.GenDebug;
import com.appsnipp.education.model.Knowledge;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.ui.detail.KnowledgeDetail;
import com.appsnipp.education.ui.menupractice.PracticeFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;


/**
 * A fragment representing a list of Items.
 */

public class QuestionHintFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private GenDebug questionDebug;
    private ArrayList<String> entityName = new ArrayList<>();
    private ArrayList<String> entityType = new ArrayList<>();
    private ArrayList<String> backList = new ArrayList<>();
    private ArrayList<Question> questionList = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QuestionHintFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static QuestionHintFragment newInstance(int columnCount) {
        QuestionHintFragment fragment = new QuestionHintFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        questionDebug = new GenDebug();

        //provide change of adapter
        RecyclerView newView = view.findViewById(R.id.search_list);

        // Set the adapter

        Context context = view.getContext();
        MyQuestionHintRecyclerViewAdapter newAdapter = new MyQuestionHintRecyclerViewAdapter(questionList);
        newView.setAdapter(newAdapter);

        ArrayList<String> chinese = new ArrayList<String>();
        chinese.add("语文");
        ArrayList<String> knowledgeGraph = new ArrayList<String>();
        Knowledge kn = new Knowledge("李白生平","语文知识点","语文概念", chinese, knowledgeGraph);
        newView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "what happened", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().add(
                        R.id.fragment_question_hint, new KnowledgeDetail(kn)
                ).commit();
            }
        });
        newAdapter.setOnItemClickListener(new MyQuestionHintRecyclerViewAdapter.OnItemClickListener(){
            @Override
            public void OnClicked(View view, int position) {
                Toast.makeText(getActivity(), "what happened", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().add(
                        R.id.fragment_question_hint, new KnowledgeDetail(kn)
                ).commit();
            }
        });

        RadioGroup radio = view.findViewById(R.id.sort_radio);
        radio.check(R.id.radio_incr);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_incr) {
                    questionList.sort(new Comparator<Question>() {
                        @Override
                        public int compare(Question o1, Question o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    MyQuestionHintRecyclerViewAdapter newAdapter = new MyQuestionHintRecyclerViewAdapter(questionList);
                    newView.setAdapter(newAdapter);
                } else {
                    questionList.sort(new Comparator<Question>() {
                        @Override
                        public int compare(Question o1, Question o2) {
                            return o2.getName().compareTo(o1.getName());
                        }
                    });
                    MyQuestionHintRecyclerViewAdapter newAdapter = new MyQuestionHintRecyclerViewAdapter(questionList);
                    newView.setAdapter(newAdapter);
                }
            }
        });

        SeekBar seek = view.findViewById(R.id.seek_filter);
        seek.setMax(20);
        seek.setLabelFor(R.id.seek_filter);
        seek.setProgress(8);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ArrayList<Question> newList = new ArrayList<>();
                for(Question q: questionList){
                    if(q.getName().length() < progress) {
                        newList.add(q);
                    }
                }
                questionList = newList;
                MyQuestionHintRecyclerViewAdapter newAdapter = new MyQuestionHintRecyclerViewAdapter(questionList);
                newView.setAdapter(newAdapter);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        AppSingleton.nowCourse = "chinese";
        TagContainerLayout tagGroup = view.findViewById(R.id.tag_history);
        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {

            @Override
            public void onTagClick(int position, String text) {
                String entityInfo = backList.get(position);
                String [] aa = entityInfo.split("080");
                String entity = aa[0];
                String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/infoByInstanceName";
                System.err.println(url);
                //空格转义
                entity = entity.replaceAll(" ","%20");
                url += "?";
                SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                        getActivity().MODE_PRIVATE);
                // change in order to prevent from Exception
                String id = preferences.getString("edukg_id", "");
                url += "id=";
                url += id;
                url += "&course=";
                url += AppSingleton.nowCourse;
                url += "&name=";
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
                                JsonObject data = jsonObject.get("data").getAsJsonObject();
                                String label = data.get("label").getAsString();
                                JsonArray property = data.get("property").getAsJsonArray();
                                String belonging = "";
                                for(JsonElement t: property) {
                                    belonging += "属性名称:" + t.getAsJsonObject().get("predicateLabel").getAsString() + " " +
                                            "属性值:" + t.getAsJsonObject().get("object") + "\n";
                                }
                                String feature = "";
                                JsonArray content = data.get("content").getAsJsonArray();
                                for(JsonElement t: content) {

                                }
                                // TODO 修改代码结构
                                (getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList<String> course = new ArrayList<>();
                                        course.add(AppSingleton.nowCourse);
                                        Knowledge kn = new Knowledge(label,"","",course,null);
                                    }
                                });
                                //处理UI需要切换到UI线程处理
                            }
                        }
                    }
                });
            }

            @Override
            public void onTagLongClick(int position, String text) {
                tagGroup.removeTag(position);
                String historyId = backList.get(position);
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
                            if(result.contains("Operation")) {
                                System.err.println("delete history succeed");
                            }
                            (getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    }
                });
                backList.remove(position);
                //TODO:del history
            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
        //[ADD HISTORIES]
        ArrayList<String> tags = new ArrayList<>();
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
                    try {
                        JsonArray jsonArray = new JsonParser().parse(result).getAsJsonArray();
                        System.err.println("DEBUG");
                        for(JsonElement t: jsonArray) {
                            String type = t.getAsJsonObject().get("type").getAsString();
                            String course = t.getAsJsonObject().get("course").getAsString();
                            String context = t.getAsJsonObject().get("context").getAsString();
                            String historyId = t.getAsJsonObject().get("history_id").getAsString();
                            System.err.println(AppSingleton.courseMap.get(AppSingleton.nowCourse));
                            System.err.println(course);
                            if ((type.equals("search") || type.equals("search_entity") || type.equals("entity"))) {
                                System.err.println("bingo...");
                                try {
                                    tags.add(context.substring(0, 6) + "..");
                                    backList.add(historyId);
                                } catch (Exception e) {

                                }
                            }
                        }
                    } catch ( Exception e){
                        System.err.println(e);
                    }
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.err.println(tags);
                            tagGroup.setTags(tags);
                        }
                    });

                }
            }
        });

        // TODO 通过 tagGroup 删除


        SearchView searchView = view.findViewById(R.id.search_view);
        //设置该SearchView默认是否自动缩小为图标
        searchView.setIconifiedByDefault(true);
        //设置该SearchView显示搜索按钮
        searchView.setSubmitButtonEnabled(true);

        searchView.setQueryHint("查找习题...");
        // TODO 回退和打叉
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_question_hint, new PracticeFragment()).commit();
                return false;
            }
        });
        // 设置字体颜色
        EditText searchEditText = (EditText) (searchView.findViewById(androidx.appcompat.R.id.search_src_text));;
        searchEditText.setTextColor(getResources().getColor(R.color.black));
        searchEditText.setHintTextColor(getResources().getColor(R.color.grey_dark));
        searchEditText.setPadding(AppSingleton.dip2px(getContext(),10),0,0,0);
        //为该SearchView组件设置事件监听器
        final boolean[] clicked = {false};
        searchView.setOnClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicked[0] == false) {
                    clicked[0] = true;
                }
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_host_fragment, new PracticeFragment()).commit();
                clicked[0] = false;
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //单机搜索按钮时激发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                OkHttpClient client = new OkHttpClient();
                String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/instanceList";
                System.err.println(url);
                System.err.println(query);
                System.err.println(AppSingleton.nowCourse);
                url += "?";
                SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                        getActivity().MODE_PRIVATE);
                // change in order to prevent from Exception
                String id = preferences.getString("edukg_id", "");
                url += "id=";
                url += id;
                url += "&searchKey=";
                url += query;
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
                            JsonArray answerArray = jsonObject.get("data").getAsJsonArray();

                            ArrayList<String> newCourseList = new ArrayList<>();
                            newCourseList.add(AppSingleton.nowCourse);
                            for(JsonElement t: answerArray) {
                                String entity = t.getAsJsonObject().get("label").getAsString();
                                String entity_type = t.getAsJsonObject().get("category").getAsString();
                                // maybe some time in the future uri would be needed
                                String uri = t.getAsJsonObject().get("uri").getAsString();
                                questionList.add(new Question(entity, newCourseList, entity_type, "", "", null));
                            }
                            (getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try_add_search(questionList);
                                    MyQuestionHintRecyclerViewAdapter newAdapter = new MyQuestionHintRecyclerViewAdapter(questionList);
                                    newView.setAdapter(newAdapter);
                                }
                            });
                            //处理UI需要切换到UI线程处理
                        }
                    }
                });
                return false;
            }

            //用户输入字符时激发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(), "你输入了字符" + newText,
                 //       Toast.LENGTH_SHORT).show();
                //如果newText不是长度为0的字符串
                if (TextUtils.isEmpty(newText)) {
                    //清除ListView的过滤
                    // TODO
                } else {
                    String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/linkInstance";
                    System.err.println(url);
                    // build request
                    StringRequest strRequest = new StringRequest
                            (Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // success
                                    try {
                                        System.err.println(response);
                                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                                        JsonArray jsonData = jsonObject.get("data").getAsJsonArray();
                                        for(JsonElement t :jsonData) {
                                            entityName.add(t.getAsJsonObject().get("label").getAsString());
                                            entityType.add(t.getAsJsonObject().get("category").getAsString());
                                        }
                                        Toast.makeText(getActivity(), "获取信息成功", Toast.LENGTH_SHORT).show();
                                        Log.w("Warn","Success");
                                    } catch (Exception e){
                                        System.err.println(e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    System.err.println("Fetch Info Error!");
                                    Log.w("warn",error.toString());
                                    String body;
                                    try {
                                        try {
                                            body = new String(error.networkResponse.data, "UTF-8");
                                        } catch(NullPointerException e) {
                                            Log.w("Warn","Null Pointer Exception");
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        // exception
                                        body = "";
                                    }
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams()
                        {
                            SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                                    getActivity().MODE_PRIVATE);
                            String id = preferences.getString("edukg_id", "");
                            Log.w("ID", id);
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("id", id);
                            params.put("context", newText);
                            params.put("course", AppSingleton.nowCourse);
                            return params;
                        }
                    };
                    AppSingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
                    //使用用户输入的内容对ListView的列表项进行过滤
                    newAdapter.setFilterText(newText);
                    newView.setAdapter(newAdapter);

                }
                return true;
            }
        });

        return view;
    }
    public void updateRecyclerView(ArrayList<Question> questionList, RecyclerView newView) {
        MyQuestionHintRecyclerViewAdapter newAdapter = new MyQuestionHintRecyclerViewAdapter(questionList);
        newView.setAdapter(newAdapter);
    }
    public void try_add_search(ArrayList<Question> questionList) {
        try {
            if(questionList.size() > 2) {
                try_sending(questionList.get(1), 0);
                try_sending(questionList.get(0), 0);
            } else if(questionList.size() > 1){
                try_sending(questionList.get(0), 0);
            }
        } catch (Exception e) {

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
                    .add("type","search")
                    .add("id",id)
                    .add("course",AppSingleton.nowCourse)
                    .add("context", t.getName() + "080" + t.getFeature() + "080" +
                            t.getText() + "080" + t.getAnswer())
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