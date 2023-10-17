/***
 * This Activity is the main activity and the entrance of the whole application.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.databinding.ActivityMainBinding;
import com.appsnipp.education.ui.helpers.BottomNavigationBehavior;
import com.appsnipp.education.ui.helpers.DarkModePrefManager;
import com.appsnipp.education.ui.questionhint.QuestionHintFragment;
import com.appsnipp.education.util.BodyParser;
import com.appsnipp.education.util.EduRequest;
import com.appsnipp.education.widget.ACache;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    ActivityMainBinding binding;
    NavHostFragment navHostFragment;
    private BottomNavigationView bottomNavigationView;
    public static Application mApplication;

    /***
     * This Function is used for the creation of the class ui of Main Activity.
     * @param savedInstanceState the bundle used for the creation of the ui
     * @return Nothing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSingleton.startTime = new Date().getTime();
        // hard coded night mode
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(view);
        setSupportActionBar(binding.appBarMain.toolbar);

        //default setting
        AppSingleton.acache = ACache.get(MainActivity.this);
        ArrayList<String> _selected = new ArrayList<>();
        _selected.add("语文");
        _selected.add("数学");
        _selected.add("英语");
        _selected.add("物理");
        AppSingleton.selectedCourse = _selected;
        ArrayList<String> _nSelected = new ArrayList<>();
        _nSelected.add("化学");
        _nSelected.add("生物");
        AppSingleton.notSelected = _nSelected;
        AppSingleton.courseMap.put("语文","chinese");
        AppSingleton.courseMap.put("数学","math");
        AppSingleton.courseMap.put("英语","english");
        AppSingleton.courseMap.put("物理","physics");
        AppSingleton.courseMap.put("化学","chemistry");
        AppSingleton.courseMap.put("生物","biology");
        AppSingleton.courseMap.put("历史","history");
        AppSingleton.courseMap.put("地理","geo");
        AppSingleton.courseMap.put("政治","politics");
        AppSingleton.courseInverseMap.put("chinese","语文");
        AppSingleton.courseInverseMap.put("math","数学");
        AppSingleton.courseInverseMap.put("english","英语");
        AppSingleton.courseInverseMap.put("physics","物理");
        AppSingleton.courseInverseMap.put("chemistry","化学");
        AppSingleton.courseInverseMap.put("biology","生物");
        AppSingleton.courseInverseMap.put("history","历史");
        AppSingleton.courseInverseMap.put("geo","地理");
        AppSingleton.courseInverseMap.put("politics","政治");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        SharedPreferences preferences = getSharedPreferences("userInfo",
                MODE_PRIVATE);
        int state = preferences.getInt("state", 0);
        if(state == 0) {
            // 尚未登录
            System.out.println("尚未登录");
        } else {
            // 通过post请求获取昵称
            String urlHistory = AppSingleton.URL_PREFIX + "/api/history";
            // get params
            preferences = getSharedPreferences("userInfo",
                    MODE_PRIVATE);
            String id = preferences.getString("id", "");
            System.err.println(id);
            BodyParser bodyParser = new BodyParser();
            bodyParser.add("id", id);
            String suffix = bodyParser.parse();
            System.err.println(urlHistory + suffix);
            // build request
            StringRequest strRequest = new StringRequest
                    (com.android.volley.Request.Method.POST, urlHistory + suffix, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // success
                            System.err.println("getHistorySuccess");
                            JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            System.err.println("Fetch Info Error!");
                            System.err.println(error.toString());
                            String body;
                            try {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    System.err.println(body);
                                } catch (NullPointerException e) {
                                    Log.w("Warn", "Null Pointer Exception");
                                }
                            } catch (UnsupportedEncodingException e) {
                                // exception
                                body = "";
                            }

                        }
                    }) {
            };
            // Access the RequestQueue through your singleton class.
            AppSingleton.getInstance(this).addToRequestQueue(strRequest);
        }

        // TODO temporarily mock some 学情分析
        String add_time_url = AppSingleton.URL_PREFIX + "/api/add_time";
        preferences = getSharedPreferences ("userInfo",
                MODE_PRIVATE);
        Map<String, String> new_time = new HashMap<>();
        new_time.put("1000","2021-07-21");
        String id = preferences.getString("id", "");
        for(Map.Entry<String, String> t: new_time.entrySet()) {
            // build request
            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("id",id)
                    .add("time", t.getKey())
                    .add("date", t.getValue())
                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(add_time_url)
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

        // 通过post请求获取昵称
        String urlOpenEdukg = AppSingleton.EDUKG_PREFIX +  "/api/typeAuth/user/login";
        System.err.println(urlOpenEdukg);
        // build request
        Activity _that = this;
        EduRequest strRequestEdukg = new EduRequest
                (com.android.volley.Request.Method.POST, urlOpenEdukg, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // success
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String id = jsonObject.get("id").toString();
                        SharedPreferences preferences = getSharedPreferences ("userInfo",
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit(); //获取编辑器
                        editor.putString("edukg_id", id);
                        editor.commit(); //
                        Log.w("EDUKG_DEBUG", id);

                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.err.println("EDUKG登录失败！");
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
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", AppSingleton.EUDKG_PHONE);
                params.put("password", AppSingleton.EDUKG_PASSWORD);
                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        AppSingleton.getInstance(this).addToRequestQueue(strRequestEdukg);

        findViewById(R.id.bottomNavigationView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        setupNavigation();
    }

    /***
     * This Function is used for the setup of the bottom navigation.
     * @param
     * @return Nothing
     */
    private void setupNavigation() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) binding.appBarMain.bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
        }

    }

    /***
     * This Function is used for the callback of the movement onKeyDown.
     * @param keyCode the key pressed (in the form of keyCode as a integer)
     * @param event the event of the key pressed (in the form of keyEvent)
     * @return Nothing
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppSingleton.endTime = new Date().getTime();
        String url = AppSingleton.URL_PREFIX +  "/api/add_time";
        System.err.println(url);
        // build request
        Activity _that = this;
        StringRequest strRequest = new StringRequest
                (com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // success
                        System.err.println(response);
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
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                SharedPreferences preferences = _that.getSharedPreferences ("userInfo",
                        _that.MODE_PRIVATE);
                // must change otherwise raise Exception
                String id = preferences.getString("id", "");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                Map<String, String> params = new HashMap<String, String>();
                Log.w("EDUKG_DEBUG", id);
                Log.w("EDUKG_DEBUG", ((Integer)((int)(AppSingleton.endTime - AppSingleton.startTime))).toString());
                Log.w("EDUKG_DEBUG",df.format(new Date()));
                params.put("id",id);
                params.put("time", ((Integer)((int)(AppSingleton.endTime - AppSingleton.startTime))).toString());
                params.put("date", df.format(new Date()));
                return params;
            }
        };
        AppSingleton.getInstance(_that).addToRequestQueue(strRequest);
        Intent intent = new Intent();
        intent.setAction("out");
        sendBroadcast(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        return super.onKeyDown(keyCode, event);
    }

    /***
     * This Function is used for the callback of the click of searchView.
     * @param
     * @return Nothing
     */
    public void onClick(View v){
        Fragment questionHintFragment = new QuestionHintFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.search_list, questionHintFragment).commit();
    }

    /***
     * This Function is used for the callback of exiting main activity.
     * @param
     * @return Nothing
     */
    @Override
    public void onBackPressed() {
        AppSingleton.endTime = new Date().getTime();
        String url = AppSingleton.URL_PREFIX +  "/api/add_time";
        System.err.println(url);
        // build request
        Activity _that = this;
        StringRequest strRequest = new StringRequest
                (com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // success
                        System.err.println(response);
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
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                SharedPreferences preferences = _that.getSharedPreferences ("userInfo",
                        _that.MODE_PRIVATE);
                // must change otherwise raise Exception
                String id = preferences.getString("id", "");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                Map<String, String> params = new HashMap<String, String>();
                Log.w("EDUKG_DEBUG", id);
                Log.w("EDUKG_DEBUG", ((Integer)((int)(AppSingleton.endTime - AppSingleton.startTime))).toString());
                Log.w("EDUKG_DEBUG",df.format(new Date()));
                params.put("id",id);
                params.put("time", ((Integer)((int)(AppSingleton.endTime - AppSingleton.startTime))).toString());
                params.put("date", df.format(new Date()));
                return params;
            }
        };
        AppSingleton.getInstance(_that).addToRequestQueue(strRequest);

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /***
     * This Function is the implementation of the function of selecting of navigation item in the bottom.
     * @param item an instance of MenuItem which is selected.
     * @return Nothing
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_dark_mode) {
            //code for setting dark mode
            //true for dark mode, false for day mode, currently toggling on each click
            DarkModePrefManager darkModePrefManager = new DarkModePrefManager(this);
            darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();

        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
