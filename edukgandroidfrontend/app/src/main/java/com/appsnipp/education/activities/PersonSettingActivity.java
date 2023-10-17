/*
 * Copyright (c) 2021. rogergcc
 */
/***
 * This Activity is used for showing and modifying the personal settings as a activity.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.ui.settings.PersonSettingFormState;
import com.appsnipp.education.ui.settings.PersonSettingViewModel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersonSettingActivity extends AppCompatActivity {
    private int setting_sex = 0;
    private String mode;
    private String id;
    private PersonSettingViewModel personSettingViewModel = new PersonSettingViewModel();

    /***
     * This Function is Called when the ui of personSettingActivity is initialized.
     * @param savedInstanceState the bundle controlling the ui of this page (activity)
     * @return Nothing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_setting);

        // 获取用户的一系列信息
        // 用户名和密码
        SharedPreferences preferences = getSharedPreferences ("userInfo",
                MODE_PRIVATE);
        String username = preferences.getString("username", null);
        String password = preferences.getString("password", null);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(PersonDetailActivity.EXTRA_SETTING_STATE);
        mode = message;

        // modify layouts according to msg
        if (Objects.equals(message, "password")){
            // setup edit text
            EditText oldPasswordEditText = findViewById(R.id.old_password);
            oldPasswordEditText.setVisibility(View.VISIBLE);
            EditText newPasswordEditText = findViewById(R.id.new_password);
            newPasswordEditText.setVisibility(View.VISIBLE);
            EditText newPassword2EditText = findViewById(R.id.new_password2);
            newPassword2EditText.setVisibility(View.VISIBLE);
            Button confirmButton = findViewById(R.id.confirm_edit);
            // setup view model listener
            personSettingViewModel.getPersonSettingFormState().observe(this, new Observer<PersonSettingFormState>() {
                @Override
                public void onChanged(@Nullable PersonSettingFormState personSettingFormState) {
                    if (personSettingFormState == null) {
                        return;
                    }
                    confirmButton.setEnabled(personSettingFormState.isDataValid());
                    if (personSettingFormState.getOldPasswordError() != null) {
                        oldPasswordEditText.setError(getString(
                                personSettingFormState.getOldPasswordError()));
                    }
                    if (personSettingFormState.getNewPasswordError() != null) {
                        newPasswordEditText.setError(getString(
                                personSettingFormState.getNewPasswordError()));
                    }
                    if (personSettingFormState.getNewPassword2Error() != null) {
                        newPassword2EditText.setError(getString(
                                personSettingFormState.getNewPassword2Error()));
                    }
                }
            });
            // set text listener
            TextWatcher afterTextChangedListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    personSettingViewModel.settingDataChanged(
                            oldPasswordEditText.getText().toString(),
                            password,
                            newPasswordEditText.getText().toString(),
                            newPassword2EditText.getText().toString()
                    );
                }
            };
            oldPasswordEditText.addTextChangedListener(afterTextChangedListener);
            newPasswordEditText.addTextChangedListener(afterTextChangedListener);
            newPassword2EditText.addTextChangedListener(afterTextChangedListener);
            newPassword2EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        confirmEdit(v);
                    }
                    return false;
                }
            });
        }
        else if(Objects.equals(message, "sex")){
            View view = findViewById(R.id.man_checked);
            view.setVisibility(View.VISIBLE);
            view = findViewById(R.id.woman_checked);
            view.setVisibility(View.VISIBLE);
        } else if(Objects.equals(message, "description")){
            View view = findViewById(R.id.description);
            view.setVisibility(View.VISIBLE);
        } else if(Objects.equals(message, "nickname")){
            View view = findViewById(R.id.nickname);
            view.setVisibility(View.VISIBLE);
        }

        // POST request
        // build request
        String url = AppSingleton.URL_PREFIX +  "/api/login";
        StringRequest strRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // success
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        String nickname = jsonObject.get("nickname").toString();
                        nickname = nickname.substring(1, nickname.length() - 1);
                        String sex = jsonObject.get("gender").toString();
                        sex = sex.substring(1, sex.length() - 1);
                        String description = jsonObject.get("description").toString();
                        description = description.substring(1, description.length() - 1);
                        // get user id
                        id = jsonObject.get("id").toString();
                        id = id.substring(1, id.length() - 1);
                        // modify text according to msg
                        if(Objects.equals(message, "sex")){
                            if (Objects.equals(sex, "man")) {
                                CheckedTextView view = findViewById(R.id.man_checked);
                                view.setBackground(getDrawable(R.drawable.edge3));
                                setting_sex = 1;
                            }
                            else if (Objects.equals(sex, "woman")) {
                                CheckedTextView view = findViewById(R.id.woman_checked);
                                view.setBackground(getDrawable(R.drawable.edge3));
                                setting_sex = 2;
                            }
                            else{
                                setting_sex = 0;
                            }
                        } else if(Objects.equals(message, "description")){
                            TextView view = findViewById(R.id.description);
                            if (Objects.equals(description, "")){
                                view.setHint("还没有自我介绍嗷~");
                            }
                            else{
                                description =  description.replace("\\n", "\n");
                                view.setText(description);
                            }
                        } else if(Objects.equals(message, "nickname")){
                            TextView view = findViewById(R.id.nickname);
                            if (Objects.equals(nickname, "")){
                                view.setHint("还没有昵称嗷~");
                            }
                            else{
                                view.setText(nickname);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.err.println("Fetch Info Error!");
                        String body;
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            // exception
                            body = "";
                        }
                        System.err.println(body);
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
     * This Function is Called when the button man is clicked.
     * @param view the view for this page (although not used)
     * @return Nothing
     */
    public void clickMan(View view) {
        CheckedTextView man_view = findViewById(R.id.man_checked);
        CheckedTextView woman_view = findViewById(R.id.woman_checked);
        man_view.setBackground(getDrawable(R.drawable.edge3));
        woman_view.setBackground(getDrawable(R.drawable.edge1));
        setting_sex = 1;
    }

    /***
     * This Function is Called when the button woman is clicked.
     * @param view the view for this page (although not used)
     * @return Nothing
     */
    public void clickWoman(View view) {
        CheckedTextView man_view = findViewById(R.id.man_checked);
        CheckedTextView woman_view = findViewById(R.id.woman_checked);
        man_view.setBackground(getDrawable(R.drawable.edge1));
        woman_view.setBackground(getDrawable(R.drawable.edge3));
        setting_sex = 2;
    }

    /***
     * This Function is Called when the button confirm editting is clicked.
     * @param view the view for this page (although not used)
     * @return Nothing
     */
    public void confirmEdit(View view) {
        // sex must be defined before confirmation
        if (Objects.equals(mode, "sex") && setting_sex == 0){
            String error = "请填写性别后重新提交";
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            return;
        }
        // nickname must not be empty
        if (Objects.equals(mode, "nickname")){
            TextView view_ = findViewById(R.id.nickname);
            if (Objects.equals(view_.getText().toString(), "")){
                String error = "昵称不能为空";
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                return;
            }
        }
        // confirm the edition
        if (Objects.equals(mode, "password"))
        {
            String url = AppSingleton.URL_PREFIX + "/api/password_edit";
            // build request
            StringRequest strRequest = new StringRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // success
                            String msg = getString(R.string.success_edit);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            // destroy activity
                            finish();
                            // redirection
                            updateUIPassword();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            System.err.println("Confirm edition Error!");
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                // exception
                                body = "";
                            }
                            System.err.println(body);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    TextView old_password = findViewById(R.id.old_password);
                    TextView new_password = findViewById(R.id.new_password);
                    params.put("id", id);
                    params.put("old_password", old_password.getText().toString());
                    params.put("new_password", new_password.getText().toString());
                    return params;
                }
            };
            // Access the RequestQueue through your singleton class.
            AppSingleton.getInstance(this).addToRequestQueue(strRequest);
        }
        else {
            String url = AppSingleton.URL_PREFIX + "/api/info_edit";
            // build request
            StringRequest strRequest = new StringRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            // success
                            String msg = getString(R.string.success_edit);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            // destroy activity
                            finish();
                            // redirection
                            updateUI();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            System.err.println("Confirm edition Error!");
                            String body;
                            try {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                } catch(NullPointerException e) {

                                }
                            } catch (UnsupportedEncodingException e) {
                                // exception
                                body = "";
                            }
                            //System.err.println(body);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", id);
                    if (Objects.equals(mode, "nickname")) {
                        TextView view_ = findViewById(R.id.nickname);
                        params.put("nickname", view_.getText().toString());
                    } else if (Objects.equals(mode, "description")) {
                        TextView view_ = findViewById(R.id.description);
                        params.put("description", view_.getText().toString());
                    } else if (Objects.equals(mode, "sex")) {
                        params.put("gender", (setting_sex == 1) ? "man" : "woman");
                    }
                    return params;
                }
            };
            // Access the RequestQueue through your singleton class.
            AppSingleton.getInstance(this).addToRequestQueue(strRequest);
        }
    }

    /***
     * This Function is Called when the button cancelling editting is clicked.
     * @param view the view for this page (although not used)
     * @return Nothing
     */
    public void cancelEdit(View view) {
        String msg = getString(R.string.cancel_edit);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        // destroy activity
        finish();
    }

    /***
     * This Function is Called when the ui is called to be updated.
     * @param
     * @return Nothing
     */
    public void updateUI(){
        Intent intent = new Intent(this, PersonDetailActivity.class);
        startActivity(intent);
    }

    /***
     * This Function is Called when the ui's password is called to be updated.
     * @param
     * @return Nothing
     */
    public void updateUIPassword(){
        // modify login state
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); //获取编辑器
        editor.putInt("state", 0);
        editor.commit(); //提交修改
        // Quit the login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}