/***
 * This Activity is used for doing exercise by the user which is a append function of our system.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.adapter.QuestionAdapter;
import com.appsnipp.education.ui.listeners.QuestionItemClickListener;
import com.appsnipp.education.widget.Counter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

public class ExerciseActivity extends AppCompatActivity implements QuestionItemClickListener {

    private Activity _that;
    private Question question = new Question();
    private ArrayList<Question> questionList = new ArrayList<>();
    private Counter counter = new Counter();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        _that = this;

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(
                        1,
                        StaggeredGridLayoutManager.VERTICAL);

        androidx.recyclerview.widget.RecyclerView rvCourses = findViewById(R.id.exercise_recycler);
        rvCourses.setLayoutManager(
                layoutManager
        );
        rvCourses.setClipToPadding(false);
        rvCourses.setHasFixedSize(true);
        ProgressBar progressExercise = findViewById(R.id.progress_exercise);
        QuestionAdapter adapter = new QuestionAdapter(this, progressExercise, counter, questionList, this);
        rvCourses.setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvCourses);
        TextView timeCount = findViewById(R.id.time_count);
        CountDownTimer countDownTimer = new CountDownTimer(1000 * 10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.err.println("正在计时");
                timeCount.setText(
                        "答题时间：" +
                                String.format("%02d:%02d", (1000 * 10000 - millisUntilFinished) / 1000 / 60,
                                        (1000 * 10000 - millisUntilFinished / 1000) % 60)
                );
            }
            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
        rvCourses.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                        View view = snapHelper.findSnapView(layoutManager);
                        //TODO 销毁所有视频
                        //RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                        //if (viewHolder != null && viewHolder instanceof ) {
                        //    //TODO  启动想要播放的视频
                        //}
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING://拖动
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动
                        break;
                }

            }
        });

        String url = AppSingleton.URL_PREFIX +  "/api/history";
        System.err.println(url);
        SharedPreferences preferences = getSharedPreferences ("userInfo",
                MODE_PRIVATE);
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
                        for(JsonElement e: jsonArray) {
                            String course = e.getAsJsonObject().get("course").getAsString();
                            String context = e.getAsJsonObject().get("context").getAsString();
                            search(context, course, progressExercise);
                            System.err.println(course);
                            System.err.println(context);
                            try {
                                String r = context.split("080")[1];
                                String entity = context.split("080")[0];
                                search(entity, course, progressExercise);
                            } catch (Exception n) {
                                String entity = context;
                                search(entity, course, progressExercise);
                            }
                        }
                    } catch (Exception e) {
                        Log.w("EDUKG", e.toString());
                    }
                }
            }
        });

    }

    public void search(String entity, String course, ProgressBar progressExercise) {
        OkHttpClient client = new OkHttpClient();
        String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/questionListByUriName";
        System.err.println(url);
        url += "?";
        SharedPreferences preferences = getSharedPreferences ("userInfo",
                MODE_PRIVATE);
        String id = preferences.getString("edukg_id", "");
        url += "id=";
        url += id;
        url += "&uriName=";
        url += entity;
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
                    ArrayList<String> nowCourse = new ArrayList<>();
                    nowCourse.add(course);
                    try {
                        if (jsonObject.get("code").getAsInt() == 0) {
                            JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
                            for(JsonElement e: jsonArray) {
                                questionList.add(new Question(
                                        entity,
                                        nowCourse,
                                        course,
                                        e.getAsJsonObject().get("qBody").getAsString(),
                                        e.getAsJsonObject().get("qAnswer").getAsString(),
                                        null,
                                        questionList.size() + 1,
                                        e.getAsJsonObject().get("id").getAsInt()
                                ));
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StaggeredGridLayoutManager layoutManager =
                                        new StaggeredGridLayoutManager(
                                                1,
                                                StaggeredGridLayoutManager.VERTICAL);

                                androidx.recyclerview.widget.RecyclerView rvCourses = findViewById(R.id.exercise_recycler);
                                rvCourses.setLayoutManager(
                                        layoutManager
                                );
                                rvCourses.setClipToPadding(false);
                                rvCourses.setHasFixedSize(true);
                                QuestionAdapter adapter = new QuestionAdapter(_that, progressExercise, counter, questionList, (QuestionItemClickListener) _that);
                                rvCourses.setAdapter(adapter);
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
    @Override
    public void onDashboardCourseClick(Question question, ImageView imageView) {

    }

    @Override
    public void onDashboardCourseLongClick(Question question, ImageView imageView) {

    }

    public void try_sending(int cnt) {
        String url = AppSingleton.URL_PREFIX +  "/api/add_history";
        System.err.println(url);
        SharedPreferences preferences = getSharedPreferences ("userInfo",
                MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("id", "");
        if(cnt <= 5) {
            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("type","question")
                    .add("id",id)
                    .add("course",AppSingleton.nowCourse)
                    .add("context", question.getText())
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
    }
    public void send_try(boolean is_true) {
        String url = AppSingleton.URL_PREFIX + "/api/add_question";
        System.err.println(url);
        SharedPreferences preferences = _that.getSharedPreferences ("userInfo",
                _that.MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("id", "");
        System.err.println(id);
        System.err.println(question.getName());
        System.err.println(AppSingleton.nowCourse);
        // build request
        OkHttpClient client = new OkHttpClient();
        String is_correct = "no";
        if(is_true == true) {
            is_correct = "yes";
        }
        FormBody body = new FormBody.Builder()
                .add("id",id)
                .add("entity", question.getName())
                .add("course", AppSingleton.nowCourse)
                .add("correctness", is_correct)
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
                    System.err.println("succeed");
                }
            }
        });
    }
}