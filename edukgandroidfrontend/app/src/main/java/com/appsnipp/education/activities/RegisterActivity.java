/***
 * This Activity is used for the registering event and the activity.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.appsnipp.education.ui.login.LoginViewModelFactory;
import com.appsnipp.education.ui.login.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;

    /***
     * This Function is used for the creation of the class ui of Register Activity.
     * @param savedInstanceState the bundle used for the creation of the ui
     * @return Nothing
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(RegisterViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText passwordEditText2 = findViewById(R.id.password2);
        final Button loginButton = findViewById(R.id.signup);
//        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final TextView jumpToLogin = findViewById(R.id.go_login);
        jumpToLogin.setOnClickListener(new View.OnClickListener() {      //  点击跳转时
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        registerViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
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
                    if (loginFormState.getPasswordError() == R.string.invalid_password2){
                        passwordEditText2.setError(getString(loginFormState.getPasswordError()));
                    }else{
                        passwordEditText.setError(getString(loginFormState.getPasswordError()));
                    }
                }
            }
        });

        registerViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
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
                registerViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        passwordEditText2.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText2.addTextChangedListener(afterTextChangedListener);
        passwordEditText2.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(), getApplicationContext());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
                registerViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), getApplicationContext());
            }
        });
    }

    /***
     * This Function is used for the updating of UI
     * @param model the viewModel of this page
     * @return Nothing
     */
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.success_register);
        // initiate successful logged in experience
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    /***
     * This Function is used when logging in failed.
     * @param errorString the error message in integer
     * @return Nothing
     */
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    /***
     * This Function is called when pressing back to main page.
     * @param view the view page of this activity
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