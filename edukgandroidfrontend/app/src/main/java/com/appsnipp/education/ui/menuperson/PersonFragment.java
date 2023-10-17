package com.appsnipp.education.ui.menuperson;

/***
 * This is the a fragment recording the personal information
 * @author Shuning Zhang
 * @version 1.0
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.activities.PersonDetailActivity;
import com.appsnipp.education.R;
import com.appsnipp.education.activities.ChatActivity;
import com.appsnipp.education.activities.ExerciseActivity;
import com.appsnipp.education.activities.FavoriteActivity;
import com.appsnipp.education.activities.LoginActivity;
import com.appsnipp.education.activities.ProgramActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PersonFragment extends Fragment {

    private PersonalInfoViewModel mViewModel;

    public static PersonFragment newInstance() {
        return new PersonFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_person, container, false);
        // 查询用户的登录状态
        SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                getActivity().MODE_PRIVATE);
        int state = preferences.getInt("state", 0);
        String username = preferences.getString("username", null);
        String password = preferences.getString("password", null);
        // 根据用户的登录状态设置有关的组件的状态
        ImageView personalInfoClickLogin = thisView.findViewById(R.id.personal_info_click_login);
        ImageView personInfoArrow = thisView.findViewById(R.id.login_arrow);
        if (state == 0) {
            // 更换为默认头像
            personalInfoClickLogin.setImageResource(R.drawable.student_girl);
            // 非登录状态则设置页面跳转登录逻辑
            personalInfoClickLogin.setOnClickListener(new View.OnClickListener() {      //  点击跳转时
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            // 不显示小箭头
            ImageView view = thisView.findViewById(R.id.login_arrow);
            view.setVisibility(View.INVISIBLE);
        }
        else{
            // 否则点击跳转到信息页面
            View.OnClickListener listener = new View.OnClickListener() {      //  点击跳转时
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
                    startActivity(intent);
                }
            };
            personalInfoClickLogin.setOnClickListener(listener);
            personInfoArrow.setOnClickListener(listener);
            // 更换为用户头像
            personalInfoClickLogin.setImageResource(R.drawable.person_login);
            // 显示小箭头
            ImageView view = thisView.findViewById(R.id.login_arrow);
            view.setVisibility(View.VISIBLE);
        }
        TextView personalInfo = thisView.findViewById(R.id.nickname_personal);
        if(state == 1){
            // 若已经登录则显示昵称
            // 通过post请求获取昵称
            String url = AppSingleton.URL_PREFIX +  "/api/login";
            System.err.println(url);
            // build request
            StringRequest strRequest = new StringRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            // success
                            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                            String nickname = jsonObject.get("nickname").toString();
                            String id = jsonObject.get("id").toString();
                            SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                                    MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit(); //获取编辑器
                            editor.putString("id", id.substring(1, id.length() - 1));
                            editor.commit(); //提交修改
                            String display = "Hi，" + nickname.substring(1, nickname.length() - 1)
                                    + " 同学";
                            personalInfo.setText(display);
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
                    params.put("name", username);
                    params.put("password", password);
                    return params;
                }
            };

            // Access the RequestQueue through your singleton class.
            AppSingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
            // 点击跳转到信息页面
            personalInfo.setOnClickListener(new View.OnClickListener() {      //  点击跳转时
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
                    startActivity(intent);
                }
            });
        }
        else{
            // 提示登录
            personalInfo.setText("Hi，欢迎登录");
            // 非登录状态则设置页面跳转登录逻辑
            personalInfo.setOnClickListener(new View.OnClickListener() {      //  点击跳转时
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        LinearLayout person2program = thisView.findViewById(R.id.person_go2program);
        LinearLayout person2exercise = thisView.findViewById(R.id.person_go2exercise);
        LinearLayout person2question = thisView.findViewById(R.id.person_go2question);
        LinearLayout person2favorites = thisView.findViewById(R.id.person_go2favorites);

        person2program.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProgramActivity.class);
                startActivity(intent);
            }
        });
        person2exercise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExerciseActivity.class);
                startActivity(intent);
            }
        });
        person2question.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });
        person2favorites.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FavoriteActivity.class);
                startActivity(intent);
            }
        });
        return thisView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PersonalInfoViewModel.class);
        // TODO: Use the ViewModel
    }

}