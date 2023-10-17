package com.appsnipp.education.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lixs.charts.RadarChartView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

import static lecho.lib.hellocharts.util.ChartUtils.COLOR_VIOLET;

public class LearnActivity extends AppCompatActivity {

    /***
     * This Function is used for the creation of the class ui of Learn Activity.
     * @param savedInstanceState the bundle to create the view of learn activity
     * @return Nothing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // Set chart
        // Zero, init values
        ArrayList<PointValue> values = new ArrayList<PointValue>();

        // First, send request
        // 通过post请求获取使用时长
        String urlGetTime = AppSingleton.URL_PREFIX +  "/api/time";
        System.err.println(urlGetTime);
        Activity _that = this;
        // build request
        StringRequest strRequestInfo = new StringRequest
                (com.android.volley.Request.Method.POST, urlGetTime, new com.android.volley.Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {
                            // success
                            JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
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
                                values.sort(new Comparator<PointValue>() {
                                    @Override
                                    public int compare(PointValue o1, PointValue o2) {
                                        if(o1.getX() < o2.getX()) return 0;
                                        else if(o1.getX() == o2.getX() && o1.getY() < o2.getY()) return 0;
                                        else return 1;
                                    }
                                });
                            }
                            _that.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //In most cased you can call data model methods in builder-pattern-like manner.
                                    Line line = new Line(values).setColor(COLOR_VIOLET).setCubic(false);
                                    line.setHasLabels(false);
                                    ArrayList<Line> lines = new ArrayList<Line>();
                                    lines.add(line);
                                    LineChartData data = new LineChartData();
                                    data.setLines(lines);
                                    Axis axisX = new Axis();
                                    data.setAxisXBottom(axisX);
                                    axisX.setHasTiltedLabels(true);
                                    LineChartView chart = findViewById(R.id.activity_learn_chart);
                                    chart.setLineChartData(data);
                                    chart.setInteractive(true);
                                }
                            });
                        } catch (Exception e) {
                            System.err.println(e);
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(_that, "获取过往学习时长失败",Toast.LENGTH_SHORT).show();
                        System.err.print(error.toString());
                        String body;
                        try {
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.w("EDUKG", body);

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
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences preferences = _that.getSharedPreferences ("userInfo",
                        _that.MODE_PRIVATE);
                // change in order to prevent from Exception
                String id = preferences.getString("id", "");
                Log.w("ID",id);
                params.put("id", id);
                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        AppSingleton.getInstance(_that).addToRequestQueue(strRequestInfo);

        for(PointValue t: values) {
            System.err.println(t.getX());
            System.err.println(t.getY());
        }
        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.parseColor("#2196F3")).setCubic(false);
        line.setHasLabels(true);
        line.setAreaTransparency(30);
        ArrayList<Line> lines = new ArrayList<Line>();
        lines.add(line);
        Axis axisY = new Axis();
        LineChartData data = new LineChartData();
        data.setLines(lines);
        Axis axisX = new Axis();
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        axisX.setHasTiltedLabels(true);
        LineChartView chart = findViewById(R.id.activity_learn_chart);
        chart.setLineChartData(data);
        chart.setInteractive(true);
        SharedPreferences preferences = getSharedPreferences ("userInfo",
                MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("id", "");
        List<Float> datas = new ArrayList<>();
        List<String> description = new ArrayList<>();

        String url = AppSingleton.URL_PREFIX + "/api/course_score";
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
                    JsonArray jsonArray = new JsonParser().parse(result).getAsJsonArray();
                    for(JsonElement e: jsonArray) {
                        float n = e.getAsJsonObject().get("score").getAsFloat();
                        if(n < 0.0) n = -n;
                        datas.add(n);
                        description.add(AppSingleton.courseInverseMap.get(e.getAsJsonObject().get("course").getAsString()));
                    }
                    _that.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RadarChartView radarChartView = findViewById(R.id.radarView);
                            radarChartView.setCanClickAnimation(true);
                            radarChartView.setDatas(datas);
                            radarChartView.setPolygonNumbers(datas.size());
                            radarChartView.setDescriptions(description);
                        }
                    });
                }
            }
        });

        //点击动画开启
        RadarChartView radarChartView = findViewById(R.id.radarView);
        radarChartView.setCanClickAnimation(true);
        radarChartView.setDatas(datas);
        radarChartView.setPolygonNumbers(6);
        radarChartView.setDescriptions(description);

        // TODO
        ArrayList<Column> columns = new ArrayList<Column>();

        url = AppSingleton.URL_PREFIX +  "/api/course_score";
        System.err.println(url);

        // TODO1
        ArrayList<Column> entity_columns = new ArrayList<Column>();
        ArrayList<SubcolumnValue> valueSubColumn = new ArrayList<SubcolumnValue>();
        String entity_url = AppSingleton.URL_PREFIX +  "/api/score";
        System.err.println(entity_url);
        // build request
        client = new OkHttpClient();
        body = new FormBody.Builder()
                .add("id",id)
                .build();
        request = new okhttp3.Request.Builder()
                .url(entity_url)
                .post(body)
                .build();
        call = client.newCall(request);
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
                            Float score = e.getAsJsonObject().get("score").getAsFloat();
                            String entity = e.getAsJsonObject().get("entity").getAsString();
                            ArrayList<SubcolumnValue> valueSubColumn = new ArrayList<SubcolumnValue>();
                            SubcolumnValue columnvalue = new SubcolumnValue(score);
                            columnvalue.setLabel(course + " " + entity);
                            valueSubColumn.add(columnvalue);
                            Column column = new Column(valueSubColumn);
                            column.setHasLabels(true);
                            entity_columns.add(column);
                        }
                        _that.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ColumnChartData entity_dataColumn = new ColumnChartData();
                                entity_dataColumn.setColumns(entity_columns);
                                Axis entity_axisXColumn = new Axis();
                                entity_dataColumn.setAxisXBottom(entity_axisXColumn);
                                ColumnChartView entity_columnChart = findViewById(R.id.activity_entity_score);
                                entity_columnChart.setColumnChartData(entity_dataColumn);
                                entity_columnChart.setInteractive(true);
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }
        });

        ColumnChartData entity_dataColumn = new ColumnChartData();
        entity_dataColumn.setColumns(entity_columns);
        Axis entity_axisXColumn = new Axis();
        entity_dataColumn.setAxisXBottom(entity_axisXColumn);
        ColumnChartView entity_columnChart = findViewById(R.id.activity_entity_score);
        entity_columnChart.setColumnChartData(entity_dataColumn);
        entity_columnChart.setInteractive(true);

    }
}
