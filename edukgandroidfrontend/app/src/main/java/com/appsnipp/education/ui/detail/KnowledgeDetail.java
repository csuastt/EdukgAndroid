package com.appsnipp.education.ui.detail;

/***
 * This knowledge detail class shows the content of the entity knowledge details
 * @author Shuning Zhang
 * @version 1.0
 */

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.adapter.DetailAdapter;
import com.appsnipp.education.model.Knowledge;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.ui.listeners.DetailItemClickListener;
import com.appsnipp.education.ui.listeners.QuestionItemClickListener;
import com.appsnipp.education.adapter.QuestionRecyclerAdapter;
import com.appsnipp.education.ui.menuscan.ScanFragment;
import com.appsnipp.education.util.TransChinese2English;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.xqhs.graphs.graph.Node;
import net.xqhs.graphs.graph.SimpleNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.gujun.android.taggroup.TagGroup;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

public class KnowledgeDetail extends Fragment implements QuestionItemClickListener, DetailItemClickListener {

    private KnowledgeDetailViewModel mViewModel;
    private Knowledge knowledge;
    private Map<String, String> mapping;
    private Map<String, String> inMapping;
    private Map<String, String> outMapping;
    private ArrayList<Question> questionFinal = new ArrayList<>();

    public KnowledgeDetail() {}
    public KnowledgeDetail(Knowledge _knowledge) {
        newInstance();
        knowledge = _knowledge;
    }
    public KnowledgeDetail(Knowledge _knowledge, Map<String, String> _mapping,
                           Map<String, String> _in_mapping, Map<String, String> _out_mapping) {
        newInstance();
        knowledge = _knowledge;
        mapping = _mapping;
        inMapping = _in_mapping;
        outMapping = _out_mapping;
    }
    public static KnowledgeDetail newInstance() {
        return new KnowledgeDetail();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.knowledge_detail_fragment, container, false);
        // init knowledge default setting : in the future will use connection
        ArrayList<String> knowledgeTag = new ArrayList<String>();
        knowledgeTag.add("数学");
        TagGroup mTagGroup = (TagGroup) view.findViewById(R.id.tag_group);
        TextView mFeatureText = (TextView) view.findViewById(R.id.texture_knowledge_text);
        TextView mBelongingText = (TextView) view.findViewById(R.id.class_knowledge_text);
        TextView mDrawableContent = (TextView) view.findViewById(R.id.knowledge_first);
        mDrawableContent.setText(mDrawableContent.getText() + " " + knowledge.getName());
        mTagGroup.setTags(knowledge.getLabel());
        mFeatureText.setText(knowledge.getFeature());
        mBelongingText.setText(knowledge.getBelonging());
//        RelativeLayout knowledgeBack = view.findViewById(R.id.knowledge_toolbar_back);
        ImageView knowledgeImage = view.findViewById(R.id.icon_knowledge_toolbar_back);

        final String[] answer = new String[1];
        final String[] notAnswer = new String[1];

        ImageView favorKnowledge = view.findViewById(R.id.knowledge_favorite);

        // 用户名和密码
        SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                getActivity().MODE_PRIVATE);
        String username = preferences.getString("username", null);
        String password = preferences.getString("password", null);

        //[ADD TO BACKEND]
        // post请求把浏览记录发给后端
        try_sending(0);
        ImageView favor = view.findViewById(R.id.knowledge_favorite);

        final boolean[] isChecked = {false};
        favor.setImageResource(R.drawable.cancel_favor);
        // 通过post请求获取收藏，然后删除搜藏
        String url = AppSingleton.URL_PREFIX +  "/api/favorites";
        System.err.println(url);
        preferences = getActivity().getSharedPreferences ("userInfo",
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
                    if(result != null) {
                        try {
                            JsonArray jsonArray = new JsonParser().parse(result).getAsJsonArray();
                            for( JsonElement t: jsonArray) {
                                if(t.getAsJsonObject().get("context").getAsString().equals(knowledge.getName())) {
                                    isChecked[0] = true;
                                    favor.setImageResource(R.drawable.add_favor);
                                    break;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            }
        });

        final boolean[] notYet = {false};
        favorKnowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isChecked[0]) {
                    isChecked[0] = true;
                    favor.setImageResource(R.drawable.add_favor);
                    favorKnowledge.setImageResource(R.drawable.add_favor);
                    if(notYet[0] == false) {
                        notYet[0] = true;
                        final String[] add_id = {""};
                        // 通过post请求获取收藏，然后删除搜藏
                        String url = AppSingleton.URL_PREFIX +  "/api/favorites";
                        System.err.println(url);
                        // build request
                        StringRequest strRequest = new StringRequest
                                (com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // success
                                        System.err.println(response);
                                        try {
                                            JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
                                            for(JsonElement ansObject: jsonArray){
                                                String favorites_id = ansObject.getAsJsonObject().get("favorites_id").getAsString();
                                                String course = ansObject.getAsJsonObject().get("course").getAsString();
                                                String context = ansObject.getAsJsonObject().get("context").getAsString();
                                                if(context.equals(knowledge.getName())) {
                                                    add_id[0] = context;
                                                    break;
                                                }
                                            }
                                            if(add_id[0].equals(knowledge.getName())) {
                                                Toast.makeText(getActivity(), "不能重复添加", Toast.LENGTH_SHORT).show();
                                            } else {
                                                callback_add(knowledge.getName());
                                            }
                                        } catch (Exception e){
                                            System.err.println(e);
                                        }
                                        notYet[0] = false;
                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // error
                                        System.err.println(error.toString());
                                        String body;
                                        try {
                                            try {
                                                body = new String(error.networkResponse.data, "UTF-8");
                                                System.err.println(body);
                                            } catch(NullPointerException e) {
                                                Log.w("Warn","Null Pointer Exception");
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            // exception
                                            body = "";
                                        }
                                        notYet[0] = false;
                                    }
                                }){
                            @Override
                            protected Map<String, String> getParams() {
                                SharedPreferences preferences = getActivity().getSharedPreferences("userInfo",
                                        getActivity().MODE_PRIVATE);
                                // must change otherwise raise Exception
                                String id = preferences.getString("id", "");
                                Log.w("EDUKG_DEBUG", id);
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id", id);
                                return params;
                            }

                        };
                        AppSingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
                    }
                } else {
                    isChecked[0] = false;
                    favorKnowledge.setImageResource(R.drawable.cancel_favor);
                    if(notYet[0] == false) {
                        notYet[0] = true;
                        final String[] del_id = {""};
                        // 通过post请求获取收藏，然后删除搜藏
                        String url = AppSingleton.URL_PREFIX +  "/api/favorites";
                        System.err.println(url);
                        // build request
                        StringRequest strRequest = new StringRequest
                                (com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // success
                                        System.err.println(response);
                                        try {
                                            JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
                                            for(JsonElement ansObject: jsonArray){
                                                String favorites_id = ansObject.getAsJsonObject().get("favorites_id").getAsString();
                                                String course = ansObject.getAsJsonObject().get("course").getAsString();
                                                String context = ansObject.getAsJsonObject().get("context").getAsString();
                                                if(context.equals(knowledge.getName())) {
                                                    del_id[0] = favorites_id;
                                                    break;
                                                }
                                            }
                                            callback_del(del_id[0]);
                                        } catch (Exception e){
                                            System.err.println(e);
                                        }
                                        notYet[0] = false;
                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // error
                                        Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                                        System.err.println(error.toString());
                                        String body;
                                        try {
                                            try {
                                                body = new String(error.networkResponse.data, "UTF-8");
                                                System.err.println(body);
                                            } catch(NullPointerException e) {
                                                Log.w("Warn","Null Pointer Exception");
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            // exception
                                            body = "";
                                        }
                                        notYet[0] = false;
                                    }
                                }){
                            @Override
                            protected Map<String, String> getParams() {
                                SharedPreferences preferences = getActivity().getSharedPreferences("userInfo",
                                        getActivity().MODE_PRIVATE);
                                // must change otherwise raise Exception
                                String id = preferences.getString("id", "");
                                Log.w("EDUKG_DEBUG", id);
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id", id);
                                return params;
                            }

                        };
                        AppSingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
                    }
                }
            }
        });

        RecyclerView questionView = view.findViewById(R.id.final_question);
        QuestionRecyclerAdapter adapter = new QuestionRecyclerAdapter(getActivity(), questionFinal, this);
        questionView.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(
                        1,
                        StaggeredGridLayoutManager.VERTICAL);
        questionView.setLayoutManager(
                layoutManager
        );
        QuestionItemClickListener _that = this;
        url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/questionListByUriName";
        //空格转义
        url += "?";
        preferences = getActivity().getSharedPreferences ("userInfo",
                getActivity().MODE_PRIVATE);
        // change in order to prevent from Exception
        id = preferences.getString("edukg_id", "");
        url += "id=";
        url += id;
        url += "&uriName=";
        url += knowledge.getName();
        client = new OkHttpClient();
        request = new okhttp3.Request.Builder().url(url).build();
        call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
                        JsonArray jsonArray = new JsonParser().parse(result).getAsJsonObject().get("data").getAsJsonArray();
                        ArrayList<String> courseName = new ArrayList<>();
                        courseName.add(AppSingleton.nowCourse);
                        for(JsonElement e: jsonArray ) {
                            questionFinal.add(new Question(
                                    knowledge.getName(),
                                    courseName,
                                    knowledge.getFeature(),
                                    e.getAsJsonObject().get("qBody").getAsString(),
                                    e.getAsJsonObject().get("qAnswer").getAsString(),
                                    null
                                    ));
                        }
                        (getActivity()).runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.P)
                            @Override
                            public void run() {
                                RecyclerView questionView = view.findViewById(R.id.final_question);
                                QuestionRecyclerAdapter adapter = new QuestionRecyclerAdapter(getActivity(), questionFinal, _that);
                                questionView.setAdapter(adapter);
                                StaggeredGridLayoutManager layoutManager =
                                        new StaggeredGridLayoutManager(
                                                1,
                                                StaggeredGridLayoutManager.VERTICAL);
                                questionView.setLayoutManager(
                                        layoutManager
                                );

                            }
                        });
                        //处理UI需要切换到UI线程处理
                    }
                }
            }
        });
        ArrayList<Question> questionList = new ArrayList<>();
        ArrayList<String> nowCourse = new ArrayList<>();
        nowCourse.add(AppSingleton.nowCourse);
        Node mEntity =  new SimpleNode(knowledge.getName());
        questionList.add(new Question(
                knowledge.getName(),
                nowCourse,
                knowledge.getLabel().get(0),
                "",
                "",
                null
                ));
        int cnt1 = 0;
        int cnt2 = 0;
        int cnt3 = 0;

        for (Map.Entry<String, String> t : inMapping.entrySet()) {
            try {
                if (!t.getKey().contains("tsinghua") && !t.getKey().contains("www")) {
                    cnt1 += 1;
                    questionList.add(new Question(
                            t.getKey(),
                            nowCourse,
                            t.getValue(),
                            "",
                            "",
                            null
                    ));
                }
            } catch (Exception e) {}
            if (cnt1 >= 3) break;
        }
        for (Map.Entry<String, String> t : outMapping.entrySet()) {
            Log.w("what the fuck", t.getKey());
            if (cnt2 >= 3) break;
            try {
                if (!t.getKey().contains("tsinghua") && !t.getKey().contains("www")) {
                    cnt2 += 1;
                    questionList.add(new Question(
                            t.getKey(),
                            nowCourse,
                            t.getValue(),
                            "",
                            "",
                            null
                    ));
                }
            } catch (Exception e) {}
        }
        for (Map.Entry<String, String> t : mapping.entrySet()) {
            Log.w("what the fuck", t.getKey());
            if (cnt3 >= 3) break;
            try {
                if (!t.getKey().contains("tsinghua") && !t.getKey().contains("www")) {
                    cnt3 += 1;
                    questionList.add(new Question(
                            t.getKey(),
                            nowCourse,
                            t.getValue(),
                            "",
                            "",
                            null
                    ));
                }
            } catch (Exception e) {}
        }
        knowledgeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mTagGroup.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
            @Override
            public void onAppend(TagGroup tagGroup, String tag) {

            }

            @Override
            public void onDelete(TagGroup tagGroup, String tag) {

            }
        });
        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {

            }
        });

        mFeatureText.setText("暂无属性");
        ImageView backDetail = view.findViewById(R.id.icon_knowledge_toolbar_back);
        backDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.knowledge_detail, new ScanFragment()).commit();
            }
        });

        RecyclerView newView = view.findViewById(R.id.final_knowledge);
        DetailAdapter newadapter = new DetailAdapter(getActivity(), questionList, this);
        newView.setAdapter(newadapter);

        StaggeredGridLayoutManager llayoutManager =
                new StaggeredGridLayoutManager(
                        1,
                        StaggeredGridLayoutManager.VERTICAL);

        newView.setLayoutManager(
                llayoutManager
        );
        newView.setClipToPadding(false);
        newView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(KnowledgeDetailViewModel.class);

        // TODO: Use the ViewModel
    }
    public void callback_add(String add_id) {
        // 通过post请求添加收藏
        String url = AppSingleton.URL_PREFIX +  "/api/add_favorites";
        System.err.println(url);
        // build request
        StringRequest strRequest = new StringRequest
                (com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // success
                        try {
                            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                            if(jsonObject.get("code").getAsString().contains("Operation Succeed.")) {
                                Toast.makeText(getActivity(), "已收藏",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "收藏失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            System.err.println(e);
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.err.println(error.toString());
                        String body;
                        try {
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                System.err.println(body);
                            } catch(NullPointerException e) {
                                Log.w("Warn","Null Pointer Exception");
                            }
                        } catch (UnsupportedEncodingException e) {
                            // exception
                            body = "";
                        }
                        Toast.makeText(getActivity(), "收藏失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                        getActivity().MODE_PRIVATE);
                String id = preferences.getString("id", "");
                Map<String, String> params = new HashMap<String, String>();
                TransChinese2English t = new TransChinese2English();
                Log.w("EDUKG_DEBUG", t.trans(knowledge.getLabel().get(0)));
                Log.w("EDUKG_DEBUG", add_id);
                Log.w("EDUKG_DEBUG", id);
                params.put("course", t.trans(knowledge.getLabel().get(0)));
                params.put("context", add_id);
                params.put("id",id);
                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        AppSingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
    }
    public void try_sending(int cnt){
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
                        .add("type","entity")
                        .add("id",id)
                        .add("course",AppSingleton.nowCourse)
                        .add("context", knowledge.getName())
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
                        try_sending(cnt + 1);
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
        } catch (Exception e) {

        }
    }

    public void callback_del(String del_id) {
        if(del_id.equals("")) {
            Toast.makeText(getActivity(),"不存在这条记录",Toast.LENGTH_SHORT).show();
        } else {
            // Access the RequestQueue through your singleton class.
            String url = AppSingleton.URL_PREFIX +  "/api/del_favorites";
            System.err.println(url);
            // build request
            StringRequest strRequest = new StringRequest
                    (com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // success
                            try {
                                JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                                if(jsonObject.get("code").getAsString().contains("Operation")) {
                                    Toast.makeText(getActivity(), "已取消收藏",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "取消收藏失败", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "取消收藏失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            String body;
                            try {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    System.err.println(body);
                                } catch(NullPointerException e) {
                                    Log.w("Warn","Null Pointer Exception");
                                }
                            } catch (UnsupportedEncodingException e) {
                                // exception
                                body = "";
                            }
                            Toast.makeText(getActivity(), "取消收藏失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams()
                {
                    SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                            getActivity().MODE_PRIVATE);
                    // must change otherwise raise Exception
                    String id = preferences.getString("id", "");
                    Log.w("ID",id);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id",id);
                    params.put("favorites_id", del_id);
                    return params;
                }
            };
            // Access the RequestQueue through your singleton class.
            AppSingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
        }
    }

    @Override
    public void onDashboardCourseClick(Question question, ImageView imageView) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.knowledge_detail, new QuestionDetail(question)).commit();
    }

    @Override
    public void onDashboardCourseLongClick(Question question, ImageView imageView) {

    }

    @Override
    public void onDashboardDetailClick(Question question, ImageView imageView) {

    }

    @Override
    public void onDashboardDetailLongClick(Question question, ImageView imageView) {

    }
}