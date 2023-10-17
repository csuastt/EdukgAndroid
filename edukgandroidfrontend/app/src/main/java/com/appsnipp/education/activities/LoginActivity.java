/***
 * This Activity is used for logging in page by the user.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.appsnipp.education.R;
import com.appsnipp.education.ui.login.LoggedInUserView;
import com.appsnipp.education.ui.login.LoginFormState;
import com.appsnipp.education.ui.login.LoginResult;
import com.appsnipp.education.ui.login.LoginViewModel;
import com.appsnipp.education.ui.login.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    /***
     * This Function is used for the creation of the class ui of Learn Activity.
     * @param savedInstanceState the bundle to create the view of learn activity
     * @return Nothing
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final TextView jumpToRegister = findViewById(R.id.go_register);
        jumpToRegister.setOnClickListener(new View.OnClickListener() {      //  点击跳转时
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
//        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
//                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                    //Complete and destroy login activity once successful
                    finish();
                }
            }
        });

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
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(), getApplicationContext());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), getApplicationContext());
            }
        });
    }

    /***
     * This Function is used for the update process after logging in.
     * @param model the model used when logging in
     * @return Nothing
     */
    private void updateUiWithUser(LoggedInUserView model) {
         String welcome = getString(R.string.success_login);
        // initiate successful logged in experience
        // [NOTE]: This part is changing between activities.
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Open outer class (close recent activity)
        // intent.setClassName("com.appsnipp.education","MainActivity");
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        Editable usernameText = usernameEditText.getText();
        Editable passwordText = passwordEditText.getText();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); //获取编辑器
        editor.putString("username", usernameText.toString());
        editor.putString("password", passwordText.toString());
        editor.putInt("state", 1);
        editor.commit(); //提交修改

        startActivity(intent);
        // [NOTE]: This is deprecated
         Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    /***
     * This Function is used when the logging in process is failed.
     * @param errorString the error message sent when login is failed.
     * @return Nothing
     */
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    /***
     * This Function is called when jumping back to main.
     * @param view the view of this activity (although not usd)
     * @return Nothing
     */
    public void backToMain(View view) {
        // back
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // finish
        finish();
    }
}