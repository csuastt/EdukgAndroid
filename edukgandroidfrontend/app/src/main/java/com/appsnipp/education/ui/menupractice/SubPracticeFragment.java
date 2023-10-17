/***
 * This Activity is used for the subfragment of the main page, which is used in tag view as a fragment.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.ui.menupractice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.activities.ChatActivity;
import com.appsnipp.education.adapter.SquareCourseRecyclerAdapter;
import com.appsnipp.education.model.CourseCard;
import com.appsnipp.education.model.Knowledge;
import com.appsnipp.education.ui.detail.KnowledgeDetail;
import com.appsnipp.education.activities.LearnActivity;
import com.appsnipp.education.ui.listeners.CoursesItemClickListener;
import com.appsnipp.education.ui.menuhome.HomeFragment;
import com.appsnipp.education.ui.menuscan.ScanFragment;
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

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubPracticeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubPracticeFragment extends Fragment implements CoursesItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int totalCourseCardCnt = 1;
    private Map<String, String> predicateAnswer;

    private Context mContext;
    private ArrayList<CourseCard> courseCards;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SubPracticeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubPracticeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubPracticeFragment newInstance(String param1, String param2) {
        SubPracticeFragment fragment = new SubPracticeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this method to create the page of this fragment.
     * @param savedInstanceState the bundle used for the creation of the page of this page.
     * @return nothing
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Use this method to create the view of this fragment.
     * @param inflater some fixed settings which is used for the creation of the view
     * @param container some fixed settings used for the creation of the view
     * @param savedInstanceState some fixed bundles used for the creation of the view of this page
     * @return nothing
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView =  inflater.inflate(R.layout.subfragment_home_course_1, container, false);
        mContext = this.getContext();
        courseCards = new ArrayList<>();
        ArrayList<PointValue> values = new ArrayList<PointValue>();
        SharedPreferences preferences = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("id", "");
        String url = AppSingleton.URL_PREFIX +  "/api/time";
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                System.err.println(response.toString());
                if(response.isSuccessful()){
                    String result = response.body().string();
                    try {
                        // success
                        JsonArray jsonArray = new JsonParser().parse(result).getAsJsonArray();
                        int maxyear = 0;
                        int maxmonth = 0;
                        for (JsonElement e : jsonArray) {
                            String time = e.getAsJsonObject().get("time").getAsString();
                            String date = e.getAsJsonObject().get("date").getAsString();
                            int year = Integer.parseInt(date.substring(0, 4));
                            int month = Integer.parseInt(date.substring(5, 7));
                            int day = Integer.parseInt(date.substring(8));
                            if(year >= maxyear) maxyear = year;
                            if(month >= maxmonth) maxmonth = month;
                        }
                        for (JsonElement e : jsonArray) {
                            String time = e.getAsJsonObject().get("time").getAsString();
                            String date = e.getAsJsonObject().get("date").getAsString();
                            int year = Integer.parseInt(date.substring(0, 4));
                            int month = Integer.parseInt(date.substring(5, 7));
                            int day = Integer.parseInt(date.substring(8));
                            if(year == maxyear && month == maxmonth) {
                                PointValue pValue = new PointValue(day, Integer.valueOf(time) / 60);
                                pValue.setLabel(date);
                                values.add(pValue);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                values.sort(new Comparator<PointValue>() {
                                    @Override
                                    public int compare(PointValue o1, PointValue o2) {
                                        if(o1.getX() < o2.getX()) return 0;
                                        else if(o1.getX() == o2.getX() && o1.getY() < o2.getY()) return 0;
                                        else return 1;
                                    }
                                });
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //In most cased you can call data model methods in builder-pattern-like manner.
                                Line line = new Line(values).setColor(Color.parseColor("#2196F3")).setCubic(false);
                                line.setHasLabelsOnlyForSelected(true);
                                line.setAreaTransparency(30);
                                ArrayList<Line> lines = new ArrayList<Line>();
                                lines.add(line);

                                LineChartData data = new LineChartData();
                                data.setLines(lines);
                                Axis axisX = new Axis();
                                Axis axisY = new Axis();

                                data.setAxisXBottom(axisX);
                                data.setAxisYLeft(axisY);
                                LineChartView chart = thisView.findViewById(R.id.chart_learn_length);
                                chart.setLineChartData(data);
                                chart.setInteractive(true);
                            }
                        });
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
            }
        });


        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.parseColor("#2196F3")).setCubic(false);
        line.setHasLabelsOnlyForSelected(true);
        line.setAreaTransparency(30);
        ArrayList<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        Axis axisX = new Axis();
        Axis axisY = new Axis();

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        LineChartView chart = thisView.findViewById(R.id.chart_learn_length);
        chart.setLineChartData(data);
        chart.setInteractive(true);
        View includeAppend1 = thisView.findViewById(R.id.append_course_1);
        includeAppend1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LearnActivity.class);
                startActivity(intent);
            }
        });
        View includeAppend2 = thisView.findViewById(R.id.append_course_2);
        View includeAppend3 = thisView.findViewById(R.id.append_course_3);
        TextView appendText1 = includeAppend1.findViewById(R.id.popular_courses_base);
        TextView appendText2 = includeAppend2.findViewById(R.id.popular_courses_base);
        TextView appendText3 = includeAppend3.findViewById(R.id.popular_courses_base);
        appendText1.setText("学情分析");
        appendText2.setText("薄弱提升");
        appendText3.setText("培优补差");
        ImageView img1 = includeAppend1.findViewById(R.id.popular_courses_photo);
        ImageView img2 = includeAppend2.findViewById(R.id.popular_courses_photo);
        ImageView img3 = includeAppend3.findViewById(R.id.popular_courses_photo);
        img1.setImageResource(R.drawable.cat_analyze);
        img2.setImageResource(R.drawable.cat_liftup);
        img3.setImageResource(R.drawable.cat_program);
        appendText1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "点击", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_practice, new ScanFragment()).commit();
            }
        });
        appendText2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "点击", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_practice, new HomeFragment()).commit();
            }
        });
        appendText3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "点击", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });
        // [INTERNET]

        preferences = getActivity().getSharedPreferences ("userInfo",
                getActivity().MODE_PRIVATE);
        int state = preferences.getInt("state", 0);
        if(state != 0) {
            // 通过post请求获取收藏
            url = AppSingleton.URL_PREFIX +  "/api/favorites";
            // build request
            StringRequest strRequest = new StringRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // success
                            System.err.println("Having response");
                            // JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            System.err.println("Fetch Info Error!");
                            System.err.println(error.toString());
                            String body;
                            try {
                                body = new String(error.networkResponse.data,"UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                // exception
                                body = "";
                            } catch (NullPointerException e) {
                                body = "";
                                //Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
                            }
                            System.err.println(body);
                        }
                    }){
                @Override
                protected Map<String, String> getParams()
                {
                    SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                            getActivity().MODE_PRIVATE);
                    String id = preferences.getString("id", "");
                    Log.w("ID",id);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id",id);
                    return params;
                }
            };
            // Access the RequestQueue through your singleton class.
            AppSingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
        } else {
            Toast.makeText(mContext, "尚未登录", Toast.LENGTH_LONG).show();
            search(AppSingleton.nowCourse, "i");
        }

        RecyclerView listOfKnowledge = thisView.findViewById(R.id.list_of_knowledge);

        url = AppSingleton.URL_PREFIX +  "/api/history";
        System.err.println(url);
        // build request
        client = new OkHttpClient();
        body = new FormBody.Builder()
                .add("id",id)
                .build();
        request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Failed");
                search(AppSingleton.nowCourse, "i");
                search(AppSingleton.nowCourse, "小");
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                System.err.println(response.toString());
                if(response.isSuccessful()){
                    String result = response.body().string();
                    System.err.println(result);
                    JsonArray jsonArray = new JsonParser().parse(result).getAsJsonArray();
                    int totalCnt = 0;
                    for(JsonElement e: jsonArray) {
                        if(totalCnt >= 3) {
                            break;
                        }
                        totalCnt += 1;
                        if(e.getAsJsonObject().get("type").getAsString().contains("search")) {
                            search(e.getAsJsonObject().get("course").getAsString(),
                                    e.getAsJsonObject().get("context").getAsString()
                                    );
                        }
                    }
                    if(jsonArray.size() == 0) {
                        search(AppSingleton.nowCourse, "i");
                        search(AppSingleton.nowCourse, "小");
                    }
                } else {
                    search(AppSingleton.nowCourse, "i");
                    search(AppSingleton.nowCourse, "小");
                }
            }
        });

        SquareCourseRecyclerAdapter adapter = new SquareCourseRecyclerAdapter(mContext, courseCards, new CoursesItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDashboardCourseClick(CourseCard courseCard, ImageView imageView) {
                ArrayList<String> courseLabel = new ArrayList<String>();
                courseLabel.add(AppSingleton.nowCourse);
                ArrayList<Knowledge> knowledgeBase = new ArrayList<Knowledge>();
                Knowledge kn = new Knowledge(courseCard.getCourseTitle(),courseCard.getQuantityCourses(),courseCard.getUrlCourse(), courseLabel, null);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_practice, new KnowledgeDetail(kn)).commit();
            }
        });
        StaggeredGridLayoutManager layout = new StaggeredGridLayoutManager(1, 1);
        layout.setOrientation(StaggeredGridLayoutManager.VERTICAL); //设置为纵向排列
        listOfKnowledge.setLayoutManager(layout);
        listOfKnowledge.setAdapter(adapter);
        return thisView;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onDashboardCourseClick(CourseCard courseCard, ImageView imageView) {

    }

    /**
     * Use this method to communicate with the backend: to search.
     * @param course the selected course as input
     * @param entity the selected entity as input
     * @return  nothing
     */
    public void search(String course, String entity) {
        String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/instanceList";
        //空格转义
        entity = entity.split(" ")[0];
        System.err.println(url);
        System.err.println(course);
        System.err.println(entity);
        url += "?";
        SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                getActivity().MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("edukg_id", "");
        url += "id=";
        url += id;
        url += "&searchKey=";
        url += entity;
        url += "&course=";
        url += course;
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
                            String label = t.getAsJsonObject().get("label").getAsString();
                            String category = t.getAsJsonObject().get("category").getAsString();
                            // maybe some time in the future uri would be needed
                            courseCards.add(new CourseCard(courseCards.size() + 1, R.drawable.white,
                                    label, category));
                        }
                        (getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StaggeredGridLayoutManager layout = new StaggeredGridLayoutManager(1, 1);
                                layout.setOrientation(StaggeredGridLayoutManager.VERTICAL); //设置为纵向排列
                                RecyclerView listOfKnowledge = getView().findViewById(R.id.list_of_knowledge);
                                SquareCourseRecyclerAdapter adapter = new SquareCourseRecyclerAdapter(mContext, courseCards, new CoursesItemClickListener() {
                                    @SuppressLint("ResourceType")
                                    @Override
                                    public void onDashboardCourseClick(CourseCard courseCard, ImageView imageView) {
                                        ArrayList<String> courseLabel = new ArrayList<String>();
                                        courseLabel.add(AppSingleton.nowCourse);
                                        ArrayList<Knowledge> knowledgeBase = new ArrayList<Knowledge>();
                                        Knowledge kn = new Knowledge(courseCard.getCourseTitle(),courseCard.getQuantityCourses(),courseCard.getUrlCourse(), courseLabel, null);
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_practice, new KnowledgeDetail(kn)).commit();
                                    }
                                });

                                listOfKnowledge.setLayoutManager(layout);
                                listOfKnowledge.setAdapter(adapter);
                            }
                        });
                        //处理UI需要切换到UI线程处理
                    }
                }
            }
        });
    }
}