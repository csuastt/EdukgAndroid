/*
 * Copyright (c) 2021. rogergcc
 */
/***
 * This Activity is used for showing and modifying the personal details as a activity.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersonDetailActivity extends AppCompatActivity {
    public static final String EXTRA_SETTING_STATE = "com.appsnipp.education.setting_state";

    /***
     * This Function is used for the creation of the ui of person details.
     * @param
     * @return Nothing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        // 获取用户的一系列信息
        // 用户名和密码
        SharedPreferences preferences = getSharedPreferences ("userInfo",
                MODE_PRIVATE);
        String username = preferences.getString("username", null);
        String password = preferences.getString("password", null);
        // 请求用户的各项信息
        String url = AppSingleton.URL_PREFIX +  "/api/login";
        // build request
        StringRequest strRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // success
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        setDetails(jsonObject, username);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.err.println("Fetch Info Error!");
                        String body;
                        try {
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                            } catch(NullPointerException e) {
                                // exception
                                Log.w("Warn", "Null Pointer Exception");
                            }
                        } catch (UnsupportedEncodingException e) {
                            // exception
                            body = "";
                        }
                        // System.err.println(body);
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", username);
                params.put("password", password);
                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        AppSingleton.getInstance(this).addToRequestQueue(strRequest);
    }

    /***
     * This Function is used for the users' details setting.
     * @param
     * @return Nothing
     */
    private void setDetails(JsonObject obj, String username) {
        // username
        TextView detail_username = findViewById(R.id.detail_usrname);
        detail_username.setText(username);
        // nickname
        String nickname = obj.get("nickname").toString();
        TextView detail_nickname = findViewById(R.id.detail_nickname);
        nickname = nickname.substring(1, nickname.length() - 1);
        if (Objects.equals(nickname, "")){
            detail_nickname.setText("昵称未设置");
        }
        else {
            detail_nickname.setText(nickname);
        }
        // sex
        String sex = obj.get("gender").toString();
        TextView detail_sex = findViewById(R.id.detail_sex);
        sex = sex.substring(1, sex.length() - 1);
        if (Objects.equals(sex, "")){
            detail_sex.setText("性别未设置");
        }
        else if (Objects.equals(sex, "man")) {
            detail_sex.setText("男生");
        }
        else {
            detail_sex.setText("女生");
        }
    }

    /***
     * This Function is Called when the user taps the Quit button.
     * @param view the view of this page (activity)
     * @return Nothing
     */
    public void quitLogin(View view) {
        // modify login state
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); //获取编辑器
        editor.putInt("state", 0);
        editor.commit(); //提交修改
        // Quit the login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        // finish
        finish();
    }

    /***
     * This Function is Called when back to main.
     * @param view the view of this page (activity)
     * @return Nothing
     */
    public void backToMain(View view) {
        // back
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // finish
        finish();
    }

    /***
     * This Function is Called when test the settings.
     * @param view the view of this page (activity)
     * @return Nothing
     */
    public void testSetting(View view) {
        // test the setting
        Intent intent = new Intent(this, PersonSettingActivity.class);
        startActivity(intent);
    }

    /***
     * This Function is Called when go to password modification.
     * @param view the view of this page (activity)
     * @return Nothing
     */
    public void modifyPassword(View view) {
        Intent intent = new Intent(this, PersonSettingActivity.class);
        intent.putExtra(EXTRA_SETTING_STATE, "password");
        startActivity(intent);
    }

    /***
     * This Function is Called when go to sex modification.
     * @param view the view of this page (activity)
     * @return Nothing
     */
    public void modifySex(View view) {
        Intent intent = new Intent(this, PersonSettingActivity.class);
        intent.putExtra(EXTRA_SETTING_STATE, "sex");
        startActivity(intent);
    }

    /***
     * This Function is Called when go to nickname modification.
     * @param view the view of this page (activity)
     * @return Nothing
     */
    public void modifyNickname(View view) {
        Intent intent = new Intent(this, PersonSettingActivity.class);
        intent.putExtra(EXTRA_SETTING_STATE, "nickname");
        startActivity(intent);
    }

    /***
     * This Function is Called when go to description modification.
     * @param view the view of this page (activity)
     * @return Nothing
     */
    public void modifyDescription(View view) {
        Intent intent = new Intent(this, PersonSettingActivity.class);
        intent.putExtra(EXTRA_SETTING_STATE, "description");
        startActivity(intent);
    }

}