/***
 * This Activity is used for the favorite page of our system which shows the favorite content the users saved.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.model.CourseCard;
import com.appsnipp.education.model.Knowledge;
import com.appsnipp.education.ui.detail.KnowledgeDetail;
import com.appsnipp.education.ui.listeners.CoursesItemClickListener;
import com.appsnipp.education.adapter.CourseRecyclerAdapter;
import com.appsnipp.education.adapter.ScanAdapter;
import com.appsnipp.education.util.RecycleDecoration;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yw.game.floatmenu.FloatItem;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

public class FavoriteActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener, CoursesItemClickListener {

    /***
     * This Function is used for the creation of the class ui of Favorite Activity.
     * @param savedInstanceState Bundle of this activity page
     * @return Nothing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        RecyclerView recycler = findViewById(R.id.content_favorite_recycler);
        ArrayList<CourseCard> courseCards = new ArrayList<>();
        Activity _that = this;
        // 和后端post请求获取收藏
        String url = AppSingleton.URL_PREFIX +  "/api/favorites";
        System.err.println(url);
        SharedPreferences preferences = this.getSharedPreferences ("userInfo",
                this.MODE_PRIVATE);
        String id = preferences.getString("id", "");
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
                    for(JsonElement element : jsonArray) {
                        String course = element.getAsJsonObject().get("course").getAsString();
                        String context = element.getAsJsonObject().get("context").getAsString();
                        courseCards.add(new CourseCard(courseCards.size() + 1,
                                R.drawable.course_coding, course, context));
                    }
                }
            }
        });

        CourseRecyclerAdapter adapter = new CourseRecyclerAdapter(this, courseCards, new CoursesItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDashboardCourseClick(CourseCard courseCard, ImageView imageView) {
                ArrayList<String> nowCourse = new ArrayList<String>();
                nowCourse.add(AppSingleton.nowCourse);
                ArrayList<Knowledge> knowledgeBase = new ArrayList<Knowledge>();
                Knowledge kn = new Knowledge(courseCard.getCourseTitle(),courseCard.getQuantityCourses(),
                        courseCard.getQuantityCourses(), nowCourse, null);
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_favorite, new KnowledgeDetail()).commit();
            }
        });
        ImageView back = findViewById(R.id.icon_favorite_toolbar_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_that, MainActivity.class);
                startActivity(intent);
            }
        });
        ScanAdapter knowledgeAdapter = new ScanAdapter(this, courseCards, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(knowledgeAdapter);
        recycler.addItemDecoration(new RecycleDecoration(40));
        recycler.addOnItemTouchListener(this);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                } else {

                }
            }
        });


        ArrayList<FloatItem> itemList = new ArrayList<>();
        FloatItem newItemQuestion = new FloatItem(
                "培养方案", Color.BLACK, Color.WHITE,
                BitmapFactory.decodeResource(getResources(), R.drawable.person_program)
        );
        FloatItem newItemPractice = new FloatItem(
                "专项练习", Color.BLACK, Color.WHITE,
                BitmapFactory.decodeResource(getResources(), R.drawable.person_exercise)
        );
        FloatItem newItemFavorite = new FloatItem(
                "知识问答", Color.BLACK, Color.WHITE,
                BitmapFactory.decodeResource(getResources(), R.drawable.person_question)
        );
        itemList.add(newItemFavorite);
        itemList.add(newItemPractice);
        itemList.add(newItemQuestion);
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

    /***
     * This Function is the callback and action when the dashboard item is clicked.
     * @param courseCard the selected courseCard
     * @param imageView the clicked imageView
     * @return Nothing
     */
    @Override
    public void onDashboardCourseClick(CourseCard courseCard, ImageView imageView) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}