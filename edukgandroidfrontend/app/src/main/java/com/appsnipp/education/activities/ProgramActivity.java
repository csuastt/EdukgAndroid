/***
 * This Activity is the main activity and the entrance of the whole application.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.adapter.ProgramAdapter;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.ui.listeners.QuestionItemClickListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yw.game.floatmenu.FloatItem;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ProgramActivity extends AppCompatActivity implements QuestionItemClickListener {

    private ArrayList<Question> questionList = new ArrayList<>();

    /***
     * This Function is used for the creation of the class ui of Program Activity.
     * @param savedInstanceState the bundle used for the creation of the ui
     * @return Nothing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);
        ImageView back = findViewById(R.id.icon_program_toolbar_back);
        Activity _that = this;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_that, MainActivity.class);
                startActivity(intent);
            }
        });
        ArrayList<FloatItem> itemList = new ArrayList<>();
        FloatItem newItemQuestion = new FloatItem(
                "知识问答", Color.BLACK, Color.WHITE,
                BitmapFactory.decodeResource(getResources(), R.drawable.person_question)
        );
        FloatItem newItemPractice = new FloatItem(
                "专项练习", Color.BLACK, Color.WHITE,
                BitmapFactory.decodeResource(getResources(), R.drawable.person_exercise)
        );
        FloatItem newItemFavorite = new FloatItem(
                "我的收藏", Color.BLACK, Color.WHITE,
                BitmapFactory.decodeResource(getResources(), R.drawable.person_favorites)
        );
        itemList.add(newItemFavorite);
        itemList.add(newItemPractice);
        itemList.add(newItemQuestion);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_left);
        ArrayList<String> entityList = new ArrayList<>();
        ArrayList<String> courseList = new ArrayList<>();
        ArrayList<String> scoreList = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences ("userInfo", MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("id", "");
        String url = AppSingleton.URL_PREFIX +  "/api/recommendation";
        System.err.println(url);
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("id",id)
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
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
                    courseList.clear();
                    scoreList.clear();
                    for(JsonElement t: jsonArray) {
                        String course = t.getAsJsonObject().get("course").getAsString();
                        String entity = t.getAsJsonObject().get("entity").getAsString();
                        courseList.add(course);
                        scoreList.add(entity);
                        searchEntity(course, entity, _that);
                    }
                    _that.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }
        });

        ArrayList<Question> questionList = new ArrayList<>();
        ArrayList<String> courseString = new ArrayList<>();
        courseString.add("语文");
        questionList.add(new Question(
                "李白",
                courseString,
                "语文",
                "李白是诗人",
                "李白的代表作是梦游天姥吟留别",
                null
        ));
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
        ProgramAdapter adapter = new ProgramAdapter(_that, null, questionList, (QuestionItemClickListener) _that);
        rvCourses.setAdapter(adapter);
    }

    /***
     * This Function is used for the callback of the button 'remember' pressed.
     * @param view the view of this page
     * @return Nothing
     */
    public void remember(View view) {
        String msg = "记住了下次就不会出现哦~";
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDashboardCourseClick(Question question, ImageView imageView) {

    }

    @Override
    public void onDashboardCourseLongClick(Question question, ImageView imageView) {

    }

    /***
     * This Function is used for the creation of the communication with backend EDUKG, sending a search request.
     * @param course the name of the course used for the backend searching
     * @param entity the name of the entity used for the backend searching
     * @param _that the activity used for backend searching
     * @return Nothing
     */
    public void searchEntity(String course, String entity, Activity _that) {
        SharedPreferences preferences = getSharedPreferences ("userInfo", MODE_PRIVATE);
        // change in order to prevent from Exception
        String url = AppSingleton.EDUKG_PREFIX + "/api/typeOpen/open/infoByInstanceName";
        String id = preferences.getString("edukg_id", "");
        entity = entity.replaceAll(" ", "%20");
        url += "id=";
        url += id;
        url += "&name=";
        url += entity;
        url += "&course=";
        url += course;
        OkHttpClient client = new OkHttpClient();
        Request request = new okhttp3.Request.Builder().url(url).build();
        Call call = client.newCall(request);
        String finalEntity = entity;
        String finalEntity1 = entity;
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
                    String finalText = "";
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    int code = jsonObject.get("code").getAsInt();
                    if(code == -1 || code == 9) {

                    } else if(code == 0) {
                        boolean flag = false;
                        JsonObject answerObject = jsonObject.get("data").getAsJsonObject();
                        JsonArray property = answerObject.get("property").getAsJsonArray();
                        JsonArray content = answerObject.get("content").getAsJsonArray();
                        String feature = "";
                        String text = "";
                        String answer = "";
                        ArrayList<String> array = new ArrayList<>();
                        array.add(course);
                        for(JsonElement t: property) {
                            try {
                                feature = t.getAsJsonObject().get("predicateLabel").getAsString() + " -> " +
                                t.getAsJsonObject().get("object").getAsString().split("#")[0];
                                break;
                            } catch( Exception e){
                                feature = t.getAsJsonObject().get("predicateLabel").getAsString() + " -> " +
                                        t.getAsJsonObject().get("object").getAsString();
                            }
                        }
                        for(JsonElement t: content) {
                            try {
                                text = t.getAsJsonObject().get("predicate_label").getAsString() + " -> " +
                                        t.getAsJsonObject().get("object_label").getAsString();
                            } catch (Exception e){
                                try {
                                    answer = t.getAsJsonObject().get("predicate_label").getAsString() + " -> "
                                            + t.getAsJsonObject().get("subject_label").getAsString();
                                } catch(Exception ee) {

                                }
                            }
                        }
                        questionList.add(new Question(
                                finalEntity1,
                                array,
                                "语文",
                                "李白是诗人",
                                "李白的代表作是梦游天姥吟留别",
                                null
                        ));
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.P)
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
                                ProgramAdapter adapter = new ProgramAdapter(_that, null, questionList, (QuestionItemClickListener) _that);
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