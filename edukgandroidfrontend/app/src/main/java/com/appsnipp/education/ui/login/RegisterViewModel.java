package com.appsnipp.education.ui.login;

/***
 * This is the register view model page.
 * @author Shuning Zhang
 * @version 1.0
 */

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.data.LoginRepository;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password, Context cxt) {
        // login through POST request
//        Result<LoggedInUser> result = loginRepository.login(username, password);

        String url = AppSingleton.URL_PREFIX + "/api/register";
        // build request
        StringRequest strRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // success
                        System.err.println("yes!");
                        loginResult.setValue(new LoginResult(new LoggedInUserView("test")));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.err.println(error.toString());
                        String body;
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            // exception
                            body = "";
                        } catch (NullPointerException e) {
                            // another exception
                            body = "";
                        }
                        if (body.contains("用户已注册")){
                            loginResult.setValue(new LoginResult(R.string.username_duplicated));
                        } else {
                            loginResult.setValue(new LoginResult(R.string.server_failed));
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
        AppSingleton.getInstance(cxt).addToRequestQueue(strRequest);
    }

    public void loginDataChanged(String username, String password, String password2) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else if (!isPassword2Valid(password, password2)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password2));
        }
        else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // if the password1 is equal to password2
    private boolean isPassword2Valid(String password, String password2){
        return Objects.equals(password, password2);
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        } else {
            if(username.trim().isEmpty()) return false;
            else {
                String regex = "([a-zA-Z0-9]{6,12})";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(username);
                return m.matches();
            }
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}